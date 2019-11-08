package com.brandslink.cloud.common.rabbitmq;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SmsSender {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public void sendSms(String message) {
		CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
		rabbitTemplate.convertAndSend(CommonRabbitConfig.EXCHANGE_SMS, CommonRabbitConfig.SMS_KEY, message, correlationId);
	}

}
