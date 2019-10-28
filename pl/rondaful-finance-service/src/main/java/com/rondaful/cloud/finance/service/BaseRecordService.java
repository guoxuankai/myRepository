package com.rondaful.cloud.finance.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.finance.mapper.BaseMapper;
import com.rondaful.cloud.finance.vo.conditions.BaseConditionVo;

public abstract class BaseRecordService<T> {
	@Autowired
	private BaseMapper<T> mapper;
	
	public boolean request(T t) {
		return this.mapper.insertSelective(t)>0;
	} 
	
	public PageInfo<T> pageQueryWithConditions(BaseConditionVo conditions){
		PageHelper.startPage(conditions.getPageNum(), conditions.getPageSize());
		return new PageInfo<T>(mapper.pageQueryWithConditions(conditions));
	}
	
	public boolean examine(T t) {
		return mapper.updateByPrimaryKeySelective(t)>0;
	}
	
	public boolean resubmission(T t) {
		return mapper.updateByPrimaryKey(t)>0;
	}
	
}
