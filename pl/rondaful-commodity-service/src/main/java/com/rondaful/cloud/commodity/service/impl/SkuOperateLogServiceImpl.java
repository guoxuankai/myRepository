package com.rondaful.cloud.commodity.service.impl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.commodity.entity.SkuOperateLog;
import com.rondaful.cloud.commodity.mapper.SkuOperateLogMapper;
import com.rondaful.cloud.commodity.service.SkuOperateLogService;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.utils.Utils;

@Service
public class SkuOperateLogServiceImpl implements SkuOperateLogService {
	
	@Autowired
	private SkuOperateLogMapper skuOperateLogMapper;

	@Override
	public void addSkuLog(SkuOperateLog skuLog) {
		skuOperateLogMapper.insert(skuLog);
	}

	@Override
	public Page<SkuOperateLog> getLogList(String systemSku) {
		List<SkuOperateLog> logList=skuOperateLogMapper.selectBySku(systemSku);
		if (logList != null && logList.size()>0) {
			for (SkuOperateLog skuOperateLog : logList) {
				skuOperateLog.setOperateBy(Utils.translation(skuOperateLog.getOperateBy()));
				skuOperateLog.setOperateInfo(Utils.translation(skuOperateLog.getOperateInfo()));
			}
		}
		PageInfo pageInfo = new PageInfo(logList);
	    return new Page(pageInfo);
	}

}
