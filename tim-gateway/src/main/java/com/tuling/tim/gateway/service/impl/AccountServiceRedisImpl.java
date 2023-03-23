package com.tuling.tim.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.tuling.tim.common.core.proxy.ProxyManager;
import com.tuling.tim.common.enums.StatusEnum;
import com.tuling.tim.common.exception.TIMException;
import com.tuling.tim.common.pojo.ChatRecordsInfo;
import com.tuling.tim.common.pojo.TIMUserInfo;
import com.tuling.tim.common.req.PushMsgReqVo;
import com.tuling.tim.common.util.RouteInfoParseUtil;
import com.tuling.tim.gateway.api.vo.req.ChatReqVO;
import com.tuling.tim.gateway.api.vo.req.LoginReqVO;
import com.tuling.tim.gateway.api.vo.req.P2PReqVO;
import com.tuling.tim.gateway.api.vo.res.RegisterInfoResVO;
import com.tuling.tim.gateway.api.vo.res.TIMServerResVO;
import com.tuling.tim.gateway.constant.Constant;
import com.tuling.tim.gateway.service.AccountService;
import com.tuling.tim.gateway.service.UserInfoCacheService;
import com.tuling.tim.server.api.ServerApi;
import com.tuling.tim.server.api.vo.req.SendMsgReqVO;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tuling.tim.common.enums.StatusEnum.OFF_LINE;
import static com.tuling.tim.gateway.constant.Constant.ACCOUNT_PREFIX;
import static com.tuling.tim.gateway.constant.Constant.ROUTE_PREFIX;

/**
 * @since JDK 1.8
 */
@Service
public class AccountServiceRedisImpl implements AccountService {
    private final static Logger LOGGER = LoggerFactory.getLogger(AccountServiceRedisImpl.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserInfoCacheService userInfoCacheService;

    @Autowired
    private OkHttpClient okHttpClient;

    @Override
    public RegisterInfoResVO register(RegisterInfoResVO info) {
        String key = ACCOUNT_PREFIX + info.getUserId();

        String name = redisTemplate.opsForValue().get(info.getUserName());
        if (null == name) {
            //为了方便查询，冗余一份
            redisTemplate.opsForValue().set(key, info.getUserName());
            redisTemplate.opsForValue().set(info.getUserName(), key);
        } else {//如果用户名已经存在了，直接把信息返回给用户
            long userId = Long.parseLong(name.split(":")[1]);
            info.setUserId(userId);
            info.setUserName(info.getUserName());
        }

        return info;
    }

    @Override
    public StatusEnum login(LoginReqVO loginReqVO) throws Exception {
        //再去Redis里查询
        String key = ACCOUNT_PREFIX + loginReqVO.getUserId();
        String userName = redisTemplate.opsForValue().get(key);
        if (null == userName) {
            return StatusEnum.ACCOUNT_NOT_MATCH;
        }

        if (!userName.equals(loginReqVO.getUserName())) {
            return StatusEnum.ACCOUNT_NOT_MATCH;
        }

        //登录成功，保存登录状态，防止重复登录
        boolean status = userInfoCacheService.saveAndCheckUserLoginStatus(loginReqVO.getUserId());
        if (status == false) {
            //重复登录
            return StatusEnum.REPEAT_LOGIN;
        }

        return StatusEnum.SUCCESS;
    }

    @Override
    public void saveRouteInfo(LoginReqVO loginReqVO, String msg) throws Exception {
        String key = ROUTE_PREFIX + loginReqVO.getUserId();
        redisTemplate.opsForValue().set(key, msg);
    }

    @Override
    public Map<Long, TIMServerResVO> loadRouteRelated() {

        Map<Long, TIMServerResVO> routes = new HashMap<>(64);


        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        ScanOptions options = ScanOptions.scanOptions()
                .match(ROUTE_PREFIX + "*")
                .build();
        Cursor<byte[]> scan = connection.scan(options);

        while (scan.hasNext()) {
            byte[] next = scan.next();
            String key = new String(next, StandardCharsets.UTF_8);
            LOGGER.info("key={}", key);
            parseServerInfo(routes, key);

        }
        try {
            scan.close();
        } catch (IOException e) {
            LOGGER.error("IOException", e);
        }

        return routes;
    }

    @Override
    public TIMServerResVO loadRouteRelatedByUserId(Long userId) {
        String value = redisTemplate.opsForValue().get(ROUTE_PREFIX + userId);

        if (value == null) {
            throw new TIMException(OFF_LINE);
        }

        TIMServerResVO TIMServerResVO = new TIMServerResVO(RouteInfoParseUtil.parse(value));
        return TIMServerResVO;
    }

    private void parseServerInfo(Map<Long, TIMServerResVO> routes, String key) {
        long userId = Long.valueOf(key.split(":")[1]);
        String value = redisTemplate.opsForValue().get(key);
        TIMServerResVO TIMServerResVO = new TIMServerResVO(RouteInfoParseUtil.parse(value));
        routes.put(userId, TIMServerResVO);
    }


    @Override
    public void pushMsg(TIMServerResVO TIMServerResVO, ChatReqVO groupReqVO) throws Exception {
        TIMUserInfo timUserInfo;
        if(groupReqVO.getSenderId() == null || groupReqVO.getSenderId() == 0) {
            timUserInfo = userInfoCacheService.loadUserInfoByUserId(groupReqVO.getReceiverId());
        } else {
            timUserInfo = userInfoCacheService.loadUserInfoByUserId(groupReqVO.getSenderId());
        }
        SendMsgReqVO vo = new SendMsgReqVO(timUserInfo.getUserName() + ":" + groupReqVO.getMsg()
                ,groupReqVO.getReceiverId(), groupReqVO.getMsgId());
        String url = "http://" + TIMServerResVO.getIp() + ":" + TIMServerResVO.getHttpPort();
        ServerApi serverApi = new ProxyManager<>(ServerApi.class, url, okHttpClient).getInstance();
        Response response = null;
        try {
            response = (Response) serverApi.sendMsg(vo);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        } finally {
            response.body().close();
        }
    }

    @Override
    public void offLine(Long userId) throws Exception {

        // TODO 这里需要用lua保证原子性
        //保证原子性原因：因为为了防止用户重复登录，登录状态不允许重复添加进redis
        //如果删除了路由，这时线程调度，另一个线程是同一个用户登录，这个时候登录状态还没删除
        //用户会发现自己重复登录，明明已经退出了，却还显示重新登录，会出现问题
        //因此需要保证原子性

        //删除路由
        redisTemplate.delete(ROUTE_PREFIX + userId);

        //删除登录状态
        userInfoCacheService.removeLoginStatus(userId);
    }

    /**
     * 离线消息入库
     * @param p2pRequest
     * @throws Exception
     */
    @Override
    public void offLineMsg(P2PReqVO p2pRequest) throws Exception {
        //离线消息存入redis
        redisTemplate.opsForZSet().add(
                Constant.OFFLINE_MSG + "-" + p2pRequest.getReceiveUserId() + "-" + p2pRequest.getUserId(),
                p2pRequest.getMsg() + "_" + p2pRequest.getMsgId(),
                p2pRequest.getMsgId());
        //TODO 未读数加1
        addUnReadMsgCount(p2pRequest);
    }

    private void addUnReadMsgCount(P2PReqVO p2pRequest) {
        if(redisTemplate.opsForValue().get(Constant.UNREAD_MSG_COUNT + p2pRequest.getReceiveUserId()) == null) {
            redisTemplate.opsForValue().set(Constant.UNREAD_MSG_COUNT + p2pRequest.getReceiveUserId(), "0");
        }
        if(!redisTemplate.opsForHash().hasKey(Constant.USER_UNREAD_MSG_COUNT + p2pRequest.getReceiveUserId(),
                p2pRequest.getUserId().toString())) {
            redisTemplate.opsForHash().put(Constant.USER_UNREAD_MSG_COUNT + p2pRequest.getReceiveUserId()
                    , p2pRequest.getUserId().toString(), "0");
        }
        //TODO 保证原子性
        redisTemplate.opsForValue().increment(Constant.UNREAD_MSG_COUNT + p2pRequest.getReceiveUserId());
        redisTemplate.opsForHash().increment(Constant.USER_UNREAD_MSG_COUNT + p2pRequest.getReceiveUserId(),
                p2pRequest.getUserId().toString(), 1);
    }

    @Override
    public void queryOffLineMsg(TIMServerResVO tIMServerResVO, Long userId, Long senderId) throws Exception {
        //获取zset中消息数量
        long count = redisTemplate.opsForZSet().zCard(Constant.OFFLINE_MSG + "-" + userId + "-" + senderId);
        //查询离线消息 一次最多3条
        Set<String> msgSet = redisTemplate.opsForZSet()
                .range(Constant.OFFLINE_MSG + "-" + userId + "-" + senderId, 0, Math.min(3, count));
        if(msgSet == null || msgSet.size() == 0) {
            throw new TIMException(StatusEnum.OFFLINE_MSG_NOT_EXIT);
        }
        List<String> msgList = msgSet.stream().collect(Collectors.toList());
        double maxScore = redisTemplate.opsForZSet().score(Constant.OFFLINE_MSG + "-" + userId + "-" + senderId, msgList.get(msgList.size() - 1));
        double minScore = redisTemplate.opsForZSet().score(Constant.OFFLINE_MSG + "-" + userId + "-" + senderId, msgList.get(0));
        LOGGER.info("minScore={}, maxScore={}", minScore, maxScore);
        for (String msg : msgList) {
            String[] readMsg = msg.split("_");
            LOGGER.info("readMsg:{}", readMsg);
            ChatReqVO chatVO = new ChatReqVO(userId, senderId, readMsg[0], Long.parseLong(readMsg[1]));
            LOGGER.info("chatVO:{}", chatVO);
            pushMsg(tIMServerResVO, chatVO);
            //redisTemplate.opsForZSet().remove(Constant.OFFLINE_MSG + userId, msg);
        }
        //数据库中的离线消息设为已读，redis中离线消息删除
        //TODO 保证原子性
        Long readCount = redisTemplate.opsForZSet().removeRangeByScore(Constant.OFFLINE_MSG + "-" + userId + "-" + senderId, minScore, maxScore);
        redisTemplate.opsForValue().decrement(Constant.UNREAD_MSG_COUNT + userId, readCount);
        redisTemplate.opsForHash().increment(Constant.USER_UNREAD_MSG_COUNT + userId,
                senderId.toString(), -readCount);
    }

    @Override
    public String getAllUnreadCount(Long userId) {
        String count = redisTemplate.opsForValue().get(Constant.UNREAD_MSG_COUNT + userId);
        LOGGER.info("getAllUnreadCount count:{}", count);
        return count;
    }

    @Override
    public String getUnreadCountByUserId(Long userId, Long senderId) {
        String count = (String) redisTemplate.opsForHash().get(Constant.USER_UNREAD_MSG_COUNT + userId,
                senderId.toString());
        LOGGER.info("getUnreadCountByUserId count:{}", count);
        return count;
    }

    @Override
    public ChatRecordsInfo getChatRecords(PushMsgReqVo vo) {
        TIMServerResVO timServerResVO = loadRouteRelatedByUserId(vo.getUserId());
        String url = "http://" + timServerResVO.getIp() + ":" + timServerResVO.getHttpPort();
        ServerApi serverApi = new ProxyManager<>(ServerApi.class, url, okHttpClient).getInstance();
        Response response = null;
        ChatRecordsInfo chatRecordsInfo = null;
        try {
            response = (Response) serverApi.getChatRecords(vo);
            String json = response.body().string();
            chatRecordsInfo = JSON.parseObject(json, ChatRecordsInfo.class);
            LOGGER.info("getChatRecords:{}", JSON.toJSONString(chatRecordsInfo));
            return chatRecordsInfo;
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        } finally {
            response.body().close();
        }
        return null;
    }


}
