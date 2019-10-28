package com.rondaful.cloud.seller.mapper;

import java.util.List;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.EbayPublishListingVariantPicture;

public interface EbayPublishListingVariantPictureMapper extends BaseMapper<EbayPublishListingVariantPicture> {
	
	void insertBatchList(List<EbayPublishListingVariantPicture> recordList);
	
	void deleteByValue(Integer listId);
}