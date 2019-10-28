package com.rondaful.cloud.commodity.mapper;

import com.rondaful.cloud.commodity.entity.SpuTortRecord;
import com.rondaful.cloud.common.mapper.BaseMapper;

public interface SkuTortRecordMapper extends BaseMapper<SpuTortRecord> {
	
	int getTortNum(SpuTortRecord record);
}