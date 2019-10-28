package com.brandslink.cloud.logistics.service;

import com.brandslink.cloud.common.service.BaseService;
import com.brandslink.cloud.logistics.model.LogisticsCargoPropModel;

public interface ILogisticsCargoPropService extends BaseService<LogisticsCargoPropModel> {

    Long editCargoProp(LogisticsCargoPropModel cargoPropModel);
}
