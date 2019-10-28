package com.rondaful.cloud.order.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.order.service.IOrderRuleService;
import com.rondaful.cloud.order.service.ISellerSkuMapService;
import com.rondaful.cloud.order.service.aliexpress.IAliexpressOrderService;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class RuleOrMapReceiver {


    private final Logger logger = LoggerFactory.getLogger(RuleOrMapReceiver.class);

    private final IOrderRuleService iOrderRuleService;

    private final ISellerSkuMapService iSellerSkuMapService;
    @Autowired
    private  IAliexpressOrderService aliexpressOrderService;
    @Autowired
    public RuleOrMapReceiver(IOrderRuleService iOrderRuleService, ISellerSkuMapService iSellerSkuMapService) {
        this.iOrderRuleService = iOrderRuleService;
        this.iSellerSkuMapService = iSellerSkuMapService;
    }

    //监听队列queue-discard-warehouse-mail
    @RabbitListener(queues = "queue-discard-warehouse-mail")
    public void warehouseOrMailProcess(String message) {   //{"deliveryWarehouseId":"111","mailTypeId":"111"}
        try {
            if (StringUtils.isNotBlank(message)) {
                logger.info("仓库或者邮寄方式废弃mq接收消息 ： " + message  );
                JSONObject obj = JSONObject.parseObject(message);
                iOrderRuleService.discardOrderRule(obj.getString("warehouseCode"), obj.getString("logisticsCode"));
            }
        } catch (Exception e) {
            logger.error("监听队列 queue-discard-warehouse-mail 后续异常，监听数据 ：" + message, e);
        }
    }

    @RabbitListener(queues = "commodity-lowerframes-queue")
    public void orderMapProcess(String message) {
        try {
            if (StringUtils.isNotBlank(message)) {
                iSellerSkuMapService.discardMap(message);
            }
        } catch (Exception e) {
            logger.error("监听队列 commodity-lowerframes-queue 后续异常，监听数据 ：" + message, e);
        }
    }

    @RabbitListener(queues = "queue.aliexpress.convert.order")
    public void aliexpressOrder(Message message){
        try {
            Map<String,Object> headers=message.getMessageProperties().getHeaders();
            aliexpressOrderService.asyncOrder(FastJsonUtils.toJsonString(headers));
        }catch (Exception e){
            logger.error("监听队列 queue.aliexpress.convert.order 后续异常，监听数据 ：" + message, e);
        }
    }


}
