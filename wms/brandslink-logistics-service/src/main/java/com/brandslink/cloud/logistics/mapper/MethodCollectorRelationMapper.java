package com.brandslink.cloud.logistics.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.logistics.model.MethodCollectorRelationModel;

import java.util.List;

public interface MethodCollectorRelationMapper extends BaseMapper<MethodCollectorRelationModel> {

    void insertBatch(List<MethodCollectorRelationModel> insertList);

    void deleteByMethodID(Long methodId);

    void updateCollectorInfoBycollectorID(MethodCollectorRelationModel relation);

    List<MethodCollectorRelationModel> selectMethodByMethodCollectorRelation(MethodCollectorRelationModel relationModel);

}