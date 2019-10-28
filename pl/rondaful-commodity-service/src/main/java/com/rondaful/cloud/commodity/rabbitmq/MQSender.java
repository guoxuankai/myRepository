package com.rondaful.cloud.commodity.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;


/**
 * MQ消息发送
 * */
@Component
public class MQSender {

    private final static Logger log = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    @Qualifier("rabbitTemplateCommodity")
    private RabbitTemplate rabbitTemplate;


    /**
     * sku下架通知
     * @param object
     */
    public void commodityLowerframes(Object object) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        log.debug("商品下架通知MQ消息开始发送==>{}{}", correlationId, object);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_COMMODITY, RabbitConfig.ROUTINGKEY_COMMODITY_LOWERFRAMES, object, correlationId);
    }


    /**
     * 商品删除通知
     * @param object
     */
    public void commodityDelete(Object object) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        log.debug("商品删除通知MQ消息开始发送==>{}{}", correlationId, object);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_COMMODITY, RabbitConfig.ROUTINGKEY_COMMODITY_DELETE, object, correlationId);
    }


    /**
     * 商品sku审核通过增加通知
     * @param object
     */
    public void commoditySkuAdd(Object object) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        log.debug("商品sku审核通过增加通知MQ消息开始发送==>{}{}", correlationId, object);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_COMMODITY, RabbitConfig.ROUTINGKEY_COMMODITY_SKU_ADD, object, correlationId);
    }

    
    /**
     * sku下架通知，卖家端消费
     * @param object
     */
    public void skuDown(Object object) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        log.debug("sku下架通知卖家端MQ消息开始发送==>{}{}", correlationId, object);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_COMMODITY, RabbitConfig.ROUTINGKEY_SKU_DOWN_STATE, object, correlationId);
    }
    
    /**
     * sku上架通知，卖家端消费
     * @param object
     */
    public void skuUp(Object object) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        log.debug("sku上架通知卖家端MQ消息开始发送==>{}{}", correlationId, object);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_COMMODITY, RabbitConfig.ROUTINGKEY_SKU_UP_STATE, object, correlationId);
    }
    
    /**
     * @Description:待审核sku/品牌数
     * @param object
     * @return void
     * @author:范津
     */
    public void unAuditNum(Object object) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        log.debug("待审核sku/品牌数通知管理后台MQ消息开始发送==>{}{}", correlationId, object);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_COMMODITY, RabbitConfig.UNAUDIT_NUM_KEY, object, correlationId);
    }
    
    /**
     * @Description:sku侵权
     * @param object
     * @return void
     * @author:范津
     */
    public void skuTortMq(Object object) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        log.debug("sku侵权通知卖家端MQ消息开始发送==>{}{}", correlationId, object);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_COMMODITY, RabbitConfig.ROUTINGKEY_SKU_TORT, object, correlationId);
    }
    
    /**
     * @Description:通知订单转单
     * @param object
     * @return void
     * @author:范津
     */
    public void orderTransferMq(Object object) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        log.error("通知订单转单MQ消息开始发送==>{}{}", correlationId, object);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_COMMODITY, RabbitConfig.ROUTINGKEY_ORDER_TRANSFER, object, correlationId);
    }
    
}
