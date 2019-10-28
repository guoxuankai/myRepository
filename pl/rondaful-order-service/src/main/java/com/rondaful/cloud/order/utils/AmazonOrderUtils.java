package com.rondaful.cloud.order.utils;

import com.rondaful.cloud.order.enums.AmazonEnum;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
@Component
public class AmazonOrderUtils {
    public static Map<String,String> map = new HashMap<>();
    static {
        PutDataToMap();
    }
    public static Map<String, String> getOrderStatusMap() {
        if (MapUtils.isNotEmpty(map)) {
            return map;
        }else{
            PutDataToMap();
            return map;
        }
    }

    public static void PutDataToMap() {
        map.put(AmazonEnum.OrderStatus.PENDING_AVAILABILITY.getMsg(), AmazonEnum.OrderStatus.PENDING_AVAILABILITY.getValue());
        map.put(AmazonEnum.OrderStatus.PENDING.getMsg(), AmazonEnum.OrderStatus.PENDING.getValue());
        map.put(AmazonEnum.OrderStatus.UNSHIPPED.getMsg(), AmazonEnum.OrderStatus.UNSHIPPED.getValue());
        map.put(AmazonEnum.OrderStatus.PARTIALLY_SHIPPED.getMsg(), AmazonEnum.OrderStatus.PARTIALLY_SHIPPED.getValue());
        map.put(AmazonEnum.OrderStatus.SHIPPED.getMsg(), AmazonEnum.OrderStatus.SHIPPED.getValue());
        map.put(AmazonEnum.OrderStatus.INVOICE_UNCONFIRMED.getMsg(), AmazonEnum.OrderStatus.INVOICE_UNCONFIRMED.getValue());
        map.put(AmazonEnum.OrderStatus.CANCELED.getMsg(), AmazonEnum.OrderStatus.CANCELED.getValue());
        map.put(AmazonEnum.OrderStatus.UNFULFILLABLE.getMsg(), AmazonEnum.OrderStatus.UNFULFILLABLE.getValue());
    }
}
