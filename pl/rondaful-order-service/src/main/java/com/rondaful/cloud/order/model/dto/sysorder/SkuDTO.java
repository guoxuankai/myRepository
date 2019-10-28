package com.rondaful.cloud.order.model.dto.sysorder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@ApiModel(value = "skuDTO")
@Data
public class SkuDTO  {
//    @ApiModelProperty(value = "订单项SKU")
//    private String sku;

    @ApiModelProperty(value = "卖家费用")
    private BigDecimal sellerShipFee;

    @ApiModelProperty(value = "供应商费用")
    private BigDecimal supplierShipFee;

    @ApiModelProperty(value = "物流商费用")
    private BigDecimal logisticCompanyShipFee;

    @ApiModelProperty(value = "是否包邮： 0,不包邮 1,包邮  ")
    private Integer freeFreight;

    public Integer getFreeFreight() {
        return freeFreight;
    }

    public void setFreeFreight(Integer freeFreight) {
        this.freeFreight = freeFreight;
    }

    public BigDecimal getSellerShipFee() {
        return sellerShipFee == null ? BigDecimal.ZERO : sellerShipFee;
    }

    public void setSellerShipFee(BigDecimal sellerShipFee) {
        this.sellerShipFee = sellerShipFee;
    }

    public BigDecimal getSupplierShipFee() {
        return supplierShipFee == null ? BigDecimal.ZERO : supplierShipFee;
    }

    public void setSupplierShipFee(BigDecimal supplierShipFee) {
        this.supplierShipFee = supplierShipFee;
    }

    public BigDecimal getLogisticCompanyShipFee() {
        return logisticCompanyShipFee == null ? BigDecimal.ZERO : logisticCompanyShipFee;
    }

    public void setLogisticCompanyShipFee(BigDecimal logisticCompanyShipFee) {
        this.logisticCompanyShipFee = logisticCompanyShipFee;
    }
}