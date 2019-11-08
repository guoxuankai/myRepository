package com.brandslink.cloud.common.controller;

import com.brandslink.cloud.common.utils.RedisUtils;
import com.brandslink.cloud.common.utils.RedissLockUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 基础控制层
 */
public class BaseController {

	@Autowired
	public HttpServletRequest request;

	@Autowired
	public HttpServletResponse response;

	@Autowired
	public RedisUtils redisUtils;

	@Autowired
	public RedissLockUtil redissLockUtil;

}
