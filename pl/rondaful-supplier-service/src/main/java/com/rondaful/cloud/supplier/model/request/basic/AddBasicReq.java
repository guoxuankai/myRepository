package com.rondaful.cloud.supplier.model.request.basic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/6/11
 * @Description:
 */
@ApiModel(value = "新增仓库实体类")
public class AddBasicReq implements Serializable {
    private static final long serialVersionUID = 2220152721016671969L;

    @ApiModelProperty(value = "仓库服务商名称code:RONDAFUL:利郎达 GOODCANG:谷仓  WMS:云仓",dataType = "String",hidden = true)
    private String firmCode;

    @ApiModelProperty(value = "归属供应商id：0(通用) ",dataType = "Integer")
    private Integer supplierId;

    @ApiModelProperty(value = "自定义标识",dataType = "String")
    private String name;

    @ApiModelProperty(value = "仓储账号id",dataType = "Integer")
    private Integer logisticsUserId;

    @ApiModelProperty(value = "应用标识",dataType = "String")
    private String appToken;

    @ApiModelProperty(value = "密钥",dataType = "String")
    private String appKey;

    public String getFirmCode() {
        return firmCode;
    }

    public void setFirmCode(String firmCode) {
        this.firmCode = firmCode;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLogisticsUserId() {
        return logisticsUserId;
    }

    public void setLogisticsUserId(Integer logisticsUserId) {
        this.logisticsUserId = logisticsUserId;
    }

    @Override
    public String toString() {
        return "AddBasicReq{" +
                "firmCode='" + firmCode + '\'' +
                ", supplierId=" + supplierId +
                ", name='" + name + '\'' +
                ", appToken='" + appToken + '\'' +
                ", appKey='" + appKey + '\'' +
                '}';
    }
}
