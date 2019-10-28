package com.rondaful.cloud.order.service;

/**
 * @Author: xqq
 * @Date: 2019/8/2
 * @Description:
 */
public interface IDeliverCallBackService {

    /**
     * 订单发货变更通知
     * @param orderTrackId
     */
    void sendDelivery(String orderTrackId);
}
