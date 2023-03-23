package com.tuling.tim.server.controller;

import com.alibaba.fastjson.JSON;
import com.tuling.tim.common.enums.StatusEnum;
import com.tuling.tim.common.pojo.ChatRecordsInfo;
import com.tuling.tim.common.req.PushMsgReqVo;
import com.tuling.tim.common.res.BaseResponse;
import com.tuling.tim.server.api.ServerApi;
import com.tuling.tim.server.api.vo.req.SendMsgReqVO;
import com.tuling.tim.server.api.vo.res.SendMsgResVO;
import com.tuling.tim.server.facade.TIMMsgFacade;
import com.tuling.tim.server.server.TIMServer;
import com.tuling.tim.server.service.IImMsgContentService;
import com.tuling.tim.server.service.IImUserMsgBoxService;
import org.beetl.ext.fn.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @since JDK 1.8
 */
@Controller
@RequestMapping("/")
public class IndexController implements ServerApi {

    private final static Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private TIMServer TIMServer;

    @Autowired
    private TIMMsgFacade timMsgFacade;

    /**
     * @param sendMsgReqVO
     * @return
     */
    @Override
    @RequestMapping(value = "sendMsg", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse<SendMsgResVO> sendMsg(@RequestBody SendMsgReqVO sendMsgReqVO) {
        BaseResponse<SendMsgResVO> res = new BaseResponse();
        TIMServer.sendMsg(sendMsgReqVO);

        SendMsgResVO sendMsgResVO = new SendMsgResVO();
        sendMsgResVO.setMsg("OK");
        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        res.setDataBody(sendMsgResVO);
        return res;
    }

    @Override
    @RequestMapping(value = "getChatRecords", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse<List<ChatRecordsInfo.DataBodyBean>> getChatRecords(@RequestBody PushMsgReqVo vo) {
        BaseResponse<List<ChatRecordsInfo.DataBodyBean>> res = new BaseResponse();
        Long ownerId = vo.getUserId();
        Long otherId = vo.getSenderId();
        List<ChatRecordsInfo.DataBodyBean> chatRecords = timMsgFacade.getChatRecords(ownerId, otherId);
        LOGGER.info("getChatRecords:{}", JSON.toJSONString(chatRecords));
        res.setDataBody(chatRecords);
        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        return res;
    }

}
