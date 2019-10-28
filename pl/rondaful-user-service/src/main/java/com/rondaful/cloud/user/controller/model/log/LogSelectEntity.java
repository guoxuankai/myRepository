package com.rondaful.cloud.user.controller.model.log;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value = "操作日志列表")
public class LogSelectEntity implements Serializable {

    private static final long serialVersionUID = -8241512847914648438L;

    @ApiModelProperty(value = "用户id",name = "userId",dataType = "Integer",required = true)
    private Integer userId;

    @ApiModelProperty(value = "用户名", name = "username", dataType = "string",required = false)
    private String username;

    @ApiModelProperty(value = "平台类型:0-供应商  1-卖家  2-后台管理",name = "platformType",dataType = "string",required = true)
    private String platformType;

    @ApiModelProperty(value = "起始创建时间",name = "createDate",dataType = "string",required = false)
    private String createDate;

    @ApiModelProperty(value = "结束创建时间",name = "closeDate",dataType = "string",required = false)
    private String closeDate;

    @ApiModelProperty(value = "指定页面",name = "currPage",dataType = "string",required = true)
    private String currPage;

    @ApiModelProperty(value = "页面行数",name = "row",dataType = "string",required = true)
    private String row;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(String closeDate) {
        this.closeDate = closeDate;
    }

    public String getCurrPage() {
        return currPage;
    }

    public void setCurrPage(String currPage) {
        this.currPage = currPage;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }
}
