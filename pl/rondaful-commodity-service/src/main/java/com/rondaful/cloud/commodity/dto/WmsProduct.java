package com.rondaful.cloud.commodity.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class WmsProduct implements Serializable{

	private static final long serialVersionUID = 1L;
	
	//SKU
	private String productSku;
	
	//SKU别名
	private String productSkuAlias;
	
	//产品图片商品图片链接，主图
	private String productPictures;
	
	//产品名称
	private String productName;
	
	//产品重量
	private float productWeight;
	
	//品牌
	private String productBrand;
	
	//物流属性
	private String logisticsAttribute;
	
	//报关价格（USD）
	private BigDecimal customsPrice;
	
	//商品链接
	private String productLink;

	//中文报关名
	private String declareCustomsCn;
	
	//英文报关名
	private String declareCustomsEn;
	
	//海关编码
	private String customsCode;
	
	//包装材料
	private String packingMaterial;
	
	//产品单位
	private String productUnit;
	
	//SPU
	private String productSpu;
	
	//本地分类ID（CODE）
	private long categoryCode;
	
	//包装重量（g）
	private double packageWeight;
	
	//产品 长 (mm)
	private double productLength;
	
	//产品 宽 (mm)
	private double productWidth;
	
	//产品 高 (mm)
	private double productHeight;
	
	//包装 长 (mm)
	private double packageLength;
	
	//包装 宽 (mm)
	private double packageWidth;
	
	//包装 高 (mm)
	private double packageHeight;
	
	//商品属性
	private String productAttribute;
	
	//来源（1品连，2ERP）
	private String dataSources;
	
	//货主编码（平台供应商ID）
	private String shipper;
	
	//货主名称(供应商公司名称)
	private String shipperName;

	
	
	public String getProductSku() {
		return productSku;
	}

	public void setProductSku(String productSku) {
		this.productSku = productSku;
	}

	public String getProductSkuAlias() {
		return productSkuAlias;
	}

	public void setProductSkuAlias(String productSkuAlias) {
		this.productSkuAlias = productSkuAlias;
	}

	public String getProductPictures() {
		return productPictures;
	}

	public void setProductPictures(String productPictures) {
		this.productPictures = productPictures;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public float getProductWeight() {
		return productWeight;
	}

	public void setProductWeight(float productWeight) {
		this.productWeight = productWeight;
	}

	public String getProductBrand() {
		return productBrand;
	}

	public void setProductBrand(String productBrand) {
		this.productBrand = productBrand;
	}

	public String getLogisticsAttribute() {
		return logisticsAttribute;
	}

	public void setLogisticsAttribute(String logisticsAttribute) {
		this.logisticsAttribute = logisticsAttribute;
	}

	public BigDecimal getCustomsPrice() {
		return customsPrice;
	}

	public void setCustomsPrice(BigDecimal customsPrice) {
		this.customsPrice = customsPrice;
	}

	public String getProductLink() {
		return productLink;
	}

	public void setProductLink(String productLink) {
		this.productLink = productLink;
	}

	public String getDeclareCustomsCn() {
		return declareCustomsCn;
	}

	public void setDeclareCustomsCn(String declareCustomsCn) {
		this.declareCustomsCn = declareCustomsCn;
	}

	public String getDeclareCustomsEn() {
		return declareCustomsEn;
	}

	public void setDeclareCustomsEn(String declareCustomsEn) {
		this.declareCustomsEn = declareCustomsEn;
	}

	public String getCustomsCode() {
		return customsCode;
	}

	public void setCustomsCode(String customsCode) {
		this.customsCode = customsCode;
	}

	public String getPackingMaterial() {
		return packingMaterial;
	}

	public void setPackingMaterial(String packingMaterial) {
		this.packingMaterial = packingMaterial;
	}

	public String getProductUnit() {
		return productUnit;
	}

	public void setProductUnit(String productUnit) {
		this.productUnit = productUnit;
	}

	public String getProductSpu() {
		return productSpu;
	}

	public void setProductSpu(String productSpu) {
		this.productSpu = productSpu;
	}

	public long getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(long categoryCode) {
		this.categoryCode = categoryCode;
	}

	public double getPackageWeight() {
		return packageWeight;
	}

	public void setPackageWeight(double packageWeight) {
		this.packageWeight = packageWeight;
	}

	public double getProductLength() {
		return productLength;
	}

	public void setProductLength(double productLength) {
		this.productLength = productLength;
	}

	public double getProductWidth() {
		return productWidth;
	}

	public void setProductWidth(double productWidth) {
		this.productWidth = productWidth;
	}

	public double getProductHeight() {
		return productHeight;
	}

	public void setProductHeight(double productHeight) {
		this.productHeight = productHeight;
	}

	public double getPackageLength() {
		return packageLength;
	}

	public void setPackageLength(double packageLength) {
		this.packageLength = packageLength;
	}

	public double getPackageWidth() {
		return packageWidth;
	}

	public void setPackageWidth(double packageWidth) {
		this.packageWidth = packageWidth;
	}

	public double getPackageHeight() {
		return packageHeight;
	}

	public void setPackageHeight(double packageHeight) {
		this.packageHeight = packageHeight;
	}

	public String getProductAttribute() {
		return productAttribute;
	}

	public void setProductAttribute(String productAttribute) {
		this.productAttribute = productAttribute;
	}

	public String getDataSources() {
		return dataSources;
	}

	public void setDataSources(String dataSources) {
		this.dataSources = dataSources;
	}

	public String getShipper() {
		return shipper;
	}

	public void setShipper(String shipper) {
		this.shipper = shipper;
	}

	public String getShipperName() {
		return shipperName;
	}

	public void setShipperName(String shipperName) {
		this.shipperName = shipperName;
	}
	
}
