package com.rondaful.cloud.order.service;

import com.rondaful.cloud.order.entity.system.SysOrderNew;

public interface IreplenishService {

    /**
     * 订单补发货:目前售后补发货调用
     * @param sysOrder
     * @return
     */
    String replenishDeliverGood(SysOrderNew sysOrder) throws Exception;

}
