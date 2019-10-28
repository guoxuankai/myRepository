package com.rondaful.cloud.supplier.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class LogisticsUser implements Serializable {

	private static final long serialVersionUID = -5913627975461898819L;

	private Long id;
	
	private Integer supplierId;
	
	private String supplier;
	
	private String logisticsId;
	
	private String warehouseCode;
	
	private String amazonCarrier;
	
	private String amazonCode;
	
	private String ebayCarrier;
	
	private String aliexpressCode;
    
    @ApiModelProperty(value = "其他amazon物流商")
    private String otherAmazonCarrier;
    
    @ApiModelProperty(value = "其他amazon物流方式代码ʽ")
    private String otherAmazonCode;
    
    @ApiModelProperty(value = "其他ebay物流商")
    private String otherEbayCarrier;
	
	private String status;
	
	private Date updateTime;
	
	private Date createTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getLogisticsId() {
		return logisticsId;
	}

	public void setLogisticsId(String logisticsId) {
		this.logisticsId = logisticsId;
	}

	public String getWarehouseCode() {
		return warehouseCode;
	}

	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}

	public String getAmazonCarrier() {
		return amazonCarrier;
	}

	public void setAmazonCarrier(String amazonCarrier) {
		this.amazonCarrier = amazonCarrier;
	}

	public String getAmazonCode() {
		return amazonCode;
	}

	public void setAmazonCode(String amazonCode) {
		this.amazonCode = amazonCode;
	}

	public String getEbayCarrier() {
		return ebayCarrier;
	}

	public void setEbayCarrier(String ebayCarrier) {
		this.ebayCarrier = ebayCarrier;
	}

	public String getOtherAmazonCarrier() {
		return otherAmazonCarrier;
	}

	public void setOtherAmazonCarrier(String otherAmazonCarrier) {
		this.otherAmazonCarrier = otherAmazonCarrier;
	}

	public String getOtherAmazonCode() {
		return otherAmazonCode;
	}

	public void setOtherAmazonCode(String otherAmazonCode) {
		this.otherAmazonCode = otherAmazonCode;
	}

	public String getOtherEbayCarrier() {
		return otherEbayCarrier;
	}

	public void setOtherEbayCarrier(String otherEbayCarrier) {
		this.otherEbayCarrier = otherEbayCarrier;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getAliexpressCode() {
		return aliexpressCode;
	}

	public void setAliexpressCode(String aliexpressCode) {
		this.aliexpressCode = aliexpressCode;
	}

	@Override
	public String toString() {
		return "LogisticsUser [id=" + id + ", supplierId=" + supplierId + ", supplier=" + supplier + ", logisticsId="
				+ logisticsId + ", warehouseCode=" + warehouseCode + ", amazonCarrier=" + amazonCarrier
				+ ", amazonCode=" + amazonCode + ", ebayCarrier=" + ebayCarrier + ", aliexpressCode=" + aliexpressCode
				+ ", otherAmazonCarrier=" + otherAmazonCarrier + ", otherAmazonCode=" + otherAmazonCode
				+ ", otherEbayCarrier=" + otherEbayCarrier + ", status=" + status + ", updateTime=" + updateTime
				+ ", createTime=" + createTime + "]";
	}

}
