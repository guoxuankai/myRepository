package com.brandslink.cloud.finance.rabbitmq;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.brandslink.cloud.finance.rabbitmq.RabbitConfig.*;

/**
 * 消息队列路由交换机绑定配置
 * */
@Configuration
public class BindConfig {

    @Bean("binding1")
    Binding bindingExchangeMessage1(@Qualifier(TEST_QUEUE) Queue queueA, @Qualifier(EXCHANGE_TEST) TopicExchange exchange1) {
        return BindingBuilder.bind(queueA).to(exchange1).with(RabbitConfig.ROUTINGKEY_TEST);
    }

}
