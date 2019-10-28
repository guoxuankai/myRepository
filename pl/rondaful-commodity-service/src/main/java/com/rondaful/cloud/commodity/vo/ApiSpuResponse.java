package com.rondaful.cloud.commodity.vo;

import java.io.Serializable;
import java.util.List;

public class ApiSpuResponse implements Serializable{
	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String spu;
	
	private Long brandId;

	private String categoryPathCn;

    private String categoryPathEn;
    
    private String brandName;
    
    private Long categoryLevel1;

    private Long categoryLevel2;

    private Long categoryLevel3;

    private String productLogisticsAttributes;

    private String vendibilityPlatform;
    
    private String masterPicture;

    private String additionalPicture;

    private String commodityDesc;

    private String searchKeywords;

    private String strength1;

    private String packingList;

    private List<String> limitCountry;

    private Integer freeFreight;
    
    private List<ApiSkuResponse> skuList;

    

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSpu() {
		return spu;
	}

	public void setSpu(String spu) {
		this.spu = spu;
	}

	public Long getBrandId() {
		return brandId;
	}

	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}

	public String getCategoryPathCn() {
		return categoryPathCn;
	}

	public void setCategoryPathCn(String categoryPathCn) {
		this.categoryPathCn = categoryPathCn;
	}

	public String getCategoryPathEn() {
		return categoryPathEn;
	}

	public void setCategoryPathEn(String categoryPathEn) {
		this.categoryPathEn = categoryPathEn;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public Long getCategoryLevel1() {
		return categoryLevel1;
	}

	public void setCategoryLevel1(Long categoryLevel1) {
		this.categoryLevel1 = categoryLevel1;
	}

	public Long getCategoryLevel2() {
		return categoryLevel2;
	}

	public void setCategoryLevel2(Long categoryLevel2) {
		this.categoryLevel2 = categoryLevel2;
	}

	public Long getCategoryLevel3() {
		return categoryLevel3;
	}

	public void setCategoryLevel3(Long categoryLevel3) {
		this.categoryLevel3 = categoryLevel3;
	}

	public String getProductLogisticsAttributes() {
		return productLogisticsAttributes;
	}

	public void setProductLogisticsAttributes(String productLogisticsAttributes) {
		this.productLogisticsAttributes = productLogisticsAttributes;
	}

	public String getVendibilityPlatform() {
		return vendibilityPlatform;
	}

	public void setVendibilityPlatform(String vendibilityPlatform) {
		this.vendibilityPlatform = vendibilityPlatform;
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

	public String getCommodityDesc() {
		return commodityDesc;
	}

	public void setCommodityDesc(String commodityDesc) {
		this.commodityDesc = commodityDesc;
	}

	public String getSearchKeywords() {
		return searchKeywords;
	}

	public void setSearchKeywords(String searchKeywords) {
		this.searchKeywords = searchKeywords;
	}

	public String getStrength1() {
		return strength1;
	}

	public void setStrength1(String strength1) {
		this.strength1 = strength1;
	}

	public String getPackingList() {
		return packingList;
	}

	public void setPackingList(String packingList) {
		this.packingList = packingList;
	}

	public List<String> getLimitCountry() {
		return limitCountry;
	}

	public void setLimitCountry(List<String> limitCountry) {
		this.limitCountry = limitCountry;
	}

	public Integer getFreeFreight() {
		return freeFreight;
	}

	public void setFreeFreight(Integer freeFreight) {
		this.freeFreight = freeFreight;
	}

	public List<ApiSkuResponse> getSkuList() {
		return skuList;
	}

	public void setSkuList(List<ApiSkuResponse> skuList) {
		this.skuList = skuList;
	}
   
}
