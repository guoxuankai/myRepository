package com.brandslink.cloud.logistics.service;

import com.brandslink.cloud.common.service.BaseService;
import com.brandslink.cloud.logistics.model.MethodCollectorRelationModel;

import java.util.List;

public interface IMethodCollectorRelationService extends BaseService<MethodCollectorRelationModel> {

    List<MethodCollectorRelationModel> selectMethodByMethodCollectorRelation(MethodCollectorRelationModel relationModel);
}
