package com.brandslink.cloud.common.exception;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.brandslink.cloud.common.entity.Result;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.utils.Utils;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.xml.bind.ValidationException;
import java.sql.SQLException;
import java.util.List;

/**
 * 全局异常捕获
 */
@ControllerAdvice
public class ExceptionHandle {

	private final Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);

	/**
	 * 捕获全局异常
	 */
	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public void exceptionHandle(HttpServletRequest request, HttpServletResponse response, Exception exception) {
		try {
			JSONObject json;
			if (exception instanceof GlobalException) {
				logger.error("全局异常", exception);
				json = (JSONObject) JSONObject.toJSON(new Result(((GlobalException) exception).getErrorCode(), exception.getMessage()));
			} else if (exception instanceof AccessDeniedException) {
				logger.error("权限异常", exception);
				json = (JSONObject) JSONObject.toJSON(new Result(ResponseCodeEnum.RETURN_CODE_100401));
			} else if (exception instanceof SQLException) {
				logger.error("数据库操作错误", exception);
				json = (JSONObject) JSONObject.toJSON(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "数据库操作异常"));
			} else if (exception instanceof DuplicateKeyException) {
				logger.error("唯一键重复", exception);
				json = (JSONObject) JSONObject.toJSON(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "编码重复"));
			} else if (exception instanceof MethodArgumentNotValidException) {
				logger.error("必输参数为空", exception);
				List<FieldError> fe = ((MethodArgumentNotValidException) exception).getBindingResult().getFieldErrors();
				json = (JSONObject) JSONObject.toJSON(new Result(ResponseCodeEnum.RETURN_CODE_100403.getCode(), fe.get(0).getDefaultMessage()));
			}
			else if (exception instanceof ConstraintViolationException) {
				logger.error("必输参数为空", exception);
				json = (JSONObject) JSONObject.toJSON(new Result(ResponseCodeEnum.RETURN_CODE_100403.getCode(), exception.getMessage()));
			} else if(exception instanceof InvalidFormatException) {
				logger.error("类型转换错误", exception);
				json = (JSONObject) JSONObject.toJSON(new Result(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "参数类型不匹配"));
			} else if(exception instanceof HttpRequestMethodNotSupportedException){
				logger.error("请求方式不匹配", exception);
				json = (JSONObject) JSONObject.toJSON(new Result(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "请求方式不匹配"));
			} else if(exception instanceof HttpMessageNotReadableException){
				logger.error("请求参数格式错误", exception);
				json = (JSONObject) JSONObject.toJSON(new Result(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "请求参数格式错误"));
			} else if(exception instanceof ValidationException){
				logger.error("请求参数格式错误", exception);
				json = (JSONObject) JSONObject.toJSON(new Result(ResponseCodeEnum.RETURN_CODE_100400.getCode(), exception.getMessage()));
			} else {
				logger.error("未知异常", exception);
				json = (JSONObject) JSONObject.toJSON(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "系统操作异常"));
			}
			json.put("msg", Utils.translation(json.getString("msg")));
			Utils.print(JSONObject.toJSONString(json, SerializerFeature.WriteMapNullValue));
		} catch (Throwable e) {
			logger.error("系统异常", e);
		}
	}

}
