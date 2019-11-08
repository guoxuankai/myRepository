package com.brandslink.cloud.common.rabbitmq;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;


/**
 * 用户权限相关发送队列
 */
@Component
public class AuthenListSender {

    private final static Logger log = LoggerFactory.getLogger(MessageSender.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 告知用户服务，某平台的权限缓存为空
     * @param platformType 平台类型   0供应商平台  1卖家平台  2管理平台
     */
    public void sendHaveNotAuthenListPlatform(Integer platformType) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        log.debug("通知用户服务某平台权限缓存为空==>{}{}", correlationId, platformType);
        rabbitTemplate.convertAndSend(CommonRabbitConfig.AUTHEN_LIST_EXCHANGE, CommonRabbitConfig.AUTHEN_LIST_KEY, platformType, correlationId);
    }

}
