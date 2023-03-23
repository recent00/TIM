package com.tuling.tim.common.pojo;

public class TIMMsgInfo {
    //消息发送者的 userId
    private Long senderId;

    //消息接收者的 userId
    private Long receiveUserId;

    private String msg;

    private Long msgId;

    private Integer msgType;

    public TIMMsgInfo() {
    }

    public TIMMsgInfo(Long senderId, Long receiveUserId, String msg, Long msgId, Integer msgType) {
        this.senderId = senderId;
        this.receiveUserId = receiveUserId;
        this.msg = msg;
        this.msgId = msgId;
        this.msgType = msgType;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
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

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }
}
