package com.brandslink.cloud.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.constant.SystemConstants;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.enums.ResponseCodeEnumSupper;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.service.TranslationService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 通用工具类
 */
@Component
public class Utils {

	private final static Logger log = LoggerFactory.getLogger(Utils.class);

	@Value("${swagger.enable}")
	public boolean dev;

	@Autowired
	private TranslationService translation;

	public static boolean isDev;

	private static TranslationService translationService;

	@PostConstruct
	public void getEnv() {
		isDev = dev;
		translationService = translation;
	}

	/**
	 * 响应输出
	 */
	public static void print(Object object) throws IOException {
		try {
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletRequest request = requestAttributes.getRequest();
			HttpServletResponse response = requestAttributes.getResponse();
			response.addHeader("Cache-Control", "no-cache");
			response.setContentType("application/json;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.print(object);
			out.flush();
			out.close();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	/**
	 * 匹配合适的返回码
	 *
	 * @param code 被匹配的返回码
	 * @return 返回结果
	 */
	public static ResponseCodeEnumSupper mapResponseCode(String code) {
		if (StringUtils.isBlank(code))
			return ResponseCodeEnum.RETURN_CODE_100500;
		if (code.equalsIgnoreCase(ResponseCodeEnum.RETURN_CODE_100500.getCode()))
			return ResponseCodeEnum.RETURN_CODE_100500;
		if (code.equalsIgnoreCase(ResponseCodeEnum.RETURN_CODE_100200.getCode()))
			return ResponseCodeEnum.RETURN_CODE_100200;
		if (code.equalsIgnoreCase(ResponseCodeEnum.RETURN_CODE_100001.getCode()))
			return ResponseCodeEnum.RETURN_CODE_100001;
		if (code.equalsIgnoreCase(ResponseCodeEnum.RETURN_CODE_100400.getCode()))
			return ResponseCodeEnum.RETURN_CODE_100400;
		if (code.equalsIgnoreCase(ResponseCodeEnum.RETURN_CODE_100401.getCode()))
			return ResponseCodeEnum.RETURN_CODE_100401;
		if (code.equalsIgnoreCase(ResponseCodeEnum.RETURN_CODE_100403.getCode()))
			return ResponseCodeEnum.RETURN_CODE_100403;
		if (code.equalsIgnoreCase(ResponseCodeEnum.RETURN_CODE_100406.getCode()))
			return ResponseCodeEnum.RETURN_CODE_100406;
		return ResponseCodeEnum.RETURN_CODE_100500;
	}

	/**
	 * 处理远程调用结果，判断结果返回码，当结果正常时返回 data中的数据已字符串的形势返回。 （本方法处理的结果的短路降级返回一律为 null）
	 *
	 * @param result       远程结果返回
	 * @param errorMessage 当返回的结果为空时的错误信息
	 * @return data 的字符串返回
	 */
	public static String returnRemoteResultDataString(String result, String errorMessage) {
		if (StringUtils.isBlank(result))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, errorMessage);
		JSONObject object = JSONObject.parseObject(result);
		if (!object.getString("errorCode").equalsIgnoreCase(ResponseCodeEnum.RETURN_CODE_100200.getCode())) {
			log.error("返回结果集：{}", result);
			throw new GlobalException(Utils.mapResponseCode(object.getString("errorCode")), object.getString("msg"));
		}
		return object.getString("data");
	}

	/**
	 * 获取服务结果异常处理
	 *
	 * @param result 远程服务返回对象
	 * @param name   系统类型
	 * @param msg    错误提示语
	 * @return
	 */
	public static String getResultData(String result, SystemConstants.nameType name, String msg) {
		if (StringUtils.isBlank(result))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, msg);
		JSONObject obj = JSONObject.parseObject(result);
		if (!obj.getString("errorCode").equalsIgnoreCase(ResponseCodeEnum.RETURN_CODE_100200.getCode())) {
			String message = (isDev ? name.getDescription() : "") + obj.getString("msg");
			log.info("调用[{}]_结果{}", name.getDescription(), obj.getString("msg"));
			throw new GlobalException(Utils.mapResponseCode(obj.getString("errorCode")), message);
		}
		return obj.getString("data");
	}

	/**
	 * 处理远程调用结果，返回JSON对象(暂不支持返回数组)
	 * 
	 * @param customMessage 自定义错误信息可传(服务名)
	 * @param result        远程结果返回
	 * @param errorMessage  当返回的结果为空时的错误信息
	 * @return data 的返回JSON对象
	 * @author: zhaojiaxing
	 */
	public static JSONObject resultParseObj(String result, String customMessage, String errorMessage) {
		if (StringUtils.isBlank(result))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, errorMessage);
		JSONObject object = JSONObject.parseObject(result);

		if (!object.getString("errorCode").equalsIgnoreCase(ResponseCodeEnum.RETURN_CODE_100200.getCode())) {
			log.error("返回结果集：{}", result);
			throw new GlobalException(Utils.mapResponseCode(object.getString("errorCode")), customMessage + ":" + object.getString("msg"));
		}
		try {
			return object.getJSONObject("data");
		} catch (Exception e) {
			throw new GlobalException(Utils.mapResponseCode(object.getString("errorCode")), "结果集：" + object.getString("data") + "，该工具不支持返回数组");
		}
	}

	/**
	 * 处理远程调用结果，返回JSON数组
	 * 
	 * @param customMessage 自定义错误信息可传(服务名)
	 * @param result        远程结果返回
	 * @param errorMessage  当返回的结果为空时的错误信息
	 * @return data 的返回JSON数组
	 * @author: zhaojiaxing
	 */
	public static JSONArray resultParseArray(String result, String customMessage, String errorMessage) {
		if (StringUtils.isBlank(result))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, errorMessage);
		JSONObject object = JSONObject.parseObject(result);

		if (!object.getString("errorCode").equalsIgnoreCase(ResponseCodeEnum.RETURN_CODE_100200.getCode())) {
			log.error("返回结果集：{}", result);
			throw new GlobalException(Utils.mapResponseCode(object.getString("errorCode")), customMessage + ":" + object.getString("msg"));
		}
		return object.getJSONArray("data");
	}

	public static Map<String, String> stringToMap(String obj) {
		String[] str = obj.substring(1, obj.length() - 1).split(",");
		Map<String, String> map = new HashMap<String, String>();
		for (String m : str) {
			map.put(m.split("=")[0].trim(), m.split("=")[1].trim());
		}
		return map;
	}

	/**
	 * 动态替换国际化
	 * 
	 * @param str
	 * @param param
	 * @return
	 */
	public static String i18n(String str, Object... param) {
		return MessageFormat.format(str, param);
	}

	/**
	 * 获取国际化结果
	 * 
	 * @param str en_us
	 * @return
	 */
	public static String i18n(String str) {
		try {
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletRequest request = requestAttributes.getRequest();
			Locale locale = LocaleContextHolder.getLocale();
			String header = request.getHeader("i18n");
			if (StringUtils.isNotBlank(header))
				locale = new Locale(header.split("_")[0], header.split("_")[1]);
			return SpringContextUtil.getApplicationContext().getMessage(str, null, locale);
		} catch (Exception e) {
			return str;
		}
	}

	/**
	 * 百度翻译 中文转英文
	 * 
	 * @return
	 */
	public static String translation(String str) {
		try {
			if (StringUtils.isBlank(str))
				return str;
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletRequest request = requestAttributes.getRequest();
			String header = request.getHeader("i18n");
			if (StringUtils.isNotBlank(header))
				str = translationService.transcation(str, "zh", header.split("_")[0].toLowerCase());
			return str;
		} catch (Exception e) {
			return str;
		}
	}

	/**
	 * 百度翻译 英文转中文
	 * 
	 * @return
	 */
	public static String translationToZh(String str) {
		try {
			if (StringUtils.isBlank(str))
				return str;
			return translationService.transcation(str, "en", "zh");
		} catch (Exception e) {
			return str;
		}
	}

}
