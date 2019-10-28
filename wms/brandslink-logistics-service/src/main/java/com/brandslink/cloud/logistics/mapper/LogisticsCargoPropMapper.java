package com.brandslink.cloud.logistics.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.logistics.model.LogisticsCargoPropModel;

public interface LogisticsCargoPropMapper extends BaseMapper<LogisticsCargoPropModel> {

    void insertUpdateSelective(LogisticsCargoPropModel cargoPropModel);
}