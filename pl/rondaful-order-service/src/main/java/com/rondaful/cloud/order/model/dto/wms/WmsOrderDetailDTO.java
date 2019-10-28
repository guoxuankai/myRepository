package com.rondaful.cloud.order.model.dto.wms;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 订单详情
 *
 * @author Blade
 * @date 2019-08-12 14:08:23
 **/
public class WmsOrderDetailDTO implements Serializable {
    private static final long serialVersionUID = 6690477939680386447L;

    @ApiModelProperty(value = "商品条码", required = true)
    private String sku;

    @ApiModelProperty(value = "数量", required = true)
    private long number;

    @ApiModelProperty(value = "品类")
    private String category;

    @ApiModelProperty(value = "商品质量[良品、次品、残次品]")
    private String quality;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "商品编码")
    private String sCode;

    @ApiModelProperty(value = "商品中文名", required = true)
    private String sName;

    @ApiModelProperty(value = "商品英文名")
    private String sEnglishName;

    @ApiModelProperty(value = "货主id", required = true)
    private String supplier;

    @ApiModelProperty(value = "商品最小单位")
    private String unit;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getsCode() {
        return sCode;
    }

    public void setsCode(String sCode) {
        this.sCode = sCode;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsEnglishName() {
        return sEnglishName;
    }

    public void setsEnglishName(String sEnglishName) {
        this.sEnglishName = sEnglishName;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
