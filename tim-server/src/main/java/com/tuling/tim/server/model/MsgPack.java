package com.tuling.tim.server.model;

import com.tuling.tim.common.pojo.MsgBiz;

public interface MsgPack {

    void execute(MsgBiz msgBiz);

    String getType();
}
