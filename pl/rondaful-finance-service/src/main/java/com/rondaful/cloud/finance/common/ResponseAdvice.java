package com.rondaful.cloud.finance.common;

import com.rondaful.cloud.finance.entity.Result;
import com.rondaful.cloud.finance.enums.ResponseCodeEnum;
import com.rondaful.cloud.finance.exception.GlobalException;
import com.rondaful.cloud.finance.utils.Utils;

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
            "/rest/api/doc"
    };


    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        // return methodParameter.hasMethodAnnotation(ResponseBody.class);
        String uri = request.getRequestURI();
        System.err.println(uri);
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
            JSONObject json = JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100200, obj));
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
