package com.brandslink.cloud.logistics.service;

import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.service.BaseService;
import com.brandslink.cloud.logistics.model.LogisticsMethodModel;

public interface ILogisticsMethodService extends BaseService<LogisticsMethodModel> {

    Long editMethod(LogisticsMethodModel methodModel);

    LogisticsMethodModel selectMethodBasicInfoByID(Long methodId);

    Page<LogisticsMethodModel> selectMethod(LogisticsMethodModel methodModel);

    void enableDisableMethod(Long methodId, Byte isValid);
}
