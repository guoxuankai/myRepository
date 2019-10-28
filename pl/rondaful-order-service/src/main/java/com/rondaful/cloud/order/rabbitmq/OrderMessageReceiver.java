package com.rondaful.cloud.order.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.order.entity.Amazon.AmazonOrder;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.eBay.EbayOrder;
import com.rondaful.cloud.order.enums.OrderSourceEnum;
import com.rondaful.cloud.order.mapper.SysOrderMapper;
import com.rondaful.cloud.order.service.IAmazonOrderService;
import com.rondaful.cloud.order.service.IConverEbayOrderService;
import com.rondaful.cloud.order.service.aliexpress.IAliexpressOrderService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class OrderMessageReceiver {
    @Autowired
    private SysOrderMapper sysOrderMapper;
    @Autowired
    private IConverEbayOrderService converEbayOrderService;
    @Autowired
    private IAmazonOrderService amazonOrderService;
    @Autowired
    IAliexpressOrderService aliexpressOrderService;

    private final static Logger _log = LoggerFactory.getLogger(OrderMessageReceiver.class);

    @RabbitListener(queues = "queue-order-comfirm-callback")
    public void updatePayStatus(String message) {
        _log.error("______________财务修改订单付款状态MQ传递过来的数据______________" + message + "______________");
        try {
            Map data = JSONObject.parseObject(message, Map.class);
            if (data != null) {
                String sysOrderId = (String) data.get("orderNo");
                String payStatus = (String) data.get("status");
                if (StringUtils.isNotBlank(sysOrderId) && StringUtils.isNotBlank(payStatus)) {
                    SysOrder order = sysOrderMapper.selectSysOrderByPrimaryKey(sysOrderId);
                    byte payStatusDB = order.getPayStatus();
                    byte orderDeliveryStatus = order.getOrderDeliveryStatus();
                    if (orderDeliveryStatus == (byte) 1 || orderDeliveryStatus == (byte) 2 || orderDeliveryStatus == (byte) 3 ||
                            orderDeliveryStatus == (byte) 4 || orderDeliveryStatus == (byte) 7) {
                        _log.error("___________订单发货状态为___________" + orderDeliveryStatus + "___________不支持修改付款状态___________");
                        return;
                    }
                    if (payStatusDB == (byte) 0 || payStatusDB == (byte) 10 || payStatusDB == (byte) 11 || payStatusDB == (byte) 21) {
                        _log.error("___________订单付款状态为___________" + payStatusDB + "___________不支持修改付款状态___________");
                        return;
                    }
                    SysOrder sysOrder = new SysOrder();
                    sysOrder.setSysOrderId(sysOrderId);
                    switch (payStatus) {
                        case "付款成功":
                            sysOrder.setPayStatus((byte) 21);
                            break;
                        case "待补款":
                            sysOrder.setPayStatus((byte) 30);
                            break;
                    }
                    sysOrderMapper.updateBySysOrderIdSelective(sysOrder);
                }
            }
        } catch (Exception e) {
            _log.error("___________订单付款MQ更改付款状态异常____________" + e);
        }
    }

    /**
     * 监听转单消息
     *
     * @param message
     */
    @RabbitListener(queues = "queue.base.convert.order")
    public void baseConvertOrder(Message message) {
        String body = new String(message.getBody());
        byte platformType = (byte) message.getMessageProperties().getHeaders().get("platformType");
        if (platformType == OrderSourceEnum.CONVER_FROM_EBAY.getValue()) {
            List<EbayOrder> list = JSONObject.parseArray(body, EbayOrder.class);
            try {
                converEbayOrderService.manualConverEbay(list);
            } catch (Exception e) {
                _log.error("监听消息ebay转单异常:", e);
            }
        }else if(platformType == OrderSourceEnum.CONVER_FROM_AMAZON.getValue()){
            try {
                List<AmazonOrder> list = JSONObject.parseArray(body, AmazonOrder.class);
                amazonOrderService.addTurnToSysOrderAndUpdateStatus(list, true);
            } catch (Exception e) {
                _log.error("监听消息Amazon转单异常:", e);
            }
        }else if(platformType == OrderSourceEnum.CONVER_FROM_ALIEXPRESS.getValue()){
            try {
                List<String> list = JSONObject.parseArray(body, String.class);
                aliexpressOrderService.toSysOrder(list);
            } catch (Exception e) {
                _log.error("监听消息Aliexpress转单异常:", e);
            }
        }

    }

}
