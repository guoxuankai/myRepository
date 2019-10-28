package com.rondaful.cloud.seller.mapper;

import java.util.List;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.PublishLog;

public interface PublishLogMapper extends BaseMapper<PublishLog>{

	List<PublishLog> getByPublishId(Long publishId);

}