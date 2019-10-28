package com.rondaful.cloud.order.service;

public interface ISysOrderExceptionHandelService {

    /**
     * 订单作废处理
     * @param orderTrackId
     */
    void cancellationOrderHandel(String orderTrackId);
}
