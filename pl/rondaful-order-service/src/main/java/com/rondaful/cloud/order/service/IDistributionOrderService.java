package com.rondaful.cloud.order.service;

import com.codingapi.tx.annotation.TxTransaction;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.system.SysOrderNew;
import com.rondaful.cloud.order.model.xingShang.response.SysOrderPackageXS;
import com.rondaful.cloud.order.model.xingShang.response.SysOrderXS;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author: zhangjinglei
 * @description: 星商订单相关接口
 * @date: 2019/5/4
 */
public interface IDistributionOrderService {

    /**
     * 创建星商订单并发货
     *
     * @param sysOrder
     * @return
     */
    Map<String, String> createSysOrderForXSNew(HttpServletRequest request, SysOrder sysOrder) throws Exception;

    /**
     * 分销商通过品连订单号查询系统订单
     *
     * @param sysOrderId
     * @return
     */
    SysOrderXS queryDistributionSysOrderByID(HttpServletRequest request, Integer platformShopId,
                                             String sysOrderId, String sourceOrderId);

    /**
     * 分销商 作废订单
     *
     * @param sysOrderId
     * @return
     */
    String cancelDistributionOrder(HttpServletRequest request, Integer platformShopId,
                                 String sysOrderId, String sourceOrderId, String cancelReason) throws Exception;

    @Transactional(rollbackFor = Exception.class)
    @TxTransaction(isStart = true)
    void unfreezeAndUpdateOrderStatus(String sysOrderId, SysOrderNew orderNew, String msg);

    @Transactional(rollbackFor = Exception.class)
    void updateOrderCancelStatus(String sysOrderId, SysOrderNew orderNew);

    void unfreezeMoney(String sysOrderId, SysOrderNew orderNew, String msg);

    @TxTransaction(isStart = true)
    @Transactional(rollbackFor = Exception.class)
    void cancelMoney(String orderId);

    /**
     * 分销商 查询订单包裹信息
     *
     * @param request
     * @param platformShopAccount
     * @param sysOrderId
     * @param sourceOrderId
     * @return
     */
    List<SysOrderPackageXS> queryDistributionSysOrderPackageByID(HttpServletRequest request, Integer platformShopId,
                                                                 String sysOrderId, String sourceOrderId);
}
