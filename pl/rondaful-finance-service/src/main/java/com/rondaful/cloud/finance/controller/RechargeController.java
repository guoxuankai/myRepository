package com.rondaful.cloud.finance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rondaful.cloud.finance.entity.RechargeRecord;
import com.rondaful.cloud.finance.entity.Result;
import com.rondaful.cloud.finance.service.RechargeService;
import com.rondaful.cloud.finance.utils.ParamCheckUtil;
import com.rondaful.cloud.finance.vo.ExamineRequestVo;
import com.rondaful.cloud.finance.vo.RechargeRequestVo;
import com.rondaful.cloud.finance.vo.conditions.RechargeConditionVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("recharge")
@Api("充值")
public class RechargeController extends BaseController {

	@Autowired
	private RechargeService service;

	@PostMapping("request")
	@ApiOperation(value = "充值申请", notes = "充值申请")
	public Result rechargeRequest(RechargeRequestVo requestVO) {
		// 参数校验
		ParamCheckUtil.isNull(requestVO);
		return new Result(service.request(new RechargeRecord(requestVO)));
	}

	@PostMapping("query")
	@ApiOperation(value = "充值申请 记录查询", notes = "充值申请记录查询(带分页条件)")
	public Result rechargeQuery(RechargeConditionVo conditions) {
		ParamCheckUtil.isNull(conditions);
		return new Result(service.pageQueryWithConditions(conditions));
	}

	@PostMapping("examine")
	@ApiOperation(value = "充值申请 审核/作废", notes = "充值申请 审核/作废")
	public Result rechargeExamine(ExamineRequestVo examineRequestVo) {
		// 参数校验
		ParamCheckUtil.isNull(examineRequestVo);
		return new Result(service.examine(new RechargeRecord(examineRequestVo)));
	}

	@PostMapping("resubmission")
	@ApiOperation(value = "充值申请 编辑重新提交", notes = "充值申请 编辑重新提交")
	public Result rechargeResubmission(RechargeRecord rechargeRecord) {
		// 参数校验
		ParamCheckUtil.isNull(rechargeRecord);
		return new Result(service.resubmission(rechargeRecord));
	}

}
