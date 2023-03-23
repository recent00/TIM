package com.tuling.tim.common.protocol;

/**
 * @since JDK 1.8
 */
public class TIMReqMsg {

    private Long requestId;
    private String reqMsg;
    private Integer type;
    private Long msgId;

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public TIMReqMsg(Long requestId, String reqMsg, Integer type, Long msgId) {
        this.requestId = requestId;
        this.reqMsg = reqMsg;
        this.type = type;
        this.msgId = msgId;
    }

    public TIMReqMsg() {
    }

    public TIMReqMsg(Long requestId, String reqMsg, Integer type) {
        this.requestId = requestId;
        this.reqMsg = reqMsg;
        this.type = type;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getReqMsg() {
        return reqMsg;
    }

    public void setReqMsg(String reqMsg) {
        this.reqMsg = reqMsg;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TIMReqMsg{" +
                "requestId=" + requestId +
                ", reqMsg='" + reqMsg + '\'' +
                ", type=" + type +
                ", msgId=" + msgId +
                '}';
    }
}
