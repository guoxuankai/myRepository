package com.rondaful.cloud.seller.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressPhotoModel;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressPublishModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AliexpressSender {
	
    private final Logger logger = LoggerFactory.getLogger(AliexpressSender.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(AliexpressPublishModel model) {
    	logger.info("aliexpress操作队列进入:{}",JSONObject.toJSONString(model));
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(RabbitConfig.ALIEXPRESS_EXCHANGE, RabbitConfig.ALIEXPRESS_ROUTINGKEY, model, correlationId);
    }

    public void sendPhoto(AliexpressPhotoModel model) {
        logger.info("AliexpressPhoto操作队列进入:{}",JSONObject.toJSONString(model));
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(RabbitConfig.ALIEXPRESS_PHOTO_EXCHANGE, RabbitConfig.ALIEXPRESS_PHOTO_ROUTINGKEY, JSONObject.toJSONString(model), correlationId);
    }

    public void sendListing(AliexpressPhotoModel model) {
        logger.info("AliexpressLISTINT操作队列进入:{}",JSONObject.toJSONString(model));
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(RabbitConfig.ALIEXPRESS_LISTING_EXCHANGE, RabbitConfig.ALIEXPRESS_LISTING_ROUTINGKEY, JSONObject.toJSONString(model), correlationId);
    }

}
 