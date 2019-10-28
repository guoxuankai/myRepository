package com.rondaful.cloud.order.service;

import com.rondaful.cloud.common.service.BaseService;
import com.rondaful.cloud.order.entity.system.SysOrderNew;

public interface ISysOrderUpdateService extends BaseService<SysOrderNew> {

    /**
     * 设置订单商品信息
     *
     * @param sysOrderNew
     * @param sysOrder
     */
    void setSysOrderItemInfo(SysOrderNew sysOrderNew, SysOrderNew sysOrder);

    /**
     *  设置包裹详情信息
     * @param loginName
     * @param sysOrderNew
     */
    void setOrderPackageDetailInfo(String loginName, SysOrderNew sysOrderNew);

    /**
     * 更新订单信息（包裹、包裹详情、订单详情等）
     * @param orderNew
     */
    void updateInfo(SysOrderNew orderNew);
}
