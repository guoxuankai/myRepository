package com.rondaful.cloud.supplier.model.dto.logistics;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Author: xqq
 * @Date: 2019/10/18
 * @Description:
 */
public class LogisticsSelectDTO extends TranLogisticsCostDTO {
    private static final long serialVersionUID = 7366427007009549387L;

    @ApiModelProperty(value = "服务商代码")
    private String spCode;

    @ApiModelProperty(value = "服务商名称")
    private String spName;

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
}
