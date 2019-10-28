package com.rondaful.cloud.supplier.rabbitmq;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 仓库操作发送对列处理
 * @author songjie
 *
 */


@Component
public class WareHouseSender {

    private final Logger logger = LoggerFactory.getLogger(WareHouseSender.class);
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void updateWareHouseStatus(String msg) {
    	logger.info("仓库停启用发送的数据{}:",msg);
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(RabbitConfig.WAREHOUSEEXCHANGE, RabbitConfig.WAREHOUSEROUTINGKEY, msg, correlationId);
    }
    
    public void wareHouseAuthorize(String msg) {
    	logger.info("仓库授权成功后发送的数据{}:",msg);
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(RabbitConfig.WAREHOUSEAUTHORIZEEXCHANGE, RabbitConfig.WAREHOUSEAUTHORIZEROUTINGKEY, msg, correlationId);
    }
    
    
}
