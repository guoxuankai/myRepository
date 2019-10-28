package com.brandslink.cloud.logistics.service;

import com.brandslink.cloud.common.service.BaseService;
import com.brandslink.cloud.logistics.model.LogisticsMethodAddressModel;

public interface ILogisticsMethodAddressService extends BaseService<LogisticsMethodAddressModel> {

    Long editMethodAddress(LogisticsMethodAddressModel addressModel);
}
