package com.rondaful.cloud.transorder.rabbitmq;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列路由交换机绑定配置
 * */
@Configuration
public class BindConfig {

    @Bean
    Binding bindingExchangeMessage(@Qualifier("queueA") Queue queueA, @Qualifier("exchange1") TopicExchange exchange1) {
        return BindingBuilder.bind(queueA).to(exchange1).with(RabbitConfig.ROUTINGKEY1);
    }

}
