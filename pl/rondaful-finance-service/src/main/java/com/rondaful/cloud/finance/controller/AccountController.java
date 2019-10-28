package com.rondaful.cloud.finance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rondaful.cloud.finance.entity.Result;
import com.rondaful.cloud.finance.entity.SellerAccount;
import com.rondaful.cloud.finance.entity.SupplierAccount;
import com.rondaful.cloud.finance.service.AccountService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("account")
@Api("账户")
public class AccountController {

	@Autowired
	private AccountService service;

	@PostMapping("seller/init")
	@ApiOperation(value = "卖家账户初始化", notes = "卖家账户初始化")
	public Result sellerInit(Integer sellerId, String sellerName) {
		// 参数校验
		return new Result(service.sellerInit(new SellerAccount(sellerId, sellerName)));
	}

	@PostMapping("supplier/init")
	@ApiOperation(value = "供应商初始化", notes = "供应商初始化")
	public Result supplierInit(Integer supplierId, String supplierName) {
		// 参数校验
		return new Result(service.supplierInit(new SupplierAccount(supplierId, supplierName)));
	}

	@PostMapping("seller")
	@ApiOperation(value = "获取卖家账户信息", notes = "获取卖家账户信息")
	public Result getSellerInfo(Integer sellerId) {
		// 参数校验
		return new Result(service.getSeller(sellerId));
	}

	@PostMapping("supplier")
	@ApiOperation(value = "获取供应商账户信息", notes = "获取供应商账户信息")
	public Result getSupplierInfo(Integer supplierId) {
		// 参数校验
		return new Result(service.getSupplier(supplierId));
	}

	@PostMapping("admin")
	@ApiOperation(value = "获取系统账户信息", notes = "获取系统账户信息")
	public Result getAdminInfo() {
		// 参数校验
		return new Result(service.getAdmin());
	}

}
