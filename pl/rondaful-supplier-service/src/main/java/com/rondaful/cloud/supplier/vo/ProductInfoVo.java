package com.rondaful.cloud.supplier.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 商品明细 VO
 *
 * @ClassName ProductInfoVo
 * @Author tianye
 * @Date 2019/4/26 16:16
 * @Version 1.0
 */
public class ProductInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品SKU")
    private String systemSku;

    @ApiModelProperty(value = "商品中文名称")
    private String commodityNameCn;

    @ApiModelProperty(value = "商品英文名称")
    private String commodityNameEn;

    @ApiModelProperty(value = "箱号")
    private Integer boxNo;

    @ApiModelProperty(value = "发货数量")
    private Long quantityShipped;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getSystemSku() {
        return systemSku;
    }

    public void setSystemSku(String systemSku) {
        this.systemSku = systemSku;
    }

    public String getCommodityNameCn() {
        return commodityNameCn;
    }

    public void setCommodityNameCn(String commodityNameCn) {
        this.commodityNameCn = commodityNameCn;
    }

    public String getCommodityNameEn() {
        return commodityNameEn;
    }

    public void setCommodityNameEn(String commodityNameEn) {
        this.commodityNameEn = commodityNameEn;
    }

    public Integer getBoxNo() {
        return boxNo;
    }

    public void setBoxNo(Integer boxNo) {
        this.boxNo = boxNo;
    }

    public Long getQuantityShipped() {
        return quantityShipped;
    }

    public void setQuantityShipped(Long quantityShipped) {
        this.quantityShipped = quantityShipped;
    }
}
