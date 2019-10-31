package com.brandslink.cloud.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.brandslink.cloud.common.config.AliConfig;
import com.brandslink.cloud.common.constant.ConstantAli;
import com.brandslink.cloud.common.rabbitmq.SmsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 阿里云短信发送
 */
@Component
public class AliSMSUtils {

	private static final Logger logger = LoggerFactory.getLogger(AliSMSUtils.class);

	public final static String product = "Dysmsapi";
	public final static String domain = "dysmsapi.aliyuncs.com";

	@Resource
	private AliConfig aliConfig;

	@Resource
	private SmsSender smsSender;

	/**
	 * 验证码发送，单参数
	 *
	 * @param phoneNumber 手机号码
	 * @param code        验证码
	 * @param type        发送验证码类型
	 * @return
	 */
	public void sendSms(String phoneNumber, String code, ConstantAli.SmsType type) {
		JSONObject json = new JSONObject();
		json.put("code", code);
		send(phoneNumber, json, type);
	}

	/**
	 * 验证码发送，多参数 如带验证码，将验证码key设置为code
	 *
	 * @param phoneNumber 手机号码
	 * @param json        key对应短信模板key
	 * @param type        发送验证码类型
	 * @return
	 */
	public void sendSms(String phoneNumber, JSONObject json, ConstantAli.SmsType type) {
		send(phoneNumber, json, type);
	}

	private void send(String phoneNumber, JSONObject json, ConstantAli.SmsType type) {
		IClientProfile profile = DefaultProfile.getProfile(aliConfig.getEndpoint(), aliConfig.getAccessKeyId(), aliConfig.getAccessKeySecret());
		SendSmsResponse sendSmsResponse = null;
		try {
			DefaultProfile.addEndpoint(aliConfig.getEndpoint(), aliConfig.getEndpoint(), product, domain);
			IAcsClient client = new DefaultAcsClient(profile);
			SendSmsRequest request = new SendSmsRequest();
			request.setMethod(MethodType.POST);
			request.setPhoneNumbers(phoneNumber);
			request.setSignName("品连优选"); // 短信签名
			request.setTemplateCode(type.getType());
			request.setTemplateParam(json.toJSONString());
			sendSmsResponse = client.getAcsResponse(request);
		} catch (ClientException e) {
			logger.error("短信发送失败!", e);
		}
		JSONObject message = JSONObject.parseObject(JSON.toJSONString(sendSmsResponse));
		message.put("phone", phoneNumber);
		message.put("type", type.getDescription());
		message.put("validationCode", json.containsKey("code") ? json.getString("code") : "多参数接口,没带Code参数!");
		smsSender.sendSms(message.toJSONString());
	}

}
