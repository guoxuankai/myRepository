package com.rondaful.cloud.commodity.service;

public interface SkuImportService {

	void updateImportRecord(Long importId,String msg);
	
	void inserPushRecordAndLog(String warehouseProviderCode,Integer accountId,
			String systemSku,int pushState,int optType,String optUser,String msg,String productState);
}
