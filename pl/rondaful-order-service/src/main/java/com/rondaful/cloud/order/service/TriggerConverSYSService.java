package com.rondaful.cloud.order.service;

import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.aliexpress.AliexpressOrderChild;
import com.rondaful.cloud.order.entity.orderRule.SKUMapMailRuleDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDTO;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface TriggerConverSYSService {

    /**
     * 触发SKU映射和邮寄规则匹配
     * @param list
     * @throws ParseException
     */
    void triggerSKUMapAndMailRuleMate(List<SKUMapMailRuleDTO> list) throws ParseException;

    /**
     * 根据条件查询待转化的所有平台订单
     * @param list
     * @return
     * @throws ParseException
     */
    Map<String, Object> queryPendingConverPlatformOrderList(List<SKUMapMailRuleDTO> list) throws ParseException;

    /**
     * 平台转单
     * @param pendingConverMap
     */
    void converPlatformOrderList(Map<String, Object> pendingConverMap);

    /**
     * 将待转ebay订单集合转系统订单
     * @param ebayPendingList
     */
    void ebayConverSYS(List<SysOrderDTO> ebayPendingList);


    /***
     * 异步转化速卖通订单集合
     * @param list
     * @param unList
     */
    void asyncUpdateSku(List<AliexpressOrderChild> list, List<String> unList);
}
