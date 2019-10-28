package com.rondaful.cloud.supplier.model.dto.basics;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/6/11
 * @Description:
 */
public class WarehouseFirmDTO implements Serializable {
    private static final long serialVersionUID = -6351539033911347459L;

    @ApiModelProperty(value = "")
    private Integer id;

    @ApiModelProperty(value = "仓库服务商名称code:RONDAFUL:利郎达 GOODCANG:谷仓")
    private String firmCode;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "归属供应商id：0(通用)  ")
    private Integer supplierId;

    @ApiModelProperty(value = "自定义标识")
    private String name;

    @ApiModelProperty(value = "应用标识")
    private String appToken;

    @ApiModelProperty(value = "密钥")
    private String appKey;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "供应链公司id")
    private Integer supplyId;

    @ApiModelProperty(value = "仓储用户")
    private Integer logisticsUserId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirmCode() {
        return firmCode;
    }

    public void setFirmCode(String firmCode) {
        this.firmCode = firmCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSupplyId() {
        return supplyId;
    }

    public void setSupplyId(Integer supplyId) {
        this.supplyId = supplyId;
    }

    public Integer getLogisticsUserId() {
        return logisticsUserId;
    }

    public void setLogisticsUserId(Integer logisticsUserId) {
        this.logisticsUserId = logisticsUserId;
    }

    @Override
    public String toString() {
        return "WarehouseFirmDTO{" +
                "id=" + id +
                ", firmCode='" + firmCode + '\'' +
                ", status=" + status +
                ", supplierId=" + supplierId +
                ", appToken='" + appToken + '\'' +
                ", appKey='" + appKey + '\'' +
                ", createBy='" + createBy + '\'' +
                ", updateBy='" + updateBy + '\'' +
                '}';
    }
}
