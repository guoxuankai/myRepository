package com.rondaful.cloud.order.utils;

import com.rondaful.cloud.order.enums.OrderDeliveryStatusNewEnum;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SysOrderUtils {
    public static Map<String,Byte> StatusMap = new HashMap<>();
    public static Map<Byte,String> ValueMap = new HashMap<>();
    static {
        PutDataToMap();
    }

    /**
     * 根据状态值获取状态名的MAP
     * @return
     */
    public static Map<String, Byte> getOrderStatusMap() {
        if (MapUtils.isNotEmpty(StatusMap)) {
            return StatusMap;
        }else{
            PutDataToMap();
            return StatusMap;
        }
    }

    /**
     * 根据状态名获取状态值的MAP
     * @return
     */
    public static Map<Byte, String> getOrderStatusValueMap() {
        if (MapUtils.isNotEmpty(ValueMap)) {
            return ValueMap;
        }else{
            PutDataValueToMap();
            return ValueMap;
        }
    }

    public static void PutDataToMap() {
        StatusMap.put(OrderDeliveryStatusNewEnum.WAIT_PAY.getMsg(), OrderDeliveryStatusNewEnum.WAIT_PAY.getValue());
        StatusMap.put(OrderDeliveryStatusNewEnum.STOCKOUT.getMsg(), OrderDeliveryStatusNewEnum.STOCKOUT.getValue());
        StatusMap.put(OrderDeliveryStatusNewEnum.WAIT_SHIP.getMsg(), OrderDeliveryStatusNewEnum.WAIT_SHIP.getValue());
        StatusMap.put(OrderDeliveryStatusNewEnum.INTERCEPTED.getMsg(), OrderDeliveryStatusNewEnum.INTERCEPTED.getValue());
        StatusMap.put(OrderDeliveryStatusNewEnum.DELIVERED.getMsg(), OrderDeliveryStatusNewEnum.DELIVERED.getValue());
        StatusMap.put(OrderDeliveryStatusNewEnum.PARTIALLYSHIPPED.getMsg(), OrderDeliveryStatusNewEnum.PARTIALLYSHIPPED.getValue());
        StatusMap.put(OrderDeliveryStatusNewEnum.CANCELLED.getMsg(), OrderDeliveryStatusNewEnum.CANCELLED.getValue());
        StatusMap.put(OrderDeliveryStatusNewEnum.COMPLETED.getMsg(), OrderDeliveryStatusNewEnum.COMPLETED.getValue());
    }

    public static void PutDataValueToMap() {
        ValueMap.put(OrderDeliveryStatusNewEnum.WAIT_PAY.getValue(), OrderDeliveryStatusNewEnum.WAIT_PAY.getMsg());
        ValueMap.put(OrderDeliveryStatusNewEnum.STOCKOUT.getValue(), OrderDeliveryStatusNewEnum.STOCKOUT.getMsg());
        ValueMap.put(OrderDeliveryStatusNewEnum.WAIT_SHIP.getValue(), OrderDeliveryStatusNewEnum.WAIT_SHIP.getMsg());
        ValueMap.put(OrderDeliveryStatusNewEnum.INTERCEPTED.getValue(), OrderDeliveryStatusNewEnum.INTERCEPTED.getMsg());
        ValueMap.put(OrderDeliveryStatusNewEnum.DELIVERED.getValue(), OrderDeliveryStatusNewEnum.DELIVERED.getMsg());
        ValueMap.put(OrderDeliveryStatusNewEnum.PARTIALLYSHIPPED.getValue(), OrderDeliveryStatusNewEnum.PARTIALLYSHIPPED.getMsg());
        ValueMap.put(OrderDeliveryStatusNewEnum.CANCELLED.getValue(), OrderDeliveryStatusNewEnum.CANCELLED.getMsg());
        ValueMap.put(OrderDeliveryStatusNewEnum.COMPLETED.getValue(), OrderDeliveryStatusNewEnum.COMPLETED.getMsg());
    }
}
