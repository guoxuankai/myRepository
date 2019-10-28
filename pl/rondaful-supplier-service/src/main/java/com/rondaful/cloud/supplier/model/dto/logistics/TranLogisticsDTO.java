package com.rondaful.cloud.supplier.model.dto.logistics;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/10/17
 * @Description:
 */
public class TranLogisticsDTO implements Serializable {
    private static final long serialVersionUID = 986334910290394302L;

    @ApiModelProperty(value = "仓库id")
    private String warehouseCode;

    @ApiModelProperty(value = "运输方式代码")
    private String code;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "运输方式中文名称")
    private String name;

    private String nameEn;

    @ApiModelProperty(value = "服务商代码")
    private String spCode;

    @ApiModelProperty(value = "服务商名称")
    private String spName;

    private String spNameEn;

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getSpCode() {
        return spCode;
    }

    public void setSpCode(String spCode) {
        this.spCode = spCode;
    }

    public String getSpName() {
        return spName;
    }

    public void setSpName(String spName) {
        this.spName = spName;
    }

    public String getSpNameEn() {
        return spNameEn;
    }

    public void setSpNameEn(String spNameEn) {
        this.spNameEn = spNameEn;
    }
}
