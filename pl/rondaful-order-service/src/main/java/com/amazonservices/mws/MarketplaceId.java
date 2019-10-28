package com.amazonservices.mws;

public class MarketplaceId {

	public MarketplaceId() {
		// TODO Auto-generated constructor stub
	}
	/** 国家名称 */
	private String countryName;
	
	/** 国家代码 */
	private String countryCode;
	
	/** 亚马逊 MWS 端点 */
	private String uri;
	
	private String MarketplaceId;

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getMarketplaceId() {
		return MarketplaceId;
	}

	public void setMarketplaceId(String marketplaceId) {
		MarketplaceId = marketplaceId;
	}
	

}
