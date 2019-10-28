package com.rondaful.cloud.commodity.service;

import java.util.List;
import java.util.Map;

import com.rondaful.cloud.commodity.entity.CommodityBase;


public interface TongToolService {

	Map<String, Object> pushToTongTool(List<CommodityBase> baseList,Long sellerId);
	
	void pushAllByPage(Map<String,Object> param);
}
