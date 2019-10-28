package com.rondaful.cloud.user.controller.utils;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.constant.ConstantAli;
import com.rondaful.cloud.common.constant.UserConstants;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.AliSMSUtils;
import com.rondaful.cloud.common.utils.Date2JsonFormat;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.user.controller.model.manage.CreateSellerUserBean;
import com.rondaful.cloud.user.entity.User;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.utils.ImtegerJsonToType;
import com.rondaful.cloud.user.utils.MD5;
import com.rondaful.cloud.user.utils.ValidatorUtil;
import net.sf.json.JsonConfig;
import net.sf.json.processors.DefaultDefaultValueProcessor;
import net.sf.json.processors.DefaultValueProcessor;
import net.sf.json.processors.JsonValueProcessor;
import net.sf.json.util.JSONUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class ControllerUtil {
	
	
    private final Logger logger = LoggerFactory.getLogger(ControllerUtil.class);

    @Autowired
    private RedisUtils redisUtils;

	@Resource
	private AliSMSUtils aliSMSUtils;


	/**
	 * 内部短信通知
	 * 6供应商创建成功  7卖家创建成功 8供应商激活成功
	 * @param codeType
	 * @param phone
	 * @param json
	 */
	public void sendSmsJson(Integer codeType, String phone, JSONObject json){
		try {
			if (phone != null) phone.trim();
			if (!ValidatorUtil.isMobile(phone))throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "手机格式错误"); //验证手机格式
			String randomCode = RandomStringUtils.randomNumeric(6);//生成短信验证码
			redisUtils.set(phone, randomCode, 120L);//将短信验证码保存到Redis中
			//调用SMS服务发送短信
			if (7 == codeType.intValue()) aliSMSUtils.sendSms(phone,json, ConstantAli.SmsType.SELLER_CREATE_OK_CODE);//卖家创建成功手机通知短信
			else if (6 == codeType.intValue()) aliSMSUtils.sendSms(phone,json, ConstantAli.SmsType.SUPPLIER_CREATE_OK_CODE);//供应商创建成功手机通知短信
			else if (8 == codeType.intValue()) aliSMSUtils.sendSms(phone, randomCode, ConstantAli.SmsType.SUPPLIER_ACTIVATE_OK_CODE);//供应商激活申请成功短信通知内容
		}catch (GlobalException e){
			logger.error("手机验证码发送失败",e);
			throw new GlobalException(e.getErrorCode(),e.getMessage());
		}catch (Exception e){
			logger.error("手机验证码发送失败",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
		}
	}




    /**
     * 	注册验证数据
     * @param user
     */
	public void dataMatch(User user) {
		//验证用户名
		/*if (username.equals(""))
			username = null;*/
		if (StringUtils.isNotBlank(user.getUsername())) {
			if ( ValidatorUtil.isUsername(user.getUsername()) ) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"用户名格式错误:不能使用中文注册");
			}
		}

		//验证密码
		if ( StringUtils.isNotBlank(user.getPassword())) {
			if (ValidatorUtil.isPassword(user.getPassword()) != true) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "密码格式错误：6-16位数字和字母字符组合,特殊字符不包括（）符号");
			}
		}
		
		//验证手机
		if (StringUtils.isNotBlank(user.getPhone())) {
			if (!ValidatorUtil.isMobile(user.getPhone())) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "手机格式错误：以1开头，后面9位数字");
			}
		}

		//验证邮箱
		if (StringUtils.isNotBlank(user.getEmail())) {
			if (ValidatorUtil.isEmail(user.getEmail()) != true) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "邮箱格式错误，请重新输入");
			}
		}

		//验证腾讯qq
		if (StringUtils.isNotBlank(user.getQq())){
			if ( !ValidatorUtil.isQq(user.getQq()) ) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "QQ格式错误，请重新输入");
			}
		}

		//校验邮编
		if (StringUtils.isNotBlank(user.getPostcode())){
			if (!ValidatorUtil.isPostcode(user.getPostcode())) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "联系人邮编格式错误，请重新输入");
			}
		}

	}

	 /**
	   * 根据当前用户信息生成token  pc端
	   * @param result
	   * @return
	   */
	  public String getPcUserToken(UserAll result) {
	    StringBuilder builder = new StringBuilder();
	    builder.append(UserConstants.REQUEST_HEADER_NAME).append("-");
	    builder.append(result.getUser().getUserid()).append("-");
	    builder.append(result.getUser().getUsername()).append("-");
	    builder.append(result.getUser().getPlatformType()).append("-");
	    logger.info("用户基础信息生成token====>"+builder.toString());
	    String token = MD5.md5Password(builder.toString());
	    token = token + "-"
	    +"PC"+"-" + String.valueOf(System.currentTimeMillis());
	    return token;
	  }

	/**
	 * 根据当前用户信息生成token  App端
	 * @param result
	 * @return
	 */
	public String getAppUserToken(UserAll result) {
		StringBuilder builder = new StringBuilder();
		builder.append(UserConstants.REQUEST_HEADER_NAME).append("-");
		builder.append(result.getUser().getUserid()).append("-");
		builder.append(result.getUser().getUsername()).append("-");
		builder.append(result.getUser().getPlatformType()).append("-");
		logger.info("用户基础信息生成token====>"+builder.toString());
		String token = MD5.md5Password(builder.toString());
		token = token + "-"
				+"APP"+"-" + String.valueOf(System.currentTimeMillis());
		return token;
	}

	/**
	 * 只生成用户信息的MD5散列值，作判断使用
	 * @param result
	 * @return
	 */
	public String getUserMD5(UserAll result) {
		StringBuilder builder = new StringBuilder();
		builder.append(UserConstants.REQUEST_HEADER_NAME).append("-");
		builder.append(result.getUser().getUserid()).append("-");
		builder.append(result.getUser().getUsername()).append("-");
		builder.append(result.getUser().getPlatformType()).append("-");
		logger.info("用户基础信息生成token====>"+builder.toString());
		String token = MD5.md5Password(builder.toString());
		return token;
	}

	
	/**
	 * 根据当前信息删除当前用户的token
	 * @param user
	 * @return
	 */
	public boolean deleteUserToken01(User user) {
		String userToken = UserConstants.REQUEST_HEADER_NAME+"-"
				+user.getUserid()+"-"
				+user.getUsername()+"-"
				+user.getPlatformType();
		String token = getBase64(userToken);
		if ( redisUtils.exists(UserConstants.REDIS_USER_KEY_fix+token)) {
			redisUtils.remove(UserConstants.REDIS_USER_KEY_fix+token);
			if ( redisUtils.exists(UserConstants.REDIS_USER_KEY_fix+token) == false ) {
				return true;
			}else {
				return false;
			}
		}
		return false;
	}

	/**
	 * 进行base64编码
	 * @param str
	 * @return
	 */
	public String getBase64(String str) {

		String aaString = str;
		String encode = null;
		try {
			encode = new Base64().encodeAsString(aaString.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encode;
	}

}
