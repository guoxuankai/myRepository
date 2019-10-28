package com.rondaful.cloud.seller.rabbitmq;


import com.rondaful.cloud.seller.mapper.AmazonPublishSubListingMapper;
import jodd.util.ArraysUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommodityUpOrDownReceiver {

    private final Logger logger = LoggerFactory.getLogger(CommodityUpOrDownReceiver.class);

    @Autowired
    private AmazonPublishSubListingMapper amazonPublishSubListingMapper;

    /**
     * 接收商品上架sku的消息推送
     * @param sku 接收的消息，就是sku
     */
    @RabbitListener(queues = "sku-up-queue")
    public void processUp(String sku) {
        try {
            logger.info("商品上架推送消息： {}",sku);// todo
            int i = amazonPublishSubListingMapper.upPLSKU(sku);
        } catch (Exception e) {
            logger.error("商品sku: " + sku + " 上架处理异常", e);
        }
    }


    /**
     * 接收商品下架的sku推送消息
     * @param sku 接收到的消息，就是sku
     */
    @RabbitListener(queues = "sku-down-queue")
    public void processDown(String sku) {
        try {
            logger.info("商品下架推送消息： {}",sku);//todo
            amazonPublishSubListingMapper.downPLSKU(sku);
        } catch (Exception e) {
            logger.error("商品sku: " + sku + " 下架处理异常", e);
        }
    }
    /**
     * 接收商品侵权的spu推送消息
     * @param sku 接收到的消息，sku
     */
    @RabbitListener(queues = "sku-tort-queue")
    public void processTort(String sku) {
        try {
            logger.info("商品侵权squ推送： {}",sku);//todo
            amazonPublishSubListingMapper.tortPLSKU(sku);
        } catch (Exception e) {
            logger.error("商品sku: " + sku + " 侵权处理异常", e);
        }
    }

}
