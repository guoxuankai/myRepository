package com.brandslink.cloud.logistics.service;

import com.brandslink.cloud.common.service.BaseService;
import com.brandslink.cloud.logistics.model.LogisticsProviderModel;

public interface ILogisticsProviderService extends BaseService<LogisticsProviderModel> {

    Long editLogisticsProvider(LogisticsProviderModel providerModel);

    LogisticsProviderModel getByLogisticsCode(String logisticsCode);

}
