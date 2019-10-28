package com.rondaful.cloud.order.service;

import com.rondaful.cloud.common.service.BaseService;
import com.rondaful.cloud.order.entity.SysOrderLog;

import java.util.List;
import java.util.Map;

public interface ISysOrderLogService extends BaseService<SysOrderLog> {
    /*
    * 通过品连ID查询订单操作日志
    * */
    List<SysOrderLog> selectSysOrderLogByOrderId(String sysOrderId);

    /**
     * 批量插入日志
     * @param list
     * @return
     */
    Integer inserts(List<SysOrderLog> list);

    /**
     * 根据订单ID查询订单进度
     * @param orderId
     * @return
     */
    Map<String, Object> queryOrderSchedule(String orderId);

    /**
     * 查询是否有重复插入的日志
     * @param sysOrderId 系统订单号
     * @param content 内容
     * @return {@link SysOrderLog}
     */
    SysOrderLog findSysOrderLogByMessage(String sysOrderId, String content);
}
