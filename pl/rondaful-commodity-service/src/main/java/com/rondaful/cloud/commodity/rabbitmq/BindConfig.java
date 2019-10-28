package com.rondaful.cloud.commodity.rabbitmq;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.rondaful.cloud.commodity.rabbitmq.RabbitConfig.*;

/**
 * 消息队列路由交换机绑定配置
 * */
@Configuration
public class BindConfig {

    @Bean("binding1")
    Binding bindingExchangeMessage1(@Qualifier(COMMODITY_LOWERFRAMES_QUEUE) Queue queueA, @Qualifier(EXCHANGE_COMMODITY) TopicExchange exchange1) {
        return BindingBuilder.bind(queueA).to(exchange1).with(RabbitConfig.ROUTINGKEY_COMMODITY_LOWERFRAMES);
    }

    @Bean("binding2")
    Binding bindingExchangeMessage2(@Qualifier(COMMODITY_DELETE_QUEUE) Queue queueA, @Qualifier(EXCHANGE_COMMODITY) TopicExchange exchange1) {
        return BindingBuilder.bind(queueA).to(exchange1).with(RabbitConfig.ROUTINGKEY_COMMODITY_DELETE);
    }

    @Bean("binding3")
    Binding bindingExchangeMessage3(@Qualifier(COMMODITY_SKU_ADD_QUEUE) Queue queueA, @Qualifier(EXCHANGE_COMMODITY) TopicExchange exchange1) {
        return BindingBuilder.bind(queueA).to(exchange1).with(RabbitConfig.ROUTINGKEY_COMMODITY_SKU_ADD);
    }

    @Bean("binding4")
    Binding bindingExchangeMessage4(@Qualifier(SKU_DOWN_STATE_QUEUE) Queue queueA, @Qualifier(EXCHANGE_COMMODITY) TopicExchange exchange1) {
        return BindingBuilder.bind(queueA).to(exchange1).with(RabbitConfig.ROUTINGKEY_SKU_DOWN_STATE);
    }
    
    @Bean("binding5")
    Binding bindingExchangeMessage5(@Qualifier(SKU_UP_STATE_QUEUE) Queue queueA, @Qualifier(EXCHANGE_COMMODITY) TopicExchange exchange1) {
        return BindingBuilder.bind(queueA).to(exchange1).with(RabbitConfig.ROUTINGKEY_SKU_UP_STATE);
    }
    
    @Bean("binding6")
    Binding bindingExchangeMessage6(@Qualifier(CMS_MESSAGE_DISPOSE_QUEUE) Queue queueA, @Qualifier(EXCHANGE_COMMODITY) TopicExchange exchange1) {
        return BindingBuilder.bind(queueA).to(exchange1).with(RabbitConfig.UNAUDIT_NUM_KEY);
    }
    
    @Bean("binding7")
    Binding bindingExchangeMessage7(@Qualifier(SKU_TORT_QUEUE) Queue queueA, @Qualifier(EXCHANGE_COMMODITY) TopicExchange exchange1) {
        return BindingBuilder.bind(queueA).to(exchange1).with(RabbitConfig.ROUTINGKEY_SKU_TORT);
    }
    
    @Bean("binding8")
    Binding bindingExchangeMessage8(@Qualifier(ORDER_TRANSFER_QUEUE) Queue queueA, @Qualifier(EXCHANGE_COMMODITY) TopicExchange exchange1) {
        return BindingBuilder.bind(queueA).to(exchange1).with(RabbitConfig.ROUTINGKEY_ORDER_TRANSFER);
    }
}
