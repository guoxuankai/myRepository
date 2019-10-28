package com.rondaful.cloud.finance.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rondaful.cloud.finance.entity.Result;
import com.rondaful.cloud.finance.entity.WithdrawalRecord;
import com.rondaful.cloud.finance.service.WithdrawalService;
import com.rondaful.cloud.finance.utils.ParamCheckUtil;
import com.rondaful.cloud.finance.vo.ExamineRequestVo;
import com.rondaful.cloud.finance.vo.WithdrawRequestVo;
import com.rondaful.cloud.finance.vo.conditions.RechargeConditionVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("withdraw")
@Api("提现")
public class WithdrawalController {

	@Autowired
	private WithdrawalService service;

	@PostMapping("request")
	@ApiOperation(value = "提现申请", notes = "提现申请")
	public Result withdrawRequest(WithdrawRequestVo requestVO) {
		// 参数校验
		ParamCheckUtil.isNull(requestVO);
		return new Result(service.request(new WithdrawalRecord(requestVO)));
	}

	@PostMapping("query")
	@ApiOperation(value = "提现申请 记录查询", notes = "提现申请记录查询(带分页条件)")
	public Result withdrawQuery(RechargeConditionVo conditions) {
		ParamCheckUtil.isNull(conditions);
		return new Result(service.pageQueryWithConditions(conditions));
	}

	@PostMapping("examine")
	@ApiOperation(value = "提现申请 审核", notes = "提现申请 审核")
	public Result withdrawExamine(ExamineRequestVo examineRequestVo) {
		// 参数校验
		ParamCheckUtil.isNull(examineRequestVo);
		return new Result(service.examine(new WithdrawalRecord(examineRequestVo)));
	}

	@PostMapping("remittance")
	@ApiOperation(value = "提现申请 打款", notes = "提现申请打款操作")
	public Result withdrawRemittance(Integer id, Date payTime, String transferReceiptUrl, String transSerialNo,
			Integer version) {
		return new Result(
				service.remittance(new WithdrawalRecord(id, payTime, transferReceiptUrl, transSerialNo, version)));
	}

	@PostMapping("resubmission")
	@ApiOperation(value = "提现申请 编辑重新提交", notes = "提现申请 编辑重新提交")
	public Result withdrawResubmission(WithdrawalRecord withdrawalRecord) {
		// 参数校验
		ParamCheckUtil.isNull(withdrawalRecord);
		return new Result(service.resubmission(withdrawalRecord));
	}

}
