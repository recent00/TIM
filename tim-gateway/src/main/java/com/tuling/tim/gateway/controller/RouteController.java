package com.tuling.tim.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.tuling.tim.common.enums.MsgBizEnum;
import com.tuling.tim.common.enums.StatusEnum;
import com.tuling.tim.common.exception.TIMException;
import com.tuling.tim.common.pojo.*;
import com.tuling.tim.common.protocol.TIMReqMsg;
import com.tuling.tim.common.req.PushMsgReqVo;
import com.tuling.tim.common.res.BaseResponse;
import com.tuling.tim.common.res.NULLBody;
import com.tuling.tim.common.route.algorithm.RouteHandle;
import com.tuling.tim.common.util.RouteInfoParseUtil;
import com.tuling.tim.gateway.api.RouteApi;
import com.tuling.tim.gateway.api.vo.req.ChatReqVO;
import com.tuling.tim.gateway.api.vo.req.LoginReqVO;
import com.tuling.tim.gateway.api.vo.req.P2PReqVO;
import com.tuling.tim.gateway.api.vo.req.RegisterInfoReqVO;
import com.tuling.tim.gateway.api.vo.res.RegisterInfoResVO;
import com.tuling.tim.gateway.api.vo.res.TIMServerResVO;
import com.tuling.tim.gateway.cache.ServerCache;
import com.tuling.tim.gateway.constant.Constant;
import com.tuling.tim.gateway.service.AccountService;
import com.tuling.tim.gateway.service.CommonBizService;
import com.tuling.tim.gateway.service.UserInfoCacheService;
import com.tuling.tim.gateway.service.impl.KafkaServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.tuling.tim.common.enums.StatusEnum.OFF_LINE;

/**
 * @since JDK 1.8
 */
@Controller
@RequestMapping("/")
public class RouteController implements RouteApi {
    private final static Logger LOGGER = LoggerFactory.getLogger(RouteController.class);

    @Autowired
    private ServerCache serverCache;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserInfoCacheService userInfoCacheService;

    @Autowired
    private CommonBizService commonBizService;

    @Autowired
    private RouteHandle routeHandle;

    @Autowired
    private KafkaServiceImpl kafkaService;

    /**
     * 群聊 API
     *
     * @param groupReqVO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "groupRoute", method = RequestMethod.POST)
    @ResponseBody()
    @Override
    public BaseResponse<NULLBody> groupRoute(@RequestBody ChatReqVO groupReqVO) throws Exception {
        BaseResponse<NULLBody> res = new BaseResponse();

        LOGGER.info("msg=[{}]", groupReqVO.toString());

        //获取所有的推送列表，从redis中拿出所有用户对应的netty服务器的路由信息
        Map<Long, TIMServerResVO> serverResVOMap = accountService.loadRouteRelated();
        for (Map.Entry<Long, TIMServerResVO> timServerResVOEntry : serverResVOMap.entrySet()) {
            Long userId = timServerResVOEntry.getKey();
            TIMServerResVO TIMServerResVO = timServerResVOEntry.getValue();
            if (userId.equals(groupReqVO.getReceiverId())) {
                //过滤掉自己
                TIMUserInfo timUserInfo = userInfoCacheService.loadUserInfoByUserId(groupReqVO.getReceiverId());
                LOGGER.warn("过滤掉了发送者 userId={}", timUserInfo.toString());
                continue;
            }
            //推送消息
            ChatReqVO chatVO = new ChatReqVO(userId, groupReqVO.getMsg());
            accountService.pushMsg(TIMServerResVO, chatVO);

        }

        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        return res;
    }


    /**
     * 私聊 API
     *
     * @param p2pRequest
     * @return
     */
    @RequestMapping(value = "p2pRoute", method = RequestMethod.POST)
    @ResponseBody()
    @Override
    public BaseResponse<NULLBody> p2pRoute(@RequestBody P2PReqVO p2pRequest) throws Exception {
        BaseResponse<NULLBody> res = new BaseResponse();

        try {
            //获取接收消息用户的路由信息
            TIMServerResVO tIMServerResVO = accountService.loadRouteRelatedByUserId(p2pRequest.getReceiveUserId());
            //p2pRequest.getReceiveUserId()==>消息接收者的 userID
            ChatReqVO chatVO = new ChatReqVO(p2pRequest.getReceiveUserId(), p2pRequest.getUserId(), p2pRequest.getMsg(), p2pRequest.getMsgId());
            accountService.pushMsg(tIMServerResVO, chatVO);
            saveMsg(p2pRequest);

            res.setCode(StatusEnum.SUCCESS.getCode());
            res.setMessage(StatusEnum.SUCCESS.getMessage());

        } catch (TIMException e) {
            accountService.offLineMsg(p2pRequest);
            saveMsg(p2pRequest);
            res.setCode(e.getErrorCode());
            res.setMessage(e.getErrorMessage());
        }
        return res;
    }

    private void saveMsg(P2PReqVO p2pRequest) {
        TIMMsgInfo timMsgInfo = new TIMMsgInfo(p2pRequest.getUserId(), p2pRequest.getReceiveUserId(),
                p2pRequest.getMsg(), p2pRequest.getMsgId(), 1);

        MsgBiz msgBiz = new MsgBiz(MsgBizEnum.MSG_DATA, JSON.toJSONString(timMsgInfo));
        //发送消息给队列，消息准备落盘到数据库
        kafkaService.send(timMsgInfo.getMsgId(), JSON.toJSONString(msgBiz));
    }


    /**
     * 处理客户端收到消息应答包
     *
     * @param
     * @return
     */
    @RequestMapping(value = "msgRes", method = RequestMethod.POST)
    @ResponseBody()
    @Override
    public BaseResponse<NULLBody> msgRes(@RequestBody TIMReqMsg timReqMsg) throws Exception {
        BaseResponse<NULLBody> res = new BaseResponse();
        try {
            LOGGER.info("消息应答包, timReqMsg={}", timReqMsg);
            MsgBiz msgBiz = new MsgBiz(MsgBizEnum.MSG_RES, JSON.toJSONString(timReqMsg));
            kafkaService.send(timReqMsg.getMsgId(), JSON.toJSONString(msgBiz));
        } catch (TIMException e) {
            res.setCode(e.getErrorCode());
            res.setMessage(e.getErrorMessage());
        }
        return res;
    }

    /**
     * 获取聊天记录
     *
     * @param
     * @return
     */
    @RequestMapping(value = "getChatRecords", method = RequestMethod.POST)
    @ResponseBody()
    @Override
    public BaseResponse<List<ChatRecordsInfo.DataBodyBean>> getChatRecords(@RequestBody PushMsgReqVo vo) {
        LOGGER.info("getChatRecords:{}", JSON.toJSONString(vo));
        BaseResponse<List<ChatRecordsInfo.DataBodyBean>> res = new BaseResponse();
        ChatRecordsInfo chatRecords = accountService.getChatRecords(vo);
        LOGGER.info("chatRecords:{}", JSON.toJSONString(chatRecords));
        res.setDataBody(chatRecords.getDataBody());
        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        return res;
    }

    /**
     * 客户端下线
     *
     * @param groupReqVO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "offLine", method = RequestMethod.POST)
    @ResponseBody()
    @Override
    public BaseResponse<NULLBody> offLine(@RequestBody ChatReqVO groupReqVO) throws Exception {
        BaseResponse<NULLBody> res = new BaseResponse();

        TIMUserInfo timUserInfo = userInfoCacheService.loadUserInfoByUserId(groupReqVO.getReceiverId());

        LOGGER.info("user [{}] offline!", timUserInfo.toString());
        accountService.offLine(groupReqVO.getReceiverId());

        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        return res;
    }

    /**
     * 登录并获取一台 TIM server
     *
     * @return
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody()
    @Override
    public BaseResponse<TIMServerResVO> login(@RequestBody LoginReqVO loginReqVO) throws Exception {
        BaseResponse<TIMServerResVO> res = new BaseResponse();

        //登录校验
        StatusEnum status = accountService.login(loginReqVO);
        if (status == StatusEnum.SUCCESS) {

            // 从zookeeper里挑选一台客户端需访问的netty服务器
            String server = routeHandle.routeServer(serverCache.getServerList(), String.valueOf(loginReqVO.getUserId()));
            LOGGER.info("userName=[{}] route server info=[{}]", loginReqVO.getUserName(), server);

            // check server available
            RouteInfo routeInfo = RouteInfoParseUtil.parse(server);
            commonBizService.checkServerAvailable(routeInfo);

            //保存路由信息 redis保存客户端（用户id）与netty服务器的路由信息
            accountService.saveRouteInfo(loginReqVO, server);

            TIMServerResVO vo = new TIMServerResVO(routeInfo);
            res.setDataBody(vo);

        }
        res.setCode(status.getCode());
        res.setMessage(status.getMessage());

        return res;
    }

    /**
     * 注册账号
     *
     * @return
     */
    @RequestMapping(value = "registerAccount", method = RequestMethod.POST)
    @ResponseBody()
    @Override
    public BaseResponse<RegisterInfoResVO> registerAccount(@RequestBody RegisterInfoReqVO registerInfoReqVO) throws Exception {
        BaseResponse<RegisterInfoResVO> res = new BaseResponse();

        long userId = System.currentTimeMillis();
        RegisterInfoResVO info = new RegisterInfoResVO(userId, registerInfoReqVO.getUserName());
        info = accountService.register(info);

        res.setDataBody(info);
        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        return res;
    }

    /**
     * 获取所有在线用户
     *
     * @return
     */
    @RequestMapping(value = "onlineUser", method = RequestMethod.POST)
    @ResponseBody()
    @Override
    public BaseResponse<Set<TIMUserInfo>> onlineUser() throws Exception {
        BaseResponse<Set<TIMUserInfo>> res = new BaseResponse();

        Set<TIMUserInfo> timUserInfos = userInfoCacheService.onlineUser();
        res.setDataBody(timUserInfos);
        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        return res;
    }

    /**
     * 拉取离线消息
     *
     * @return
     */
    @RequestMapping(value = "queryOfflineMsg", method = RequestMethod.POST)
    @ResponseBody()
    @Override
    public BaseResponse<NULLBody> queryOfflineMsg(@RequestBody PushMsgReqVo vo) throws Exception {
        LOGGER.info("queryOfflineMsg userId: {}", vo.getUserId());
        BaseResponse<NULLBody> res = new BaseResponse();
        TIMServerResVO TIMServerResVO = accountService.loadRouteRelatedByUserId(vo.getUserId());
        accountService.queryOffLineMsg(TIMServerResVO, vo.getUserId(), vo.getSenderId());
        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        return res;
    }

    @RequestMapping(value = "getAllUnreadCount", method = RequestMethod.POST)
    @ResponseBody()
    @Override
    public BaseResponse<String> getAllUnreadCount(@RequestBody PushMsgReqVo vo) {
        BaseResponse<String> res = new BaseResponse();
        LOGGER.info("vo userId:{}, senderId:{}", vo.getUserId(), vo.getSenderId());
        String count = accountService.getAllUnreadCount(vo.getUserId());
        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        res.setDataBody(count);
        return res;
    }

    @RequestMapping(value = "getUnreadCountByUserId", method = RequestMethod.POST)
    @ResponseBody()
    @Override
    public BaseResponse<String> getUnreadCountByUserId(@RequestBody PushMsgReqVo vo) {
        BaseResponse<String> res = new BaseResponse();
        LOGGER.info("vo userId:{}, senderId:{}", vo.getUserId(), vo.getSenderId());
        String count = accountService.getUnreadCountByUserId(vo.getUserId(), vo.getSenderId());
        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        res.setDataBody(count);
        return res;
    }

}
