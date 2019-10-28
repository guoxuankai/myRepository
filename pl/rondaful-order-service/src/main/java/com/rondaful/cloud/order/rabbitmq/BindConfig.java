package com.rondaful.cloud.order.rabbitmq;


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
    Binding bindingExchangeDiscardWarehouseMail(@Qualifier("queueDiscardWarehouseMail") Queue queueDiscardWarehouseMail,
                                                @Qualifier("exchange1") TopicExchange exchange1){
        return BindingBuilder.bind(queueDiscardWarehouseMail).to(exchange1).with(RabbitConfig.ROUTINGKEY_DISCARD_WAREHOUSE_OR_MAIL);
    }

    @Bean
    Binding bindingExchangeOrderStockOut(@Qualifier(RabbitConfig.QUEUE_ORDER_STOCK_OUT)Queue queueOrderStockOut,
                                         @Qualifier(RabbitConfig.EXCHANGE_ORDER_STOCK_OUT)TopicExchange exchangeOrderStockOut){
        return BindingBuilder.bind(queueOrderStockOut).to(exchangeOrderStockOut).with(RabbitConfig.ROUTINGKEY_ORDER_STOCK_OUT);
    }

    @Bean
    Binding bindingExchangeFinanceOrderCancel(@Qualifier("queueFinanceOrderCancel")Queue queueFinanceOrderCancel,
                                         @Qualifier("exchangeFinanceOrderCancel")TopicExchange exchangeFinanceOrderCancel){
        return BindingBuilder.bind(queueFinanceOrderCancel).to(exchangeFinanceOrderCancel).with(RabbitConfig.ROUTINGKEY_FINANCE_ORDER_CANCEL);
    }

    @Bean
    Binding bindingExchangeFinanceOrderConfirm(@Qualifier("queueFinanceOrderConfirm")Queue queueFinanceOrderConfirm,
                                              @Qualifier("exchangeFinanceOrderConfirm")TopicExchange exchangeFinanceOrderConfirm){
        return BindingBuilder.bind(queueFinanceOrderConfirm).to(exchangeFinanceOrderConfirm).with(RabbitConfig.ROUTINGKEY_FINANCE_ORDER_CONFIRM);
    }

    @Bean
    Binding bindingExchangeWarehouseDeliveryRecord(@Qualifier("queueWarehouseDeliveryRecord")Queue queueWarehouseDeliveryRecord,
                                               @Qualifier("exchangeWarehouseDeliveryRecord")TopicExchange exchangeWarehouseDeliveryRecord){
        return BindingBuilder.bind(queueWarehouseDeliveryRecord).to(exchangeWarehouseDeliveryRecord).with(RabbitConfig.ROUTINGKEY_WAREHOUSE_DELIVERY_RECORD);
    }

    @Bean
    Binding aliexpressOrder(@Qualifier("queueAliexpressConvertOrder") Queue queueAliexpressConvertOrder,
                            @Qualifier("exchangeAliexpressConvertOrder") TopicExchange exchangeAliexpressConvertOrder){
        return BindingBuilder.bind(queueAliexpressConvertOrder).to(exchangeAliexpressConvertOrder).with(RabbitConfig.ROUTINGKEY_ALIEXPRESS_CONVERT_ORDER);
    }

    @Bean
    Binding GoodcangStockchange(@Qualifier("queueGoodcangStockchange") Queue queueGoodcangStockchange,
                            @Qualifier("exchangeGoodCangStockChange") TopicExchange exchangeGoodCangStockChange){
        return BindingBuilder.bind(queueGoodcangStockchange).to(exchangeGoodCangStockChange).with(RabbitConfig.ROUTINGKEY_GOODCANG_STOCKCHANGE);
    }

    @Bean
    Binding GoodcangSendreceiving(@Qualifier("queueGoodcangSendreceiving") Queue queueGoodcangSendreceiving,
                            @Qualifier("exchangeGoodCangSendReceiving") TopicExchange exchangeGoodCangSendReceiving){
        return BindingBuilder.bind(queueGoodcangSendreceiving).to(exchangeGoodCangSendReceiving).with(RabbitConfig.ROUTINGKEY_GOODCANG_SENDRECEIVING);
    }

    @Bean
    Binding baseConvertOrder(@Qualifier("queueBaseConvertOrder") Queue queueBaseConvertOrder,
                            @Qualifier("exchangeBaseConvertOrder") TopicExchange exchangeBaseConvertOrder){
        return BindingBuilder.bind(queueBaseConvertOrder).to(exchangeBaseConvertOrder).with(RabbitConfig.ROUTINGKEY_BASE_CONVERT_ORDER);
    }

}
