package com.rondaful.cloud.seller.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EbayConfig {

	@Value("${ebay.appid}")
	private String appid;

	@Value("${ebay.developer}")
	private String developer;

	@Value("${ebay.cert}")
	private String cert;

	@Value("${ebay.ruName}")
	private String ruName;

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getDeveloper() {
		return developer;
	}

	public void setDeveloper(String developer) {
		this.developer = developer;
	}

	public String getCert() {
		return cert;
	}

	public void setCert(String cert) {
		this.cert = cert;
	}

	public String getRuName() {
		return ruName;
	}

	public void setRuName(String ruName) {
		this.ruName = ruName;
	}
}
