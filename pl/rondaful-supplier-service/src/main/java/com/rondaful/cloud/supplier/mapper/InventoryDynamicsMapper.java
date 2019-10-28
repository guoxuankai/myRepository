package com.rondaful.cloud.supplier.mapper;

import java.util.List;
import java.util.Map;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.InventoryDynamics;

public interface InventoryDynamicsMapper extends BaseMapper<InventoryDynamics> {
	
	public int insertBatchInventoryDynamics(List<InventoryDynamics> invDynamicsList);
	public List<InventoryDynamics> getInventoryDyListByIds(Map<String,Object> param);
}