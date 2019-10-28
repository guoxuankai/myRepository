package com.rondaful.cloud.seller.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.seller.vo.EmpowerVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EmpowerSender {
	
    private final Logger logger = LoggerFactory.getLogger(EmpowerSender.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(EmpowerVo vo) {
    	logger.info("Empower操作队列进入:{}",JSONObject.toJSONString(vo));
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(RabbitConfig.Empower_EXCHANGE, RabbitConfig.Empower_ROUTINGKEY, JSONObject.toJSONString(vo), correlationId);
    }
}
 