package com.rondaful.cloud.finance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rondaful.cloud.finance.entity.Result;
import com.rondaful.cloud.finance.service.LogisticsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("logistics")
@Api("物流")
public class LogisticsController {

	@Autowired
	private LogisticsService service;

	@PostMapping("query")
	@ApiOperation(value = "物流补扣单查询", notes = "物流补扣单查询")
	public Result logisticsQuery(Integer sellerId) {
		return new Result(service.getSupplementBySellerId(sellerId));
	}

}
