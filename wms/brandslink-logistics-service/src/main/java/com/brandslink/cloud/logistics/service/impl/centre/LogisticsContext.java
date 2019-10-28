package com.brandslink.cloud.logistics.service.impl.centre;

import com.brandslink.cloud.logistics.entity.centre.*;
import com.brandslink.cloud.logistics.service.LogisticsStrategyService;

/**
 * 物流商策略上下文
 * @author guoxuankai
 * @date 2019/7/31 16:24
 */
public class LogisticsContext {

    private LogisticsStrategyService strategy;

    public LogisticsContext(LogisticsStrategyService strategy) {
        this.strategy = strategy;
    }

    public PlaceOrderResult createOrder(BaseOrder baseOrder) throws Exception {

        return strategy.createOrder(baseOrder);
    }

    public PrintLabelResult printLabel(BaseLabel baseLabel) throws Exception {

        return strategy.printLabel(baseLabel);
    }

    public TrackingNumberResult getTrackingNumber(BaseTrackingNumber baseTrackingNumber) throws Exception {

        return strategy.getTrackingNumber(baseTrackingNumber);
    }


}
