package com.rondaful.cloud.seller.service;

import java.util.List;

import com.rondaful.cloud.seller.entity.PublishLog;
import com.rondaful.cloud.seller.enums.PublishLogEnum;

public interface PublishLogService {

	/**
	 * 添加刊登操作日志
	 * @param content
	 * @param type
	 * @param operatorId
	 * @param operatorName
	 */
	void insert (String content,PublishLogEnum type,Integer operatorId,String operatorName,Long publishId);
	
	/**
	 * 根据刊登id查询
	 * @param publishId
	 * @return
	 */
	List<PublishLog> getByPublishId(Long publishId);
}
