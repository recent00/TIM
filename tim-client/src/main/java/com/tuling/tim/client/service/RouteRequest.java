package com.tuling.tim.client.service;

import com.tuling.tim.client.vo.req.GroupReqVO;
import com.tuling.tim.client.vo.req.LoginReqVO;
import com.tuling.tim.client.vo.req.P2PReqVO;
import com.tuling.tim.client.vo.res.ChatRecordResVo;
import com.tuling.tim.client.vo.res.TIMServerResVO;
import com.tuling.tim.client.vo.res.OnlineUsersResVO;
import com.tuling.tim.common.pojo.TIMMsgInfo;

import java.util.List;

/**
 *
 * @since JDK 1.8
 */
public interface RouteRequest {

    /**
     * 群发消息
     * @param groupReqVO 消息
     * @throws Exception
     */
    void sendGroupMsg(GroupReqVO groupReqVO) throws Exception;


    /**
     * 私聊
     * @param p2PReqVO
     * @throws Exception
     */
    void sendP2PMsg(P2PReqVO p2PReqVO)throws Exception;

    /**
     * 发送消息确认包
     * @throws Exception
     */
    void ensureMsg(Long msgId) throws Exception;

    /**
     * 获取服务器
     * @return 服务ip+port
     * @param loginReqVO
     * @throws Exception
     */
    TIMServerResVO.ServerInfo getTIMServer(LoginReqVO loginReqVO) throws Exception;

    /**
     * 获取所有在线用户
     * @return
     * @throws Exception
     */
    List<OnlineUsersResVO.DataBodyBean> onlineUsers()throws Exception ;


    void offLine() ;

    void queryOfflineMsg(Long userId, Long senderId);

    /**
     * 获取总的未读数
     * @return
     */
    String getAllUnreadCount(Long userId);

    /**
     * 通过userId获取未读数
     * @param userId
     * @return
     */
    String getUnreadCountByUserId(Long userId, Long senderId);

    /**
     * 查询聊天记录
     * @param ownerId 自己的id
     * @param otherId 对方的id
     * @return
     */
    List<ChatRecordResVo.DataBodyBean> getChatRecordsByUserId(Long ownerId, Long otherId);
}
