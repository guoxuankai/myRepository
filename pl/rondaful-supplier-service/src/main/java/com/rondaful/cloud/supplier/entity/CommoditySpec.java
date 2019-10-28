package com.rondaful.cloud.supplier.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品规格表
 * 实体类对应的数据表为：  t_commodity_spec
 * @author zzx
 * @date 2018-12-11 14:11:01
 */
@ApiModel(value ="CommoditySpec")
public class CommoditySpec implements Serializable {
	
	private static final long serialVersionUID = 1L;
	 
    @ApiModelProperty(value = "唯一id")
    private Long id;

    @ApiModelProperty(value = "版本号")
    private Long version;

    @ApiModelProperty(value = "关联商品id")
    private Long commodityId;
    
    @Excel(name = "Brandlink_SPU" ,orderNum = "0")
    @ApiModelProperty(value = "SPU")
    private String SPU;

    @Excel(name = "Brandlink_SKU" ,orderNum = "1")
    @ApiModelProperty(value = "系统sku")
    private String systemSku;
    
    @Excel(name = "Supplier_SKU" ,orderNum = "2")
    @ApiModelProperty(value = "供应商sku")
    private String supplierSku;

    @Excel(name = "BrandName" ,orderNum = "3")
    @ApiModelProperty(value = "商品品牌")
    private String brandName;

    @ApiModelProperty(value = "生产商")
    private String producer;
    
    @Excel(name = "Length(cm)" ,orderNum = "4")
    @ApiModelProperty(value = "商品尺寸长度")
    private BigDecimal commodityLength;

    @Excel(name = "Width(cm)" ,orderNum = "5")
    @ApiModelProperty(value = "商品尺寸宽度")
    private BigDecimal commodityWidth;

    @Excel(name = "Height(cm)" ,orderNum = "6")
    @ApiModelProperty(value = "商品尺寸高度")
    private BigDecimal commodityHeight;

    @Excel(name = "Weight(g)" ,orderNum = "7")
    @ApiModelProperty(value = "商品重量")
    private BigDecimal commodityWeight;
    
    @Excel(name = "packingLength" ,orderNum = "8")
    @ApiModelProperty(value = "包装尺寸长度", required = true)
    private BigDecimal packingLength;
    
    @Excel(name = "packingWidth" ,orderNum = "9")
    @ApiModelProperty(value = "包装尺寸宽度", required = true)
    private BigDecimal packingWidth;

    @Excel(name = "packingHeight" ,orderNum = "10")
    @ApiModelProperty(value = "包装尺寸高度", required = true)
    private BigDecimal packingHeight;
    
    @Excel(name = "packingWeight" ,orderNum = "11")
    @ApiModelProperty(value = "包装重量", required = true)
    private BigDecimal packingWeight;
    
    @Excel(name = "commoditySpec" ,orderNum = "12")
    @ApiModelProperty(value = "商品规格列表，属性名:属性值，多个以|隔开")
    private String commoditySpec;

    @ApiModelProperty(value = "商品价")
    private BigDecimal commodityPrice;
    
    @Excel(name = "product cost" ,orderNum = "13")
    @ApiModelProperty(value = "商品价美元")
    private BigDecimal commodityPriceUs;

    @Excel(name = "retailPrice" ,orderNum = "14")
    @ApiModelProperty(value = "建议零售价")
    private BigDecimal retailPrice;
   
    @Excel(name = "commodityNameChinese" ,orderNum = "15")
    @ApiModelProperty(value = "商品中文名称")
    private String commodityNameCn;

    @Excel(name = "commodityNameEnglish" ,orderNum = "16")
    @ApiModelProperty(value = "商品英文名称")
    private String commodityNameEn;
    
    @Excel(name = "keyword-us" ,orderNum = "17")
    private String searchKeywordsEn;
    
    @Excel(name = "keyword-cn" ,orderNum = "18")
    private String searchKeywordsCn;
    
    @Excel(name = "keyword-fr" ,orderNum = "19")
    private String searchKeywordsFr;
    
    @Excel(name = "keyword-de" ,orderNum = "20")
    private String searchKeywordsDe;
    
    @Excel(name = "keyword-it" ,orderNum = "21")
    private String searchKeywordsIt;
    
    @Excel(name = "strengthEn" ,orderNum = "22")
    private String strengthEn;
    
    @Excel(name = "strengthCn" ,orderNum = "23")
    private String strengthCn;
    
    @Excel(name = "strengthFr" ,orderNum = "24")
    private String strengthFr;
    
    @Excel(name = "strengthDe" ,orderNum = "25")
    private String strengthDe;
    
    @Excel(name = "strengthIt" ,orderNum = "26")
    private String strengthIt;
    
    @Excel(name = "packingListEn" ,orderNum = "27")
    private String packingListEn;
    
    @Excel(name = "packingListCn" ,orderNum = "28")
    private String packingListCn;
    
    @Excel(name = "packingListFr" ,orderNum = "29")
    private String packingListFr;
    
    @Excel(name = "packingListDe" ,orderNum = "30")
    private String packingListDe;
    
    @Excel(name = "packingListIt" ,orderNum = "31")
    private String packingListIt;
    
    @Excel(name = "commodityDescEn" ,orderNum = "32")
    private String commodityDescEn;

    @Excel(name = "commodityDescCn" ,orderNum = "33")
    private String commodityDescCn;
    
    @Excel(name = "commodityDescFr" ,orderNum = "34")
    private String commodityDescFr;
    
    @Excel(name = "commodityDescDe" ,orderNum = "35")
    private String commodityDescDe;
    
    @Excel(name = "commodityDescIt" ,orderNum = "36")
    private String commodityDescIt;

    @Excel(name = "productFeaturesEn" ,orderNum = "37")
    @ApiModelProperty(value = "产品特性英文，多个以|隔开")
    private String productFeaturesEn;
    
    @Excel(name = "productFeaturesCn" ,orderNum = "38")
    @ApiModelProperty(value = "产品特性中文，多个以|隔开")
    private String productFeaturesCn;
    
    @ApiModelProperty(value = "商品标题")
    private String tittle;
    
    @ApiModelProperty(value = "SKU附图，多个以|隔开")
    private String additionalPicture;

    @ApiModelProperty(value = "关联属性id，多个以,隔开")
    private String attributeId;

    @ApiModelProperty(value = "商品状态，-1：待提交，0：待审核，1：待上架，2：审核失败，3：已上架")
    private Integer state;

    @ApiModelProperty(value = "是否上架")
    private Boolean isUp;

    @ApiModelProperty(value = "库存")
    private int inventory;
    
    @ApiModelProperty(value = "SKU主图")
    private String masterPicture;

    @ApiModelProperty(value = "审核描述")
    private String auditDesc;

    @ApiModelProperty(value = "供应商id")
    private String supplierId;

    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    @ApiModelProperty(value = "供应商公司名称")
    private String supplierCompanyName;
    
    @ApiModelProperty(value = "佣金百分比")
    private Double feeRate;
    
    @ApiModelProperty(value = "佣金，和佣金百分比二选一")
    private Double feePrice;
    
    @ApiModelProperty(value = "报关中文名")
    private String customsNameCn;

    @ApiModelProperty(value = "报关英文名")
    private String customsNameEn;

    @ApiModelProperty(value = "报关价格")
    private BigDecimal customsPrice;

    @ApiModelProperty(value = "报关重量")
    private BigDecimal customsWeight;

    @ApiModelProperty(value = "海关编码")
    private String customsCode;
    
    @ApiModelProperty(value = "分类中文名称")
    private String categoryName;
    
    //分类英文文名称
    private String categoryName2;

    //供应商所属供应链公司Id
    private Integer supChainCompanyId;
    //供应商所属供应链公司名称
    private String supChainCompanyName;
    
    @ApiModelProperty(value = "下架类型，1：手工下架，2：ERP接口下架")
    private Integer downStateType;
    
    @ApiModelProperty(value = "所属一级分类id")
    private Long categoryLevel1;
    
    @ApiModelProperty(value = "所属二级分类id")
    private Long categoryLevel2;
    
    @ApiModelProperty(value = "所属三级分类id")
    private Long categoryLevel3;
    
    @ApiModelProperty(value = "佣金美元")
    private Double feePriceUs;
    
    @ApiModelProperty(value = "已售数量")
    private Integer saleNum;
    
    @ApiModelProperty(value = "产品物流属性", required = true)
    private String productLogisticsAttributes;

    @ApiModelProperty(value = "最低销售价，USD美元")
    private BigDecimal lowestSalePrice;
    
    @ApiModelProperty(value = "1：包邮，0：不包邮")
    private int freeFreight;
    
    @ApiModelProperty(value = "英文规格，属性名:属性值，多个以|隔开")
    private String commoditySpecEn;
    
    @ApiModelProperty(value = "中文规格值")
    private String specValueCn;
    
    @ApiModelProperty(value = "英文规格值")
    private String specValueEn;
   

    
    
    public String getSpecValueCn() {
		return specValueCn;
	}

	public void setSpecValueCn(String specValueCn) {
		this.specValueCn = specValueCn;
	}

	public String getSpecValueEn() {
		return specValueEn;
	}

	public void setSpecValueEn(String specValueEn) {
		this.specValueEn = specValueEn;
	}

	public String getCommoditySpecEn() {
		return commoditySpecEn;
	}

	public void setCommoditySpecEn(String commoditySpecEn) {
		this.commoditySpecEn = commoditySpecEn;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(Long commodityId) {
        this.commodityId = commodityId;
    }

    public String getSupplierSku() {
        return supplierSku;
    }

    public void setSupplierSku(String supplierSku) {
        this.supplierSku = supplierSku == null ? null : supplierSku.trim();
    }

    public BigDecimal getCommodityPrice() {
        return commodityPrice;
    }

    public void setCommodityPrice(BigDecimal commodityPrice) {
        this.commodityPrice = commodityPrice;
    }

    public BigDecimal getRetailPrice() {
        return retailPrice;
    }

    public String getSupplierCompanyName() {
        return supplierCompanyName;
    }

    public void setSupplierCompanyName(String supplierCompanyName) {
        this.supplierCompanyName = supplierCompanyName;
    }

    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }

    public String getSystemSku() {
        return systemSku;
    }

    public void setSystemSku(String systemSku) {
        this.systemSku = systemSku == null ? null : systemSku.trim();
    }

    public String getCommoditySpec() {
        return commoditySpec;
    }

    public void setCommoditySpec(String commoditySpec) {
        this.commoditySpec = commoditySpec == null ? null : commoditySpec.trim();
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId == null ? null : attributeId.trim();
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Boolean getIsUp() {
        return isUp;
    }

    public void setIsUp(Boolean isUp) {
        this.isUp = isUp;
    }

    public int getInventory() {
		return inventory;
	}

	public void setInventory(int inventory) {
		this.inventory = inventory;
	}

	public String getAuditDesc() {
        return auditDesc;
    }

    public void setAuditDesc(String auditDesc) {
        this.auditDesc = auditDesc;
    }

    public String getSPU() {
        return SPU;
    }

    public void setSPU(String SPU) {
        this.SPU = SPU;
    }

    public String getMasterPicture() {
        return masterPicture;
    }

    public void setMasterPicture(String masterPicture) {
        this.masterPicture = masterPicture;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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

    public BigDecimal getPackingWeight() {
        return packingWeight;
    }

    public void setPackingWeight(BigDecimal packingWeight) {
        this.packingWeight = packingWeight;
    }

    public BigDecimal getPackingWidth() {
        return packingWidth;
    }

    public void setPackingWidth(BigDecimal packingWidth) {
        this.packingWidth = packingWidth;
    }

    public BigDecimal getPackingLength() {
        return packingLength;
    }

    public void setPackingLength(BigDecimal packingLength) {
        this.packingLength = packingLength;
    }

    public BigDecimal getPackingHeight() {
        return packingHeight;
    }

    public void setPackingHeight(BigDecimal packingHeight) {
        this.packingHeight = packingHeight;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
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

    public String getProductFeaturesEn() {
        return productFeaturesEn;
    }

    public void setProductFeaturesEn(String productFeaturesEn) {
        this.productFeaturesEn = productFeaturesEn;
    }

    public String getAdditionalPicture() {
        return additionalPicture;
    }

    public void setAdditionalPicture(String additionalPicture) {
        this.additionalPicture = additionalPicture;
    }

	public Double getFeeRate() {
		return feeRate;
	}

	public void setFeeRate(Double feeRate) {
		this.feeRate = feeRate;
	}

	public Double getFeePrice() {
		return feePrice;
	}

	public void setFeePrice(Double feePrice) {
		this.feePrice = feePrice;
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

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getSearchKeywordsEn() {
		return searchKeywordsEn;
	}

	public void setSearchKeywordsEn(String searchKeywordsEn) {
		this.searchKeywordsEn = searchKeywordsEn;
	}

	public String getSearchKeywordsCn() {
		return searchKeywordsCn;
	}

	public void setSearchKeywordsCn(String searchKeywordsCn) {
		this.searchKeywordsCn = searchKeywordsCn;
	}

	public String getSearchKeywordsFr() {
		return searchKeywordsFr;
	}

	public void setSearchKeywordsFr(String searchKeywordsFr) {
		this.searchKeywordsFr = searchKeywordsFr;
	}

	public String getSearchKeywordsDe() {
		return searchKeywordsDe;
	}

	public void setSearchKeywordsDe(String searchKeywordsDe) {
		this.searchKeywordsDe = searchKeywordsDe;
	}

	public String getSearchKeywordsIt() {
		return searchKeywordsIt;
	}

	public void setSearchKeywordsIt(String searchKeywordsIt) {
		this.searchKeywordsIt = searchKeywordsIt;
	}

	public String getStrengthEn() {
		return strengthEn;
	}

	public void setStrengthEn(String strengthEn) {
		this.strengthEn = strengthEn;
	}

	public String getStrengthCn() {
		return strengthCn;
	}

	public void setStrengthCn(String strengthCn) {
		this.strengthCn = strengthCn;
	}

	public String getStrengthFr() {
		return strengthFr;
	}

	public void setStrengthFr(String strengthFr) {
		this.strengthFr = strengthFr;
	}

	public String getStrengthDe() {
		return strengthDe;
	}

	public void setStrengthDe(String strengthDe) {
		this.strengthDe = strengthDe;
	}

	public String getStrengthIt() {
		return strengthIt;
	}

	public void setStrengthIt(String strengthIt) {
		this.strengthIt = strengthIt;
	}

	public String getPackingListEn() {
		return packingListEn;
	}

	public void setPackingListEn(String packingListEn) {
		this.packingListEn = packingListEn;
	}

	public String getPackingListCn() {
		return packingListCn;
	}

	public void setPackingListCn(String packingListCn) {
		this.packingListCn = packingListCn;
	}

	public String getPackingListFr() {
		return packingListFr;
	}

	public void setPackingListFr(String packingListFr) {
		this.packingListFr = packingListFr;
	}

	public String getPackingListDe() {
		return packingListDe;
	}

	public void setPackingListDe(String packingListDe) {
		this.packingListDe = packingListDe;
	}

	public String getPackingListIt() {
		return packingListIt;
	}

	public void setPackingListIt(String packingListIt) {
		this.packingListIt = packingListIt;
	}

	public String getCommodityDescEn() {
		return commodityDescEn;
	}

	public void setCommodityDescEn(String commodityDescEn) {
		this.commodityDescEn = commodityDescEn;
	}

	public String getCommodityDescCn() {
		return commodityDescCn;
	}

	public void setCommodityDescCn(String commodityDescCn) {
		this.commodityDescCn = commodityDescCn;
	}

	public String getCommodityDescFr() {
		return commodityDescFr;
	}

	public void setCommodityDescFr(String commodityDescFr) {
		this.commodityDescFr = commodityDescFr;
	}

	public String getCommodityDescDe() {
		return commodityDescDe;
	}

	public void setCommodityDescDe(String commodityDescDe) {
		this.commodityDescDe = commodityDescDe;
	}

	public String getCommodityDescIt() {
		return commodityDescIt;
	}

	public void setCommodityDescIt(String commodityDescIt) {
		this.commodityDescIt = commodityDescIt;
	}

	public String getProductFeaturesCn() {
		return productFeaturesCn;
	}

	public void setProductFeaturesCn(String productFeaturesCn) {
		this.productFeaturesCn = productFeaturesCn;
	}

	public Integer getSupChainCompanyId() {
		return supChainCompanyId;
	}

	public void setSupChainCompanyId(Integer supChainCompanyId) {
		this.supChainCompanyId = supChainCompanyId;
	}

	public String getSupChainCompanyName() {
		return supChainCompanyName;
	}

	public void setSupChainCompanyName(String supChainCompanyName) {
		this.supChainCompanyName = supChainCompanyName;
	}

	public String getCategoryName2() {
		return categoryName2;
	}

	public void setCategoryName2(String categoryName2) {
		this.categoryName2 = categoryName2;
	}

	public Integer getDownStateType() {
		return downStateType;
	}

	public void setDownStateType(Integer downStateType) {
		this.downStateType = downStateType;
	}

	public Long getCategoryLevel3() {
		return categoryLevel3;
	}

	public void setCategoryLevel3(Long categoryLevel3) {
		this.categoryLevel3 = categoryLevel3;
	}

	public BigDecimal getCommodityPriceUs() {
		return commodityPriceUs;
	}

	public void setCommodityPriceUs(BigDecimal commodityPriceUs) {
		this.commodityPriceUs = commodityPriceUs;
	}

	public Double getFeePriceUs() {
		return feePriceUs;
	}

	public void setFeePriceUs(Double feePriceUs) {
		this.feePriceUs = feePriceUs;
	}

	public Integer getSaleNum() {
		return saleNum;
	}

	public void setSaleNum(Integer saleNum) {
		this.saleNum = saleNum;
	}

	public String getProductLogisticsAttributes() {
		return productLogisticsAttributes;
	}

	public void setProductLogisticsAttributes(String productLogisticsAttributes) {
		this.productLogisticsAttributes = productLogisticsAttributes;
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

	public BigDecimal getLowestSalePrice() {
		return lowestSalePrice;
	}

	public void setLowestSalePrice(BigDecimal lowestSalePrice) {
		this.lowestSalePrice = lowestSalePrice;
	}

	public int getFreeFreight() {
		return freeFreight;
	}

	public void setFreeFreight(int freeFreight) {
		this.freeFreight = freeFreight;
	}
    
}