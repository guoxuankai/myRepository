package com.brandslink.cloud.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AliConfig {

	@Value("${ali_oos_endpoint}")
	public String endpoint;

	@Value("${ali_oos_accessKeyId}")
	public String accessKeyId;

	@Value("${ali_oos_accessKeySecret}")
	public String accessKeySecret;

	@Value("${ali_sms_endpoint}")
	public String smsEndpoint;

	public String getEndpoint() {
		return endpoint;
	}

	public String getAccessKeyId() {
		return accessKeyId;
	}

	public String getAccessKeySecret() {
		return accessKeySecret;
	}

	public String getSmsEndpoint() {
		return smsEndpoint;
	}

}
