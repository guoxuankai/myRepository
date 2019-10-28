package com.rondaful.cloud.user.rabbitmq;


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
    Binding bindingExchangeMessage01(@Qualifier("queueS") Queue queueS, @Qualifier("exchange1") TopicExchange exchange1) {
        return BindingBuilder.bind(queueS).to(exchange1).with(RabbitConfig.ROUTINGKEY1);
    }

    /**
     * 推送卖家财务修改失败记录
     * @param financeSellerInit
     * @param exchange1
     * @return
     */
    @Bean
    Binding bindingExchangeMessage02(@Qualifier("financeSellerInit") Queue financeSellerInit, @Qualifier("exchange1") TopicExchange exchange1) {
        return BindingBuilder.bind(financeSellerInit).to(exchange1).with(RabbitConfig.FINANCESELLERINITKEY);
    }

    @Bean("bindingExchangeMessage03")
    Binding bindingExchangeMessage03(@Qualifier("cmsMessageQueue") Queue cmsMessageQueue, @Qualifier("userExchange") TopicExchange userExchange) {
        return BindingBuilder.bind(cmsMessageQueue).to(userExchange).with(RabbitConfig.CMS_MESSAGE_QUEUE_KEY);
    }


}
