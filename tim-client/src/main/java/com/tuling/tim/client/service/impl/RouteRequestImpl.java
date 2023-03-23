package com.tuling.tim.client.service.impl;

import com.alibaba.fastjson.JSON;
import com.tuling.tim.client.config.AppConfiguration;
import com.tuling.tim.client.service.EchoService;
import com.tuling.tim.client.service.RouteRequest;
import com.tuling.tim.client.thread.ContextHolder;
import com.tuling.tim.client.vo.req.GroupReqVO;
import com.tuling.tim.client.vo.req.LoginReqVO;
import com.tuling.tim.client.vo.req.P2PReqVO;
import com.tuling.tim.client.vo.res.ChatRecordResVo;
import com.tuling.tim.client.vo.res.UnreadCountResVO;
import com.tuling.tim.common.constant.Constants;
import com.tuling.tim.common.pojo.TIMMsgInfo;
import com.tuling.tim.common.protocol.TIMReqMsg;
import com.tuling.tim.common.req.PushMsgReqVo;
import com.tuling.tim.client.vo.res.OnlineUsersResVO;
import com.tuling.tim.client.vo.res.TIMServerResVO;
import com.tuling.tim.common.core.proxy.ProxyManager;
import com.tuling.tim.common.enums.StatusEnum;
import com.tuling.tim.common.exception.TIMException;
import com.tuling.tim.common.res.BaseResponse;
import com.tuling.tim.gateway.api.RouteApi;
import com.tuling.tim.gateway.api.vo.req.ChatReqVO;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @since JDK 1.8
 */
@Service
public class RouteRequestImpl implements RouteRequest {

    private final static Logger LOGGER = LoggerFactory.getLogger(RouteRequestImpl.class);

    @Autowired
    private OkHttpClient okHttpClient;

    @Value("${tim.gateway.url}")
    private String gatewayUrl;

    @Autowired
    private EchoService echoService;


    @Autowired
    private AppConfiguration appConfiguration;

    @Override
    public void sendGroupMsg(GroupReqVO groupReqVO) throws Exception {
        RouteApi routeApi = new ProxyManager<>(RouteApi.class, gatewayUrl, okHttpClient).getInstance();
        ChatReqVO chatReqVO = new ChatReqVO(groupReqVO.getUserId(), groupReqVO.getMsg());
        Response response = null;
        try {
            response = (Response) routeApi.groupRoute(chatReqVO);
        } catch (Exception e) {
            LOGGER.error("exception", e);
        } finally {
            response.body().close();
        }
    }

    @Override
    public void sendP2PMsg(P2PReqVO p2PReqVO) throws Exception {
        RouteApi routeApi = new ProxyManager<>(RouteApi.class, gatewayUrl, okHttpClient).getInstance();
        com.tuling.tim.gateway.api.vo.req.P2PReqVO vo = new com.tuling.tim.gateway.api.vo.req.P2PReqVO();
        vo.setMsg(p2PReqVO.getMsg());
        vo.setReceiveUserId(p2PReqVO.getReceiveUserId());
        vo.setUserId(p2PReqVO.getUserId());
        vo.setMsgId(p2PReqVO.getMsgId());

        Response response = null;
        try {
            response = (Response) routeApi.p2pRoute(vo);
            String json = response.body().string();
            BaseResponse baseResponse = JSON.parseObject(json, BaseResponse.class);

            // account offline.用户离线
            if (baseResponse.getCode().equals(StatusEnum.OFF_LINE.getCode())) {
                LOGGER.error(p2PReqVO.getReceiveUserId() + ":" + StatusEnum.OFF_LINE.getMessage());
            }

        } catch (Exception e) {
            LOGGER.error("exception", e);
        } finally {
            response.body().close();
        }
    }

    @Override
    public void ensureMsg(Long msgId) throws Exception {
        TIMReqMsg timReqMsg = new TIMReqMsg(appConfiguration.getUserId(), ""
                , Constants.CommandType.RECEIVE, msgId);
        RouteApi routeApi = new ProxyManager<>(RouteApi.class, gatewayUrl, okHttpClient).getInstance();
        routeApi.msgRes(timReqMsg);

    }

    @Override
    public TIMServerResVO.ServerInfo getTIMServer(LoginReqVO loginReqVO) throws Exception {

        RouteApi routeApi = new ProxyManager<>(RouteApi.class, gatewayUrl, okHttpClient).getInstance();
        com.tuling.tim.gateway.api.vo.req.LoginReqVO vo = new com.tuling.tim.gateway.api.vo.req.LoginReqVO();
        vo.setUserId(loginReqVO.getUserId());
        vo.setUserName(loginReqVO.getUserName());

        Response response = null;
        TIMServerResVO TIMServerResVO = null;
        try {
            response = (Response) routeApi.login(vo);
            String json = response.body().string();
            TIMServerResVO = JSON.parseObject(json, TIMServerResVO.class);

            //重复失败
            if (!TIMServerResVO.getCode().equals(StatusEnum.SUCCESS.getCode())) {
                echoService.echo(TIMServerResVO.getMessage());

                // when client in reConnect state, could not exit.
                if (ContextHolder.getReconnect()) {
                    echoService.echo("###{}###", StatusEnum.RECONNECT_FAIL.getMessage());
                    throw new TIMException(StatusEnum.RECONNECT_FAIL);
                }

                System.exit(-1);
            }

        } catch (Exception e) {
            LOGGER.error("exception", e);
        } finally {
            response.body().close();
        }

        return TIMServerResVO.getDataBody();
    }

    @Override
    public List<OnlineUsersResVO.DataBodyBean> onlineUsers() throws Exception {
        RouteApi routeApi = new ProxyManager<>(RouteApi.class, gatewayUrl, okHttpClient).getInstance();

        Response response = null;
        OnlineUsersResVO onlineUsersResVO = null;
        try {
            response = (Response) routeApi.onlineUser();
            String json = response.body().string();
            onlineUsersResVO = JSON.parseObject(json, OnlineUsersResVO.class);

        } catch (Exception e) {
            LOGGER.error("exception", e);
        } finally {
            response.body().close();
        }

        return onlineUsersResVO.getDataBody();
    }

    @Override
    public void offLine() {
        RouteApi routeApi = new ProxyManager<>(RouteApi.class, gatewayUrl, okHttpClient).getInstance();
        ChatReqVO vo = new ChatReqVO(appConfiguration.getUserId(), "offLine");
        Response response = null;
        try {
            response = (Response) routeApi.offLine(vo);
        } catch (Exception e) {
            LOGGER.error("exception", e);
        } finally {
            response.body().close();
        }
    }

    @Override
    public void queryOfflineMsg(Long userId, Long senderId) {
        RouteApi routeApi = new ProxyManager<>(RouteApi.class, gatewayUrl, okHttpClient).getInstance();
        PushMsgReqVo vo = new PushMsgReqVo(userId, senderId);
        try {
            routeApi.queryOfflineMsg(vo);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    @Override
    public String getAllUnreadCount(Long userId) {
        RouteApi routeApi = new ProxyManager<>(RouteApi.class, gatewayUrl, okHttpClient).getInstance();
        PushMsgReqVo vo = new PushMsgReqVo(userId, 0L);
        UnreadCountResVO unreadCountResVO = null;
        try {
            Response response = (Response) routeApi.getAllUnreadCount(vo);
            String json = response.body().string();
            unreadCountResVO = JSON.parseObject(json, UnreadCountResVO.class);
            return unreadCountResVO.getDataBody();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String getUnreadCountByUserId(Long userId, Long senderId) {
        RouteApi routeApi = new ProxyManager<>(RouteApi.class, gatewayUrl, okHttpClient).getInstance();
        PushMsgReqVo vo = new PushMsgReqVo(userId, senderId);
        UnreadCountResVO unreadCountResVO = null;
        try {
            Response response = (Response) routeApi.getUnreadCountByUserId(vo);
            String json = response.body().string();
            unreadCountResVO = JSON.parseObject(json, UnreadCountResVO.class);
            return unreadCountResVO.getDataBody();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<ChatRecordResVo.DataBodyBean> getChatRecordsByUserId(Long ownerId, Long otherId) {
        RouteApi routeApi = new ProxyManager<>(RouteApi.class, gatewayUrl, okHttpClient).getInstance();
        PushMsgReqVo vo = new PushMsgReqVo(ownerId, otherId);
        ChatRecordResVo chatRecordResVo = null;
        try {
            Response response = (Response) routeApi.getChatRecords(vo);
            String json = response.body().string();
            chatRecordResVo = JSON.parseObject(json, ChatRecordResVo.class);
            return chatRecordResVo.getDataBody();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }
}
