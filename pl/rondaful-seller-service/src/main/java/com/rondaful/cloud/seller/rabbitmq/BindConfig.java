package com.rondaful.cloud.seller.rabbitmq;


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
    
    @Bean
    Binding bindingEbayOpertionExchangeMessage(@Qualifier("ebayOpertionQueue") Queue queueA, @Qualifier("ebayExchange") TopicExchange exchange1) {
        return BindingBuilder.bind(queueA).to(exchange1).with(RabbitConfig.EBAY_OPERTION_ROUTINGKEY);
    }

    @Bean
    Binding bindingAliexpressExchangeMessage(@Qualifier("aliexpressQueue") Queue queue, @Qualifier("aliexpressExchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(RabbitConfig.ALIEXPRESS_ROUTINGKEY);
    }

    @Bean
    Binding bindingEmpowerExchangeMessage(@Qualifier("empowerQueue") Queue queue, @Qualifier("empowerExchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(RabbitConfig.Empower_ROUTINGKEY);
    }

    @Bean
    Binding bindingAliexpressPhotoExchangeMessage(@Qualifier("aliexpressPhotoQueue") Queue queue, @Qualifier("aliexpressPhotoExchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(RabbitConfig.ALIEXPRESS_PHOTO_ROUTINGKEY);
    }

    @Bean
    Binding bindingAliexpressListintExchangeMessage(@Qualifier("aliexpressListingQueue") Queue queue, @Qualifier("aliexpressListingExchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(RabbitConfig.ALIEXPRESS_LISTING_ROUTINGKEY);
    }

    @Bean
    Binding bindingEbayListintExchangeMessage(@Qualifier("ebayListingQueue") Queue queue, @Qualifier("ebayListingExchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(RabbitConfig.EBAY_LISTING_ROUTINGKEY);
    }

}
  