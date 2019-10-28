package com.rondaful.cloud.order.entity.goodcang;

import io.swagger.annotations.ApiModelProperty;

public class GoodCangFeeDetails {

    @ApiModelProperty(value = "总费用")
    private Float totalFee;

    @ApiModelProperty(value = "运输费")
    private Float SHIPPING;

    @ApiModelProperty(value = "操作费用")
    private Float OPF;

    @ApiModelProperty(value = "燃油附加费")
    private Float FSC;

    @ApiModelProperty(value = "关税")
    private Float DT;

    @ApiModelProperty(value = "挂号")
    private Float RSF;

    @ApiModelProperty(value = "其它费用")
    private Float OTF;

    @ApiModelProperty(value = "其它费用")
    private String currencyCode;

    public Float getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Float totalFee) {
        this.totalFee = totalFee;
    }

    public Float getSHIPPING() {
        return SHIPPING;
    }

    public void setSHIPPING(Float SHIPPING) {
        this.SHIPPING = SHIPPING;
    }

    public Float getOPF() {
        return OPF;
    }

    public void setOPF(Float OPF) {
        this.OPF = OPF;
    }

    public Float getFSC() {
        return FSC;
    }

    public void setFSC(Float FSC) {
        this.FSC = FSC;
    }

    public Float getDT() {
        return DT;
    }

    public void setDT(Float DT) {
        this.DT = DT;
    }

    public Float getRSF() {
        return RSF;
    }

    public void setRSF(Float RSF) {
        this.RSF = RSF;
    }

    public Float getOTF() {
        return OTF;
    }

    public void setOTF(Float OTF) {
        this.OTF = OTF;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}
