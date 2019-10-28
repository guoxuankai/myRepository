package com.rondaful.cloud.seller.common.task;

import java.io.Serializable;

import org.apache.http.HttpStatus;

/**
 * 	分析后的报告结果
 * @author ouxiangfeng
 *
 */
public class AmazonReportListResult implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9172980868474057481L;

	/** 消息id */
	private  Long messageId;

	/** 结果code,如：Error  */
	private String resultCode;
	
	/** 结果code 如：5000  */
	private  Long resultMessageCode;
	
	/**
	 * 	错误消息内容
	 */
	private String resultDescription;
	
	/** http/amazon 请求 错误号  */
	private Integer httErrorCode = -1;

	
	
	/** 卖家id */
	private String merchantId;
	
	/** 站点id */
	private String marketplaceId;
	
	private String feedSubmissionId;
	
	/**
	 * 	处理情况
	 */
	private Integer processStatys;
	
	public Long getMessageId() {
		return messageId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public Long getResultMessageCode() {
		return resultMessageCode;
	}

	public void setResultMessageCode(Long resultMessageCode) {
		this.resultMessageCode = resultMessageCode;
	}

	public String getResultDescription() {
		return resultDescription;
	}

	public void setResultDescription(String resultDescription) {
		this.resultDescription = resultDescription;
	}

	public Integer getHttErrorCode() {
		return httErrorCode;
	}

	public void setHttErrorCode(Integer httErrorCode) {
		this.httErrorCode = httErrorCode;
		if(httErrorCode == HttpStatus.SC_INTERNAL_SERVER_ERROR)
		{
			this.resultDescription = "amazon服务器错误";
			//logger.warn("amazon服务器错误",FeedSubmissionId);
			//return AmazonConstants.RESPORT_RESULT_UPLOADING;
		}else if(httErrorCode == HttpStatus.SC_SERVICE_UNAVAILABLE)
		{
			this.resultDescription = "请求太快，被amazom扼杀掉了。";
			//logger.warn("请求太快，被amazom扼杀掉了。.FeedSubmissionId:{}",FeedSubmissionId);
			//return AmazonConstants.RESPORT_RESULT_REQUESTTHROTTLED;
		}else if(httErrorCode == HttpStatus.SC_NOT_FOUND)
		{
			this.resultDescription = "未生成报告";
			//logger.warn("请求太快，被amazom扼杀掉了。.FeedSubmissionId:{}",FeedSubmissionId);
			//return AmazonConstants.RESPORT_RESULT_REQUESTTHROTTLED;
		}
		
		
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMarketplaceId() {
		return marketplaceId;
	}

	public void setMarketplaceId(String marketplaceId) {
		this.marketplaceId = marketplaceId;
	}

	public String getFeedSubmissionId() {
		return feedSubmissionId;
	}

	public void setFeedSubmissionId(String feedSubmissionId) {
		this.feedSubmissionId = feedSubmissionId;
	}

	public Integer getProcessStatys() {
		return processStatys;
	}

	public void setProcessStatys(Integer processStatys) {
		this.processStatys = processStatys;
	}
	
	
	
	
	
	
}

