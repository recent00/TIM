package com.tuling.tim.common.pojo;

import com.tuling.tim.common.enums.MsgBizEnum;

public class MsgBiz {

    MsgBizEnum msgBizEnum;

    String body;

    public MsgBiz() {
    }

    public MsgBiz(MsgBizEnum msgBizEnum, String body) {
        this.msgBizEnum = msgBizEnum;
        this.body = body;
    }

    public MsgBizEnum getMsgBizEnum() {
        return msgBizEnum;
    }

    public void setMsgBizEnum(MsgBizEnum msgBizEnum) {
        this.msgBizEnum = msgBizEnum;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
