package com.brandslink.cloud.user.rabbitmq;


import com.alibaba.fastjson.JSON;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.user.mapper.UserInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class ReceivingInWorkReceiver {

    private static Logger logger = LoggerFactory.getLogger(ReceivingInWorkReceiver.class);

    @Resource
    private UserInfoMapper userInfoMapper;


    @RabbitListener(queues = "warehouse-queue1")
    public void process(String message) {
        logger.info("监听队列，接收仓库名称修改信息：{}", message);

        Map map = JSON.parseObject(message, Map.class);
        String warehouseCode = (String) map.get("warehouseCode");
        String warehouseName = (String) map.get("warehouseName");

        try {
            userInfoMapper.updateWarehouseNameByWarehouseId(warehouseCode, warehouseName);
        } catch (Exception e) {
            logger.error("监听队列，接收仓库名称修改信息异常：{}", e.getMessage());
            e.printStackTrace();
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100408);
        }
    }
}



