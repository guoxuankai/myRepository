package com.brandslink.cloud.common.rabbitmq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


public class CommonMsgSendConfirmCallBack implements RabbitTemplate.ConfirmCallback {

    private final static Logger log = LoggerFactory.getLogger(CommonMsgSendConfirmCallBack.class);

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.debug("MQ消息发送Exchange成功==>{}", correlationData);
        } else {
            log.debug("MQ消息发送Exchange失败==>{}", correlationData);
        }
    }
}
