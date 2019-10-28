package com.rondaful.cloud.commodity.vo;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class SkuExportVo{

	@ApiModelProperty(value = "一级分类")
	private Long category_level_1;
	
	@ApiModelProperty(value = "二级分类")
	private Long category_level_2;
	
	@ApiModelProperty(value = "三级分类")
	private Long category_level_3;
	
	@ApiModelProperty(value = "创建开始时间")
	private String startTime;
	
	@ApiModelProperty(value = "创建结束时间")
	private String endTime;
	
	@ApiModelProperty(value = "商品状态")
	private Integer autiState;
	
	@ApiModelProperty(value = "商品名称")
	private String commodityName;
	
	@ApiModelProperty(value = "系统sku")
	private String systemSku;
	
	@ApiModelProperty(value = "供应商sku")
	private String supplierSku;
	
	@ApiModelProperty(value = "系统spu")
	private String SPU;
	
	@ApiModelProperty(value = "供应商ID")
	private Long supplierId;
	
	@ApiModelProperty(value = "可售平台")
	private String vendibilityPlatform;
	
	@ApiModelProperty(value = "所属品牌id")
	private Long brandId;
	
	@ApiModelProperty(value = "spu的id数组")
	private List<String> ids;

	
	
	
	public Long getCategory_level_1() {
		return category_level_1;
	}

	public void setCategory_level_1(Long category_level_1) {
		this.category_level_1 = category_level_1;
	}

	public Long getCategory_level_2() {
		return category_level_2;
	}

	public void setCategory_level_2(Long category_level_2) {
		this.category_level_2 = category_level_2;
	}

	public Long getCategory_level_3() {
		return category_level_3;
	}

	public void setCategory_level_3(Long category_level_3) {
		this.category_level_3 = category_level_3;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Integer getAutiState() {
		return autiState;
	}

	public void setAutiState(Integer autiState) {
		this.autiState = autiState;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

	public String getSystemSku() {
		return systemSku;
	}

	public void setSystemSku(String systemSku) {
		this.systemSku = systemSku;
	}

	public String getSupplierSku() {
		return supplierSku;
	}

	public void setSupplierSku(String supplierSku) {
		this.supplierSku = supplierSku;
	}

	public String getSPU() {
		return SPU;
	}

	public void setSPU(String sPU) {
		SPU = sPU;
	}

	public Long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}

	public String getVendibilityPlatform() {
		return vendibilityPlatform;
	}

	public void setVendibilityPlatform(String vendibilityPlatform) {
		this.vendibilityPlatform = vendibilityPlatform;
	}

	public Long getBrandId() {
		return brandId;
	}

	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}
}
