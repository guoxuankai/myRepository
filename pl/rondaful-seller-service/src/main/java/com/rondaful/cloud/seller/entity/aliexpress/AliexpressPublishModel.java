package com.rondaful.cloud.seller.entity.aliexpress;

import com.google.common.collect.Lists;
import com.rondaful.cloud.seller.entity.AliexpressProductCountryPrice;
import com.rondaful.cloud.seller.entity.AliexpressPublishListingAttribute;
import com.rondaful.cloud.seller.entity.AliexpressPublishListingProduct;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * 什么刊登详情
 * @author chenhan
 * Publish
 *
 */
@ApiModel(description = "速卖通详情AliexpressPublishModel")
public class AliexpressPublishModel implements java.io.Serializable {

	@ApiModelProperty(value = "主键id，自增")
	private Long id;

	@ApiModelProperty(value = "刊登状态 1: 草稿  2: 刊登中 3: 刊登失败 4:审核中  5: 审核失败,6:正在销售 7 已下架")
	private Integer publishStatus;

	@ApiModelProperty(value = "品连账号")
	private String plAccount;

	@ApiModelProperty(value = "卖家账号(自定义账号)")
	private String publishAccount;

	@ApiModelProperty(value = "授权id")
	private Long empowerId;

	@ApiModelProperty(value = "卖家ID")
	private String sellerId;

	@ApiModelProperty(value = "第一分类")
	private Long categoryId1;

	@ApiModelProperty(value = "第二分类")
	private Long categoryId2;

	@ApiModelProperty(value = "第三分类")
	private Long categoryId3;

	@ApiModelProperty(value = "第四分类")
	private Long categoryId4;

	@ApiModelProperty(value = "产品标题")
	private String title;

	@ApiModelProperty(value = "刊登类型 2：单属性 1：多属性")
	private Integer publishType;

	@ApiModelProperty(value = "品连spu或sku")
	private String plSpuSku;

	@ApiModelProperty(value = "品连spu")
	private String plSpu;

	@ApiModelProperty(value = "图片")
	private String productImage;

	@ApiModelProperty(value = "刊登图片")
	private String publishProductImage;

	@ApiModelProperty(value = "备注信息")
	private String remark;

	@ApiModelProperty(value = "商品详情")
	private String productDetails;

	@ApiModelProperty(value = "手机端描述")
	private Boolean mobileTerminal;

	@ApiModelProperty(value = "手机备注信息")
	private String mobileRemark;
	@ApiModelProperty(value = "刊登成功速卖通商品id")
	private Long itemId;
	//详情部分

	@ApiModelProperty(value = "选择属性")
	private String selectAttributes;

	@ApiModelProperty(value = "区域调价 percentage：按比例 relative：按金额 absolute：直接报价")
	private String regionalPricing;

	@ApiModelProperty(value = "批发价")
	private Boolean wholesale;

	@ApiModelProperty(value = "购买数量")
	private Integer bulkOrder;

	@ApiModelProperty(value = "价格基础上减免折扣")
	private Integer bulkDiscount;

	@ApiModelProperty(value = "库存扣减策略，总共有2种：下单减库存(place_order_withhold)和支付减库存(payment_success_deduct)。")
	private String reduceStrategy;

	@ApiModelProperty(value = "发货期(取值范围:1-60;单位:天)")
	private Integer deliveryTime;

	@ApiModelProperty(value = "最小计量单位")
	private Integer unit;

	@ApiModelProperty(value = "销售方式 1按件出售 2打包出售（价格按照包计算）")
	private Integer salesMethod;

	@ApiModelProperty(value = "每包件数。 打包销售情况，lotNum>1,非打包销售情况,lotNum=1")
	private Integer lotNum;

	@ApiModelProperty(value = "商品包装后的重量(公斤/袋)")
	private BigDecimal packagingWeight;

	@ApiModelProperty(value = "是否自定义计重.true为自定义计重,false反之")
	private Boolean isPackSell;

	@ApiModelProperty(value = "买家购买")
	private Integer buyersPurchase;

	@ApiModelProperty(value = "买家每多买")
	private Integer buyersMore;

	@ApiModelProperty(value = "重量增加")
	private BigDecimal increaseWeight;

	@ApiModelProperty(value = "长")
	private Integer packageLength;

	@ApiModelProperty(value = "宽")
	private Integer packageWidth;

	@ApiModelProperty(value = "高")
	private Integer packageHeight;

	@ApiModelProperty(value = "运费模板")
	private Long freightTemplateId;

	@ApiModelProperty(value = "服务模板")
	private Long promiseTemplateId;

	@ApiModelProperty(value = "商品分组")
	private Long groupId;

	@ApiModelProperty(value = "商品有效天数")
	private Integer wsValidNum;

	@ApiModelProperty(value = "是否是平台listing 0是历史刊登数据 1是新刊登 2速卖通平台")
	private Integer platformListing;

	@ApiModelProperty(value = "更新状态 1更新中2更新成功3更新失败")
	private Integer updateStatus;

	@ApiModelProperty(value = "收货国家")
	private String shipCountry;

	@ApiModelProperty(value = "物流类型")
	private String logisticsType;

	@ApiModelProperty(value = "发货仓库")
	private String warehouseCode;

	@ApiModelProperty(value = "物流时效")
	private String logisticsAging;

	@ApiModelProperty(value = "刊登商品AliexpressPublishListingProduct")
	private List<AliexpressPublishListingProduct> listProduct = Lists.newArrayList();
	@ApiModelProperty(value = "刊登属性AliexpressPublishListingAttribute")
	private List<AliexpressPublishListingAttribute> listAttribute = Lists.newArrayList();
	@ApiModelProperty(value = "刊登商品区域调价AliexpressProductCountryPrice")
	private List<AliexpressProductCountryPrice> listProductCountryPrice = Lists.newArrayList();

	public List<AliexpressProductCountryPrice> getListProductCountryPrice() {
		return listProductCountryPrice;
	}

	public void setListProductCountryPrice(List<AliexpressProductCountryPrice> listProductCountryPrice) {
		this.listProductCountryPrice = listProductCountryPrice;
	}

	public List<AliexpressPublishListingAttribute> getListAttribute() {
		return listAttribute;
	}

	public void setListAttribute(List<AliexpressPublishListingAttribute> listAttribute) {
		this.listAttribute = listAttribute;
	}

	public List<AliexpressPublishListingProduct> getListProduct() {
		return listProduct;
	}

	public void setListProduct(List<AliexpressPublishListingProduct> listProduct) {
		this.listProduct = listProduct;
	}

	//
	@ApiModelProperty(value = "前端要求 用此字段保存所有的数据信息")
	private String ext;

	public String getProductDetails() {
		return productDetails;
	}

	public void setProductDetails(String productDetails) {
		this.productDetails = productDetails;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPublishAccount() {
		return publishAccount;
	}

	public void setPublishAccount(String publishAccount) {
		this.publishAccount = publishAccount;
	}

	public String getSellerId() {
		return sellerId;
	}

	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	public Long getCategoryId1() {
		return categoryId1;
	}

	public void setCategoryId1(Long categoryId1) {
		this.categoryId1 = categoryId1;
	}

	public Long getCategoryId2() {
		return categoryId2;
	}

	public void setCategoryId2(Long categoryId2) {
		this.categoryId2 = categoryId2;
	}

	public Long getCategoryId3() {
		return categoryId3;
	}

	public void setCategoryId3(Long categoryId3) {
		this.categoryId3 = categoryId3;
	}

	public Long getCategoryId4() {
		return categoryId4;
	}

	public void setCategoryId4(Long categoryId4) {
		this.categoryId4 = categoryId4;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getPublishType() {
		return publishType;
	}

	public void setPublishType(Integer publishType) {
		this.publishType = publishType;
	}

	public String getPlSpuSku() {
		return plSpuSku;
	}

	public void setPlSpuSku(String plSpuSku) {
		this.plSpuSku = plSpuSku;
	}

	public String getProductImage() {
		return productImage;
	}

	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Boolean getMobileTerminal() {
		return mobileTerminal;
	}

	public void setMobileTerminal(Boolean mobileTerminal) {
		this.mobileTerminal = mobileTerminal;
	}

	public String getMobileRemark() {
		return mobileRemark;
	}

	public void setMobileRemark(String mobileRemark) {
		this.mobileRemark = mobileRemark;
	}

	public String getSelectAttributes() {
		return selectAttributes;
	}

	public void setSelectAttributes(String selectAttributes) {
		this.selectAttributes = selectAttributes;
	}

	public String getRegionalPricing() {
		return regionalPricing;
	}

	public void setRegionalPricing(String regionalPricing) {
		this.regionalPricing = regionalPricing;
	}

	public Boolean getWholesale() {
		return wholesale;
	}

	public void setWholesale(Boolean wholesale) {
		this.wholesale = wholesale;
	}

	public Integer getBulkOrder() {
		return bulkOrder;
	}

	public void setBulkOrder(Integer bulkOrder) {
		this.bulkOrder = bulkOrder;
	}

	public Integer getBulkDiscount() {
		return bulkDiscount;
	}

	public void setBulkDiscount(Integer bulkDiscount) {
		this.bulkDiscount = bulkDiscount;
	}

	public String getReduceStrategy() {
		return reduceStrategy;
	}

	public void setReduceStrategy(String reduceStrategy) {
		this.reduceStrategy = reduceStrategy;
	}

	public Integer getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(Integer deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public Integer getUnit() {
		return unit;
	}

	public void setUnit(Integer unit) {
		this.unit = unit;
	}

	public Integer getSalesMethod() {
		return salesMethod;
	}

	public void setSalesMethod(Integer salesMethod) {
		this.salesMethod = salesMethod;
	}

	public Integer getLotNum() {
		return lotNum;
	}

	public void setLotNum(Integer lotNum) {
		this.lotNum = lotNum;
	}

	public Boolean getIsPackSell() {
		return isPackSell;
	}

	public void setIsPackSell(Boolean isPackSell) {
		isPackSell = isPackSell;
	}

	public Integer getBuyersPurchase() {
		return buyersPurchase;
	}

	public void setBuyersPurchase(Integer buyersPurchase) {
		this.buyersPurchase = buyersPurchase;
	}

	public Integer getBuyersMore() {
		return buyersMore;
	}

	public void setBuyersMore(Integer buyersMore) {
		this.buyersMore = buyersMore;
	}

	public BigDecimal getIncreaseWeight() {
		return increaseWeight;
	}

	public void setIncreaseWeight(BigDecimal increaseWeight) {
		this.increaseWeight = increaseWeight;
	}

	public Integer getPackageLength() {
		return packageLength;
	}

	public void setPackageLength(Integer packageLength) {
		this.packageLength = packageLength;
	}

	public Integer getPackageWidth() {
		return packageWidth;
	}

	public void setPackageWidth(Integer packageWidth) {
		this.packageWidth = packageWidth;
	}

	public Integer getPackageHeight() {
		return packageHeight;
	}

	public void setPackageHeight(Integer packageHeight) {
		this.packageHeight = packageHeight;
	}

	public Long getFreightTemplateId() {
		return freightTemplateId;
	}

	public void setFreightTemplateId(Long freightTemplateId) {
		this.freightTemplateId = freightTemplateId;
	}

	public Long getPromiseTemplateId() {
		return promiseTemplateId;
	}

	public void setPromiseTemplateId(Long promiseTemplateId) {
		this.promiseTemplateId = promiseTemplateId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Integer getWsValidNum() {
		return wsValidNum;
	}

	public void setWsValidNum(Integer wsValidNum) {
		this.wsValidNum = wsValidNum;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public Long getEmpowerId() {
		return empowerId;
	}

	public void setEmpowerId(Long empowerId) {
		this.empowerId = empowerId;
	}

	public String getPlAccount() {
		return plAccount;
	}

	public void setPlAccount(String plAccount) {
		this.plAccount = plAccount;
	}

	public BigDecimal getPackagingWeight() {
		return packagingWeight;
	}

	public void setPackagingWeight(BigDecimal packagingWeight) {
		this.packagingWeight = packagingWeight;
	}

	public Integer getPublishStatus() {
		return publishStatus;
	}

	public void setPublishStatus(Integer publishStatus) {
		this.publishStatus = publishStatus;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getPublishProductImage() {
		return publishProductImage;
	}

	public void setPublishProductImage(String publishProductImage) {
		this.publishProductImage = publishProductImage;
	}

	public Integer getPlatformListing() {
		return platformListing;
	}

	public void setPlatformListing(Integer platformListing) {
		this.platformListing = platformListing;
	}

	public Integer getUpdateStatus() {
		return updateStatus;
	}

	public void setUpdateStatus(Integer updateStatus) {
		this.updateStatus = updateStatus;
	}

	public String getShipCountry() {
		return shipCountry;
	}

	public void setShipCountry(String shipCountry) {
		this.shipCountry = shipCountry;
	}

	public String getLogisticsType() {
		return logisticsType;
	}

	public void setLogisticsType(String logisticsType) {
		this.logisticsType = logisticsType;
	}

	public String getWarehouseCode() {
		return warehouseCode;
	}

	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}

	public String getLogisticsAging() {
		return logisticsAging;
	}

	public void setLogisticsAging(String logisticsAging) {
		this.logisticsAging = logisticsAging;
	}

	public String getPlSpu() {
		return plSpu;
	}

	public void setPlSpu(String plSpu) {
		this.plSpu = plSpu;
	}
}
