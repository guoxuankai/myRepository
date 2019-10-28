package com.rondaful.cloud.supplier.model.dto.procurement;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * @Author: xqq
 * @Date: 2019/6/20
 * @Description:
 */
@ApiModel(value = "明细实体返回类")
public class ProcurementListDeatilDTO extends ProcurementListDTO {
    private static final long serialVersionUID = 8274859122955806709L;

    @ApiModelProperty(value = "图片")
    private String pictureUrl;

    @ApiModelProperty(value = "供应商sku")
    private String supplierSku;

    @ApiModelProperty(value = "产品名称")
    private String commodityName;

    @ApiModelProperty(value = "可用库存")
    private Integer availableQty;


    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getSupplierSku() {
        return supplierSku;
    }

    public void setSupplierSku(String supplierSku) {
        this.supplierSku = supplierSku;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public Integer getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(Integer availableQty) {
        this.availableQty = availableQty;
    }

}
