package com.rondaful.cloud.commodity.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import cn.afterturn.easypoi.excel.annotation.Excel;

/**
* @Description:sku导出dto
* @author:范津 
* @date:2019年5月22日 下午4:28:48
 */
public class SkuExportDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Excel(name = "品连SPU[Brandlink SPU]",width=26)
    private String SPU;
	
	@Excel(name = "品连SKU[Brandlink SKU]",width=26)
	private String systemSku;
	
	@Excel(name = "供应商SKU[Supplier SKU]",width=28)
    private String supplierSku;
	
	@Excel(name = "商品中文名称[Product Chinese Name]",width=30)
    private String commodityNameCn;

    @Excel(name = "商品英文名称[[Product English Name]",width=30)
    private String commodityNameEn;
    
    @Excel(name = "商品分类[Category]",width=40)
    private String categoryName;
    
    @Excel(name = "商品价格[Product Cost](USD)",width=30)
    private BigDecimal commodityPriceUs;
    
    @Excel(name = "商品佣金[Fee]",width=20)
    private String fee;
    
    @Excel(name = "属性[Product Spec]",width=20)
    private String commoditySpec;
    
    @Excel(name = "长[Length](cm)",width=20)
    private BigDecimal commodityLength;

    @Excel(name = "宽[Width](cm)",width=20)
    private BigDecimal commodityWidth;

    @Excel(name = "高[Height](cm)",width=20)
    private BigDecimal commodityHeight;
    
    @Excel(name = "包装长[Packing Length](cm)",width=26)
    private BigDecimal packingLength;
    
    @Excel(name = "包装宽[Packing Width](cm)",width=26)
    private BigDecimal packingWidth;

    @Excel(name = "包装高[Packing Height](cm)",width=26)
    private BigDecimal packingHeight;
    
    @Excel(name = "商品重量[Weight](g)",width=20)
    private BigDecimal commodityWeight;
    
    @Excel(name = "包装重量[Packing Weight](g)",width=20)
    private BigDecimal packingWeight;
    
    @Excel(name = "状态[State]",width=20)
    private String state;
    
    @Excel(name = "供应商[supplier]",width=20)
    private String supplierCompanyName;
    
    @Excel(name = "创建时间[creatTime]",width=20)
    private String creatTime;

    
    
	public String getSPU() {
		return SPU;
	}

	public void setSPU(String sPU) {
		SPU = sPU;
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

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public BigDecimal getCommodityPriceUs() {
		return commodityPriceUs;
	}

	public void setCommodityPriceUs(BigDecimal commodityPriceUs) {
		this.commodityPriceUs = commodityPriceUs;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
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

	public BigDecimal getCommodityWeight() {
		return commodityWeight;
	}

	public void setCommodityWeight(BigDecimal commodityWeight) {
		this.commodityWeight = commodityWeight;
	}

	public BigDecimal getPackingWeight() {
		return packingWeight;
	}

	public void setPackingWeight(BigDecimal packingWeight) {
		this.packingWeight = packingWeight;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getSupplierCompanyName() {
		return supplierCompanyName;
	}

	public void setSupplierCompanyName(String supplierCompanyName) {
		this.supplierCompanyName = supplierCompanyName;
	}

	public String getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(String creatTime) {
		this.creatTime = creatTime;
	}
    
}
