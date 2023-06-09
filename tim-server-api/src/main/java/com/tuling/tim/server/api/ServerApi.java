package com.tuling.tim.server.api;

import com.tuling.tim.common.req.PushMsgReqVo;
import com.tuling.tim.server.api.vo.req.SendMsgReqVO;

/**
 *
 * @since JDK 1.8
 */
public interface ServerApi {

    /**
     * Push msg to client
     * @param sendMsgReqVO
     * @return
     * @throws Exception
     */
    Object sendMsg(SendMsgReqVO sendMsgReqVO) throws Exception;

    /**
     * 获取聊天记录
     * @param vo
     * @return
     */
    Object getChatRecords(PushMsgReqVo vo);
}
