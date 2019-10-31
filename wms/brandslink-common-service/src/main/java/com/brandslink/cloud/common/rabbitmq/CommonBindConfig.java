package com.brandslink.cloud.common.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.brandslink.cloud.common.rabbitmq.CommonRabbitConfig.CMS_MESSAGE_NOTICE_EXCHANGE;
import static com.brandslink.cloud.common.rabbitmq.CommonRabbitConfig.CMS_MESSAGE_NOTICE_QUEUE;

@Configuration
public class CommonBindConfig {

	@Bean("sms")
	Binding bindingExchangeMessage1(@Qualifier("queueSms") Queue queueSms,
			@Qualifier("exchangeSms") TopicExchange exchange) {
		return BindingBuilder.bind(queueSms).to(exchange).with(CommonRabbitConfig.SMS_KEY);
	}

	@Bean("cms")
	Binding bindingExchangeMessage2(@Qualifier(CommonRabbitConfig.CMS_MESSAGE_NOTICE_QUEUE) Queue queueCms,
								   @Qualifier(CommonRabbitConfig.CMS_MESSAGE_NOTICE_EXCHANGE) TopicExchange exchange) {
		return BindingBuilder.bind(queueCms).to(exchange).with(CommonRabbitConfig.CMS_MESSAGE_NOTICE_KEY);
	}

	@Bean("authenlist")
	Binding bindingExchangeAuthenList(@Qualifier(CommonRabbitConfig.AUTHEN_LIST_QUEUE) Queue queueAuthenList,
									  @Qualifier(CommonRabbitConfig.AUTHEN_LIST_EXCHANGE) TopicExchange exchange){
		return BindingBuilder.bind(queueAuthenList).to(exchange).with(CommonRabbitConfig.AUTHEN_LIST_KEY);
	}

	@Bean("delay")
	Binding bindingExchangeDelay(@Qualifier(CommonRabbitConfig.TEST_DELAY_QUEUE) Queue TEST_DELAY_QUEUE,
									  @Qualifier(CommonRabbitConfig.TEST_DELAY_EXCHANGE) CustomExchange TEST_DELAY_EXCHANGE){
		return BindingBuilder.bind(TEST_DELAY_QUEUE).to(TEST_DELAY_EXCHANGE).with(CommonRabbitConfig.TEST_DELAY_KEY).noargs();
	}

}
