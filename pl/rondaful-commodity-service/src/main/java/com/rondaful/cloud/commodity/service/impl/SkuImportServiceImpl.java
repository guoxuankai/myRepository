package com.rondaful.cloud.commodity.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.rondaful.cloud.commodity.entity.SkuImport;
import com.rondaful.cloud.commodity.entity.SkuPushLog;
import com.rondaful.cloud.commodity.entity.SkuPushRecord;
import com.rondaful.cloud.commodity.mapper.GoodCangMapper;
import com.rondaful.cloud.commodity.mapper.SkuImportMapper;
import com.rondaful.cloud.commodity.service.SkuImportService;

@Service
public class SkuImportServiceImpl implements SkuImportService {
	
	@Autowired
	private SkuImportMapper skuImportMapper;
	
	@Autowired
	private GoodCangMapper goodCangMapper;

	
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void updateImportRecord(Long importId, String msg) {
		SkuImport record=skuImportMapper.selectByPrimaryKey(importId);
		record.setStatus(2);
		record.setTaskDetail(msg);
		skuImportMapper.updateByPrimaryKeySelective(record);
	}


	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void inserPushRecordAndLog(String warehouseProviderCode, Integer accountId, String systemSku, int pushState,
			int optType, String optUser, String msg, String productState) {
		
		SkuPushRecord record=new SkuPushRecord();
		Map<String, Object> recordParam=new HashMap<String, Object>();
		recordParam.put("warehouseProviderCode", warehouseProviderCode);
		recordParam.put("accountId", accountId);
		recordParam.put("systemSku", systemSku);
		List<SkuPushRecord> recordList=goodCangMapper.querySkuPushRecord(recordParam);
		if (recordList==null || recordList.size()==0) {
			record.setWarehouseProviderCode(warehouseProviderCode);
			record.setAccountId(accountId);
			record.setSystemSku(systemSku);
			record.setPushState(pushState);
			record.setProductState(productState);
			goodCangMapper.insertSkuPushRecord(record);
		}else {
			record.setId(recordList.get(0).getId());
			record.setVersion(recordList.get(0).getVersion());
			record.setPushState(pushState);
			record.setProductState(productState);
			goodCangMapper.updateSkuPushRecord(record);
		}
		
		//增加推送操作日志
		SkuPushLog pushLogg=new SkuPushLog();
		pushLogg.setRecordId(record.getId());
		pushLogg.setOptType(optType);
		pushLogg.setOptUser(optUser);
		pushLogg.setContent(pushState==1?"推送成功，"+msg:"推送失败，"+msg);
		goodCangMapper.insertSkuPushLog(pushLogg);
	}

}
