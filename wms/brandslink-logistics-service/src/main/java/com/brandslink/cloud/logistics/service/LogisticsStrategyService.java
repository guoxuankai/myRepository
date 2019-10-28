package com.brandslink.cloud.logistics.service;

import com.brandslink.cloud.logistics.entity.centre.*;

/**
 * 物流商策略接口
 *
 * @author guoxuankai
 * @date 2019/7/31 15:58
 */
public interface LogisticsStrategyService {

    /**
     * 下单
     *
     * @param baseOrder
     * @return
     */
    PlaceOrderResult createOrder(BaseOrder baseOrder) throws Exception;

    /**
     * 打印标签
     * @param baseLabel
     * @return
     */
    PrintLabelResult printLabel(BaseLabel baseLabel) throws Exception;


    /**
     * 获取跟踪号
     * @param baseTrackingNumber
     * @return
     */
    TrackingNumberResult getTrackingNumber(BaseTrackingNumber baseTrackingNumber) throws Exception;


}
