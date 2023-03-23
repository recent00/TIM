package com.tuling.tim.gateway.service;

import com.tuling.tim.common.enums.StatusEnum;
import com.tuling.tim.common.pojo.ChatRecordsInfo;
import com.tuling.tim.common.req.PushMsgReqVo;
import com.tuling.tim.gateway.api.vo.req.ChatReqVO;
import com.tuling.tim.gateway.api.vo.req.LoginReqVO;
import com.tuling.tim.gateway.api.vo.req.P2PReqVO;
import com.tuling.tim.gateway.api.vo.res.RegisterInfoResVO;
import com.tuling.tim.gateway.api.vo.res.TIMServerResVO;

import java.util.List;
import java.util.Map;

/**
 * 账户服务
 *
 * @since JDK 1.8
 */
public interface AccountService {

    /**
     * 注册用户
     *
     * @param info 用户信息
     * @return
     * @throws Exception
     */
    RegisterInfoResVO register(RegisterInfoResVO info) throws Exception;

    /**
     * 登录服务
     *
     * @param loginReqVO 登录信息
     * @return true 成功 false 失败
     * @throws Exception
     */
    StatusEnum login(LoginReqVO loginReqVO) throws Exception;

    /**
     * 保存路由信息
     *
     * @param msg        服务器信息
     * @param loginReqVO 用户信息
     * @throws Exception
     */
    void saveRouteInfo(LoginReqVO loginReqVO, String msg) throws Exception;

    /**
     * 加载所有用户的路有关系
     *
     * @return 所有的路由关系
     */
    Map<Long, TIMServerResVO> loadRouteRelated();

    /**
     * 获取某个用户的路有关系
     *
     * @param userId
     * @return 获取某个用户的路有关系
     */
    TIMServerResVO loadRouteRelatedByUserId(Long userId);


    /**
     * 推送消息
     *
     * @param TIMServerResVO
     * @param groupReqVO     消息
     * @throws Exception
     */
    void pushMsg(TIMServerResVO TIMServerResVO, ChatReqVO groupReqVO) throws Exception;

    /**
     * 用户下线
     *
     * @param userId 下线用户ID
     * @throws Exception
     */
    void offLine(Long userId) throws Exception;

    /**
     * 保存离线消息
     * @param p2pRequest
     * @throws Exception
     */
    void offLineMsg(P2PReqVO p2pRequest) throws Exception;

    /**
     * 拉取离线消息
     * @param TIMServerResVO
     * @param userId
     * @param senderId
     * @throws Exception
     */
    void queryOffLineMsg(TIMServerResVO TIMServerResVO, Long userId, Long senderId) throws Exception;

    /**
     * 获取总的未读数
     * @return
     */
    String getAllUnreadCount(Long userId);

    /**
     * 获取userId发来消息的未读数
     * @param userId
     * @return
     */
    String getUnreadCountByUserId(Long userId, Long senderId);

    ChatRecordsInfo getChatRecords(PushMsgReqVo vo);
}
