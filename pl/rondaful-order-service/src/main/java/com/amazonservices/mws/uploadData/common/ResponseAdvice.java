package com.amazonservices.mws.uploadData.common;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.rondaful.cloud.common.entity.Result;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.BigDecimal2JsonFormat;
import com.rondaful.cloud.common.utils.Date2JsonFormat;
import com.amazonservices.mws.uploadData.utils.Utils;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * 全局响应代理
 */
@ControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    @Autowired
    public HttpServletRequest request;


    /**
     * 排除过滤的URI
     */
    private final static String[] exclude = {
            "/rest/api/doc" 
    		
    };


    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        // return methodParameter.hasMethodAnnotation(ResponseBody.class);
        String uri = request.getRequestURI();
        if (isExclude(uri)) {
            return false;
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object obj, MethodParameter methodParameter,
                                  MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest req,
                                  ServerHttpResponse res) {

        try {
        	JsonConfig jsonConfig = new JsonConfig();
            jsonConfig.registerJsonValueProcessor(Date.class, new Date2JsonFormat());
            jsonConfig.registerJsonValueProcessor(BigDecimal.class, new BigDecimal2JsonFormat());
            JSONObject json = JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100200, obj), jsonConfig);
            Utils.print(json);
        } catch (IOException e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
        }
        return null;
    }


    public boolean isExclude(String uri) {
        for (String s : exclude) {
            if (uri.startsWith(s)) {
                return true;
            }
        }
        return false;
    }
    
    
    
    
    
    

}
