package com.rondaful.cloud.seller.common.task;

import java.io.Serializable;

import org.apache.http.HttpStatus;

/**
 * 	分析后的报告结果
 * @author ouxiangfeng
 *
 */
public class AmazonSubmitFeedResult implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9172980868474057481L;

	/** feedSubmissionId  */
	private  String feedSubmissionId;

	/** 结果code,如：Error  */
	private String errorType;
	
	private String ErrorCode;
	
	/**
	 * 	错误消息内容
	 */
	private String resultDescription;
	
	/** http/amazon 请求 错误号  */
	private Integer httErrorCode = -1;

	/** 文件路径 */
	private String filePath;

	
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFeedSubmissionId() {
		return feedSubmissionId;
	}

	public void setFeedSubmissionId(String feedSubmissionId) {
		this.feedSubmissionId = feedSubmissionId;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public String getErrorCode() {
		return ErrorCode;
	}

	public void setErrorCode(String errorCode) {
		ErrorCode = errorCode;
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
	}


	
	
	
	
	
}
