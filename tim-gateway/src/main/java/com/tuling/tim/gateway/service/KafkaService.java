package com.tuling.tim.gateway.service;

public interface KafkaService {

    void send(Long key, String msg);
}
