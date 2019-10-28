package com.rondaful.cloud.order.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @ProjectName: Rondaful
 * @Package: com.rondaful.cloud.order.entity
 * @ClassName: judgeAfterSaleDTO
 * @Author: Superhero
 * @Description:
 * @Date: 2019/9/2 18:41
 */
@ApiModel(value = "合并订单才有：判断售后数量")
public class JudgeAfterSaleDTO {
    @ApiModelProperty(value = "包裹号")
    private String packageId;
    @ApiModelProperty(value = "SKU")
    private String sku;
    @ApiModelProperty(value = "原订单该SKU的数量")
    private Long count;
    @ApiModelProperty(value = "进行过售后的数量")
    private Long afterAount;
    @ApiModelProperty(value = "原订单ID")
    private String sysOrderId;
    @ApiModelProperty(value = "该订单该SKU是否进行过售后")
    private boolean isAfterSale;

    public JudgeAfterSaleDTO() {
    }

    public JudgeAfterSaleDTO(String packageId, String sku, Long count, Long afterAount, String sysOrderId, boolean isAfterSale) {
        this.packageId = packageId;
        this.sku = sku;
        this.count = count;
        this.afterAount = afterAount;
        this.sysOrderId = sysOrderId;
        this.isAfterSale = isAfterSale;
    }

    @Override
    public String toString() {
        return "JudgeAfterSaleDTO{" +
                "packageId='" + packageId + '\'' +
                ", sku='" + sku + '\'' +
                ", count=" + count +
                ", afterAount=" + afterAount +
                ", sysOrderId='" + sysOrderId + '\'' +
                ", isAfterSale=" + isAfterSale +
                '}';
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getAfterAount() {
        return afterAount;
    }

    public void setAfterAount(Long afterAount) {
        this.afterAount = afterAount;
    }

    public String getSysOrderId() {
        return sysOrderId;
    }

    public void setSysOrderId(String sysOrderId) {
        this.sysOrderId = sysOrderId;
    }

    public boolean isAfterSale() {
        return isAfterSale;
    }

    public void setAfterSale(boolean afterSale) {
        isAfterSale = afterSale;
    }
}
