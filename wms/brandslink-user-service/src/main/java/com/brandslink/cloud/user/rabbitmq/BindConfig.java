package com.brandslink.cloud.user.rabbitmq;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列路由交换机绑定配置
 */
@Configuration
public class BindConfig {

    @Bean("binding1")
    Binding bindingExchangeMessage1(@Qualifier(RabbitConfig.TEST_QUEUE) Queue queueA, @Qualifier(RabbitConfig.EXCHANGE_TEST) TopicExchange exchange1) {
        return BindingBuilder.bind(queueA).to(exchange1).with(RabbitConfig.ROUTINGKEY_TEST);
    }

    @Bean("bindingC1")
    Binding bingQueue1ToExchange(@Qualifier(RabbitConfig.CUSTOMER_QUEUE) Queue queue1, @Qualifier(RabbitConfig.EXCHANGE_CUSTOMER) FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queue1).to(fanoutExchange);
    }
}
