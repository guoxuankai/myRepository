package com.rondaful.cloud.transorder.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;


/**
 * 消息发送
 * */
@Component
public class TestSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send() {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE1, RabbitConfig.ROUTINGKEY1, "我是消息1", correlationId);
    }

}
