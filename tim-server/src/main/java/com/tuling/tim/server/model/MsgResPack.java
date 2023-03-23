package com.tuling.tim.server.model;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tuling.tim.common.enums.MsgBizEnum;
import com.tuling.tim.common.pojo.MsgBiz;
import com.tuling.tim.common.protocol.TIMReqMsg;
import com.tuling.tim.server.pojo.ImMsgContent;
import com.tuling.tim.server.service.IImMsgContentService;
import com.tuling.tim.server.service.IImUserMsgBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MsgResPack implements MsgPack {

    @Autowired
    IImMsgContentService iImMsgContentService;
    @Autowired
    IImUserMsgBoxService iImUserMsgBoxService;

    private String type = MsgBizEnum.MSG_RES.getCode();


    @Override
    public void execute(MsgBiz msgBiz) {
        String body = msgBiz.getBody();
        TIMReqMsg timReqMsg = JSON.parseObject(body, TIMReqMsg.class);
        updateReceived(timReqMsg);
    }

    @Override
    public String getType() {
        return type;
    }

    private void updateReceived(TIMReqMsg timReqMsg) {
        Long msgId = timReqMsg.getMsgId();
        UpdateWrapper<ImMsgContent> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("mid", msgId);
        updateWrapper.set("is_received", 1);
        boolean update = iImMsgContentService.update(null, updateWrapper);
    }
}
