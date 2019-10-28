package com.rondaful.cloud.finance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rondaful.cloud.finance.entity.Result;
import com.rondaful.cloud.finance.service.FinanceRecordService;
import com.rondaful.cloud.finance.utils.ParamCheckUtil;
import com.rondaful.cloud.finance.vo.conditions.FinanceRecordConditionVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("finance")
@Api("财务报表")
public class FinanceRecordController {

	@Autowired
	private FinanceRecordService service;

	@PostMapping("query")
	@ApiOperation(value = "财务管理 记录查询", notes = "财务管理 记录查询(带分页条件)")
	public Result financeQuery(FinanceRecordConditionVo conditions) {
		ParamCheckUtil.isNull(conditions);
		return new Result(service.pageQueryWithConditions(conditions));
	}

}
