package com.rondaful.cloud.order.entity.supplier;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 *  数据库返回对像
 * @author lxx
 * @date 2018-12-04 15:53:16
 */
@ExcelTarget("SkuSalesRecordExportDTO")
public class SkuSalesRecordExportDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    @Excel(name  = "单号")
    private String oddNumbers;

    @Excel(name  = "品连sku")
    private String sku;

    @Excel(name = "商品标题")
    private String skuTitle;

    @Excel(name = "采购单价/USD")
    private BigDecimal skuPrice;

    @Excel(name = "运费单价/USD")
    private BigDecimal supplierSkuPerShipFee;

    @Excel(name = "数量")
    private Integer skuQuantity;

    @Excel(name = "发货时间", format = "yyyy-MM-dd HH:mm:ss")
    private Date deliveryTime;

    @Excel(name  = "服务费/USD")
    private BigDecimal serviceCharge;


    @Excel(name = "商品总价/USD")
    private BigDecimal totalAmount;

    @Excel(name = "总运费/USD")
    private BigDecimal totalFreight;

    public String getOddNumbers() {
        return oddNumbers;
    }

    public void setOddNumbers(String oddNumbers) {
        this.oddNumbers = oddNumbers;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getSkuTitle() {
        return skuTitle;
    }

    public void setSkuTitle(String skuTitle) {
        this.skuTitle = skuTitle;
    }

    public BigDecimal getSkuPrice() {
        return skuPrice;
    }

    public void setSkuPrice(BigDecimal skuPrice) {
        this.skuPrice = skuPrice;
    }

    public BigDecimal getSupplierSkuPerShipFee() {
        return supplierSkuPerShipFee;
    }

    public void setSupplierSkuPerShipFee(BigDecimal supplierSkuPerShipFee) {
        this.supplierSkuPerShipFee = supplierSkuPerShipFee;
    }

    public Integer getSkuQuantity() {
        return skuQuantity;
    }

    public void setSkuQuantity(Integer skuQuantity) {
        this.skuQuantity = skuQuantity;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public BigDecimal getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(BigDecimal serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalFreight() {
        return totalFreight;
    }

    public void setTotalFreight(BigDecimal totalFreight) {
        this.totalFreight = totalFreight;
    }


}
