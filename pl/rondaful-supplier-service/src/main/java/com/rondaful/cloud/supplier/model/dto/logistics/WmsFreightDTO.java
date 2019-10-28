package com.rondaful.cloud.supplier.model.dto.logistics;

import java.math.BigDecimal;
import java.util.Map;

public class WmsFreightDTO {

    //邮寄方式名称
    private String methodCnName;

    private String methodEnName;

    private Object supportPlatform;
    //邮寄方式编码
    private String methodCode;
    //国家编码
    private String country;
    //是否收取偏远费
    private String isRemoteFee;
    //不可发货物
    private Object unsupportCargo;
    //承诺到达天数
    private Object promiseDays;
    //重量范围
    private Object weightRange;
    //长度限制
    private Map<String,Object> limitLength;
    //打折前费用
    private BigDecimal prediscountFee;
    //打折后费用
    private BigDecimal discountedCharge;
    //处理费
    private BigDecimal handlingCharge;
    //总运费(物流费用)
    private BigDecimal totalFee;
    //附加费
    private BigDecimal afterChargeFee;
    //偏远费
    private BigDecimal remoteFee;
    //费用总和
    private BigDecimal allFee;
    //最快时效
    private Double minDay;
    //最慢时效
    private Double maxDay;

    public String getMethodCnName() {
        return methodCnName;
    }

    public void setMethodCnName(String methodCnName) {
        this.methodCnName = methodCnName;
    }

    public String getMethodEnName() {
        return methodEnName;
    }

    public void setMethodEnName(String methodEnName) {
        this.methodEnName = methodEnName;
    }

    public Object getSupportPlatform() {
        return supportPlatform;
    }

    public void setSupportPlatform(Object supportPlatform) {
        this.supportPlatform = supportPlatform;
    }

    public String getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(String methodCode) {
        this.methodCode = methodCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIsRemoteFee() {
        return isRemoteFee;
    }

    public void setIsRemoteFee(String isRemoteFee) {
        this.isRemoteFee = isRemoteFee;
    }

    public Object getUnsupportCargo() {
        return unsupportCargo;
    }

    public void setUnsupportCargo(Object unsupportCargo) {
        this.unsupportCargo = unsupportCargo;
    }

    public Object getPromiseDays() {
        return promiseDays;
    }

    public void setPromiseDays(Object promiseDays) {
        this.promiseDays = promiseDays;
    }

    public Object getWeightRange() {
        return weightRange;
    }

    public void setWeightRange(Object weightRange) {
        this.weightRange = weightRange;
    }

    public Map<String, Object> getLimitLength() {
        return limitLength;
    }

    public void setLimitLength(Map<String, Object> limitLength) {
        this.limitLength = limitLength;
    }

    public BigDecimal getPrediscountFee() {
        return prediscountFee;
    }

    public void setPrediscountFee(BigDecimal prediscountFee) {
        this.prediscountFee = prediscountFee;
    }

    public BigDecimal getDiscountedCharge() {
        return discountedCharge;
    }

    public void setDiscountedCharge(BigDecimal discountedCharge) {
        this.discountedCharge = discountedCharge;
    }

    public BigDecimal getHandlingCharge() {
        return handlingCharge;
    }

    public void setHandlingCharge(BigDecimal handlingCharge) {
        this.handlingCharge = handlingCharge;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public BigDecimal getAfterChargeFee() {
        return afterChargeFee;
    }

    public void setAfterChargeFee(BigDecimal afterChargeFee) {
        this.afterChargeFee = afterChargeFee;
    }

    public BigDecimal getRemoteFee() {
        return remoteFee;
    }

    public void setRemoteFee(BigDecimal remoteFee) {
        this.remoteFee = remoteFee;
    }

    public BigDecimal getAllFee() {
        return allFee;
    }

    public void setAllFee(BigDecimal allFee) {
        this.allFee = allFee;
    }

    public Double getMinDay() {
        return minDay;
    }

    public void setMinDay(Double minDay) {
        this.minDay = minDay;
    }

    public Double getMaxDay() {
        return maxDay;
    }

    public void setMaxDay(Double maxDay) {
        this.maxDay = maxDay;
    }

    @Override
    public String toString() {
        return "WmsFreightDTO{" +
                "methodCnName='" + methodCnName + '\'' +
                ", methodEnName='" + methodEnName + '\'' +
                ", supportPlatform=" + supportPlatform +
                ", methodCode='" + methodCode + '\'' +
                ", country='" + country + '\'' +
                ", isRemoteFee='" + isRemoteFee + '\'' +
                ", unsupportCargo=" + unsupportCargo +
                ", promiseDays=" + promiseDays +
                ", weightRange=" + weightRange +
                ", limitLength=" + limitLength +
                ", prediscountFee=" + prediscountFee +
                ", discountedCharge=" + discountedCharge +
                ", handlingCharge=" + handlingCharge +
                ", totalFee=" + totalFee +
                ", afterChargeFee=" + afterChargeFee +
                ", remoteFee=" + remoteFee +
                ", allFee=" + allFee +
                ", minDay=" + minDay +
                ", maxDay=" + maxDay +
                '}';
    }
}
