package com.tuling.tim.server.model;

import com.alibaba.fastjson.JSON;
import com.tuling.tim.common.enums.MsgBizEnum;
import com.tuling.tim.common.pojo.MsgBiz;
import com.tuling.tim.common.pojo.TIMMsgInfo;
import com.tuling.tim.server.pojo.ImMsgContent;
import com.tuling.tim.server.pojo.ImUserMsgBox;
import com.tuling.tim.server.service.IImMsgContentService;
import com.tuling.tim.server.service.IImUserMsgBoxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MsgDataPack implements MsgPack {
    private final static Logger LOGGER = LoggerFactory.getLogger(MsgDataPack.class);

    @Autowired
    IImMsgContentService iImMsgContentService;
    @Autowired
    IImUserMsgBoxService iImUserMsgBoxService;

    private String type = MsgBizEnum.MSG_DATA.getCode();

    @Override
    public void execute(MsgBiz msgBiz) {
        String body = msgBiz.getBody();
        TIMMsgInfo timMsgInfo = JSON.parseObject(body, TIMMsgInfo.class);
        LOGGER.info("ImConsumer, timMsgInfo:{}", timMsgInfo);
        saveImMsgContent(timMsgInfo);
        saveImUserMsgBox(timMsgInfo);
    }

    @Override
    public String getType() {
        return type;
    }

    private void saveImMsgContent(TIMMsgInfo timMsgInfo) {
        ImMsgContent imMsgContent = new ImMsgContent();
        imMsgContent.setContent(timMsgInfo.getMsg());
        imMsgContent.setSenderId(timMsgInfo.getSenderId());
        imMsgContent.setRecipientId(timMsgInfo.getReceiveUserId());
        imMsgContent.setMid(timMsgInfo.getMsgId());
        imMsgContent.setMsgType(1);
        imMsgContent.setIsReceived(0);
        imMsgContent.setCreateTime(new Date());
        iImMsgContentService.save(imMsgContent);
    }

    private void saveImUserMsgBox(TIMMsgInfo timMsgInfo) {
        ImUserMsgBox sender = new ImUserMsgBox();
        ImUserMsgBox receiver = new ImUserMsgBox();
        //发信箱
        sender.setBoxType(1);
        sender.setOwnerUid(timMsgInfo.getSenderId());
        sender.setOtherUid(timMsgInfo.getReceiveUserId());
        sender.setMid(timMsgInfo.getMsgId());
        sender.setCreateTime(new Date());
        iImUserMsgBoxService.save(sender);
        receiver.setBoxType(2);
        receiver.setOwnerUid(timMsgInfo.getReceiveUserId());
        receiver.setOtherUid(timMsgInfo.getSenderId());
        receiver.setMid(timMsgInfo.getMsgId());
        receiver.setCreateTime(new Date());
        iImUserMsgBoxService.save(receiver);
    }
}
