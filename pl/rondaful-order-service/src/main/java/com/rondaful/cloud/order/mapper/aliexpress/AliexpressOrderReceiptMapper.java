package com.rondaful.cloud.order.mapper.aliexpress;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.aliexpress.AliexpressOrderReceipt;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AliexpressOrderReceiptMapper extends BaseMapper<AliexpressOrderReceipt> {

    /**
     * 根据订单号获取订单物流信息
     * @param orderId
     * @return
     */
    AliexpressOrderReceipt getByOrderId(@Param("orderId") String orderId);

    /**
     * 批量插入
     * @param list
     * @return
     */
    Integer inserts(@Param("list") List<AliexpressOrderReceipt> list);
}