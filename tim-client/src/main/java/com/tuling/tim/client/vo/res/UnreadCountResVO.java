package com.tuling.tim.client.vo.res;

import java.io.Serializable;

public class UnreadCountResVO implements Serializable {

    private String code;
    private String message;
    private Object reqNo;
    private String dataBody;

    public UnreadCountResVO() {
    }

    public UnreadCountResVO(String code, String message, Object reqNo, String dataBody) {
        this.code = code;
        this.message = message;
        this.reqNo = reqNo;
        this.dataBody = dataBody;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getReqNo() {
        return reqNo;
    }

    public void setReqNo(Object reqNo) {
        this.reqNo = reqNo;
    }

    public String getDataBody() {
        return dataBody;
    }

    public void setDataBody(String dataBody) {
        this.dataBody = dataBody;
    }
}
