package com.rondaful.cloud.commodity.service;

import java.util.List;
import java.util.Map;

import com.rondaful.cloud.commodity.dto.WmsCategory;
import com.rondaful.cloud.commodity.entity.CommoditySpec;

public interface WmsPushService {

	void addCategory(String appKey,String appToken,List<WmsCategory> categoryList);
	
	void updateCategory(String appKey,String appToken,WmsCategory category);
	
	Map<String, Object> addProduct(Integer accountId,int type,List<CommoditySpec> commoditySpecs,String optUser);
	
	
	void addAllProductByPage(Integer accountId,int total,Long supplierId,String optUser,Integer status);
	
}
