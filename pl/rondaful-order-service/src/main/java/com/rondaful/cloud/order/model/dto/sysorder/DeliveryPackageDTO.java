package com.rondaful.cloud.order.model.dto.sysorder;

import com.rondaful.cloud.order.entity.system.SysOrderNew;
import com.rondaful.cloud.order.entity.system.SysOrderPackage;

import java.io.Serializable;
import java.util.List;

/**
 * 包裹发货的dto
 *
 * @author Blade
 * @date 2019-07-25 16:35:18
 **/
public class DeliveryPackageDTO implements Serializable {
    private static final long serialVersionUID = -3736408524507098221L;

    /**
     * 如果是 合并 包裹订单，则该值为合并包裹
     * 如果是 普通订单 和 拆分包裹订单， 则该值为第一条记录
     */
    private SysOrderPackage sysOrderPackage;

    private List<SysOrderNew> sysOrderNewList;

    // 是否合并包裹的订单
    private boolean mergedPackageOrder;

    public SysOrderPackage getSysOrderPackage() {
        return sysOrderPackage;
    }

    public void setSysOrderPackage(SysOrderPackage sysOrderPackage) {
        this.sysOrderPackage = sysOrderPackage;
    }

    public List<SysOrderNew> getSysOrderNewList() {
        return sysOrderNewList;
    }

    public void setSysOrderNewList(List<SysOrderNew> sysOrderNewList) {
        this.sysOrderNewList = sysOrderNewList;
    }

    public boolean getMergedPackageOrder() {
        return mergedPackageOrder;
    }

    public void setMergedPackageOrder(boolean mergedPackageOrder) {
        this.mergedPackageOrder = mergedPackageOrder;
    }
}
