package com.rondaful.cloud.user.controller;


import com.netflix.discovery.EurekaClient;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * 通用控制层
 * */
@RestController
@Api(description = "通用控制层")
public class CommonController {

	@Value("${swagger.enable}")
	public boolean isDev;

	@Autowired
	private EurekaClient eurekaClient;


	@ApiOperation(value = "服务下线", notes = "")
	@RequestMapping(value = "/offline", method = RequestMethod.GET)
	public void offLine() {
		if (isDev) {
			eurekaClient.shutdown();
		}
	}

}
