package com.rondaful.cloud.commodity.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 商品基础信息表
 * 实体类对应的数据表为：  t_commodity_base
 *
 * @author zzx
 * @date 2018-12-04 14:05:46
 */
@ApiModel(value = "CommodityBase")
public class CommodityBase implements Serializable {
	
	private static final long serialVersionUID = 1L;
	 
    @ApiModelProperty(value = "唯一id")
    private Long id;

    @ApiModelProperty(value = "更新时间")
    private String updateTime;

    @ApiModelProperty(value = "版本号")
    private Long version;

    @ApiModelProperty(value = "创建时间")
    private String creatTime;

    @ApiModelProperty(value = "所属供应商id", required = true)
    private Long supplierId;

    @ApiModelProperty(value = "所属一级分类id", required = true)
    private Long categoryLevel1;

    @ApiModelProperty(value = "所属二级分类id", required = true)
    private Long categoryLevel2;

    @ApiModelProperty(value = "所属三级分类id", required = true)
    private Long categoryLevel3;

    @ApiModelProperty(value = "标题", required = true)
    private String title;

    @ApiModelProperty(value = "生产商")
    private String producer;

    @ApiModelProperty(value = "所属品牌id", required = true)
    private Long brandId;

    @ApiModelProperty(value = "默认仓库", required = true)
    private String defaultRepository;

    @ApiModelProperty(value = "是否私模产品", required = true)
    private Boolean isPrivateModel;

    @ApiModelProperty(value = "产品上市时间")
    private String productMarketTime;

    @ApiModelProperty(value = "产品物流属性", required = true)
    private String productLogisticsAttributes;

    @ApiModelProperty(value = "可售平台", required = true)
    private String vendibilityPlatform;

    @ApiModelProperty(value = "商品标签")
    private String label;

    @ApiModelProperty(value = "SPU")
    private String SPU;

    @ApiModelProperty(value = "SPU主图")
    private String masterPicture;

    @ApiModelProperty(value = "商品附图")
    private String additionalPicture;

    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    @ApiModelProperty(value = "一级分类名称")
    private String categoryName1;

    @ApiModelProperty(value = "二级分类名称")
    private String categoryName2;

    @ApiModelProperty(value = "三级分类名称")
    private String categoryName3;

    @ApiModelProperty(value = "商品状态，-1：待提交，0：审核中，1：待上架，2：已拒绝，3：已上架，4：已下架")
    private Integer state;

    @ApiModelProperty(value = "关联spu码id")
    private Long spuId;

    @ApiModelProperty(value = "关注商品id")
    private Long focusId;

    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    @ApiModelProperty(value = "供应商公司名称")
    private String supplierCompanyName;

    @ApiModelProperty(value = "商品描述")
    private String commodityDesc;

    @ApiModelProperty(value = "搜索关键字")
    private String searchKeywords;

    @ApiModelProperty(value = "商品类型")
    private String spuType;

    @ApiModelProperty(value = "系统sku")
    private String systemSku;

    @ApiModelProperty(value = "商品中文名称")
    private String commodityNameCn;

    @ApiModelProperty(value = "商品英文名称")
    private String commodityNameEn;

    @ApiModelProperty(value = "商品价")
    private BigDecimal commodityPrice;

    @ApiModelProperty(value = "佣金百分比")
    private Double feeRate;

    @ApiModelProperty(value = "佣金，和佣金百分比二选一")
    private Double feePrice;

    //商品规格名，多个用逗号隔开
    private String commoditySpecKeys;

    @ApiModelProperty(value = "商品亮点1")
    private String strength1;

    @ApiModelProperty(value = "商品亮点2")
    private String strength2;

    @ApiModelProperty(value = "商品亮点3")
    private String strength3;

    @ApiModelProperty(value = "商品亮点4")
    private String strength4;

    @ApiModelProperty(value = "商品亮点5")
    private String strength5;

    @ApiModelProperty(value = "包装清单")
    private String packingList;

    @ApiModelProperty(value = "商品资质图片，多个以|隔开，最多5个")
    private String provePicture;

    @ApiModelProperty(value = "分类属性")
    private String categoryAttr;

    @ApiModelProperty(value = "自定义属性")
    private String customAttr;

    @ApiModelProperty(value = "商品销售类型")
    private Integer saleType;

    @ApiModelProperty("商品推广创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date promotionCreateDate;

    @ApiModelProperty("商品推广id")
    private Integer promotionId;

    @ApiModelProperty(value = "库存")
    private int inventory;

    @ApiModelProperty(value = "商品价美元")
    private BigDecimal commodityPriceUs;
    
    @ApiModelProperty(value = "建议销售价，USD美元")
    private BigDecimal suggestSalePrice;

    @ApiModelProperty(value = "最低销售价，USD美元")
    private BigDecimal lowestSalePrice;
    
    @ApiModelProperty(value = "禁售国家")
    private List<String> limitCountry;
    
    @ApiModelProperty(value = "禁售卖家")
    private List<String> limitSeller;

    //分类属性数组
    private List<Map<String, String>> categoryAttrList;

    //自定义属性数组
    private List<Map<String, String>> customAttrList;

    //sku已选属性
    private Map<String, List<String>> selectedAttrMap;

    private List<CommoditySpec> commoditySpecList;

    private CommodityDetails commodityDetails;

    @ApiModelProperty(value = "erp商品的SPU")
    private String supplierSpu;

    private String supplierSku;

    @ApiModelProperty(value = "1：可标识侵权，0：不可以")
    private int tortFlag;
    
    @ApiModelProperty(value = "指定卖家ID数组")
    private List<String> belongSeller;
    
    @ApiModelProperty(value = "1：包邮，0：不包邮")
    private Integer freeFreight;
    
    @ApiModelProperty(value = "商品是否指定了其它卖家，1：可售，-1：不可售")
    private Integer canSale;
    
    @ApiModelProperty(value = "亚马逊可售站点,多个用逗号分隔")
    private String amazonSite;
    
    @ApiModelProperty(value = "eBay可售站点,多个用逗号分隔")
    private String ebaySite;
    
    @ApiModelProperty(value = "spu已售数，所有sku已售数量之和")
    private int saleNum;
    
    @ApiModelProperty(value = "spu刊登数，所有sku刊登数量之和")
    private int publishNum;

    @ApiModelProperty(value = "搜索条件，库存开始值")
    private Integer inventoryStart;
    
    @ApiModelProperty(value = "搜索条件，库存结束值")
    private Integer inventoryEnd;
    
    @ApiModelProperty(value = "搜索条件，商品价开始值")
    private String priceStart;
    
    @ApiModelProperty(value = "搜索条件，商品价结束值")
    private String priceEnd;
    
    @ApiModelProperty(value = "排序条件，销售量排序，asc/desc")
    private String saleNumOrder;
    
    @ApiModelProperty(value = "排序条件，刊登数排序，asc/desc")
    private String publishNumOrder;
    
    @ApiModelProperty(value = "所有sku仓库ID组合")
    private String warehouseId;
    
    //是否多仓库，0否，1是
    private Integer multiWarehouse;
    
    //指定卖家ID，
    private Long belongSellerId;
    
    private Boolean isUp;
    
    private List<Long> ids;
    
    //是否多仓库价格：0不是，1是
    private int multiPriceFlag;
    
    
	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public Integer getMultiWarehouse() {
		return multiWarehouse;
	}

	public void setMultiWarehouse(Integer multiWarehouse) {
		this.multiWarehouse = multiWarehouse;
	}

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getSaleNumOrder() {
		return saleNumOrder;
	}

	public void setSaleNumOrder(String saleNumOrder) {
		this.saleNumOrder = saleNumOrder;
	}

	public String getPublishNumOrder() {
		return publishNumOrder;
	}

	public void setPublishNumOrder(String publishNumOrder) {
		this.publishNumOrder = publishNumOrder;
	}

	public Integer getInventoryStart() {
		return inventoryStart;
	}

	public void setInventoryStart(Integer inventoryStart) {
		this.inventoryStart = inventoryStart;
	}

	public Integer getInventoryEnd() {
		return inventoryEnd;
	}

	public void setInventoryEnd(Integer inventoryEnd) {
		this.inventoryEnd = inventoryEnd;
	}

	public String getPriceStart() {
		return priceStart;
	}

	public void setPriceStart(String priceStart) {
		this.priceStart = priceStart;
	}

	public String getPriceEnd() {
		return priceEnd;
	}

	public void setPriceEnd(String priceEnd) {
		this.priceEnd = priceEnd;
	}

	public int getPublishNum() {
		return publishNum;
	}

	public void setPublishNum(int publishNum) {
		this.publishNum = publishNum;
	}

	public int getSaleNum() {
		return saleNum;
	}

	public void setSaleNum(int saleNum) {
		this.saleNum = saleNum;
	}

	public String getAmazonSite() {
		return amazonSite;
	}

	public void setAmazonSite(String amazonSite) {
		this.amazonSite = amazonSite;
	}

	public String getEbaySite() {
		return ebaySite;
	}

	public void setEbaySite(String ebaySite) {
		this.ebaySite = ebaySite;
	}

	public Integer getFreeFreight() {
		return freeFreight;
	}

	public void setFreeFreight(Integer freeFreight) {
		this.freeFreight = freeFreight;
	}

	public Integer getCanSale() {
		return canSale;
	}

	public void setCanSale(Integer canSale) {
		this.canSale = canSale;
	}

	public List<String> getBelongSeller() {
		return belongSeller;
	}

	public void setBelongSeller(List<String> belongSeller) {
		this.belongSeller = belongSeller;
	}

	public int getTortFlag() {
		return tortFlag;
	}

	public void setTortFlag(int tortFlag) {
		this.tortFlag = tortFlag;
	}

	public BigDecimal getSuggestSalePrice() {
        return suggestSalePrice;
    }

    public void setSuggestSalePrice(BigDecimal suggestSalePrice) {
        this.suggestSalePrice = suggestSalePrice;
    }

    public BigDecimal getLowestSalePrice() {
        return lowestSalePrice;
    }

    public void setLowestSalePrice(BigDecimal lowestSalePrice) {
        this.lowestSalePrice = lowestSalePrice;
    }

    public int getInventory() {
		return inventory;
	}

	public void setInventory(int inventory) {
		this.inventory = inventory;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime == null ? null : updateTime.trim();
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime == null ? null : creatTime.trim();
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer == null ? null : producer.trim();
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getDefaultRepository() {
        return defaultRepository;
    }

    public void setDefaultRepository(String defaultRepository) {
        this.defaultRepository = defaultRepository == null ? null : defaultRepository.trim();
    }

    public Boolean getIsPrivateModel() {
        return isPrivateModel;
    }

    public void setIsPrivateModel(Boolean isPrivateModel) {
        this.isPrivateModel = isPrivateModel;
    }

    public String getProductMarketTime() {
        return productMarketTime;
    }

    public void setProductMarketTime(String productMarketTime) {
        this.productMarketTime = productMarketTime == null ? null : productMarketTime.trim();
    }

    public String getProductLogisticsAttributes() {
        return productLogisticsAttributes;
    }

    public void setProductLogisticsAttributes(String productLogisticsAttributes) {
        this.productLogisticsAttributes = productLogisticsAttributes == null ? null : productLogisticsAttributes.trim();
    }

    public String getVendibilityPlatform() {
        return vendibilityPlatform;
    }

    public void setVendibilityPlatform(String vendibilityPlatform) {
        this.vendibilityPlatform = vendibilityPlatform == null ? null : vendibilityPlatform.trim();
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getSpuId() {
        return spuId;
    }

    public void setSpuId(Long spuId) {
        this.spuId = spuId;
    }

    public String getSPU() {
        return SPU;
    }

    public void setSPU(String SPU) {
        this.SPU = SPU;
    }

    public String getCategoryName1() {
        return categoryName1;
    }

    public void setCategoryName1(String categoryName1) {
        this.categoryName1 = categoryName1;
    }

    public String getCategoryName2() {
        return categoryName2;
    }

    public void setCategoryName2(String categoryName2) {
        this.categoryName2 = categoryName2;
    }

    public String getCategoryName3() {
        return categoryName3;
    }

    public void setCategoryName3(String categoryName3) {
        this.categoryName3 = categoryName3;
    }

    public List<CommoditySpec> getCommoditySpecList() {
        return commoditySpecList;
    }

    public void setCommoditySpecList(List<CommoditySpec> commoditySpecList) {
        this.commoditySpecList = commoditySpecList;
    }

    public CommodityDetails getCommodityDetails() {
        return commodityDetails;
    }

    public void setCommodityDetails(CommodityDetails commodityDetails) {
        this.commodityDetails = commodityDetails;
    }

    public String getMasterPicture() {
        return masterPicture;
    }

    public void setMasterPicture(String masterPicture) {
        this.masterPicture = masterPicture;
    }

    public Long getFocusId() {
        return focusId;
    }

    public void setFocusId(Long focusId) {
        this.focusId = focusId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getAdditionalPicture() {
        return additionalPicture;
    }

    public void setAdditionalPicture(String additionalPicture) {
        this.additionalPicture = additionalPicture;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSupplierCompanyName() {
        return supplierCompanyName;
    }

    public void setSupplierCompanyName(String supplierCompanyName) {
        this.supplierCompanyName = supplierCompanyName;
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

    public String getSpuType() {
        return spuType;
    }

    public void setSpuType(String spuType) {
        this.spuType = spuType;
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

    public BigDecimal getCommodityPrice() {
        return commodityPrice;
    }

    public void setCommodityPrice(BigDecimal commodityPrice) {
        this.commodityPrice = commodityPrice;
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

    public String getCommoditySpecKeys() {
        return commoditySpecKeys;
    }

    public void setCommoditySpecKeys(String commoditySpecKeys) {
        this.commoditySpecKeys = commoditySpecKeys;
    }

    public String getStrength1() {
        return strength1;
    }

    public void setStrength1(String strength1) {
        this.strength1 = strength1;
    }

    public String getStrength2() {
        return strength2;
    }

    public void setStrength2(String strength2) {
        this.strength2 = strength2;
    }

    public String getStrength3() {
        return strength3;
    }

    public void setStrength3(String strength3) {
        this.strength3 = strength3;
    }

    public String getStrength4() {
        return strength4;
    }

    public void setStrength4(String strength4) {
        this.strength4 = strength4;
    }

    public String getStrength5() {
        return strength5;
    }

    public void setStrength5(String strength5) {
        this.strength5 = strength5;
    }

    public String getPackingList() {
        return packingList;
    }

    public void setPackingList(String packingList) {
        this.packingList = packingList;
    }

    public String getProvePicture() {
        return provePicture;
    }

    public void setProvePicture(String provePicture) {
        this.provePicture = provePicture;
    }

    public String getCategoryAttr() {
        return categoryAttr;
    }

    public void setCategoryAttr(String categoryAttr) {
        this.categoryAttr = categoryAttr;
    }

    public String getCustomAttr() {
        return customAttr;
    }

    public void setCustomAttr(String customAttr) {
        this.customAttr = customAttr;
    }

    public List<Map<String, String>> getCategoryAttrList() {
        return categoryAttrList;
    }

    public void setCategoryAttrList(List<Map<String, String>> categoryAttrList) {
        this.categoryAttrList = categoryAttrList;
    }

    public List<Map<String, String>> getCustomAttrList() {
        return customAttrList;
    }

    public void setCustomAttrList(List<Map<String, String>> customAttrList) {
        this.customAttrList = customAttrList;
    }

    public Map<String, List<String>> getSelectedAttrMap() {
        return selectedAttrMap;
    }

    public void setSelectedAttrMap(Map<String, List<String>> selectedAttrMap) {
        this.selectedAttrMap = selectedAttrMap;
    }

    public String getSupplierSpu() {
        return supplierSpu;
    }

    public void setSupplierSpu(String supplierSpu) {
        this.supplierSpu = supplierSpu;
    }

    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }

    public Integer getSaleType() {
        return saleType;
    }

    public void setSaleType(Integer saleType) {
        this.saleType = saleType;
    }

    public Date getPromotionCreateDate() {
        return promotionCreateDate;
    }

    public void setPromotionCreateDate(Date promotionCreateDate) {
        this.promotionCreateDate = promotionCreateDate;
    }

    public String getSupplierSku() {
        return supplierSku;
    }

    public void setSupplierSku(String supplierSku) {
        this.supplierSku = supplierSku;
    }

    public BigDecimal getCommodityPriceUs() {
        return commodityPriceUs;
    }

    public void setCommodityPriceUs(BigDecimal commodityPriceUs) {
        this.commodityPriceUs = commodityPriceUs;
    }

	public List<String> getLimitCountry() {
		return limitCountry;
	}

	public void setLimitCountry(List<String> limitCountry) {
		this.limitCountry = limitCountry;
	}

	public List<String> getLimitSeller() {
		return limitSeller;
	}

	public void setLimitSeller(List<String> limitSeller) {
		this.limitSeller = limitSeller;
	}

	public Long getBelongSellerId() {
		return belongSellerId;
	}

	public void setBelongSellerId(Long belongSellerId) {
		this.belongSellerId = belongSellerId;
	}

	public Boolean getIsUp() {
		return isUp;
	}

	public void setIsUp(Boolean isUp) {
		this.isUp = isUp;
	}

	public int getMultiPriceFlag() {
		return multiPriceFlag;
	}

	public void setMultiPriceFlag(int multiPriceFlag) {
		this.multiPriceFlag = multiPriceFlag;
	}
	
    
}