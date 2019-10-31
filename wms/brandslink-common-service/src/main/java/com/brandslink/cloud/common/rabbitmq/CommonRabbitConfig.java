package com.brandslink.cloud.common.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class  CommonRabbitConfig {

	public static final String QUEUE_SMS = "queueSms";
	public static final String SMS_KEY = "smsKey";
	public static final String EXCHANGE_SMS = "exchangeSms";

	public static final String CMS_MESSAGE_NOTICE_QUEUE = "cms-message-notice-queue";
	public static final String CMS_MESSAGE_NOTICE_KEY = "cms-message-notice-key";
	public static final String CMS_MESSAGE_NOTICE_EXCHANGE = "cms-message-notice-exchange";

	//authen_list
	public static final String AUTHEN_LIST_QUEUE = "authen-list-queue";
	public static final String AUTHEN_LIST_KEY = "authen-list-key";
	public static final String AUTHEN_LIST_EXCHANGE = "authen-list-exchange";


	// 延迟消费
	public static final String TEST_DELAY_EXCHANGE = "testDelayExchange";
	public static final String TEST_DELAY_QUEUE = "testDelayQueue";
	public static final String TEST_DELAY_KEY = "testDelayKey";


	@Bean("queueSms")
	public Queue queueSms() {
		return new Queue(QUEUE_SMS, true);
	}

	@Bean(CMS_MESSAGE_NOTICE_QUEUE)
	public Queue queueCms() {
		return new Queue(CMS_MESSAGE_NOTICE_QUEUE, true);
	}

	@Bean(AUTHEN_LIST_QUEUE)
	public Queue queueAuthenList(){
		return new Queue(AUTHEN_LIST_QUEUE,true);
	}

	@Bean("exchangeSms")
	public TopicExchange exchangeSms() {
		return new TopicExchange(EXCHANGE_SMS);
	}

	@Bean(CMS_MESSAGE_NOTICE_EXCHANGE)
	public TopicExchange exchangeCms() {
		return new TopicExchange(CMS_MESSAGE_NOTICE_EXCHANGE);
	}

	@Bean(AUTHEN_LIST_EXCHANGE)
	public TopicExchange exchangeAuthenList() {
		return new TopicExchange(AUTHEN_LIST_EXCHANGE);
	}

	// 延迟队列
	@Bean(TEST_DELAY_QUEUE)
	public Queue TEST_DELAY_QUEUE() {
		return new Queue(TEST_DELAY_QUEUE, true);
	}

	// 延迟交换机
	@Bean(TEST_DELAY_EXCHANGE)
	public CustomExchange delayExchange() {
		Map<String, Object> map = new HashMap<>();
		map.put("x-delayed-type", "direct");
		return new CustomExchange(TEST_DELAY_EXCHANGE, "x-delayed-message",true, false, map);
	}

	@Bean
	public CommonMsgSendConfirmCallBack msgSendConfirmCallBack() {
		return new CommonMsgSendConfirmCallBack();
	}

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
			CommonMsgSendConfirmCallBack msgSendConfirmCallBack) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setConfirmCallback(msgSendConfirmCallBack);
		return template;
	}

}
