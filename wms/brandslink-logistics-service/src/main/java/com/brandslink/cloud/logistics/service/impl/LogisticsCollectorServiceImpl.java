package com.brandslink.cloud.logistics.service.impl;

import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.service.impl.BaseServiceImpl;
import com.brandslink.cloud.logistics.entity.centre.CollectorVo;
import com.brandslink.cloud.logistics.mapper.LogisticsCollectorMapper;
import com.brandslink.cloud.logistics.mapper.MethodCollectorRelationMapper;
import com.brandslink.cloud.logistics.model.CollectorMethodModel;
import com.brandslink.cloud.logistics.model.LogisticsCollectorModel;
import com.brandslink.cloud.logistics.model.MethodCollectorRelationModel;
import com.brandslink.cloud.logistics.service.ILogisticsCollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogisticsCollectorServiceImpl extends BaseServiceImpl<LogisticsCollectorModel> implements ILogisticsCollectorService {
    @Autowired
    private LogisticsCollectorMapper collectorMapper;
    @Autowired
    private MethodCollectorRelationMapper relationMapper;

    @Override
    public Long editCollector(LogisticsCollectorModel collectorModel) {
        if (collectorModel.getId() != null){
            //更新邮寄方式仓库揽收商关系表冗余字段
            MethodCollectorRelationModel relation = new MethodCollectorRelationModel();
            relation.setCollectorId(collectorModel.getId());
            relation.setCollectorCode(collectorModel.getCollectorCode());
            relation.setCollectorName(collectorModel.getCollectorName());
            relationMapper.updateCollectorInfoBycollectorID(relation);
        }
        collectorMapper.insertUpdateSelective(collectorModel);
        return collectorModel.getId();
    }

    @Override
    public Page<CollectorMethodModel> selectMethodListByCollectorId(Long collectorId) {
        List<CollectorMethodModel> list = collectorMapper.selectMethodListByCollectorId(collectorId);
        return new Page<>(list);
    }

    @Override
    public CollectorVo getByCode(String logisticsCode, String methodCode,String warehouseCode) {
        CollectorVo byCode = collectorMapper.getByCode(logisticsCode, methodCode,warehouseCode);
        return byCode;
    }

    @Override
    public Page<LogisticsCollectorModel> selectCollector(LogisticsCollectorModel collectorModel) {
        List<LogisticsCollectorModel> list = collectorMapper.page(collectorModel);
        return new Page<>(list);
    }
}
