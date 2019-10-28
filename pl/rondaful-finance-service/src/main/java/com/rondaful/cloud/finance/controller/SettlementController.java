package com.rondaful.cloud.finance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rondaful.cloud.finance.entity.Result;
import com.rondaful.cloud.finance.entity.SettlementRegistInfo;
import com.rondaful.cloud.finance.service.SettlementService;
import com.rondaful.cloud.finance.utils.ParamCheckUtil;
import com.rondaful.cloud.finance.vo.conditions.BaseConditionVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("settlement")
@Api("结算")
public class SettlementController {

	@Autowired
	private SettlementService service;

	@PostMapping("query")
	@ApiOperation(value = "结算查询", notes = "结算记录查询(带分页条件)")
	public Result settlementQuery(BaseConditionVo conditions) {
		ParamCheckUtil.isNull(conditions);
		return new Result(service.pageQueryWithConditions(conditions));
	}

	@PostMapping("regist")
	@ApiOperation(value = "结算信息注册", notes = "结算信息注册")
	public Result settlementRegist(Integer supplierId, String supplierName, String settlementCycle) {
		return new Result(
				service.settlementRegist(new SettlementRegistInfo(supplierId, supplierName, settlementCycle)));
	}

	@PostMapping("modify")
	@ApiOperation(value = "结算信息修改", notes = "结算信息修改")
	public Result settlementModify(Integer supplierId, String supplierName, String settlementCycle) {
		return new Result(
				service.settlementModify(new SettlementRegistInfo(supplierId, supplierName, settlementCycle)));
	}
}
