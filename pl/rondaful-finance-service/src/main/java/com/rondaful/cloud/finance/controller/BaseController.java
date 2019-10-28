package com.rondaful.cloud.finance.controller;


import org.springframework.beans.factory.annotation.Autowired;

import com.rondaful.cloud.finance.utils.RedisUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 基础控制层
 * */
public class BaseController {

    @Autowired
    public HttpServletRequest request;

    @Autowired
    public HttpServletResponse response;

    @Autowired
    public RedisUtils redisUtils;



}
