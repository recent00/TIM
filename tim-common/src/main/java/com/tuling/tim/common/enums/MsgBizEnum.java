package com.tuling.tim.common.enums;

import java.util.ArrayList;
import java.util.List;

/**
 *   
 */

public enum MsgBizEnum {

    /** 消息应答包 */
    MSG_RES("1", "消息应答包"),
    /** 消息数据包 */
    MSG_DATA("2", "消息数据包"),
    ;


    /** 枚举值码 */
    private final String type;

    /** 枚举描述 */
    private final String message;

    /**
     * 构建一个 StatusEnum 。
     * @param code 枚举值码。
     * @param message 枚举描述。
     */
    private MsgBizEnum(String code, String message) {
        this.type = code;
        this.message = message;
    }

    /**
     * 得到枚举值码。
     * @return 枚举值码。
     */
    public String getCode() {
        return type;
    }

    /**
     * 得到枚举描述。
     * @return 枚举描述。
     */
    public String getMessage() {
        return message;
    }


}
