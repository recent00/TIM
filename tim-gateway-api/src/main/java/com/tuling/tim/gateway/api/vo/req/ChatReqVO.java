package com.tuling.tim.gateway.api.vo.req;

import com.tuling.tim.common.req.BaseRequest;

import javax.validation.constraints.NotNull;

/**
 * Google Protocol 编解码发送
 *
 * @since JDK 1.8
 */
public class ChatReqVO extends BaseRequest {

    @NotNull(message = "receiverId 不能为空")
    private Long receiverId;

    @NotNull(message = "receiverId 不能为空")
    private Long senderId;

    public ChatReqVO(Long receiverId, Long senderId, String msg, Long msgId) {
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.msg = msg;
        this.msgId = msgId;
    }

    public Long getSenderId() {
        return senderId;
    }

    @Override
    public String toString() {
        return "ChatReqVO{" +
                "receiverId=" + receiverId +
                ", senderId=" + senderId +
                ", msg='" + msg + '\'' +
                ", msgId=" + msgId +
                '}';
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    @NotNull(message = "msg 不能为空")
    private String msg;

    @NotNull(message = "msgId 不能为空")
    private Long msgId;

    public ChatReqVO() {
    }

    public ChatReqVO(Long receiverId, String msg, Long msgId) {
        this.receiverId = receiverId;
        this.msg = msg;
        this.msgId = msgId;
    }

    public ChatReqVO(Long receiverId, String msg) {
        this.receiverId = receiverId;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

}
