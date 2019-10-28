package com.brandslink.cloud.gateway.exception;


import com.brandslink.cloud.gateway.entity.Massage;
import com.brandslink.cloud.gateway.enums.ResponseCodeEnum;
import com.brandslink.cloud.gateway.utils.Utils;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 全局异常捕获
 * */
@ControllerAdvice
public class ExceptionHandle {

    /**
     * 捕获全局异常
     * */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public void exceptionHandle(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        try {
            JSONObject json = null;
            if (exception instanceof GlobalException) {
                json = JSONObject.fromObject(new Massage(((GlobalException) exception).getErrorCode(), exception.getMessage()));
            } else {
                json = JSONObject.fromObject(new Massage(ResponseCodeEnum.RETURN_CODE_100500.getCode(), exception.getMessage()));
            }
            Utils.print(json);
        } catch (Exception e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
        }
    }

}
