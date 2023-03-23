package com.tuling.tim.server.kafka.consumer;

import com.alibaba.fastjson.JSON;
import com.tuling.tim.common.pojo.MsgBiz;
import com.tuling.tim.server.config.BeanConfig;
import com.tuling.tim.server.service.IImMsgContentService;
import com.tuling.tim.server.service.IImUserMsgBoxService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;


@Component
public class ImConsumer {
    private final static Logger LOGGER = LoggerFactory.getLogger(ImConsumer.class);
    private final static String TOPIC_NAME = "im-p2p-msg";

    @Autowired
    IImMsgContentService iImMsgContentService;
    @Autowired
    IImUserMsgBoxService iImUserMsgBoxService;

    @Autowired
    BeanConfig beanConfig;

    @KafkaListener(topics = TOPIC_NAME, groupId = "IM-1")
    public void listenIMGroup(ConsumerRecord<String, String> record, Acknowledgment ack) {
        String msg = record.value();
        LOGGER.info("ImConsumer, msg:{}", msg);
        MsgBiz msgBiz = JSON.parseObject(msg, MsgBiz.class);
        beanConfig.getMap().get(msgBiz.getMsgBizEnum().getCode()).execute(msgBiz);
        //手动提交offset
        ack.acknowledge();
    }
}
