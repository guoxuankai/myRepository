package com.brandslink.cloud.logistics.service.impl;

import com.alibaba.fastjson.JSON;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.service.impl.BaseServiceImpl;
import com.brandslink.cloud.logistics.enums.CommonEnum;
import com.brandslink.cloud.logistics.mapper.LogisticsMethodMapper;
import com.brandslink.cloud.logistics.mapper.LogisticsProviderMapper;
import com.brandslink.cloud.logistics.mapper.MethodCollectorRelationMapper;
import com.brandslink.cloud.logistics.mapper.MethodZoneFreightMapper;
import com.brandslink.cloud.logistics.model.LogisticsMethodModel;
import com.brandslink.cloud.logistics.model.LogisticsProviderModel;
import com.brandslink.cloud.logistics.model.MethodCollectorRelationModel;
import com.brandslink.cloud.logistics.model.MethodZoneFreightModel;
import com.brandslink.cloud.logistics.service.ILogisticsMethodService;
import com.brandslink.cloud.logistics.utils.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LogisticsMethodServiceImpl extends BaseServiceImpl<LogisticsMethodModel> implements ILogisticsMethodService {
    @Autowired
    private LogisticsProviderMapper providerMapper;
    @Autowired
    private LogisticsMethodMapper methodMapper;
    @Autowired
    private MethodCollectorRelationMapper relationMapper;
    @Autowired
    private MethodZoneFreightMapper zoneMapper;
    @Autowired
    private UserUtil userUtil;

    private final static Logger _log = LoggerFactory.getLogger(LogisticsMethodServiceImpl.class);

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long editMethod(LogisticsMethodModel methodModel) {
        Long modelId = methodModel.getId();
        if (modelId != null){
            //更新邮寄方式分区表中冗余字段
            MethodZoneFreightModel zoneModel = new MethodZoneFreightModel();
            zoneModel.setMethodId(methodModel.getId());
            zoneModel.setLogisticsMethodCode(methodModel.getLogisticsMethodCode());
            zoneModel.setLogisticsMethodName(methodModel.getLogisticsMethodName());
            zoneMapper.updateMethodInfoByMethodID(zoneModel);
        }
        methodMapper.insertUpdateSelective(methodModel);
        Byte isValid = methodModel.getIsValid();
        //如果邮寄方式状态为启用，判断物流商状态为禁用时将其启用
        if (isValid.intValue() == CommonEnum.EnabledDisable.Enabled.getCode()){
            LogisticsProviderModel providerModel = providerMapper.selectByPrimaryKey(methodModel.getProviderId());
            if (providerModel.getIsValid() == CommonEnum.EnabledDisable.Disable.getCode()){
                providerModel.setIsValid(isValid);
                providerModel.setUpdateBy(methodModel.getUpdateBy());
                providerMapper.updateByPrimaryKey(providerModel);
            }
        }
        Long methodId = methodModel.getId();
        relationMapper.deleteByMethodID(methodId);
        List<MethodCollectorRelationModel> relationList = methodModel.getRelationList();
        for (MethodCollectorRelationModel dto : relationList) {
            dto.setUpdateBy(userUtil.getUserName());
            dto.setProviderId(methodModel.getProviderId());
            dto.setMethodId(methodId);
        }
        relationMapper.insertBatch(relationList);
        return methodId;
    }

    @Override
    public LogisticsMethodModel selectMethodBasicInfoByID(Long methodId) {
        LogisticsMethodModel method = methodMapper.selectMethodBasicInfoByID(methodId);
        method.setSenderId(method.getSender() == null ? null : method.getSender().getId());
        method.setCollectManId(method.getCollectMan() == null ? null : method.getCollectMan().getId());
        method.setRefunderId(method.getRefunder() == null ? null : method.getRefunder().getId());
        return method;
    }

    @Override
    public Page<LogisticsMethodModel> selectMethod(LogisticsMethodModel method) {
        List<LogisticsMethodModel> list = methodMapper.page(method);
        return new Page<>(list);
    }

    @Override
    public void enableDisableMethod(Long methodId, Byte isValid) {
        LogisticsMethodModel method = new LogisticsMethodModel();
        method.setId(methodId);
        method.setIsValid(isValid);
        method.setUpdateBy(userUtil.getUserName());
        methodMapper.updateByPrimaryKeySelective(method);
        //如果邮寄方式状态为启用，判断物流商状态为禁用时将其启用
        if (isValid.intValue() == CommonEnum.EnabledDisable.Enabled.getCode()){
            LogisticsProviderModel providerModel = providerMapper.selectProviderInfoByMethodID(methodId);
            _log.error("___________{}___________", JSON.toJSONString(providerModel));
            if (providerModel.getIsValid() == CommonEnum.EnabledDisable.Disable.getCode()){
                providerModel.setIsValid(isValid);
                providerModel.setUpdateBy(userUtil.getUserName());
                providerMapper.updateByPrimaryKey(providerModel);
            }
        }
    }
}
