package com.rondaful.cloud.commodity.controller;


import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.common.utils.RedissLockUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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

    @Autowired
    public RedissLockUtil redissLockUtil;
    
    /**
     * @Description:是否搜索商品英文名称
     * @return
     * @author:范津
     */
    protected boolean isEnNameSearch() {
    	boolean flag=false;
    	//根据请求头判断是中文还是英文商品名称搜索
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();
		String header = request.getHeader("i18n");
		if (StringUtils.isNotBlank(header) && "en".equals(header.split("_")[0].toLowerCase())) {
			flag=true;
		}
		return flag;
    }

}
