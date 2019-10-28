package com.rondaful.cloud.commodity.service;


import com.rondaful.cloud.commodity.entity.SkuOperateLog;
import com.rondaful.cloud.common.entity.Page;

public interface SkuOperateLogService {

	void addSkuLog(SkuOperateLog skuLog);
	
	Page<SkuOperateLog> getLogList(String systemSku);
}
