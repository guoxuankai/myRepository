package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.EbayPublishBuyerRequirements;

public interface EbayPublishBuyerRequirementsMapper extends BaseMapper<EbayPublishBuyerRequirements> {
	
	void deleteByValue(EbayPublishBuyerRequirements record);
	
	
	EbayPublishBuyerRequirements selectByValue(EbayPublishBuyerRequirements record);
	
	
}