package com.brandslink.cloud.logistics.service.impl;

import com.brandslink.cloud.common.service.impl.BaseServiceImpl;
import com.brandslink.cloud.logistics.model.MethodCollectorRelationModel;
import com.brandslink.cloud.logistics.mapper.MethodCollectorRelationMapper;
import com.brandslink.cloud.logistics.service.IMethodCollectorRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MethodCollectorRelationServiceImpl extends BaseServiceImpl<MethodCollectorRelationModel> implements IMethodCollectorRelationService {

    @Autowired
    private MethodCollectorRelationMapper relationMapper;
    @Override
    public List<MethodCollectorRelationModel> selectMethodByMethodCollectorRelation(MethodCollectorRelationModel relationModel) {
        List<MethodCollectorRelationModel> list = relationMapper.selectMethodByMethodCollectorRelation(relationModel);
        return list;
    }
}
