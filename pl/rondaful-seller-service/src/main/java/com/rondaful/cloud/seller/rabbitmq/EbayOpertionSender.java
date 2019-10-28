package com.rondaful.cloud.seller.rabbitmq;

import java.util.UUID;

import com.rondaful.cloud.seller.entity.ebay.EbayListingMQModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.seller.vo.PublishListingVO;

@Component
public class EbayOpertionSender {
	
    private final Logger logger = LoggerFactory.getLogger(EbayOpertionSender.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(PublishListingVO vo) {
    	logger.info("ebay操作队列进入:{}",JSONObject.toJSONString(vo));
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(RabbitConfig.EBAY_OPERTION_EXCHANGE, RabbitConfig.EBAY_OPERTION_ROUTINGKEY, vo, correlationId);
    }

    public void sendListing(EbayListingMQModel model) {
        logger.info("sendListing操作队列进入:{}",JSONObject.toJSONString(model));
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(RabbitConfig.EBAY_LISTING_EXCHANGE, RabbitConfig.EBAY_LISTING_ROUTINGKEY, JSONObject.toJSONString(model), correlationId);
    }
}
 