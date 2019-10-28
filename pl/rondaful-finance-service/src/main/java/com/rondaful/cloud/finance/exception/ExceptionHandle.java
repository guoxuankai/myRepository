package com.rondaful.cloud.finance.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rondaful.cloud.finance.entity.Result;
import com.rondaful.cloud.finance.enums.ResponseCodeEnum;
import com.rondaful.cloud.finance.utils.Utils;

import net.sf.json.JSONObject;

/**
 * 全局异常捕获
 */
@ControllerAdvice
public class ExceptionHandle {

	private static Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);

	/**
	 * 捕获全局异常
	 */
	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public void exceptionHandle(HttpServletRequest request, HttpServletResponse response, Exception exception) {
		try {
			JSONObject json = null;
			if (exception instanceof GlobalException) {
				json = JSONObject
						.fromObject(new Result(((GlobalException) exception).getErrorCode(), exception.getMessage()));
			} else {
				json = JSONObject
						.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), exception.getMessage()));
			}
			Utils.print(json);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
		}
	}

}
