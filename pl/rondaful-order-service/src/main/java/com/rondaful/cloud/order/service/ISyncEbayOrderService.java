package com.rondaful.cloud.order.service;

import com.rondaful.cloud.common.service.BaseService;
import com.rondaful.cloud.order.entity.eBay.EbayOrder;
import org.quartz.SchedulerException;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface ISyncEbayOrderService extends BaseService<EbayOrder> {

    /**
     * 同步ebay订单
     * 现在定时触发的动作交给 rondaful-task-scheduler 服务
     * @throws Exception
     */
    String syncEbayOrders() throws Exception;

    /**
     * 自动同步ebay订单
     * @throws SchedulerException
     * @throws ParseException
     */
    void autoSyncEBayOrders() throws Exception;

    /**
     * 暂停同步ebay订单
     */
    void pauseAutoSyncEbay() throws SchedulerException;

    /**
     * 手工同步eBay订单
     * @param modTimeFrom  开始时间点
     * @param modTimeTo  结束时间点
     * @throws Exception
     */
    void manualSycEbay(String modTimeFrom, String modTimeTo);

    /**
     * 根据条件查询Bay平台订单列表
     * @param map
     * @return
     */
    List<EbayOrder> queryEbayOrderList(Map<String, Object> map);

    /**
     * 根据平台订单号orderId查询订单详情
     * @param orderId
     * @return
     */
    EbayOrder queryEbayOrderDetail(String orderId);

    void testSyncEbayOrder();
}
