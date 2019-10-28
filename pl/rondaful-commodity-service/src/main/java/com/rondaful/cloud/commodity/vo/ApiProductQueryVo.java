package com.rondaful.cloud.commodity.vo;

public class ApiProductQueryVo extends ApiQueryBaseVo{
	
	private String categoryLevel1;
	private String categoryLevel2;
	private String categoryLevel3;
	private String commodityNameCn;
	private String commodityNameEn;
	private String systemSku;
	private String spu;
	private String freeFreight;
	private String vendibilityPlatform;
	
	
	public String getCategoryLevel1() {
		return categoryLevel1;
	}
	public void setCategoryLevel1(String categoryLevel1) {
		this.categoryLevel1 = categoryLevel1;
	}
	public String getCategoryLevel2() {
		return categoryLevel2;
	}
	public void setCategoryLevel2(String categoryLevel2) {
		this.categoryLevel2 = categoryLevel2;
	}
	public String getCategoryLevel3() {
		return categoryLevel3;
	}
	public void setCategoryLevel3(String categoryLevel3) {
		this.categoryLevel3 = categoryLevel3;
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
	public String getSystemSku() {
		return systemSku;
	}
	public void setSystemSku(String systemSku) {
		this.systemSku = systemSku;
	}
	public String getSpu() {
		return spu;
	}
	public void setSpu(String spu) {
		this.spu = spu;
	}
	public String getFreeFreight() {
		return freeFreight;
	}
	public void setFreeFreight(String freeFreight) {
		this.freeFreight = freeFreight;
	}
	public String getVendibilityPlatform() {
		return vendibilityPlatform;
	}
	public void setVendibilityPlatform(String vendibilityPlatform) {
		this.vendibilityPlatform = vendibilityPlatform;
	}
	
}
