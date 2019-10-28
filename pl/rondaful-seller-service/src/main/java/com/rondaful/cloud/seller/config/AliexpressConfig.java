package com.rondaful.cloud.seller.config;

import com.qimencloud.api.DefaultQimenCloudClient;
import com.taobao.api.DefaultTaobaoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AliexpressConfig {

	@Value("${seller.taobao.app-key}")
	private String appKey;
	@Value("${seller.taobao.app-secret}")
	private String appSecret;
	@Value("${seller.taobao.url}")
	private String url;
	//访问中转项目地址
	@Value("${seller.taobao.aliexpress-url}")
	private String aliexpressUrl;

	@Value("${seller.taobao.gateway-url}")
	private String gatewayUrl;


//	@Bean
//	public DefaultTaobaoClient client(){
//		DefaultQimenCloudClient client = new DefaultQimenCloudClient(this.getUrl(), this.getAppKey(), this.getAppSecret());
//		client.setIgnoreSSLCheck(true);
//		return client;
//	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAliexpressUrl() {
		return aliexpressUrl;
	}

	public void setAliexpressUrl(String aliexpressUrl) {
		this.aliexpressUrl = aliexpressUrl;
	}

	public String getGatewayUrl() {
		return gatewayUrl;
	}

	public void setGatewayUrl(String gatewayUrl) {
		this.gatewayUrl = gatewayUrl;
	}
}
