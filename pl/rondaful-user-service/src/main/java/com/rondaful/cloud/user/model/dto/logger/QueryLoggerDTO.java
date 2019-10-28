package com.rondaful.cloud.user.model.dto.logger;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: xqq
 * @Date: 2019/6/12
 * @Description:
 */
public class QueryLoggerDTO implements Serializable {
    private static final long serialVersionUID = 7342820987290019804L;

    @ApiModelProperty(value = "登录名", name = "loginName", dataType = "string",required = false)
    private String loginName;

    @ApiModelProperty(value = "平台类型:0-供应商  1-卖家  2-后台管理",name = "platformType",dataType = "string",required = true)
    private Integer platformType;

    @ApiModelProperty(value = "起始创建时间",name = "createDate",dataType = "string",required = false)
    private Date startTime;

    @ApiModelProperty(value = "结束创建时间",name = "closeDate",dataType = "string",required = false)
    private Date endTime;

    @ApiModelProperty(value = "每页最多条数",name = "currPage",dataType = "string",required = true)
    private Integer pageSize;

    @ApiModelProperty(value = "当前页",name = "row",dataType = "string",required = true)
    private Integer currentPage;

    private String languageType;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Integer getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Integer platformType) {
        this.platformType = platformType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public String getLanguageType() {
        return languageType;
    }

    public void setLanguageType(String languageType) {
        this.languageType = languageType;
    }
}
