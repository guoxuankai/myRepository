package com.rondaful.cloud.order.service.impl;

import com.rondaful.cloud.common.enums.MessageEnum;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.entity.SysOrderLog;
import com.rondaful.cloud.order.entity.system.SysOrderNew;
import com.rondaful.cloud.order.entity.system.SysOrderPackage;
import com.rondaful.cloud.order.entity.system.SysOrderPackageDetail;
import com.rondaful.cloud.order.enums.OrderDeliveryStatusNewEnum;
import com.rondaful.cloud.order.enums.OrderHandleLogEnum;
import com.rondaful.cloud.order.enums.OrderPackageHandleEnum;
import com.rondaful.cloud.order.mapper.SysOrderNewMapper;
import com.rondaful.cloud.order.mapper.SysOrderPackageMapper;
import com.rondaful.cloud.order.rabbitmq.OrderMessageSender;
import com.rondaful.cloud.order.remote.RemoteSupplierService;
import com.rondaful.cloud.order.service.ISysOrderExceptionHandelService;
import com.rondaful.cloud.order.service.ISysOrderLogService;
import com.rondaful.cloud.order.service.ISysOrderService;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 订单异常处理(废弃单)
 */
@Service
public class SysOrderExceptionHandelImpl implements ISysOrderExceptionHandelService {
    @Autowired
    private ISysOrderService sysOrderService;

    @Autowired
    private SysOrderPackageMapper sysOrderPackageMapper;

    @Autowired
    private SysOrderNewMapper sysOrderNewMapper;

    @Autowired
    private ISysOrderLogService sysOrderLogService;

    @Autowired
    private OrderMessageSender orderMessageSender;

    @Autowired
    private RemoteSupplierService remoteSupplierService;

    private static Logger _log = LoggerFactory.getLogger(GoodCangServiceImpl.class);

    @Override
    public void cancellationOrderHandel(String orderTrackId) {
        _log.error("进入订单作废处理：{}",orderTrackId);
        //根据包裹号获取订单ID
        SysOrderPackage sysOrderPackage = sysOrderPackageMapper.queryOrderPackageByPk(orderTrackId);
        if (sysOrderPackage == null){
            return;
        }
        String orderId = sysOrderPackage.getSysOrderId();
        if (sysOrderPackage.getOperateStatus().equals(OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue())){//合并的包裹
            orderId = sysOrderPackage.getOperateSysOrderId();
        }
        String sysOrderId = orderId.split(Constants.SplitSymbol.HASH_TAG)[0];

        //调用拦截接口
        try {
            String msg = this.interceptSystemOrder(sysOrderId, orderTrackId);
            _log.error("{}，作废包裹拦截结果：{}",orderTrackId, msg);
        } catch (Exception e) {
            _log.error("{}作废包裹拦截失败：{}", orderTrackId, e);
        }
    }

    /**
     * description: 拦截订单：先查询系统订单状态是不是配货中状态，如果是可以拦截，不是的话提示尚未配货或已经发货,拦截失败
     * @Param: sysOrderId  订单ID
     * @Param: orderTrackId  有异常的包裹号
     * @return java.lang.String   拦截的结果
     * create by lijiantao
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public String interceptSystemOrder(String sysOrderId, String orderTrackId) {
        try {
            //TODO 订单发货状态:1待发货,2缺货,3配货中,4已拦截,5已发货,6部分发货,7已作废,8已完成
            SysOrderNew orderNew = sysOrderService.queryOrderByOther(sysOrderId);
            Byte orderStatus = orderNew.getOrderDeliveryStatus();
            if (orderStatus == OrderDeliveryStatusNewEnum.WAIT_SHIP.getValue()) {
                if (orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.GENERAL.getValue()) ||
                        orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue())) {
                    //拦截单包裹订单：根据仓库类型来拦截订单-取消冻结-修改订单状态
                    cancelMoneyAndUpdateOrderStatusAfterIntercept(sysOrderId, orderNew);
                } else if (orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.SPLIT.getValue())) {
                    //拦截多包裹订单： 标记为异常单
                    sysOrderNewMapper.updateToExceptionOrder(sysOrderId);
                    sysOrderPackageMapper.updateException(orderTrackId);
                } else {
                    _log.error("错误的订单类型,订单ID：{}", sysOrderId);
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "错误的订单类型，拦截失败！");
                }
            } else {
                throwOtherException(orderStatus);
            }
        } catch (GlobalException e) {
            if (e.toString().contains("包裹拦截成功，订单取消冻结失败")) {
                return Constants.InterceptResponse.RESPONSE_1;
            } else {
                throw e;
            }
        }
        return Constants.Intercept.INTERCEPT_SUCCESS;
    }

    public void throwOtherException(Byte orderStatus) {
        if (orderStatus == OrderDeliveryStatusNewEnum.INTERCEPTED.getValue()) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "该订单是被拦截状态，无需再次拦截！");
        } else {
            if (orderStatus < OrderDeliveryStatusNewEnum.WAIT_SHIP.getValue()) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "该订单尚未配货，拦截失败！");
            } else if (orderStatus == OrderDeliveryStatusNewEnum.CANCELLED.getValue()) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "该订单已作废，拦截失败！");
            } else if (orderStatus == OrderDeliveryStatusNewEnum.COMPLETED.getValue()) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "该订单已完成，拦截失败！");
            } else {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "该订单已经发货，拦截失败！");
            }
        }
    }

    /**
     * 取消财务冻结并更新订单状态
     * @param sysOrderId
     * @param orderNew
     */
    public void cancelMoneyAndUpdateOrderStatusAfterIntercept(String sysOrderId, SysOrderNew orderNew) {
        //取消冻结，取消失败改为异常订单
        this.cancelMoneyAfterIntercept(sysOrderId, orderNew);

        //拦截成功且取消冻结成功则更改为已拦截状态
        this.updateOrderInterceptStatus(orderNew);

        //根据订单类型更改包裹跟踪号，包裹详情号
        sysOrderService.updateOrderTrackId(sysOrderId, orderNew);

        //插入日志及发送消息
        this.insertLogAndSendMessage(sysOrderId, orderNew);

        //推送发货数量到仓库
        this.updateLocalShipping(orderNew.getSysOrderPackageList());

    }

    /**
     * 推送发货数量到仓库
     * @param sysOrderPackageList
     */
    public void updateLocalShipping(List<SysOrderPackage> sysOrderPackageList){
        for (SysOrderPackage sysOrderPackage : sysOrderPackageList){
            for (SysOrderPackageDetail item : sysOrderPackage.getSysOrderPackageDetailList()) {
                String sku = item.getSku();
                Integer skuQuantity = item.getSkuQuantity();
                _log.info("推送发货数量到仓库，请求参数：仓库id:{},sku:{},sku数量：{}", sysOrderPackage.getDeliveryWarehouseId(), sku, skuQuantity * (-1));
                String result = remoteSupplierService.updateLocalShipping(sysOrderPackage.getDeliveryWarehouseId(), sku, skuQuantity * (-1));//把正数变为负数
                _log.info("推送发货数量到仓库，返回：{}", result);
            }
        }

    }

    /**
     * 拦截插入日志及发送消息
     * @param sysOrderId
     * @param orderNew
     */
    public void insertLogAndSendMessage(String sysOrderId, SysOrderNew orderNew){
        String operateSysOrderId = orderNew.getSysOrderPackageList().get(0).getOperateSysOrderId();
        if (StringUtils.isNotBlank(operateSysOrderId)) {  //合并订单
            List<String> list = Arrays.asList(operateSysOrderId.split("\\#"));
            for (String orderId : list) {
                sysOrderLogService.insertSelective(new SysOrderLog(orderId, OrderHandleLogEnum.Content.ORDER_INTERCEPT.orderIntercept(orderId),
                        OrderHandleLogEnum.OrderStatus.STATUS_4.getMsg(), "系统"));

                try {
                    orderMessageSender.sendOrderStockOut(orderNew.getSellerPlAccount(), orderId,
                            MessageEnum.ORDER_INTERCEPT_NOTICE, null);
                } catch (JSONException e) {
                    _log.error("拦截订单发送消息失败,订单ID：{}", orderId);
                }
            }
        } else { //普通或者拆包订单
            sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.ORDER_INTERCEPT.orderIntercept(sysOrderId),
                    OrderHandleLogEnum.OrderStatus.STATUS_4.getMsg(), "系统"));

            try {
                orderMessageSender.sendOrderStockOut(orderNew.getSellerPlAccount(), sysOrderId,
                        MessageEnum.ORDER_INTERCEPT_NOTICE, null);
            } catch (JSONException e) {
                _log.error("拦截订单发送消息失败,订单ID：{}", sysOrderId);
            }
        }
    }

    /**
     * 更改订单状态为已拦截
     * @param orderNew
     */
    public void updateOrderInterceptStatus(SysOrderNew orderNew) {
        String operateSysOrderId = orderNew.getSysOrderPackageList().get(0).getOperateSysOrderId();
        if (StringUtils.isNotBlank(operateSysOrderId)) {//合并订单
            List<String> list = Arrays.asList(operateSysOrderId.split("\\#"));
            for (String id : list) {
                orderNew.setSysOrderId(id);
                sysOrderService.reactivationOrder(orderNew);
                sysOrderNewMapper.resetOrderErrorStatus(id);//重置订单异常标记
            }
        } else {//普通订单或者拆分订单
            sysOrderService.reactivationOrder(orderNew);
            sysOrderNewMapper.resetOrderErrorStatus(orderNew.getSysOrderId());//重置订单异常标记
        }
    }

    /**
     * 取消财务冻结
     * @param sysOrderId
     * @param orderNew
     */
    public void cancelMoneyAfterIntercept(String sysOrderId, SysOrderNew orderNew) {
        if (orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue())) {
            for (String orderId : Arrays.asList(orderNew.getSysOrderPackageList().get(0).getOperateSysOrderId().split("\\#"))) {
                cancelMoney(orderNew, orderId);
            }
        } else {
            cancelMoney(orderNew, sysOrderId);
        }
    }

    /**
     * 取消财务冻结
     * @param orderId
     * @param orderNew
     */
    public void cancelMoney(SysOrderNew orderNew, String orderId) {
        try {
            sysOrderService.cancelMoney(orderId);
        } catch (Exception e) {
            _log.error("订单取消冻结失败！订单ID：{}，异常原因：{}", orderNew.getSysOrderId(), e);
            updateErrorOrderInfo(orderNew);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "订单取消冻结失败");
        }
    }

    /**
     * 【包裹拦截成功，订单取消冻结失败】的处理
     * @param orderNew
     */
    public void updateErrorOrderInfo(SysOrderNew orderNew) {
        if (orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.SPLIT.getValue()) |
                orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.GENERAL.getValue())) {
            sysOrderNewMapper.updateToExceptionOrder(orderNew.getSysOrderId()); //变成异常订单
            for (SysOrderPackage orderPackage : orderNew.getSysOrderPackageList()) {
                //所有包裹添加异常信息：包裹拦截成功，订单取消冻结失败，请联系客服进行人工处理
                sysOrderPackageMapper.updatePackageErrorInfo(orderPackage.getOrderTrackId());
            }
        } else {   //合包的订单
            List<String> list = Arrays.asList(orderNew.getSysOrderPackageList().get(0).getOperateSysOrderId().split("\\#"));
            list.forEach(id -> sysOrderNewMapper.updateToExceptionOrder(id));
            String orderTrackId = orderNew.getSysOrderPackageList().get(0).getOrderTrackId();
            //所有包裹添加异常信息：包裹拦截成功，订单取消冻结失败，请联系客服进行人工处理
            sysOrderPackageMapper.updatePackageErrorInfo(orderTrackId);
        }
    }


}

