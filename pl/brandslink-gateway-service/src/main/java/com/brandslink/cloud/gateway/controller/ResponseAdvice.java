package com.brandslink.cloud.gateway.controller;

import com.brandslink.cloud.gateway.entity.Massage;
import com.brandslink.cloud.gateway.enums.ResponseCodeEnum;
import com.brandslink.cloud.gateway.exception.GlobalException;
import com.brandslink.cloud.gateway.utils.Utils;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
            "/v2/api-docs",
            "/swagger"
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
            JSONObject json = JSONObject.fromObject(new Massage(ResponseCodeEnum.RETURN_CODE_100200, obj));
            Utils.print(json);
        } catch (IOException e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
        }
        return null;
    }


    public boolean isExclude(String uri){
        for(String s : exclude){
            if(uri.startsWith(s)){
                return true;
            }
        }
        return false;
    }

}
