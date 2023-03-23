package com.tuling.tim.common.req;

import com.tuling.tim.common.req.BaseRequest;


public class PushMsgReqVo extends BaseRequest {

    private Long userId;

    private Long senderId;

    public PushMsgReqVo(Long userId, Long senderId) {
        this.userId = userId;
        this.senderId = senderId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public PushMsgReqVo() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public PushMsgReqVo(Long userId) {
        this.userId = userId;
    }
}
