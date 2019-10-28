package com.brandslink.cloud.user.dto.response;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 客户/货主信息
 *
 * @ClassName CustomerShipperDetailResponseDTO
 * @Author tianye
 * @Date 2019/7/19 14:17
 * @Version 1.0
 */
public class CustomerShipperDetailResponseDTO implements Serializable {

    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @ApiModelProperty(value = "中文名称")
    private String chineseName;

    @ApiModelProperty(value = "货主名称")
    private String shipperName;

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }
}
