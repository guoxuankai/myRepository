package com.rondaful.cloud.seller.service;

import com.rondaful.cloud.seller.entity.EbayPublishListingOperationLog;

import java.util.List;

public interface IEbayPublishListingOperationLogService {
	
	void insert(EbayPublishListingOperationLog record) throws Exception;
	
	List<EbayPublishListingOperationLog> findOperationLogList(Integer listingId) throws Exception;

}
