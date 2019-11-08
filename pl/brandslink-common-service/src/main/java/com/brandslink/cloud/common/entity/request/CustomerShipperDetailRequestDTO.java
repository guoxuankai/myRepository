package com.brandslink.cloud.common.entity.request;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 客户/货主编码
 *
 * @ClassName CustomerShipperDetailResponseDTO
 * @Author tianye
 * @Date 2019/7/19 14:17
 * @Version 1.0
 */
public class CustomerShipperDetailRequestDTO implements Serializable {

    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @ApiModelProperty(value = "货主编码")
    private String shipperCode;

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getShipperCode() {
        return shipperCode;
    }

    public void setShipperCode(String shipperCode) {
        this.shipperCode = shipperCode;
    }
}
