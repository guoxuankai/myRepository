package com.rondaful.cloud.seller.service.impl;

import java.util.List;

import com.rondaful.cloud.seller.entity.EbayPublishListingOperationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rondaful.cloud.seller.mapper.EbayPublishListingOperationLogMapper;
import com.rondaful.cloud.seller.service.IEbayPublishListingOperationLogService;

@Service
public class EbayPublishListingOperationLogServiceImpl implements IEbayPublishListingOperationLogService {

	@Autowired
	private EbayPublishListingOperationLogMapper  logMapper;
	
	@Override
	public void insert(EbayPublishListingOperationLog record) {
		logMapper.insertSelective(record);
	}

	@Override
	public List<EbayPublishListingOperationLog> findOperationLogList(Integer listingId) {
		EbayPublishListingOperationLog log = new EbayPublishListingOperationLog();
		log.setListingId(listingId);
		return logMapper.page(log);
	}

}
