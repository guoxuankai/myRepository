package com.rondaful.cloud.seller.entity.amazon;

import java.math.BigDecimal;

public class AmazonVarRequestProduct {
	/** 品连sku */
	private String varPlSku;
	/** 平台sku */
	private String varPlatformSku;
	/** Price.xsd 价格 */
	private BigDecimal varPricing;
	/** 货币类型 */
	private String varStandardPriceUnit;
	
	private String varTitle;
	
	/** Inventory.xsd */
	/** 库存   */
	private Integer varQuantity;
	/** 商品编码 */
	private String varStandardProductID;
	/** 类型  ISBN,  UPC, ASIN, GTIN ,GCID ,PZN */
	private String varStandardProductType;
	
	/** xxx  Product.xsd.MfrPartNumber */
	private String varPartNumber;
	
	/** 变体属性 */
	String variationThemeJson;

	public String getVarPlSku() {
		return varPlSku;
	}

	public void setVarPlSku(String varPlSku) {
		this.varPlSku = varPlSku;
	}

	public String getVarPlatformSku() {
		return varPlatformSku;
	}

	public void setVarPlatformSku(String varPlatformSku) {
		this.varPlatformSku = varPlatformSku;
	}


	public BigDecimal getVarPricing() {
		return varPricing;
	}

	public void setVarPricing(BigDecimal varPricing) {
		this.varPricing = varPricing;
	}

 
	public String getVarStandardPriceUnit() {
		return varStandardPriceUnit;
	}

	public void setVarStandardPriceUnit(String varStandardPriceUnit) {
		this.varStandardPriceUnit = varStandardPriceUnit;
	}

	public Integer getVarQuantity() {
		return varQuantity;
	}

	public void setVarQuantity(Integer varQuantity) {
		this.varQuantity = varQuantity;
	}

	public String getVarStandardProductID() {
		return varStandardProductID;
	}

	public void setVarStandardProductID(String varStandardProductID) {
		this.varStandardProductID = varStandardProductID;
	}

	public String getVarStandardProductType() {
		return varStandardProductType;
	}

	public void setVarStandardProductType(String varStandardProductType) {
		this.varStandardProductType = varStandardProductType;
	}

	public String getVarPartNumber() {
		return varPartNumber;
	}

	public void setVarPartNumber(String varPartNumber) {
		this.varPartNumber = varPartNumber;
	}

	public String getVariationThemeJson() {
		return variationThemeJson;
	}

	public void setVariationThemeJson(String variationThemeJson) {
		this.variationThemeJson = variationThemeJson;
	}

	
	
}
