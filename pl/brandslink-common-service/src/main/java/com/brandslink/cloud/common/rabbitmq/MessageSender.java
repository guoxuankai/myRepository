package com.brandslink.cloud.common.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 发送后台服务通知消息
 */
@Component
public class MessageSender {

	private final static Logger log = LoggerFactory.getLogger(MessageSender.class);

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public void sendMessage(String message) {
		CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
		log.debug("通知CMS服务MQ消息开始发送==>{}{}", correlationId, message);
		rabbitTemplate.convertAndSend(CommonRabbitConfig.CMS_MESSAGE_NOTICE_EXCHANGE, CommonRabbitConfig.CMS_MESSAGE_NOTICE_KEY, message, correlationId);
	}

}
