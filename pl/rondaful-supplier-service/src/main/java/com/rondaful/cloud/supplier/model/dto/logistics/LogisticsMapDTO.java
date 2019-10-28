package com.rondaful.cloud.supplier.model.dto.logistics;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/10/16
 * @Description:
 */
public class LogisticsMapDTO implements Serializable {
    private static final long serialVersionUID = 3949647998033810123L;

    @ApiModelProperty(value = "")
    private Integer id;

    @ApiModelProperty(value = "物流方式id")
    private Integer logisticsId;

    @ApiModelProperty(value = "平台类型")
    private Integer platform;

    @ApiModelProperty(value = "平台物流商")
    private String platformLogistics;

    @ApiModelProperty(value = "平台物流方式")
    private String platformLogisticsService;

    @ApiModelProperty(value = "回传字段 1:物流商单号 2:跟踪号")
    private Integer backField;

    @ApiModelProperty(value = "回传网址")
    private String backUrl;

    @ApiModelProperty(value = "是否是线上物流")
    private Boolean isOnLine;

    @ApiModelProperty(value = "物流追踪号码校验规则，采用正则表达")
    private String trackingNoRegex;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLogisticsId() {
        return logisticsId;
    }

    public void setLogisticsId(Integer logisticsId) {
        this.logisticsId = logisticsId;
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public String getPlatformLogistics() {
        return platformLogistics;
    }

    public void setPlatformLogistics(String platformLogistics) {
        this.platformLogistics = platformLogistics;
    }

    public String getPlatformLogisticsService() {
        return platformLogisticsService;
    }

    public void setPlatformLogisticsService(String platformLogisticsService) {
        this.platformLogisticsService = platformLogisticsService;
    }

    public Integer getBackField() {
        return backField;
    }

    public void setBackField(Integer backField) {
        this.backField = backField;
    }

    public String getBackUrl() {
        return backUrl;
    }

    public void setBackUrl(String backUrl) {
        this.backUrl = backUrl;
    }


    public Boolean getIsOnLine() {
        return isOnLine;
    }

    public void setIsOnLine(Boolean isOnLine) {
        this.isOnLine = isOnLine;
    }

    public String getTrackingNoRegex() {
        return trackingNoRegex;
    }

    public void setTrackingNoRegex(String trackingNoRegex) {
        this.trackingNoRegex = trackingNoRegex;
    }
}
