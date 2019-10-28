package com.rondaful.cloud.seller.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rondaful.cloud.seller.entity.PublishLog;
import com.rondaful.cloud.seller.enums.PublishLogEnum;
import com.rondaful.cloud.seller.mapper.PublishLogMapper;
import com.rondaful.cloud.seller.service.PublishLogService;

@Service
public class PublishLogServiceImpl  implements PublishLogService {

	@Autowired
	private PublishLogMapper publishLogMapper;
	
	@Override
	public void insert(String content,PublishLogEnum type,Integer operatorId,String operatorName,Long publishId) {
		PublishLog publishLog=new PublishLog();
		publishLog.setContent(content);
		publishLog.setCreateTime(new Date());
		publishLog.setOperatorId(operatorId);
		publishLog.setOperatorName(operatorName);
		publishLog.setType(type.getCode());
		publishLog.setPublishId(publishId);
		publishLogMapper.insertSelective(publishLog);
	}


	@Override
	public List<PublishLog> getByPublishId(Long publishId) {
		return publishLogMapper.getByPublishId(publishId);
	}
	
	
}
