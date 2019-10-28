package com.brandslink.cloud.logistics.service.impl;

import com.brandslink.cloud.common.service.impl.BaseServiceImpl;
import com.brandslink.cloud.logistics.enums.CommonEnum;
import com.brandslink.cloud.logistics.mapper.LogisticsMethodMapper;
import com.brandslink.cloud.logistics.mapper.LogisticsProviderMapper;
import com.brandslink.cloud.logistics.model.LogisticsMethodModel;
import com.brandslink.cloud.logistics.model.LogisticsProviderModel;
import com.brandslink.cloud.logistics.service.ILogisticsProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogisticsProviderServiceImpl extends BaseServiceImpl<LogisticsProviderModel> implements ILogisticsProviderService {
    @Autowired
    private LogisticsProviderMapper providerMapper;
    @Autowired
    private LogisticsMethodMapper methodMapper;
    @Override
    public Long editLogisticsProvider(LogisticsProviderModel providerModel) {
        Long providerId = providerModel.getId();
        providerMapper.insertUpdateSelective(providerModel);
        Byte isValid = providerModel.getIsValid();
        //更新物流商下所有邮寄方式状态
        if (providerId != null && isValid.intValue() == CommonEnum.EnabledDisable.Disable.getCode()){
            LogisticsMethodModel methodModel = new LogisticsMethodModel();
            methodModel.setProviderId(providerId);
            methodModel.setIsValid(isValid);
            methodModel.setUpdateBy(providerModel.getUpdateBy());
            methodMapper.updateMethodInfoByProviderID(methodModel);
        }
        return providerModel.getId();
    }

    @Override
    public LogisticsProviderModel getByLogisticsCode(String logisticsCode) {
        return providerMapper.getByLogisticsCode(logisticsCode);
    }

}
