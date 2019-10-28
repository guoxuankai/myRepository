package com.rondaful.cloud.supplier.mapper;

import java.util.List;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.WareHouseServiceProvider;

public interface WareHouseServiceProviderMapper extends BaseMapper<WareHouseServiceProvider> {
	
	List<WareHouseServiceProvider> selectByParamValue(WareHouseServiceProvider provider);
}
