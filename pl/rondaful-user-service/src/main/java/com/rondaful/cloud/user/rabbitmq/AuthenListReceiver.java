package com.rondaful.cloud.user.rabbitmq;


import com.rondaful.cloud.user.service.MenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 权限相关队列接受
 */
@Component
public class AuthenListReceiver {

    private Logger logger = LoggerFactory.getLogger(AuthenListReceiver.class);

    @Autowired
    private MenuService menuService;

    /**
     * 监听队列 authen-list-queue，将对应平台的权限数据进行缓存
     * @param platformType 平台类型   0供应商平台  1卖家平台  2管理平台
     */
    @RabbitListener(queues = "queue-a")
    public void process(String platformType) {
        try {
            logger.info("从队列 authen-list-queue 中接受到值：{}",platformType);
            List<String> menuUrlsByPlatform = menuService.getMenuUrlsByPlatform(Integer.valueOf(platformType));
        }catch (Exception e){
            logger.error("从队列authen-list-queue中处理业务异常：" + platformType,e);
        }
    }
}
