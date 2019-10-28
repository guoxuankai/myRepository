package com.rondaful.cloud.commodity.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


public class ApiSkuResponse implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Long id;
	
	private Long commodityId;

    private String systemSku;
    
    private String commodityNameCn;

    private String commodityNameEn;
    
    private BigDecimal commodityPriceUs;
    
    private String commoditySpec;
    
    private BigDecimal commodityLength;

    private BigDecimal commodityWidth;

    private BigDecimal commodityHeight;

    private BigDecimal commodityWeight;
    
    private BigDecimal packingLength;
    
    private BigDecimal packingWidth;

    private BigDecimal packingHeight;
    
    private BigDecimal packingWeight;

    private String customsNameCn;

    private String customsNameEn;

    private BigDecimal customsPrice;

    private BigDecimal customsWeight;

    private String customsCode;
    
    private String masterPicture;
    
    private String additionalPicture;
    
    private String warehousePriceGroup;

    private List<ApiSkuWarehouseInfo> warehouseInfo;
    
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(Long commodityId) {
		this.commodityId = commodityId;
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

	public BigDecimal getCommodityPriceUs() {
		return commodityPriceUs;
	}

	public void setCommodityPriceUs(BigDecimal commodityPriceUs) {
		this.commodityPriceUs = commodityPriceUs;
	}

	public String getCommoditySpec() {
		return commoditySpec;
	}

	public void setCommoditySpec(String commoditySpec) {
		this.commoditySpec = commoditySpec;
	}

	public BigDecimal getCommodityLength() {
		return commodityLength;
	}

	public void setCommodityLength(BigDecimal commodityLength) {
		this.commodityLength = commodityLength;
	}

	public BigDecimal getCommodityWidth() {
		return commodityWidth;
	}

	public void setCommodityWidth(BigDecimal commodityWidth) {
		this.commodityWidth = commodityWidth;
	}

	public BigDecimal getCommodityHeight() {
		return commodityHeight;
	}

	public void setCommodityHeight(BigDecimal commodityHeight) {
		this.commodityHeight = commodityHeight;
	}

	public BigDecimal getCommodityWeight() {
		return commodityWeight;
	}

	public void setCommodityWeight(BigDecimal commodityWeight) {
		this.commodityWeight = commodityWeight;
	}

	public BigDecimal getPackingLength() {
		return packingLength;
	}

	public void setPackingLength(BigDecimal packingLength) {
		this.packingLength = packingLength;
	}

	public BigDecimal getPackingWidth() {
		return packingWidth;
	}

	public void setPackingWidth(BigDecimal packingWidth) {
		this.packingWidth = packingWidth;
	}

	public BigDecimal getPackingHeight() {
		return packingHeight;
	}

	public void setPackingHeight(BigDecimal packingHeight) {
		this.packingHeight = packingHeight;
	}

	public BigDecimal getPackingWeight() {
		return packingWeight;
	}

	public void setPackingWeight(BigDecimal packingWeight) {
		this.packingWeight = packingWeight;
	}

	public String getCustomsNameCn() {
		return customsNameCn;
	}

	public void setCustomsNameCn(String customsNameCn) {
		this.customsNameCn = customsNameCn;
	}

	public String getCustomsNameEn() {
		return customsNameEn;
	}

	public void setCustomsNameEn(String customsNameEn) {
		this.customsNameEn = customsNameEn;
	}

	public BigDecimal getCustomsPrice() {
		return customsPrice;
	}

	public void setCustomsPrice(BigDecimal customsPrice) {
		this.customsPrice = customsPrice;
	}

	public BigDecimal getCustomsWeight() {
		return customsWeight;
	}

	public void setCustomsWeight(BigDecimal customsWeight) {
		this.customsWeight = customsWeight;
	}

	public String getCustomsCode() {
		return customsCode;
	}

	public void setCustomsCode(String customsCode) {
		this.customsCode = customsCode;
	}

	public String getMasterPicture() {
		return masterPicture;
	}

	public void setMasterPicture(String masterPicture) {
		this.masterPicture = masterPicture;
	}

	public String getAdditionalPicture() {
		return additionalPicture;
	}

	public void setAdditionalPicture(String additionalPicture) {
		this.additionalPicture = additionalPicture;
	}

	public List<ApiSkuWarehouseInfo> getWarehouseInfo() {
		return warehouseInfo;
	}

	public void setWarehouseInfo(List<ApiSkuWarehouseInfo> warehouseInfo) {
		this.warehouseInfo = warehouseInfo;
	}

	public String getWarehousePriceGroup() {
		return warehousePriceGroup;
	}

	public void setWarehousePriceGroup(String warehousePriceGroup) {
		this.warehousePriceGroup = warehousePriceGroup;
	}
	
}
