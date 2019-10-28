package com.rondaful.cloud.supplier.model.dto.storage;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/6/18
 * @Description:
 */
public class SmCodeDTO implements Serializable {

    private static final long serialVersionUID = -908964690481072280L;
    @ApiModelProperty(value = "服务方式编号")
    private String smCode;
    @ApiModelProperty(value = "服务方式名称")
    private String smCodeName;

    public SmCodeDTO(){}

    public SmCodeDTO(String smCode, String smCodeName) {
        this.smCode = smCode;
        this.smCodeName = smCodeName;
    }

    public String getSmCode() {
        return smCode;
    }

    public void setSmCode(String smCode) {
        this.smCode = smCode;
    }

    public String getSmCodeName() {
        return smCodeName;
    }

    public void setSmCodeName(String smCodeName) {
        this.smCodeName = smCodeName;
    }
}
