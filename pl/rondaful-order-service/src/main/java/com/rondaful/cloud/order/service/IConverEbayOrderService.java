package com.rondaful.cloud.order.service;

import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.eBay.EbayOrder;
import org.quartz.SchedulerException;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface IConverEbayOrderService {

    /**
     * ebay订单转换任务,定时触发的动作交给rondaful-task-scheduler服务
     * @return String
     * @throws Exception Exception
     */
    String convertEbayToSysOrder() throws Exception;

    /**
     * 自动转化ebay订单
     * @throws ParseException
     * @throws SchedulerException
     */
    void autoConverEBayToSys() throws Exception;

    /**
     * 暂停转化ebay订单
     */
    void pauseAutoConverEbay() throws SchedulerException;

    /**
     * 手工转eBay订单到系统订单
     * @param ebayOrders
     * @throws Exception
     */
    String manualConverEbay(List<EbayOrder> ebayOrders) throws Exception;

    /**
     * 处理转化前的ebay订单集合
     * @param list
     */
    void dealPendingConverEbayList(List<SysOrder> list) throws ParseException;

    /**
     * 暂停所有定时任务JOB
     */
    void pauseAllJob() throws SchedulerException;

    /**
     * 删除某个定时任务
     * @param jobName
     * @param jobGroup
     */
    void deleteJob(String jobName, String jobGroup) throws SchedulerException;

    /**
     * 将SKU映射和邮寄规则匹配后结果分类
     * @param afterMapSysList
     * @param isAutoConver
     * @return
     */
    Map<String, Object> splitResultData(List<SysOrder> afterMapSysList, String isAutoConver);

    /**
     * 转单结果持久化核心方法
     * @param map
     * @param isAutoConver
     */
    void transactionPersistData(Map<String, Object> map, String isAutoConver);

    /**
     * 处理转换失败的ebay订单
     */
    void dealConvertFailEbayOrder();
}
