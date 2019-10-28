package com.brandslink.cloud.user.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;


/**
 * MQ消息发送
 * */
@Component
public class MQSender {

    private final static Logger log = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    @Qualifier("rabbitTemplateCommodity")
    private RabbitTemplate rabbitTemplate;


    /**
     * 测试mq
     * @param object
     */
    public void commodityLowerframes(Object object) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        log.debug("MQ消息开始发送==>{}{}", correlationId, object);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_TEST, RabbitConfig.ROUTINGKEY_TEST, object, correlationId);
    }

}
