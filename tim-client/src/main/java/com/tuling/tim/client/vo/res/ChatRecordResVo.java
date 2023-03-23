package com.tuling.tim.client.vo.res;

import java.io.Serializable;
import java.util.List;

public class ChatRecordResVo implements Serializable {

    private String code;
    private String message;
    private Object reqNo;
    private List<DataBodyBean> dataBody;

    public ChatRecordResVo() {
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

    public List<DataBodyBean> getDataBody() {
        return dataBody;
    }

    public void setDataBody(List<DataBodyBean> dataBody) {
        this.dataBody = dataBody;
    }

    public static class DataBodyBean {
        /**
         * userId : 1545574841528
         * userName : zhangsan
         */

        private long userId;
        private String msg;

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
