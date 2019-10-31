package com.brandslink.cloud.common.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 测试延迟消息
 */
@Component
public class DelaySender {

	private final static Logger log = LoggerFactory.getLogger(DelaySender.class);

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public void sendMessage(String message) {
		CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
		log.debug("延迟MQ消息开始发送==>{}{}", correlationId, message);
		rabbitTemplate.convertAndSend(CommonRabbitConfig.TEST_DELAY_EXCHANGE, CommonRabbitConfig.TEST_DELAY_KEY, message, m -> {
				m.getMessageProperties().setHeader("x-delay", 10000); // 设置延迟 5 秒消费
				return m;
		}, correlationId);
	}

}
