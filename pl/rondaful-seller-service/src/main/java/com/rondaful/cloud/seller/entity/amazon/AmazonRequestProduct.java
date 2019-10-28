package com.rondaful.cloud.seller.entity.amazon;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.rondaful.cloud.common.utils.JsoupUtil;
import com.rondaful.cloud.seller.generated.ConditionInfo;
import com.rondaful.cloud.seller.generated.ProductImage;
import io.swagger.annotations.ApiModelProperty;

import jodd.util.ArraysUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 接受刊登接交的参数
 * @author ouxiangfeng
 * Listings.xsd
 *
 */
public class AmazonRequestProduct<T> implements java.io.Serializable {

	/** ===================规则匹配所需要参数部分========================*/
	@ApiModelProperty(value = "变体主题，使用规则生成时使用")
	@JsonIgnore
	private String variationTheme;                            //todo

	@ApiModelProperty(value = "店铺名称，使用规则生成时使用")
	@JsonIgnore
	private String shopName;                                    //todo

	@ApiModelProperty(value = "店铺Id，使用规则生成时使用")
	@JsonIgnore
	private String shopId;

	@ApiModelProperty(value = "asin")                          //todo
	private String asin;

	@ApiModelProperty(value = "单个商品项的刊登状态 '")
	private Integer subPublishStatus;

	@ApiModelProperty(value = "规则模板ID")
	private Long tempalteRuleId;


	/** ============头部 部分 begin ==================== */
	/** 账号 */
	@NotBlank(message="卖家账号id不能为空")
	@ApiModelProperty(value="卖家账号的id",required=true)
	private String merchantIdentifier;                          //todo
	
	/** 站点Id(注意，之前是传入code，现在是站点id) */
	/** 站点国家代码  */
	@NotBlank(message="国家编码(站点)不能为空")
	@ApiModelProperty(value="站点Id",required=true)
	private String countryCode;                                //todo

	@ApiModelProperty(value = "变体数据")
	private JSONObject vari;

	@ApiModelProperty(value = "品连得SPU")
	private String spu;
	/** ============头部 部分 end ==================== */
	
	
	
	/** 产品分类  对应amazon_category表中的attributes，如美国站点：item_type_keyword，其它站点:recommended_browse_nodes， */
	// 如果是非美国站：product.getDescriptionData().getRecommendedBrowseNode().add(e)
	// 美国站：product.getDescriptionData().getItemType().add(e)
	@ApiModelProperty(value="分类属性标识，[0]=第一分类的分类id,[1]=第二分类的分类id",required=true)
	@NotEmpty(message="产品分类不能这空")
	private Long productCategory[] ;

	@ApiModelProperty(value="分类属性标识（品连），[0]=第一分类的分类id,[1]=第二分类的分类id",required=true)
	private Long productCategoryPLId[] ;


	@ApiModelProperty(value = "第一分类路径")
	private String productCategory1Path;


	@ApiModelProperty(value = "第二分类路径")
	private String productCategory2Path;
	
	/** 当前选择的模板 */
	@ApiModelProperty(value="分类模板，当前选择的模板名称，如：Sports",required=true)
	@NotBlank(message="分类模板不能为空")
	private String templatesName;
	
	/** 当前选择的模板 */
	@ApiModelProperty(value="分类模板，二级模板",required=true)
    // @NotBlank(message="分类模板不能为空")
	private String templatesName2;
	
	
	/** 当前选择的子模板 */
	//@ApiModelProperty(value="分类模板，当前选择的模板名称，如：SportingGoods",required=true)
	//private String templatesChildName;

	/** 是否多属性 */
	@ApiModelProperty(value="是否为多属性，false否/true是",allowableValues="true,false", hidden=false,required=true)
	private Boolean isMultiattribute = Boolean.FALSE;
	
	/** Inventory.xsd 发货时间 ：从订单生成到发货之间的天数，默认2天内发货 (1到30之间的整数)。*/
	@ApiModelProperty(value="发货时间 ：从订单生成到发货之间的天数，默认2天内发货 (1到30之间的整数)")
	@Min(value=1,message="发货时间不能小于1天")
	@Max(value=30,message="发货时间不能大于30天")
	private Integer fulfillmentLatency;
	
	
	/** 品连sku */
	@ApiModelProperty(value="品连sku",required=true)
	@NotBlank(message="品连sku不能为空")
	private String plSku;
	
	/** 平台sku 对应product.xsd.SKU */
	@ApiModelProperty(value="平台sku",required=true)
	@NotBlank(message="平台sku不能为空")
	private String sku;
	
	
	/** 商品编码 */
	@ApiModelProperty(value="标准的商品编码"/*,required=true*/)
	// @NotBlank(message="商品编码不能为空")
	private String standardProductID;
	
	/** 类型  ISBN,  UPC, ASIN, GTIN ,GCID ,PZN */
	@ApiModelProperty(value="标准的商品编码对应的类型 : ISBN,  UPC, ASIN, GTIN ,GCID ,PZN", allowableValues ="ISBN,  UPC, ASIN, GTIN ,GCID ,PZN", required=true)
	@NotBlank(message="必须选择一个商品编码类型")
	private String standardProductType;
	
	/** Price.xsd 价格 */
//	@ApiModelProperty(value="价格",required=true)
//	private BigDecimal pricing;
	
	/** 货币类型 */
//	@ApiModelProperty(value="价格所属的货币类型:USD,GBP,EUR,JPY,CAD,CNY,INR,MXN,BRL,AUD,TRY",required=true)
//	private String currencyCode;
	
	/** Inventory.xsd */
	/** 库存数量   */
	@ApiModelProperty(value="库存数量",required=true)
	@Min(value=1,message="库存数量不能小于1")
	@Max(value=999999999,message="库存数量不能大于999999999")
	private Long quantity;
	
	/** 品牌 DescriptionData.brand  */
	@ApiModelProperty(value="品牌")
	private String brand;
	
	/** Price.xsd Price.standardPrice 标准 价格 */
	@ApiModelProperty(value="标准 价格 ",required=true)
	@DecimalMin(value="0.001",message="价格不能小于0.001元")
	@DecimalMax(value="999999999",message="价格不能大于999999999元")
	private BigDecimal standardPrice;
	/** Price.xsd Price BaseCurrencyCodeWithDefault 单位 */
	// USD,GBP,EUR,JPY,CAD,CNY,INR,AUD,BRL,MXN,TRY 
	@ApiModelProperty(value="标准 价格所属货币类型：USD,GBP,EUR,JPY,CAD,CNY,INR,AUD,BRL,MXN,TRY ",
			allowableValues ="USD,GBP,EUR,JPY,CAD,CNY,INR,AUD,BRL,MXN,TRY",required=true)
	@NotBlank(message="必须选择一个货币类型类型")
	private String standardPriceUnit;

	@ApiModelProperty(value="利润率")
	private String profitMargin;
	
	/** Product.xsd.manufacturer 制造商  */
	@ApiModelProperty(value="制造商 ")
	@NotBlank(message="制造商不能为空")
	private String manufacturer;
	
	/** partNumber Product.xsd.MfrPartNumber 零件号码*/
	@ApiModelProperty(value="零件号码 ")
	@NotBlank(message="Part Number不能为空")
	private String mfrPartNumber;
	
	/** 标题/父体标题  Product.xsd.DescriptionData.title */
	@ApiModelProperty(value="标题/父体标题 ",required=true)
	@NotBlank(message="标题不能为空")
	private String title;

	/**导出字段 */
	@ApiModelProperty(value="导出字段")
	private String exportUse;


	/** 父体标题   */
	// private String prentTitle;
	/** ================================ */
	
	/* =================== so List 变体主题 ========================= */


	@ApiModelProperty(value="变体列表 ")
	List<AmazonRequestProduct> varRequestProductList = new ArrayList<>() ;
	/* ============================================ */
	
	/* ===============多组图 so list============================= */
	/**
	 * 图片必须符合以下要求:
		产品必须填充至少85%的图像。图像必须只显示待售的产品，很少或没有道具，没有标识、水印或嵌入图像。图像可能只包含产品的一部分文本。
		主要图片必须有纯白色的背景，必须是照片(不是图画)，并且不能包含排除的附件。
		图像的最长端必须至少有1000像素，最短端至少有500像素才能缩放。
		图像最长不得超过10000像素。
		JPEG是首选的图像格式，但您也可以使用TIFF和GIF文件。
	 */
	/** AmazonEnvelope.Message.ProductImage    ProductImage.xsd  */
	/** 图片类型 图片地址*/
	@ApiModelProperty(value="图片列表 ",required=true)
	private List<ProductImage> images = new ArrayList<>();
	
	
	/* =========================================== */
	/** 产品亮点 */ 
	@ApiModelProperty(value="产品亮点列表 ")
	private List<String> bulletPoint =  new ArrayList<>();
	
	/** 搜索关键词 */
	@ApiModelProperty(value="搜索关键词")
	@NotEmpty
	private List<String> searchTerms = new ArrayList<>();
	
	/* 尺寸  DescriptionData Product.Dimensions.length ,width ,height*/
	@ApiModelProperty(value="尺寸单位：MM,CM,M,IN,FT,inches,feet,meters,decimeters,centimeters,millimeters,micrometers,nanometers,picometers"
			,allowableValues="MM,CM,M,IN,FT,inches,feet,meters,decimeters,centimeters,millimeters,micrometers,nanometers,picometers")
	private String dimensionUnitOfMeasure;
	
	@DecimalMin(value="0.000",message="尺寸长不能小于0.001")
	@DecimalMax(value="999999999",message="尺寸长不能大于999999999")
	@ApiModelProperty(value="尺寸长")
	private BigDecimal dimensionLength;
	
	@ApiModelProperty(value="尺寸宽")
	@DecimalMin(value="0.000",message="尺寸宽不能小于0.001")
	@DecimalMax(value="999999999",message="尺寸宽不能大于999999999")
	private BigDecimal dimensionWidth;
	
	@DecimalMin(value="0.000",message="尺寸高不能小于0.001")
	@DecimalMax(value="999999999",message="尺寸高不能大于999999999")
	@ApiModelProperty(value="尺寸高")
	private BigDecimal dimensionHeight;

	public String getExportUse() {
		return exportUse;
	}

	public void setExportUse(String exportUse) {
		this.exportUse = exportUse;
	}

	/** 重量单位 **/
	@ApiModelProperty(value="重量单位:GR,KG,OZ,LB,MG",allowableValues="GR,KG,OZ,LB,MG")
	private String weightUnitOfMeasure;
	
	/** 重量 包装前重量 Product.DescriptionData.PackageWeight 包装后     **/
	// PositiveWeightDimension packageWeight;
	@DecimalMin(value="0.000",message="包装前重量不能小于0.001")
	@DecimalMax(value="999999999",message="包装前重量不能大于999999999")
	@ApiModelProperty(value="包装前重量")
	private BigDecimal packageWeight;
	
	
	/**重量 包装后重量 Product.DescriptionData.ItemWeight 包装前 */ 
	@ApiModelProperty(value="包装后重量")
	@DecimalMin(value="0.000",message="包装后重量不能小于0.001")
	@DecimalMax(value="999999999",message="包装后重量不能大于999999999")
	private BigDecimal itemWeight;

    /** 物品状况+物品状况描述  Product.Condition
     conditionType=New,UsedLikeNew,UsedVeryGood,UsedGood,UsedAcceptable,CollectibleLikeNew,CollectibleVeryGood,CollectibleGood,CollectibleAcceptable,Refurbished,Club
     conditionNote=text
     * */
	@ApiModelProperty(value="物品状况",
			notes="type:New,UsedLikeNew,UsedVeryGood,UsedGood,UsedAcceptable,CollectibleLikeNew,CollectibleVeryGood,CollectibleGood,CollectibleAcceptable,Refurbished,Club",
			required=true)
    ConditionInfo conditionInfo;
    
    
    /** 产品描述  Product.DescriptionData.Description */
	@ApiModelProperty(value="产品描述 ")
    private String description;
    
    /** 当前 分类的属性  new Product.ProductData().setSports(value); */
	@ApiModelProperty(value="当前 分类的属性的json串,如果是变体，这个参数是变体属性的json串,如果有必要，可以通知后台增加专为变体承载的参数。productType必需"
			,notes="{\"productType\":\"SportingGoods\",\"alarm\":\"alarm\",\"availableCourses\":\"availableCourses\",\"batteryLife\":[],\"styleKeywords\":[]}")
	@NotBlank(message="商品属性不能为空")
    private String categoryPropertyJson;
	
	/** add v2.2.0_5.17
	 使用此字段可指示要出售的商品中包含的单元数，以便每个单元都是为单独销售打包的。
	  */
	private BigInteger itemPackageQuantity;
	/** add v2.2.0_5.17
	 使用此字段指示要出售的项目中包含的离散项目的数量，以便每个项目都不是为单独销售而打包的。例如，如果您销售的是一箱10包袜子，而每个包包含3双袜子，那么这个箱子的temPackageQuantity = 10, NumberOfItems = 30。
	 */
	private BigInteger numberOfItems;	
	
	/** add v2.2.0_5.17 */
	private String designer;
	private List<String> targetAudience = new ArrayList<>();
	private List<String> supplierDeclaredDGHZRegulation = new ArrayList<>();
	private String hazmatUnitedNationsRegulatoryID;
	private String safetyDataSheetURL;
	
   
	/** 临时 属性 */
	private String version;
	private String batchNo;
	private String ext;
	
	@ApiModelProperty(value="是否有必填项字段,0没有1有 ")
	private Integer hasRequired;
	
	@ApiModelProperty(value="物流方式1价格最低  2综合最优  3时效最快")
	private Integer logisticsType;
	
	@ApiModelProperty(value="发货仓库Id ")
	private Integer warehouseId;
	
    @ApiModelProperty(value = "物流编码")
    private String logisticsCode;

	/*listing id */
	private Long id;
	
	//sku对应商品的状态 1：待上架（下架），3：已上架
	@ApiModelProperty(value="sku对应商品的状态 1：待上架（下架），3：已上架")
	private Integer plSkuStatus;
	
	@ApiModelProperty(value="对应sku到对应仓库的库存数量（当为空的时候代表老数据或者该仓库不存在此sku）")
	private Long plSkuCount;
	
	//计价模板,用于刊登页面显示相关json信息
	private String computeTemplateJson;
	
    //销售人员Id
    private Integer saleUserId;
    
    //捆绑销售数量
    private Integer plSkuSaleNum;

    
    public AmazonRequestProduct() {}
     
///////////////////////////////get set/////////////////////////////////////////





	public String getProfitMargin() {
		return profitMargin;
	}

	public Integer getPlSkuSaleNum() {
		return plSkuSaleNum;
	}

	public void setPlSkuSaleNum(Integer plSkuSaleNum) {
		this.plSkuSaleNum = plSkuSaleNum;
	}

	public void setProfitMargin(String profitMargin) {
		this.profitMargin = profitMargin;
	}

	public String getMerchantIdentifier() {
		return merchantIdentifier;
	}

	public Integer getSaleUserId() {
		return saleUserId;
	}

	public void setSaleUserId(Integer saleUserId) {
		this.saleUserId = saleUserId;
	}

	public String getComputeTemplateJson() {
		return computeTemplateJson;
	}

	public void setComputeTemplateJson(String computeTemplateJson) {
		this.computeTemplateJson = computeTemplateJson;
	}

	public String getLogisticsCode() {
		return logisticsCode;
	}

	public void setLogisticsCode(String logisticsCode) {
		this.logisticsCode = logisticsCode;
	}

	public Long getPlSkuCount() {
		return plSkuCount;
	}

	public void setPlSkuCount(Long plSkuCount) {
		this.plSkuCount = plSkuCount;
	}

	public Integer getPlSkuStatus() {
		return plSkuStatus;
	}

	public void setPlSkuStatus(Integer plSkuStatus) {
		this.plSkuStatus = plSkuStatus;
	}

	public Integer getHasRequired() {
		return hasRequired;
	}

	public void setHasRequired(Integer hasRequired) {
		this.hasRequired = hasRequired;
	}

	public String getDesigner() {
		if(StringUtils.isBlank(designer))
		{
			return null;
		}
		return designer;
	}

	public void setDesigner(String designer) {
		this.designer = designer;
	}

	

	 

	public List<String> getTargetAudience() {
		return targetAudience;
	}

	public void setTargetAudience(List<String> targetAudience) {
		this.targetAudience = targetAudience;
	}

	

	public List<String> getSupplierDeclaredDGHZRegulation() {
		return supplierDeclaredDGHZRegulation;
	}

	public void setSupplierDeclaredDGHZRegulation(List<String> supplierDeclaredDGHZRegulation) {
		this.supplierDeclaredDGHZRegulation = supplierDeclaredDGHZRegulation;
	}

	public String getHazmatUnitedNationsRegulatoryID() {
		if(StringUtils.isBlank(hazmatUnitedNationsRegulatoryID))
		{
			return null;
		}
		return hazmatUnitedNationsRegulatoryID;
	}

	public void setHazmatUnitedNationsRegulatoryID(String hazmatUnitedNationsRegulatoryID) {
		this.hazmatUnitedNationsRegulatoryID = hazmatUnitedNationsRegulatoryID;
	}

	public String getSafetyDataSheetURL() {
		if(StringUtils.isBlank(safetyDataSheetURL))
		{
			return null;
		}
		return safetyDataSheetURL;
	}

	public void setSafetyDataSheetURL(String safetyDataSheetURL) {
		this.safetyDataSheetURL = safetyDataSheetURL;
	}

	public BigInteger getItemPackageQuantity() {
		return itemPackageQuantity;
	}

	public void setItemPackageQuantity(BigInteger itemPackageQuantity) {
		this.itemPackageQuantity = itemPackageQuantity;
	}

	public BigInteger getNumberOfItems() {
		return numberOfItems;
	}

	public void setNumberOfItems(BigInteger numberOfItems) {
		this.numberOfItems = numberOfItems;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTemplatesName2() {
		return templatesName2;
	}

	public void setTemplatesName2(String templatesName2) {
		this.templatesName2 = templatesName2;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = JsoupUtil.filterEmoji(ext);
	}

	public Long[] getProductCategory() {
		return productCategory;
	}

	public Integer[] getProductCategoryForInteger() {
		ArrayList<Integer> integers = new ArrayList<>();
		Integer[] arr = new Integer[integers.size()];
		if(productCategory == null || productCategory.length == 0)
			return arr;
		for(int i = 0;i< productCategory.length;i++){
			if(productCategory[i] == null)
			{
				continue;
			}
			integers.add(productCategory[i].intValue());
		}

		return integers.toArray(arr);
	}
	public void setProductCategory(Long[] productCategory) {
		this.productCategory = productCategory;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public void setMerchantIdentifier(String merchantIdentifier) {
		this.merchantIdentifier = merchantIdentifier;
	}

 

	/**
	 * 获取的是站点Id，不是国家编码了
	 * @return
	 */
	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	 

	public String getTemplatesName() {
		return templatesName;
	}

	public void setTemplatesName(String templatesName) {
		this.templatesName = templatesName;
	}

 
	public Integer getFulfillmentLatency() {
		return fulfillmentLatency;
	}

	public void setFulfillmentLatency(Integer fulfillmentLatency) {
		this.fulfillmentLatency = fulfillmentLatency;
	}

	public String getPlSku() {
		return plSku;
	}

	public void setPlSku(String plSku) {
		this.plSku = plSku;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getStandardProductID() {
		return standardProductID;
	}

	public void setStandardProductID(String standardProductID) {
		this.standardProductID = standardProductID;
	}

	public String getStandardProductType() {
		return standardProductType;
	}

	public void setStandardProductType(String standardProductType) {
		this.standardProductType = standardProductType;
	}

 
	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public String getBrand() {
		return (brand == null || "".equals(brand.trim())) ? null : brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public BigDecimal getStandardPrice() {
		return standardPrice;
	}

	public void setStandardPrice(BigDecimal standardPrice) {
		this.standardPrice = standardPrice;
	}

	public String getStandardPriceUnit() {
		return standardPriceUnit;
	}

	public void setStandardPriceUnit(String standardPriceUnit) {
		this.standardPriceUnit = standardPriceUnit;
	}

	public String getManufacturer() {
		return (manufacturer == null || "".equals(manufacturer.trim())) ? null : manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getMfrPartNumber() {
		return (mfrPartNumber == null || "".equals(mfrPartNumber.trim())) ? null : mfrPartNumber;
	}

	public void setMfrPartNumber(String mfrPartNumber) {
		this.mfrPartNumber = mfrPartNumber;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	 
 

	public Boolean getIsMultiattribute() {
		/*if(CollectionUtils.isNotEmpty(this.getVarRequestProductList()))
		{
			return Boolean.TRUE;
		}
		return Boolean.FALSE;*/
		return isMultiattribute;
	}

	public void setIsMultiattribute(Boolean isMultiattribute) {
		this.isMultiattribute = isMultiattribute;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<AmazonRequestProduct> getVarRequestProductList() {
		return varRequestProductList;
	}


	public void setVarRequestProductList(List<AmazonRequestProduct> varRequestProductList) {
		this.varRequestProductList = varRequestProductList;
	}

	public List<ProductImage> getImages() {
		return images;
	}

	public void setImages(List<ProductImage> images) {
		this.images = images;
	}

	public List<String> getBulletPoint() {
		return bulletPoint;
	}

	public void setBulletPoint(List<String> bulletPoint) {

		this.bulletPoint = bulletPoint.stream().filter(
				var -> StringUtils.isNotBlank(var)).collect(Collectors.toList());
		ArrayList<String> strings = new ArrayList<>();
		for (String var :  this.bulletPoint) {
			strings.add(JsoupUtil.filterEmoji(var));
		}
		this.bulletPoint = strings;
	}

	public List<String> getSearchTerms() {
		return searchTerms;
	}

	public void setSearchTerms(List<String> searchTerms) {
		this.searchTerms = searchTerms.stream().filter(
				var -> StringUtils.isNotBlank(var)).collect(Collectors.toList());
		ArrayList<String> strings = new ArrayList<>();
		for(String s : this.searchTerms){
			strings.add(JsoupUtil.filterEmoji(s));
		}
		this.searchTerms = strings;

	}

	public String getDimensionUnitOfMeasure() {
		return dimensionUnitOfMeasure;
	}

	public void setDimensionUnitOfMeasure(String dimensionUnitOfMeasure) {
		this.dimensionUnitOfMeasure = dimensionUnitOfMeasure;
	}

	

	public String getWeightUnitOfMeasure() {
		return weightUnitOfMeasure;
	}

	public void setWeightUnitOfMeasure(String weightUnitOfMeasure) {
		this.weightUnitOfMeasure = weightUnitOfMeasure;
	}

	

	public ConditionInfo getConditionInfo() {
		return conditionInfo;
	}

	public void setConditionInfo(ConditionInfo conditionInfo) {
		this.conditionInfo = conditionInfo;
	}

	public String getDescription() {
		return (description == null || "".equals(description.trim())) ? null : description;
	}

	public void setDescription(String description) {
		this.description = JsoupUtil.filterEmoji(description);
	}

	public String getCategoryPropertyJson() {
		return categoryPropertyJson;
	}

	public void setCategoryPropertyJson(String categoryPropertyJson) {
		this.categoryPropertyJson = categoryPropertyJson;
	}

	public BigDecimal getDimensionLength() {
		return dimensionLength;
	}

	public void setDimensionLength(BigDecimal dimensionLength) {
		this.dimensionLength = dimensionLength;
	}

	public BigDecimal getDimensionWidth() {
		return dimensionWidth;
	}

	public void setDimensionWidth(BigDecimal dimensionWidth) {
		this.dimensionWidth = dimensionWidth;
	}

	public BigDecimal getDimensionHeight() {
		return dimensionHeight;
	}

	public void setDimensionHeight(BigDecimal dimensionHeight) {
		this.dimensionHeight = dimensionHeight;
	}

	public BigDecimal getPackageWeight() {
		return packageWeight;
	}

	public void setPackageWeight(BigDecimal packageWeight) {
		this.packageWeight = packageWeight;
	}

	public BigDecimal getItemWeight() {
		return itemWeight;
	}

	public void setItemWeight(BigDecimal itemWeight) {
		this.itemWeight = itemWeight;
	}


	public String getVariationTheme() {
		return variationTheme;
	}

	public void setVariationTheme(String variationTheme) {
		this.variationTheme = variationTheme;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getProductCategory1Path() {
		return productCategory1Path;
	}

	public void setProductCategory1Path(String productCategory1Path) {
		this.productCategory1Path = productCategory1Path;
	}

	public String getProductCategory2Path() {
		return productCategory2Path;
	}

	public void setProductCategory2Path(String productCategory2Path) {
		this.productCategory2Path = productCategory2Path;
	}

	public JSONObject getVari() {
		return vari;
	}

	public void setVari(JSONObject vari) {
		this.vari = vari;
	}

	public String getSpu() {
		return spu;
	}

	public void setSpu(String spu) {
		this.spu = spu;
	}

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public Integer getSubPublishStatus() {
		return subPublishStatus;
	}

	public void setSubPublishStatus(Integer subPublishStatus) {
		this.subPublishStatus = subPublishStatus;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public Integer getLogisticsType() {
		return logisticsType;
	}

	public void setLogisticsType(Integer logisticsType) {
		this.logisticsType = logisticsType;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Long getTempalteRuleId() {
		return tempalteRuleId;
	}

	public void setTempalteRuleId(Long tempalteRuleId) {
		this.tempalteRuleId = tempalteRuleId;
	}

	public Long[] getProductCategoryPLId() {
		return productCategoryPLId;
	}

	public void setProductCategoryPLId(Long[] productCategoryPLId) {
		this.productCategoryPLId = productCategoryPLId;
	}

		/*public static void main(String [] s){

		String ss = "<p>staywithu-美国 ewew<br/>1<br/>1<br/>1&nbsp; &nbsp;\uD83E\uDD41<br/></p>";

		String pattern = "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\uD83E\uDD00-\uD83E\uDDFF]|[\u2600-\u27ff] ";
		ss.contains("\\")

		String re = JsoupUtil.filterEmoji(ss);
		System.out.println("111");


	}*/
}
