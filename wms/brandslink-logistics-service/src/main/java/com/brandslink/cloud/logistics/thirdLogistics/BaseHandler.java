package com.brandslink.cloud.logistics.thirdLogistics;

import com.brandslink.cloud.logistics.entity.LogisticsDeliverCallBack;
import com.brandslink.cloud.logistics.entity.centre.BaseOrder;

public interface BaseHandler {

    LogisticsDeliverCallBack deliverSingle(BaseOrder baseOrder) throws Exception;
}
