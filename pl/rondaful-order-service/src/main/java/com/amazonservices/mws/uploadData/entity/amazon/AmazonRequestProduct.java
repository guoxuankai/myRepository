package com.amazonservices.mws.uploadData.entity.amazon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import com.rondaful.cloud.seller.generated.ConditionInfo;
import com.rondaful.cloud.seller.generated.ProductImage;

import io.swagger.annotations.ApiModelProperty;

/**
 * 接受刊登接交的参数
 * @author ouxiangfeng
 * Listings.xsd
 *
 */
public class AmazonRequestProduct<T> implements java.io.Serializable {
	
	
	/** ============头部 部分 begin ==================== */
	/** 账号 */
	@NotBlank(message="卖家账号id不能为空")
	@ApiModelProperty(value="卖家账号的id",required=true)
	private String merchantIdentifier;
	
	/** 站点Id */
	//private String MarketplaceIdListId;
	/** 站点国家代码  */
	@NotBlank(message="国家编码(站点)不能为空")
	@ApiModelProperty(value="国家编码",allowableValues="BR,CA,MX,US,DE,ES,FR,GB,IN,IT,TR,AU,JP,CN",required=true)
	private String countryCode;
	/** ============头部 部分 end ==================== */
	
	
	
	/** 产品分类  对应amazon_category表中的attributes，如美国站点：item_type_keyword，其它站点:recommended_browse_nodes， */
	// 如果是非美国站：product.getDescriptionData().getRecommendedBrowseNode().add(e)
	// 美国站：product.getDescriptionData().getItemType().add(e)
	@ApiModelProperty(value="分类属性标识，[0]=第一分类的分类id,[1]=第二分类的分类id",required=true)
	@NotEmpty(message="产品分类不能这空")
	private Long productCategory[] ;
	
	/** 当前选择的模板 */
	@ApiModelProperty(value="分类模板，当前选择的模板名称，如：Sports",required=true)
	@NotBlank(message="分类模板不能为空")
	private String templatesName;
	
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
	@ApiModelProperty(value="标准的商品编码",required=true)
	@NotBlank(message="商品编码不能为空")
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
	@Max(value=10000000,message="库存数量不能大于10000000")
	private Long quantity;
	
	/** 品牌 DescriptionData.brand  */
	@ApiModelProperty(value="品牌")
	private String brand;
	
	/** Price.xsd Price.standardPrice 标准 价格 */
	@ApiModelProperty(value="标准 价格 ",required=true)
	@DecimalMin(value="0.01",message="价格不能小于0.01元")
	private BigDecimal standardPrice;
	/** Price.xsd Price BaseCurrencyCodeWithDefault 单位 */
	// USD,GBP,EUR,JPY,CAD,CNY,INR,AUD,BRL,MXN,TRY 
	@ApiModelProperty(value="标准 价格所属货币类型：USD,GBP,EUR,JPY,CAD,CNY,INR,AUD,BRL,MXN,TRY ",
			allowableValues ="USD,GBP,EUR,JPY,CAD,CNY,INR,AUD,BRL,MXN,TRY",required=true)
	@NotBlank(message="必须选择一个货币类型类型")
	private String standardPriceUnit;
	
	/** Product.xsd.manufacturer 制造商  */
	@ApiModelProperty(value="制造商 ")
	private String manufacturer;
	
	/** partNumber Product.xsd.MfrPartNumber 零件号码*/
	@ApiModelProperty(value="零件号码 ")
	private String mfrPartNumber;
	
	/** 标题/父体标题  Product.xsd.DescriptionData.title */
	@ApiModelProperty(value="标题/父体标题 ",required=true)
	@NotBlank(message="标题不能为空")
	private String title;
	
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
	private List<String> searchTerms = new ArrayList<>();
	
	/* 尺寸  DescriptionData Product.Dimensions.length ,width ,height*/
	@ApiModelProperty(value="尺寸单位：MM,CM,M,IN,FT,inches,feet,meters,decimeters,centimeters,millimeters,micrometers,nanometers,picometers"
			,allowableValues="MM,CM,M,IN,FT,inches,feet,meters,decimeters,centimeters,millimeters,micrometers,nanometers,picometers")
	private String dimensionUnitOfMeasure;
	@ApiModelProperty(value="尺寸长")
	private Long dimensionLength;
	@ApiModelProperty(value="尺寸宽")
	private Long dimensionWidth;
	@ApiModelProperty(value="尺寸高")
	private Long dimensionHeight;
    
    /** 重量单位 **/
	@ApiModelProperty(value="重量单位:GR,KG,OZ,LB,MG",allowableValues="GR,KG,OZ,LB,MG")
	private String weightUnitOfMeasure;
	/** 重量 包装前重量 Product.DescriptionData.PackageWeight 包装后     **/
	// PositiveWeightDimension packageWeight;
	@ApiModelProperty(value="包装前重量")
	private Long packageWeight;
	/**重量 包装后重量 Product.DescriptionData.ItemWeight 包装前 */ 
	@ApiModelProperty(value="包装后重量")
	private Long itemWeight;

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
    private String categoryPropertyJson;
   
	/** 临时 属性 */
	private String version;
	private String batchNo;
	
	private String ext;
	
    public AmazonRequestProduct() {}
    
///////////////////////////////get set/////////////////////////////////////////
    
    
    
	public String getMerchantIdentifier() {
		return merchantIdentifier;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public Long[] getProductCategory() {
		return productCategory;
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
		return brand;
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
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getMfrPartNumber() {
		return mfrPartNumber;
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
		this.bulletPoint = bulletPoint;
	}

	public List<String> getSearchTerms() {
		return searchTerms;
	}

	public void setSearchTerms(List<String> searchTerms) {
		this.searchTerms = searchTerms;
	}

	public String getDimensionUnitOfMeasure() {
		return dimensionUnitOfMeasure;
	}

	public void setDimensionUnitOfMeasure(String dimensionUnitOfMeasure) {
		this.dimensionUnitOfMeasure = dimensionUnitOfMeasure;
	}

	public Long getDimensionLength() {
		return dimensionLength;
	}

	public void setDimensionLength(Long dimensionLength) {
		this.dimensionLength = dimensionLength;
	}

	public Long getDimensionWidth() {
		return dimensionWidth;
	}

	public void setDimensionWidth(Long dimensionWidth) {
		this.dimensionWidth = dimensionWidth;
	}

	public Long getDimensionHeight() {
		return dimensionHeight;
	}

	public void setDimensionHeight(Long dimensionHeight) {
		this.dimensionHeight = dimensionHeight;
	}

	public String getWeightUnitOfMeasure() {
		return weightUnitOfMeasure;
	}

	public void setWeightUnitOfMeasure(String weightUnitOfMeasure) {
		this.weightUnitOfMeasure = weightUnitOfMeasure;
	}

	public Long getPackageWeight() {
		return packageWeight;
	}

	public void setPackageWeight(Long packageWeight) {
		this.packageWeight = packageWeight;
	}

	public Long getItemWeight() {
		return itemWeight;
	}

	public void setItemWeight(Long itemWeight) {
		this.itemWeight = itemWeight;
	}

	public ConditionInfo getConditionInfo() {
		return conditionInfo;
	}

	public void setConditionInfo(ConditionInfo conditionInfo) {
		this.conditionInfo = conditionInfo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategoryPropertyJson() {
		return categoryPropertyJson;
	}

	public void setCategoryPropertyJson(String categoryPropertyJson) {
		this.categoryPropertyJson = categoryPropertyJson;
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
