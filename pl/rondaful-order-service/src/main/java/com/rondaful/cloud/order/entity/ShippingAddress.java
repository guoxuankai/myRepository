package com.rondaful.cloud.order.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ShippingAddress {

    @ApiModelProperty(value = "收货人姓名")
    private String shipToName;

    @ApiModelProperty(value = "收货目的地/国家名")
    private String shipToCountryName;

    @ApiModelProperty(value = "收货国家双字母代码")
    private String shipToCountryCode;

    @ApiModelProperty(value = "收货省/州名")
    private String shipToState;

    @ApiModelProperty(value = "收货城市")
    private String shipToCity;

    @ApiModelProperty(value = "收货地址1")
    private String shipToAddrStreet1;

    @ApiModelProperty(value = "收货地址2")
    private String shipToAddrStreet2;

    @ApiModelProperty(value = "收货地址3")
    private String shipToAddrStreet3;

    @ApiModelProperty(value = "收货邮编")
    private String shipToPostalCode;

    @ApiModelProperty(value = "收货人电话")
    private String shipToPhone;

    @ApiModelProperty(value = "收货人email")
    private String shipToEmail;

    public String getShipToName() {
        return shipToName;
    }

    public void setShipToName(String shipToName) {
        this.shipToName = shipToName;
    }

    public String getShipToCountryName() {
        return shipToCountryName;
    }

    public void setShipToCountryName(String shipToCountryName) {
        this.shipToCountryName = shipToCountryName;
    }

    public String getShipToCountryCode() {
        return shipToCountryCode;
    }

    public void setShipToCountryCode(String shipToCountryCode) {
        this.shipToCountryCode = shipToCountryCode;
    }

    public String getShipToState() {
        return shipToState;
    }

    public void setShipToState(String shipToState) {
        this.shipToState = shipToState;
    }

    public String getShipToCity() {
        return shipToCity;
    }

    public void setShipToCity(String shipToCity) {
        this.shipToCity = shipToCity;
    }

    public String getShipToAddrStreet1() {
        return shipToAddrStreet1;
    }

    public void setShipToAddrStreet1(String shipToAddrStreet1) {
        this.shipToAddrStreet1 = shipToAddrStreet1;
    }

    public String getShipToAddrStreet2() {
        return shipToAddrStreet2;
    }

    public void setShipToAddrStreet2(String shipToAddrStreet2) {
        this.shipToAddrStreet2 = shipToAddrStreet2;
    }

    public String getShipToAddrStreet3() {
        return shipToAddrStreet3;
    }

    public void setShipToAddrStreet3(String shipToAddrStreet3) {
        this.shipToAddrStreet3 = shipToAddrStreet3;
    }

    public String getShipToPostalCode() {
        return shipToPostalCode;
    }

    public void setShipToPostalCode(String shipToPostalCode) {
        this.shipToPostalCode = shipToPostalCode;
    }

    public String getShipToPhone() {
        return shipToPhone;
    }

    public void setShipToPhone(String shipToPhone) {
        this.shipToPhone = shipToPhone;
    }

    public String getShipToEmail() {
        return shipToEmail;
    }

    public void setShipToEmail(String shipToEmail) {
        this.shipToEmail = shipToEmail;
    }
}
