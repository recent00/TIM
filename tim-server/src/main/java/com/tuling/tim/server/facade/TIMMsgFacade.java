package com.tuling.tim.server.facade;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tuling.tim.common.pojo.ChatRecordsInfo;
import com.tuling.tim.server.pojo.ImMsgContent;
import com.tuling.tim.server.pojo.ImUserMsgBox;
import com.tuling.tim.server.service.IImMsgContentService;
import com.tuling.tim.server.service.IImUserMsgBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TIMMsgFacade {

    @Autowired
    private IImUserMsgBoxService iImUserMsgBoxService;

    @Autowired
    private IImMsgContentService iImMsgContentService;

    public List<ChatRecordsInfo.DataBodyBean> getChatRecords(Long ownerId, Long otherId) {
        QueryWrapper<ImUserMsgBox> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("mid").eq("owner_uid", ownerId).and(qw -> {
            qw.eq("other_uid", otherId);
        });
        List<ImUserMsgBox> list = iImUserMsgBoxService.list(queryWrapper);
        List<Long> mids = list.stream().map(imUserMsgBox -> imUserMsgBox.getMid()).collect(Collectors.toList());
        if(mids.size() == 0) {
            return new ArrayList<>();
        }
        List<ImMsgContent> imMsgContents = iImMsgContentService.listByIds(mids);
        List<ChatRecordsInfo.DataBodyBean> dataBodyBeanList = imMsgContents.stream().map(imMsgContent -> {
            ChatRecordsInfo.DataBodyBean dataBodyBean = new ChatRecordsInfo.DataBodyBean();
            dataBodyBean.setUserId(imMsgContent.getSenderId());
            dataBodyBean.setMsg(imMsgContent.getContent());
            return dataBodyBean;
        }).collect(Collectors.toList());
        return dataBodyBeanList;
    }
}
