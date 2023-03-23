package com.tuling.tim.gateway.service.impl;

import com.tuling.tim.common.enums.StatusEnum;
import com.tuling.tim.common.exception.TIMException;
import com.tuling.tim.gateway.constant.Constant;
import com.tuling.tim.gateway.controller.RouteController;
import com.tuling.tim.gateway.service.KafkaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
public class KafkaServiceImpl implements KafkaService {
    private final static Logger LOGGER = LoggerFactory.getLogger(KafkaServiceImpl.class);
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void send(Long key, String msg) {
        LOGGER.info("KafkaServiceImpl key={}, msg={}", key, msg);
        try {
            kafkaTemplate.send(Constant.TOPIC_NAME, 0, key.toString(), msg);
        } catch (Exception e) {
            throw new TIMException(StatusEnum.FAIL);
        }
    }
}
