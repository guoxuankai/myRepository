package com.rondaful.cloud.seller.entity.amazon;

/**
 *	 批量刊登返回的结果集
 * @author ouxiangfeng
 *
 */
public class AmazonQueryLoadTaskResult implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5171584053010759623L;

	//listingids,pl_account,merchant_identifier,publish_site,publish_account
	/** id集合，以逗号分隔 */
	private String listingIds;
	
	/** 品连账号 */
	private String plAccount;
	
	/** 刊登账号 */
	private String publishAccount;
	
	/** 卖家ID */
	private String merchantIdentifier;
	
	/** 站点 */
	private String publishSite;

 
	/** 刊登的授权token */
	private String amwToken;
	
	/**
	 *	 刊登类型 2：单属性 1：多属性'
	 */
	private Integer publishType;
	
	
	
	public Integer getPublishType() {
		return publishType;
	}

	public void setPublishType(Integer publishType) {
		this.publishType = publishType;
	}

	public String getListingIds() {
		return listingIds;
	}

	public void setListingIds(String listingIds) {
		this.listingIds = listingIds;
	}

	public String getPlAccount() {
		return plAccount;
	}

	public void setPlAccount(String plAccount) {
		this.plAccount = plAccount;
	}

	public String getPublishAccount() {
		return publishAccount;
	}

	public void setPublishAccount(String publishAccount) {
		this.publishAccount = publishAccount;
	}

	public String getMerchantIdentifier() {
		return merchantIdentifier;
	}

	public void setMerchantIdentifier(String merchantIdentifier) {
		this.merchantIdentifier = merchantIdentifier;
	}

	public String getPublishSite() {
		return publishSite;
	}

	public void setPublishSite(String publishSite) {
		this.publishSite = publishSite;
	}

	public String getAmwToken() {
		return amwToken;
	}

	public void setAmwToken(String amwToken) {
		this.amwToken = amwToken;
	}
	
	
	

}
