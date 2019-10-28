package com.brandslink.cloud.logistics.service.impl;

import com.brandslink.cloud.common.service.impl.BaseServiceImpl;
import com.brandslink.cloud.logistics.mapper.LogisticsCargoPropMapper;
import com.brandslink.cloud.logistics.model.LogisticsCargoPropModel;
import com.brandslink.cloud.logistics.service.ILogisticsCargoPropService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogisticsCargoPropServiceImpl extends BaseServiceImpl<LogisticsCargoPropModel> implements ILogisticsCargoPropService {
    @Autowired
    private LogisticsCargoPropMapper cargoPropMapper;
    @Override
    public Long editCargoProp(LogisticsCargoPropModel cargoPropModel) {
        cargoPropMapper.insertUpdateSelective(cargoPropModel);
        return cargoPropModel.getId();
    }
}
