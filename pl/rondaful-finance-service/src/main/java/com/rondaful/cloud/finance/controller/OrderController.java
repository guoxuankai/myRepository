package com.rondaful.cloud.finance.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rondaful.cloud.finance.entity.OrderRecord;
import com.rondaful.cloud.finance.entity.Result;
import com.rondaful.cloud.finance.service.OrderService;
import com.rondaful.cloud.finance.utils.ParamCheckUtil;
import com.rondaful.cloud.finance.vo.OrderRequestVo;
import com.rondaful.cloud.finance.vo.conditions.OrderConditionVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("order")
@Api("订单支付明细")
public class OrderController {

	@Autowired
	private OrderService service;

	@PostMapping("generate")
	@ApiOperation(value = "订单生成", notes = "订单生成")
	public Result generate(OrderRequestVo orderRequestVo) {
		// 参数校验
		ParamCheckUtil.isNull(orderRequestVo);
		return new Result(service.request(new OrderRecord(orderRequestVo)));
	}

	// 订单取消：取消冻结
	@PostMapping("cancel")
	@ApiOperation(value = "订单取消", notes = "订单取消")
	public Result cancel(String orderNo) {
		// 参数校验
		return new Result(service.cancel(orderNo));
	}

	// 订单分页查询
	@PostMapping("query")
	@ApiOperation(value = "订单查询", notes = "订单查询")
	public Result query(OrderConditionVo orderConditionVo) {
		// 参数校验
		ParamCheckUtil.isNull(orderConditionVo);
		return new Result(service.pageQueryWithConditions(orderConditionVo));
	}

	// 订单出库：扣款
	@PostMapping("confirm")
	@ApiOperation(value = "订单确认", notes = "订单确认")
	public Result confirm(String orderNo, BigDecimal supplementAmount) {
		// 参数校验
		return new Result(service.confirm(orderNo, supplementAmount));
	}

	// 退款

}
