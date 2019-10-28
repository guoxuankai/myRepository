package com.rondaful.cloud.supplier.model.dto.basics;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/6/14
 * @Description:
 */
public class WarehouseInitDTO implements Serializable {
    private static final long serialVersionUID = -7461430482231351414L;

    private String appToken;

    private String appKey;

    @ApiModelProperty(value = "仓库服务商名称code:RONDAFUL:利郎达 GOODCANG:谷仓")
    private String firmCode;

    private String name;

    private List<WarehouseListDTO> list;

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

    public String getFirmCode() {
        return firmCode;
    }

    public void setFirmCode(String firmCode) {
        this.firmCode = firmCode;
    }

    public List<WarehouseListDTO> getList() {
        return list;
    }

    public void setList(List<WarehouseListDTO> list) {
        this.list = list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
