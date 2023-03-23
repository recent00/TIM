package com.tuling.tim.client.vo.req;

import com.tuling.tim.common.req.BaseRequest;

import javax.validation.constraints.NotNull;

/**
 * 单聊请求
 *
 * @since JDK 1.8
 */
public class P2PReqVO extends BaseRequest {

    @NotNull(message = "userId 不能为空")
    //消息发送者的 userId
    private Long userId;


    @NotNull(message = "userId 不能为空")
    //消息接收者的 userId
    private Long receiveUserId;


    @NotNull(message = "msg 不能为空")
    private String msg;

    @NotNull(message = "msgId 不能为空")
    private Long msgId;

    public P2PReqVO() {
    }

    public P2PReqVO(Long userId, Long receiveUserId, String msg, Long msgId) {
        this.userId = userId;
        this.receiveUserId = receiveUserId;
        this.msg = msg;
        this.msgId = msgId;
    }

    public P2PReqVO(Long userId, Long receiveUserId, String msg) {
        this.userId = userId;
        this.receiveUserId = receiveUserId;
        this.msg = msg;
    }

    public Long getReceiveUserId() {
        return receiveUserId;
    }

    public void setReceiveUserId(Long receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    @Override
    public String toString() {
        return "P2PReqVO{" +
                "userId=" + userId +
                ", receiveUserId=" + receiveUserId +
                ", msg='" + msg + '\'' +
                ", msgId=" + msgId +
                '}';
    }
}
