package com.rondaful.cloud.order.rabbitmq;


import com.alibaba.fastjson.JSON;
import com.rondaful.cloud.common.enums.MessageEnum;
import com.rondaful.cloud.order.entity.goodcang.GoodCangSubscibe.GoodCangAccepDto;
import com.rondaful.cloud.order.entity.supplier.DeliveryRecord;
import com.rondaful.cloud.order.enums.OrderSourceEnum;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * 订单相关消息发送
 */
@Component
public class OrderMessageSender {

    private final Logger logger = LoggerFactory.getLogger(OrderMessageSender.class);

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public OrderMessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    /**
     * 发送订单相关mq消息
     *
     * @param sellerName           用户（卖家名称）
     * @param orderId              订单id
     * @param orderMessageTypeEnum 订单消息类型
     * @param errorReason          异常原因
     * @throws JSONException 异常
     */
    public void sendOrderStockOut(String sellerName, String orderId,
                                  MessageEnum orderMessageTypeEnum,
                                  String errorReason) throws JSONException {
        logger.info("订单消息通知到MQ日志：用户：" + sellerName + " 订单id " + orderId + " 类型key " +
                orderMessageTypeEnum.getTypeKey() + " 异常信息 " + errorReason);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_ORDER_STOCK_OUT, RabbitConfig.ROUTINGKEY_ORDER_STOCK_OUT,
                new JSONObject() {
                    {
                        put("sellerName", sellerName);
                        put("orderId", orderId);
                        put("messageType", orderMessageTypeEnum.getTypeKey());
                        put("errorReason", errorReason);
                    }
                }.toString(), new CorrelationData(UUID.randomUUID().toString()));
    }

    /**
     * 批量导入订单相关mq消息
     *
     * @param userName             用户（卖家名称）
     * @param content              内容
     * @param orderMessageTypeEnum 订单消息类型
     * @param belongSys            平台
     * @param fileUrl              文件路径
     * @param fileName             文件名
     * @throws JSONException 异常
     */
    public void sendOrderForImported(String userName, String content,
                                     MessageEnum orderMessageTypeEnum,
                                     String belongSys, String fileUrl, String fileName) throws JSONException {
        logger.error("订单消息通知到MQ日志：用户：" + userName + " 类型key " +
                "ORDER_IMPORT_NOTICE" + " 内容 " + content + "平台：" + belongSys);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_ORDER_STOCK_OUT, RabbitConfig.ROUTINGKEY_ORDER_STOCK_OUT,
                new JSONObject() {
                    {
                        put("userName", userName);
                        put("content", content);
                        put("messageType", "ORDER_IMPORT_NOTICE");
                        put("belongSys", belongSys);
                        put("fileUrl", fileUrl);
                        put("fileName", fileName);
                    }
                }.toString(), new CorrelationData(UUID.randomUUID().toString()));
    }


    /**
     * 财务订单取消相关mq消息
     *
     * @param orderNo 系统订单号
     */
    public void sendFinanceOrderCancelMQ(String orderNo) throws JSONException {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_FINANCE_ORDER_CANCEL, RabbitConfig.ROUTINGKEY_FINANCE_ORDER_CANCEL,
                orderNo, new CorrelationData(UUID.randomUUID().toString()));
    }

    /**
     * 财务订单确认扣款相关mq消息
     *
     * @param orderNo 系统订单号
     */
    public void sendFinanceOrderConfirmMQ(String orderNo, String actualLogisticFare) throws JSONException {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_FINANCE_ORDER_CONFIRM, RabbitConfig.ROUTINGKEY_FINANCE_ORDER_CONFIRM,
                new JSONObject() {
                    {
                        put("orderNo", orderNo);
                        put("actualLogisticFare", actualLogisticFare);
                    }
                }.toString(), new CorrelationData(UUID.randomUUID().toString()));
    }

    /**
     * 发货推送出库记录MQ消息
     *
     * @param deliveryRecord 出库记录对象
     */
    public void sendDeliverInfoToWareHouseMQ(DeliveryRecord deliveryRecord) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_WAREHOUSE_DELIVERY_RECORD, RabbitConfig.ROUTINGKEY_WAREHOUSE_DELIVERY_RECORD,
                com.alibaba.fastjson.JSONObject.toJSON(deliveryRecord).toString(), new CorrelationData(UUID.randomUUID().toString()));
        logger.error("_______________________推送出库记录MQ消息成功_______________________");
    }


    /**
     * 发货谷仓库存变更消息
     *
     * @param goodCangAccepDto 接收谷仓发送api
     */
    public void sendGoodCangStockChange(GoodCangAccepDto goodCangAccepDto) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_GOODCANG_STOCKCHANGE, RabbitConfig.ROUTINGKEY_GOODCANG_STOCKCHANGE,
                com.alibaba.fastjson.JSONObject.toJSON(goodCangAccepDto).toString(), new CorrelationData(UUID.randomUUID().toString()));
        logger.error("_______________________推送发送谷仓库存变更MQ消息成功_______________________");
    }

    /**
     * 发送谷仓入库单推送MQ消息
     *
     * @param goodCangAccepDto 谷仓入库单推送API
     */
    public void sendGoodCangReceiving(GoodCangAccepDto goodCangAccepDto) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_GOODCANG_SENDRECEIVING, RabbitConfig.ROUTINGKEY_GOODCANG_SENDRECEIVING,
                com.alibaba.fastjson.JSONObject.toJSON(goodCangAccepDto).toString(), new CorrelationData(UUID.randomUUID().toString()));
        logger.error("_______________________推送发送谷仓入库单推送MQ消息成功_______________________");
    }


    public void sendBaseConvertOrder(List list, OrderSourceEnum orderSourceEnum) {
        String body = JSON.toJSONString(list);
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("platformType", orderSourceEnum.getValue());
        Message message = new Message(body.getBytes(), messageProperties);
        this.rabbitTemplate.send(RabbitConfig.EXCHANGE_BASE_CONVERT_ORDER, RabbitConfig.ROUTINGKEY_BASE_CONVERT_ORDER, message, new CorrelationData(UUID.randomUUID().toString()));
        logger.info("发送平台订单转系统订单MQ消息成功");
    }

}
