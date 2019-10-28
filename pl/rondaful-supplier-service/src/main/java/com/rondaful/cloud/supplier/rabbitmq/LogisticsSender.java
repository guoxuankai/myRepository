package com.rondaful.cloud.supplier.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;


/**
 * 消息发送
 * */
@Component
public class LogisticsSender {
	
	private final Logger logger = LoggerFactory.getLogger(LogisticsSender.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendLogisticsDiscardMQ(String warehouseId,String logisticsCode) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE1, RabbitConfig.DISCARD_WAREHOUSE_MAIL, new JSONObject() {
        	{
        		put("warehouseId",warehouseId);
        		put("logisticsCode",logisticsCode);
        	}
        }.toString(), correlationId);
    }

}
