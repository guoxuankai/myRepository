package com.brandslink.cloud.common.constant;
/**
 * @Description: 库存相关操作状态枚举
 * @author: xd
 * @date:2019年6月18日 上午17:10:30
 */
public class InventoryConstant {

    /**
     * 入库相关
     */
    public static final String ININVENTORY_ARRIVAL_NOTICE = "ARRIVAL_NOTICE";//到货通知

    public static final String ININVENTORY_QC_FINISH = "QC_FINISH";//QC完成

    public static final String ININVENTORY_AFFIRM_PUTAWAY = "AFFIRM_PUTAWAY";//确认上架

    /**
     * 出库相关
     */
    public static final String OUTINVENTORY_ALLOCATION_CARGO = "ALLOCATION_CARGO";//出库配货

    public static final String OUTINVENTORY_PICKING_ORDERS = "PICKING_ORDERS";//生成拣货单

    public static final String OUTINVENTORY_AFFIRM_SOLD_OUT = "AFFIRM_SOLD_OUT";//确认下架

    public static final String OUTINVENTORY_ACCOUNT_BILL = "ACCOUNT_BILL";//结单

    /**
     * 出库取消相关
     */
    public static final String ORDERCANCEL_PACKAGE_FAILURE = "PACKAGE_FAILURE";//原包裹库存分配失败

    public static final String ORDERCANCEL_PACKAGE_NOT_GENERATE = "NOT_GENERATE";//原包裹未生成拣货单

    public static final String ORDERCANCEL_NOT_STATEMENT = "NOT_STATEMENT";//原包裹未结单

    /**
     * 异常相关
     */
    public static final String  QC_EXCEPTION = "QC_EXCEPTION";//质检异常

    public static final String PUTAWAY_EXCEPTION = "PUTAWAY_EXCEPTION";//上架异常

    public static final String ORDER_PICKING_EXCEPTION  ="ORDER_PICKING_EXCEPTION";//拣货异常

    public static final String REVIEW_EXCEPTION = "REVIEW_EXCEPTION";//复核异常

    public static final String IN_WAREHOUSE_EXCEPTION ="IN_WAREHOUSE_EXCEPTION";//库内异常
}
