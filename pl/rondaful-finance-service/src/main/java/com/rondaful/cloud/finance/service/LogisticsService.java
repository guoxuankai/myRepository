package com.rondaful.cloud.finance.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rondaful.cloud.finance.entity.LogisticsFareSupplement;
import com.rondaful.cloud.finance.mapper.LogisticsFareSupplementMapper;

/**
 * 物流相关业务实现
 *
 */
@Service
public class LogisticsService {

	@Autowired
	private LogisticsFareSupplementMapper mapper;

	public List<LogisticsFareSupplement> getSupplementBySellerId(Integer sellerId) {
		return mapper.selectBySellerId(sellerId);
	}

}
