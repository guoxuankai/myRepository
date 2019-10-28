package com.rondaful.cloud.order.mapper.aliexpress;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.aliexpress.AliexpressOrderMoney;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AliexpressOrderMoneyMapper extends BaseMapper<AliexpressOrderMoney> {


    /**
     * 根据订单号获取金钱相关
     * @param orderId
     * @return
     */
    AliexpressOrderMoney getByOrderId(@Param("orderId") String orderId);

    /**
     * 批量插入
     * @param list
     * @return
     */
    Integer inserts(List<AliexpressOrderMoney> list);
}