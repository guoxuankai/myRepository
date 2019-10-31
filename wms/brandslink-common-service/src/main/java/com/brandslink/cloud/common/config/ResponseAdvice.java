package com.brandslink.cloud.common.config;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.brandslink.cloud.common.entity.Result;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.DateUtils;
import com.brandslink.cloud.common.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * 全局响应代理
 */
@ControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

	@Autowired
	public HttpServletRequest request;

	@Autowired
	public HttpServletResponse response;

	private static SerializeConfig sc = new SerializeConfig();
	/**
	 * 排除过滤的URI
	 */
	@Value("${system.ignore.response.advice}")
	private String[] exclude;

	@Override
	public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
		for (String s : exclude) {
			if (new AntPathRequestMatcher(s.trim()).matches(request)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object obj, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest req, ServerHttpResponse res) {
		try {
			checkException(obj);
			sc.put(Date.class, new SimpleDateFormatSerializer(DateUtils.FORMAT_2));
			JSONObject json = (JSONObject) JSONObject.toJSON(new Result(ResponseCodeEnum.RETURN_CODE_100200, obj));
			json.put("msg", Utils.i18n(json.getString("msg")));
			Utils.print(JSONObject.toJSONString(json, sc, SerializerFeature.WriteMapNullValue));
		} catch (IOException e) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
		}
		return null;
	}

	/**
	 * 检查是否系统级异常
	 * 
	 * @param object
	 */
	public void checkException(Object object) {
		int status = response.getStatus();
		response.setStatus(HttpServletResponse.SC_OK);
		if (status == HttpServletResponse.SC_NOT_FOUND) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100404);
		} else if (status == HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, (String) ((Map) object).get("message"));
		} else if (status == HttpServletResponse.SC_UNAUTHORIZED) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401, (String) ((Map) object).get("message"));
		}
		// ... 其他状态码在这里扩展
	}

}
