package com.tuling.tim.gateway.api;

import com.tuling.tim.common.protocol.TIMReqMsg;
import com.tuling.tim.common.req.PushMsgReqVo;
import com.tuling.tim.common.res.BaseResponse;
import com.tuling.tim.common.res.NULLBody;
import com.tuling.tim.gateway.api.vo.req.ChatReqVO;
import com.tuling.tim.gateway.api.vo.req.LoginReqVO;
import com.tuling.tim.gateway.api.vo.req.P2PReqVO;
import com.tuling.tim.gateway.api.vo.req.RegisterInfoReqVO;
import com.tuling.tim.gateway.api.vo.res.RegisterInfoResVO;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Route Api
 *
 * @since JDK 1.8
 */
public interface RouteApi {

    /**
     * group chat
     *
     * @param groupReqVO
     * @return
     * @throws Exception
     */
    Object groupRoute(ChatReqVO groupReqVO) throws Exception;

    /**
     * Point to point chat
     *
     * @param p2pRequest
     * @return
     * @throws Exception
     */
    Object p2pRoute(P2PReqVO p2pRequest) throws Exception;


    /**
     * Offline account
     *
     * @param groupReqVO
     * @return
     * @throws Exception
     */
    Object offLine(ChatReqVO groupReqVO) throws Exception;

    /**
     * Login account
     *
     * @param loginReqVO
     * @return
     * @throws Exception
     */
    Object login(LoginReqVO loginReqVO) throws Exception;

    /**
     * Register account
     *
     * @param registerInfoReqVO
     * @return
     * @throws Exception
     */
    BaseResponse<RegisterInfoResVO> registerAccount(RegisterInfoReqVO registerInfoReqVO) throws Exception;

    /**
     * Get all online users
     *
     * @return
     * @throws Exception
     */
    Object onlineUser() throws Exception;

    /**
     * query offline msg
     * @return
     * @throws Exception
     */
    BaseResponse<NULLBody> queryOfflineMsg(PushMsgReqVo vo) throws Exception;

    /**
     * 获取总的未读数
     * @return
     */
    Object getAllUnreadCount(PushMsgReqVo vo);

    /**
     * 获取指定userId发来的未读数
     * @param vo
     * @return
     */
    Object getUnreadCountByUserId(PushMsgReqVo vo);

    /**
     * 客户端接收消息应答包
     * @param timReqMsg
     * @return
     * @throws Exception
     */
    Object msgRes(TIMReqMsg timReqMsg) throws Exception;

    /**
     * 获取消息聊天记录
     * @param vo
     * @return
     */
    Object getChatRecords(PushMsgReqVo vo);
}
