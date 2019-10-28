package com.rondaful.cloud.finance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rondaful.cloud.finance.entity.SystemFinanceRecord;
import com.rondaful.cloud.finance.mapper.SystemFinanceRecordMapper;

@Service
public class FinanceRecordService extends BaseRecordService<SystemFinanceRecord>{
	
	@SuppressWarnings("unused")
	@Autowired
	private SystemFinanceRecordMapper mapper;
	
	
	
	
}
