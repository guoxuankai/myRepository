package com.rondaful.cloud.order.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.order.entity.orderRule.SKUMapMailRuleDTO;
import com.rondaful.cloud.order.service.TriggerConverSYSService;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Collections;

/**
 * @ProjectName: Rondaful
 * @Package: com.rondaful.cloud.order.rabbitmq
 * @ClassName: SKUMapToConvertOrderReceiver
 * @Author: Superhero
 * @Description: 添加、编辑SKU映射触发转单队列监听
 * @Date: 2019/9/5 15:10
 */
@Component
public class SKUMapToConvertOrderReceiver {
    @Autowired
    private TriggerConverSYSService triggerConverSYSService;

    private final static Logger logger = LoggerFactory.getLogger(SKUMapToConvertOrderReceiver.class);

    @RabbitListener(queues = "order-transfer-queue")
    public void skuMapToConvertOrder(String jsonStr) {
        logger.error("新增或编辑SKU映射触发转单参数：{}", jsonStr);
        try {
            SKUMapMailRuleDTO skuMapMailRuleDTO = JSONObject.parseObject(jsonStr, SKUMapMailRuleDTO.class);
            logger.error("新增或编辑SKU映射触发转单参数：{}", FastJsonUtils.toJsonString(skuMapMailRuleDTO));
            if (skuMapMailRuleDTO!=null) {
                try {
                    triggerConverSYSService.triggerSKUMapAndMailRuleMate(Collections.singletonList(skuMapMailRuleDTO));
                } catch (ParseException e) {
                    logger.error("新增或编辑SKU映射触发转单异常：{}", e);
                }
            }
        } catch (Exception e) {
            logger.error("新增或编辑SKU映射触发转单错误，参数：{},异常信息：{}", jsonStr,e);
        }
    }
}
