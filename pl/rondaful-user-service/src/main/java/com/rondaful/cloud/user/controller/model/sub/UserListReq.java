package com.rondaful.cloud.user.controller.model.sub;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/3/22
 * @Description:
 */
@ApiModel(value = "查询子用户列表实体类")
public class UserListReq implements Serializable {
    private static final long serialVersionUID = -1098614042191652636L;

    @ApiModelProperty(value = "子账号的上一级，即：卖家，供应商，品联管理后台", name = "parentId", dataType = "string",required = true)
    private String parentId;
    @ApiModelProperty(value = "平台：卖家，供应商，品联管理后台", name = "platformType", dataType = "string",required = true)
    private String platformType;
    @ApiModelProperty(value = "账号的状态 ,需要数据：硬性条件1.父id  查询条件  1.账户状态，子账号名称", name = "status", dataType = "String")
    private String status;
    @ApiModelProperty(value = "账号名称", name = "userName", dataType = "string")
    private String userName;
    @ApiModelProperty(value = "数据页数", name = "currPage", dataType = "string",required = true)
    private String currPage;
    @ApiModelProperty(name = "row", value = "数据数量", dataType = "string",required = true)
    private String row;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    @Override
    public String toString() {
        return "UserListReq{" +
                "parentId='" + parentId + '\'' +
                ", platformType='" + platformType + '\'' +
                ", status='" + status + '\'' +
                ", userName='" + userName + '\'' +
                ", currPage='" + currPage + '\'' +
                ", row='" + row + '\'' +
                '}';
    }
}
