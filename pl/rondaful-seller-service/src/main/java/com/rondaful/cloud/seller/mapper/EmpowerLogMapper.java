package com.rondaful.cloud.seller.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.EmpowerLog;

public interface EmpowerLogMapper extends BaseMapper<EmpowerLog> {
	
	
	List<EmpowerLog>  selectByPrimaryKey(@Param("id")Integer id,@Param("handler")String handler);
	
	
	
	
	
}