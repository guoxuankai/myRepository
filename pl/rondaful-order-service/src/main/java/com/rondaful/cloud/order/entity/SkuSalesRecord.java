package com.rondaful.cloud.order.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * 
 * 实体类对应的数据表为：  tb_sku_sales_record
 * @author lxx
 * @date 2019-07-23 18:22:29
 */
@ApiModel(value ="SkuSalesRecord")
public class SkuSalesRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "系统订单号")
    private String sysOrderId;

    @ApiModelProperty(value = "订单跟踪号(包裹号)")
    private String orderTrackId;

    @ApiModelProperty(value = "品连sku")
    private String sku;

    @ApiModelProperty(value = "sku数量")
    private Integer skuQuantity;

    @ApiModelProperty(value = "商品标题")
    private String skuTitle;
    @ApiModelProperty(value = "商品英文标题")
    private String skuTitleEn;

    @ApiModelProperty(value = "商品URL")
    private String itemURL;

    @ApiModelProperty(value = "商品属性")
    private String itemAttr;

    @ApiModelProperty(value = "采购单价/USD(商品单价)")
    private BigDecimal skuPrice;

    @ApiModelProperty(value = "商品总价")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "卖家费用")
    private BigDecimal sellerShipFee;

    @ApiModelProperty(value = "供应商费用")
    private BigDecimal supplierShipFee;

    @ApiModelProperty(value = "物流商费用")
    private BigDecimal logisticCompanyShipFee;

    @ApiModelProperty(value = "卖家单个sku运费单价")
    private BigDecimal sellerSkuPerShipFee;

    @ApiModelProperty(value = "供应商单个sku运费单价")
    private BigDecimal supplierSkuPerShipFee;

    @ApiModelProperty(value = "物流商单个sku运费单价")
    private BigDecimal logisticCompanySkuPerShipFee;

    @ApiModelProperty(value = "发货时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deliveryTime;

    @ApiModelProperty(value = "发货仓库id")
    private Integer deliveryWarehouseId;

    @ApiModelProperty(value = "发货仓库名称")
    private String deliveryWarehouseName;

    @ApiModelProperty(value = "是否包邮。1-包邮，0-不包邮")
    private Integer freeFreight;

    @ApiModelProperty(value = "服务费/USD")
    private BigDecimal serviceCharge;

    @ApiModelProperty(value = "创建人")
    private String creater;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "修改人")
    private String modifier;

    @ApiModelProperty(value = "修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifiedTime;

    @ApiModelProperty(value = "供应商ID")
    private Integer supplierId;

    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    @ApiModelProperty(value = "卖家ID")
    private Integer sellerId;

    @ApiModelProperty(value = "卖家名称")
    private String sellerName;

    @ApiModelProperty(value = "仓库服务商名称")
    private String wserviceName;

    @ApiModelProperty(value = "发货开始时间")
    private String beginDate;

    @ApiModelProperty(value = "发货结束时间")
    private String endDate;

    @ApiModelProperty(value = "商品总数量")
    private Integer totalSkuQty;

    @ApiModelProperty(value = "商品总价格$")
    private BigDecimal totalSkuPrice;

    @ApiModelProperty(value = "总服务费$")
    private BigDecimal totalServiceCharge;


    /**
     * 主账号标识，0：主账号，1子账号
     */
    private Integer topFlag;


    /**
     * 仓库id列表
     */

    private List<String> wIds;


    /**
     * 供应商列表
     */
    private List<String> suppliers;

    /**
     * 仓库id列表
     */

    private List<Integer> warehouseIdList;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSysOrderId() {
        return sysOrderId;
    }

    public void setSysOrderId(String sysOrderId) {
        this.sysOrderId = sysOrderId == null ? null : sysOrderId.trim();
    }

    public String getOrderTrackId() {
        return orderTrackId;
    }

    public void setOrderTrackId(String orderTrackId) {
        this.orderTrackId = orderTrackId == null ? null : orderTrackId.trim();
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku == null ? null : sku.trim();
    }

    public Integer getSkuQuantity() {
        return skuQuantity;
    }

    public void setSkuQuantity(Integer skuQuantity) {
        this.skuQuantity = skuQuantity;
    }

    public String getSkuTitle() {
        return skuTitle;
    }

    public void setSkuTitle(String skuTitle) {
        this.skuTitle = skuTitle == null ? null : skuTitle.trim();
    }

    public String getSkuTitleEn() {
        return skuTitleEn;
    }

    public void setSkuTitleEn(String skuTitleEn) {
        this.skuTitleEn = skuTitleEn;
    }

    public String getItemURL() {
        return itemURL;
    }

    public void setItemURL(String itemURL) {
        this.itemURL = itemURL;
    }

    public String getItemAttr() {
        return itemAttr;
    }

    public void setItemAttr(String itemAttr) {
        this.itemAttr = itemAttr;
    }
    public BigDecimal getSkuPrice() {
        return skuPrice;
    }

    public void setSkuPrice(BigDecimal skuPrice) {
        this.skuPrice = skuPrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getSellerShipFee() {
        return sellerShipFee;
    }

    public void setSellerShipFee(BigDecimal sellerShipFee) {
        this.sellerShipFee = sellerShipFee;
    }

    public BigDecimal getSupplierShipFee() {
        return supplierShipFee;
    }

    public void setSupplierShipFee(BigDecimal supplierShipFee) {
        this.supplierShipFee = supplierShipFee;
    }

    public BigDecimal getLogisticCompanyShipFee() {
        return logisticCompanyShipFee;
    }

    public void setLogisticCompanyShipFee(BigDecimal logisticCompanyShipFee) {
        this.logisticCompanyShipFee = logisticCompanyShipFee;
    }

    public BigDecimal getSellerSkuPerShipFee() {
        return sellerSkuPerShipFee;
    }

    public void setSellerSkuPerShipFee(BigDecimal sellerSkuPerShipFee) {
        this.sellerSkuPerShipFee = sellerSkuPerShipFee;
    }

    public BigDecimal getSupplierSkuPerShipFee() {
        return supplierSkuPerShipFee;
    }

    public void setSupplierSkuPerShipFee(BigDecimal supplierSkuPerShipFee) {
        this.supplierSkuPerShipFee = supplierSkuPerShipFee;
    }

    public BigDecimal getLogisticCompanySkuPerShipFee() {
        return logisticCompanySkuPerShipFee;
    }

    public void setLogisticCompanySkuPerShipFee(BigDecimal logisticCompanySkuPerShipFee) {
        this.logisticCompanySkuPerShipFee = logisticCompanySkuPerShipFee;
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Integer getDeliveryWarehouseId() {
        return deliveryWarehouseId;
    }

    public void setDeliveryWarehouseId(Integer deliveryWarehouseId) {
        this.deliveryWarehouseId = deliveryWarehouseId;
    }

    public String getDeliveryWarehouseName() {
        return deliveryWarehouseName;
    }

    public void setDeliveryWarehouseName(String deliveryWarehouseName) {
        this.deliveryWarehouseName = deliveryWarehouseName == null ? null : deliveryWarehouseName.trim();
    }

    public Integer getFreeFreight() {
        return freeFreight;
    }

    public void setFreeFreight(Integer freeFreight) {
        this.freeFreight = freeFreight;
    }

    public BigDecimal getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(BigDecimal serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater == null ? null : creater.trim();
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier == null ? null : modifier.trim();
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName == null ? null : supplierName.trim();
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }
    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }


    public String getWserviceName() {
        return wserviceName;
    }

    public void setWserviceName(String wserviceName) {
        this.wserviceName = wserviceName;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getTopFlag() {
        return topFlag;
    }

    public void setTopFlag(Integer topFlag) {
        this.topFlag = topFlag;
    }
    public List<String> getwIds() {
        return wIds;
    }

    public void setwIds(List<String> wIds) {
        this.wIds = wIds;
    }

    public List<String> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<String> suppliers) {
        this.suppliers = suppliers;
    }


    public Integer getTotalSkuQty() {
        return totalSkuQty;
    }

    public void setTotalSkuQty(Integer totalSkuQty) {
        this.totalSkuQty = totalSkuQty;
    }

    public BigDecimal getTotalSkuPrice() {
        return totalSkuPrice;
    }

    public void setTotalSkuPrice(BigDecimal totalSkuPrice) {
        this.totalSkuPrice = totalSkuPrice;
    }

    public BigDecimal getTotalServiceCharge() {
        return totalServiceCharge;
    }

    public void setTotalServiceCharge(BigDecimal totalServiceCharge) {
        this.totalServiceCharge = totalServiceCharge;
    }

    public List<Integer> getWarehouseIdList() {
        return warehouseIdList;
    }

    public void setWarehouseIdList(List<Integer> warehouseIdList) {
        this.warehouseIdList = warehouseIdList;
    }
}