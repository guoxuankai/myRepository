package com.rondaful.cloud.seller.common.task;

import java.io.InputStream;
import java.util.List;

import com.rondaful.cloud.seller.utils.AmazonContentMD5;

public class LoadProductRequest implements java.io.Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 4711921485813394072L;

	/** 站点 */
	private String publishSite;
	
	/** 卖家ID */
	private String merchantIdentifier;
	
	
	/** xml  */
	private String feedSubmissionId;
	
	/** msg type */
	private String msgType;
	
 
	/** 刊登的授权token */
	private String amwToken;

	/** 刊登的内容 */
	private InputStream body;
	
	/** 刊登的子id集合  */
	private List<Long> subIds;
	
 


	public List<Long> getSubIds() {
		return subIds;
	}


	public void setSubIds(List<Long> subIds) {
		this.subIds = subIds;
	}


	public InputStream getBody() {
		return body;
	}


	public void setBody(String body) {
		this.body = AmazonContentMD5.toInputStream(body);
	}
	public void setBody(InputStream body) {
		this.body = body;
	}

	public String getPublishSite() {
		return publishSite;
	}


	public void setPublishSite(String publishSite) {
		this.publishSite = publishSite;
	}


	public String getMerchantIdentifier() {
		return merchantIdentifier;
	}


	public void setMerchantIdentifier(String merchantIdentifier) {
		this.merchantIdentifier = merchantIdentifier;
	}


	public String getFeedSubmissionId() {
		return feedSubmissionId;
	}


	public void setFeedSubmissionId(String feedSubmissionId) {
		this.feedSubmissionId = feedSubmissionId;
	}


	public String getMsgType() {
		return msgType;
	}


	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}


	public String getAmwToken() {
		return amwToken;
	}


	public void setAmwToken(String amwToken) {
		this.amwToken = amwToken;
	}
	
	
	
}
