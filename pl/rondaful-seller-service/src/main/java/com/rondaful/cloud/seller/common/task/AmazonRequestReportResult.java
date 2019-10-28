package com.rondaful.cloud.seller.common.task;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 报告返回对象
 */
public class AmazonRequestReportResult implements Serializable {

    /** resultId:当请求不同接口时返回不同的ID  */
    private String resultId;

    /** sourceId :原来被传入的ID */
    private String sourceId;

    /** 报告状态 _DONE_ 为已准备好 */
    private String reportProcessingStatus;

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

    @ApiModelProperty(value = "亚马逊报告开始处理时间")
    private String beginTime;

    @ApiModelProperty(value = "亚马逊报告结束处理时间")
    private String endTime;

    public AmazonRequestReportResult() {
    }

    public AmazonRequestReportResult(String errorType, String errorCode, String resultDescription, Integer httErrorCode) {
        this.errorType = errorType;
        ErrorCode = errorCode;
        this.resultDescription = resultDescription;
        this.httErrorCode = httErrorCode;
    }

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getReportProcessingStatus() {
        return reportProcessingStatus;
    }

    public void setReportProcessingStatus(String reportProcessingStatus) {
        this.reportProcessingStatus = reportProcessingStatus;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
