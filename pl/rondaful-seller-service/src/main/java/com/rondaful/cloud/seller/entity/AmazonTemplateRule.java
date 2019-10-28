package com.rondaful.cloud.seller.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;

/**
 * amazon刊登模板规则
 */
@ApiModel(description = "amazon刊登模板规则对象")
public class AmazonTemplateRule implements Serializable {


	
    //@ApiModelProperty(value = "自增id", required = true)
    private Long id;

    @ApiModelProperty(value = "授权ID")
    private Long empowerId;


    @NotBlank(message="模板名称不能为空")
    @ApiModelProperty(value = "模板名称")
    private String templateName;

    @ApiModelProperty(value = "是否默认模板 0 ：是 1：否  2:全局默认模板")
    private Integer defaultTemplate;


    @NotBlank(message="商品第一分类不能为空")
    @ApiModelProperty(value = "商品第一分类, [{\"检索分类映射表\":\"检索分类映射表\",\"no\":\"1\"}]")
    private String categoryFirstRule;

    @NotBlank(message="商品第二分类不能为空")
    @ApiModelProperty(value = "商品第二分类, [{\"不设置商品第二分类\":\"不设置商品第二分类\",\"no\":\"1\"},{\"检索分类映射表\":\"检索分类映射表\",\"no\":\"1\"}]")
    private String categorySecondRule;

    
    @ApiModelProperty(value = "刊登类型，1:单属性格式,2:多属性格式,3:自动确定")
    private Integer publishType;


    @ApiModelProperty(value = "从订单生成到发货之间的天数，默认2天内发货 (1到30之间的整数)")
    private Integer fulfillmentLatency;

    @NotBlank(message="平台SKU不能为空")
    @ApiModelProperty(value = "平台SKU ， [{\"固定值\":\"1111\",\"no\":\"1\"},{\"商品名称\":\"商品名称\",\"no\":\"4\"},{\"品连sku\":\"品连sku\",\"no\":\"2\"}]")
    private String platformSkuRule;

    @ApiModelProperty(value = "品牌名    [{\"默认值\":\"1111\",\"no\":\"1\"},{\"取店铺名称\":\"取店铺名称\",\"no\":\"4\"},{\"实际品牌名称\":\"实际品牌名称\",\"no\":\"2\"}，{\"实际品牌名称若为空\":\"55555555\",\"no\":\"2\"}]")
    private String brandRule;

    @NotBlank(message="商品标题不能为空")
    @ApiModelProperty(value = "商品标题    [{\"商品英文名\":\"商品英文名\",\"no\":\"1\"},{\"品牌名称+商品英文名\":\"品牌名称+商品英文名\",\"no\":\"4\"}]")
    private String productTitleRule;

    @ApiModelProperty(value = "商品编码    [{\"用户自行填写\":\"用户自行填写\",\"no\":\"1\"},{\"自动获取UPC\":\"自动获取UPC\",\"no\":\"2\"},{\"自动获取EAN\":\"自动获取EAN\",\"no\":\"3\"}]")
    private String productNoRule;

    //@NotBlank(message="价格不能为空")
    @ApiModelProperty(value = "价格  [{\"用户自行填写\":\"用户自行填写\",\"no\":\"1\"}]")
    private String productPriceRule;

    @NotBlank(message="可售数不能为空")
    @ApiModelProperty(value = "可售数  [{\"默认值\":\"默认值\",\"no\":\"1\"}]")
    private String quantityRule;

    @ApiModelProperty(value = "制造商 [{\"默认值\":\"1111\",\"no\":\"1\"},{\"取店铺名称\":\"取店铺名称\",\"no\":\"4\"},{\"实际品牌名称\":\"实际品牌名称\",\"no\":\"2\"}，{\"实际品牌名称若为空\":\"55555555\",\"no\":\"2\"}]")
    private String manufacturerRule;

    @ApiModelProperty(value = "part_number [{\"默认值\":\"1111\",\"no\":\"1\"},{\"设置为平台SKU\":\"设置为平台SKU\",\"no\":\"2\"}]")
    private String partNumber;

    @ApiModelProperty(value = "商品描述  [{\"随机欢迎语\":[\"aaa\",\"bbbbb\",\"ccccc\"],\"no\":\"1\"},{\"商品标题\":\"商品标题\",\"no\":\"2\"},{\"商品卖点\":\"商品卖点\",\"no\":\"3\"},{\"商品描述\":\"商品描述\",\"no\":\"2\"},{\"包装清单\":\"包装清单\",\"no\":\"2\"},{\"随机结束语\":[\"1111\",\"2222\",\"33333\"],\"no\":\"2\"}]")
    private String descriptionRule;

    @ApiModelProperty(value = "父体图片 \"主图\":[{\"从SPU图片中随机取一张\",\"从SPU图片中随机取一张\"},{\"混合所有SKU的主图并从中随机取一张\":\"混合所有SKU的主图并从中随机取一张\"},{\"若SPU图片为空或不足取\":\"混合所有SKU的主图并从中随机取一张\"}]")
    private String parentMainImageRule;

    @ApiModelProperty(value = "父体图片  \"附图\":[{\"从SPU图片中随机取\":{\"min\":1,\"max\":8}},{\"混合所有SKU的附图并从中随机取随机取\":{\"min\":1,\"max\":8}},{\"若SPU图片为空或不足取\":\"混合所有SKU的附图并从中随机取随机取\"}]")
    private String parentAdditionImageRule;

    @ApiModelProperty(value = "子体图片 \"主图\":[{\"从SKU图片中随机取一张\",\"从SKU图片中随机取一张\"}]")
    private String childMainImageRule;

    @ApiModelProperty(value = "子体图片 \"附图\":[{\"从SKU图片中随机取\":{\"min\":1,\"max\":8}}]")
    private String childAdditionImageRule;
    
    @ApiModelProperty(value = "计价模板")
    private String computeTemplate;

    //@ApiModelProperty(value = "创建人id")
    private Long createUserId;

    //@ApiModelProperty(value = "创建时间")
    private Date createTime;

    //@ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /*店铺授权账号*/
    private String empowerAccount;
    
    private String createUserName;
    
    private Integer timeType;
    
	private String startCreateTime;
	
	private String endCreateTime;
    
	private String updateUserName;
	
	private Long updateUserId;
	
	/*站点*/
	private String webName;
	
	/*上一个模板名字*/
	private String beforeTemplateName ;
	
	/*第三方id账号*/
	private String thirdPartyName;
	
	/*方便分页带上通用模板动态sql。1:加上通用模板分页，0不加*/
	private Integer isPage=0;
	
	/*店铺账号授权id*/
	private List<Long> empowerIds;
	
	/*主账号id*/
	private Integer topUserId;
	
    private static final long serialVersionUID = 1L;

    
    
    
    
    public Integer getTopUserId() {
		return topUserId;
	}

	public void setTopUserId(Integer topUserId) {
		this.topUserId = topUserId;
	}

	public List<Long> getEmpowerIds() {
		return empowerIds;
	}

	public void setEmpowerIds(List<Long> empowerIds) {
		this.empowerIds = empowerIds;
	}

	public int getIsPage() {
		return isPage;
	}

	public void setIsPage(int isPage) {
		this.isPage = isPage;
	}

	public String getBeforeTemplateName() {
		return beforeTemplateName;
	}

	public void setBeforeTemplateName(String beforeTemplateName) {
		this.beforeTemplateName = beforeTemplateName;
	}

	public String getWebName() {
		return webName;
	}

	public void setWebName(String webName) {
		this.webName = webName;
	}

	public String getUpdateUserName() {
		return updateUserName;
	}

	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}

	public Long getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(Long updateUserId) {
		this.updateUserId = updateUserId;
	}

	public String getStartCreateTime() {
		return startCreateTime;
	}

	public void setStartCreateTime(String startCreateTime) {
		this.startCreateTime = startCreateTime;
	}

	public String getEndCreateTime() {
		return endCreateTime;
	}

	public void setEndCreateTime(String endCreateTime) {
		this.endCreateTime = endCreateTime;
	}

	

	public Integer getTimeType() {
		return timeType;
	}

	public void setTimeType(Integer timeType) {
		this.timeType = timeType;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmpowerId() {
        return empowerId;
    }

    public void setEmpowerId(Long empowerId) {
        this.empowerId = empowerId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName == null ? null : templateName.trim();
    }

    public Integer getDefaultTemplate() {
        return defaultTemplate;
    }

    public void setDefaultTemplate(Integer defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
    }

    public String getCategoryFirstRule() {
        return categoryFirstRule;
    }

    public void setCategoryFirstRule(String categoryFirstRule) {
        this.categoryFirstRule = categoryFirstRule == null ? null : categoryFirstRule.trim();
    }

    public String getCategorySecondRule() {
        return categorySecondRule;
    }

    public void setCategorySecondRule(String categorySecondRule) {
        this.categorySecondRule = categorySecondRule == null ? null : categorySecondRule.trim();
    }

    public Integer getPublishType() {
        return publishType;
    }

    public void setPublishType(Integer publishType) {
        this.publishType = publishType;
    }

    public Integer getFulfillmentLatency() {
        return fulfillmentLatency;
    }

    public void setFulfillmentLatency(Integer fulfillmentLatency) {
        this.fulfillmentLatency = fulfillmentLatency;
    }

    public String getPlatformSkuRule() {
        return platformSkuRule;
    }

    public void setPlatformSkuRule(String platformSkuRule) {
        this.platformSkuRule = platformSkuRule == null ? null : platformSkuRule.trim();
    }

    public String getBrandRule() {
        return brandRule;
    }

    public void setBrandRule(String brandRule) {
        this.brandRule = brandRule == null ? null : brandRule.trim();
    }

    public String getProductTitleRule() {
        return productTitleRule;
    }

    public void setProductTitleRule(String productTitleRule) {
        this.productTitleRule = productTitleRule == null ? null : productTitleRule.trim();
    }

    public String getProductNoRule() {
        return productNoRule;
    }

    public void setProductNoRule(String productNoRule) {
        this.productNoRule = productNoRule == null ? null : productNoRule.trim();
    }

    public String getProductPriceRule() {
        return productPriceRule;
    }

    public void setProductPriceRule(String productPriceRule) {
        this.productPriceRule = productPriceRule == null ? null : productPriceRule.trim();
    }

    public String getQuantityRule() {
        return quantityRule;
    }

    public void setQuantityRule(String quantityRule) {
        this.quantityRule = quantityRule == null ? null : quantityRule.trim();
    }

    public String getManufacturerRule() {
        return manufacturerRule;
    }

    public void setManufacturerRule(String manufacturerRule) {
        this.manufacturerRule = manufacturerRule == null ? null : manufacturerRule.trim();
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber == null ? null : partNumber.trim();
    }

    public String getDescriptionRule() {
        return descriptionRule;
    }

    public void setDescriptionRule(String descriptionRule) {
        this.descriptionRule = descriptionRule == null ? null : descriptionRule.trim();
    }

    public String getParentMainImageRule() {
        return parentMainImageRule;
    }

    public void setParentMainImageRule(String parentMainImageRule) {
        this.parentMainImageRule = parentMainImageRule == null ? null : parentMainImageRule.trim();
    }

    public String getParentAdditionImageRule() {
        return parentAdditionImageRule;
    }

    public void setParentAdditionImageRule(String parentAdditionImageRule) {
        this.parentAdditionImageRule = parentAdditionImageRule == null ? null : parentAdditionImageRule.trim();
    }

    public String getChildMainImageRule() {
        return childMainImageRule;
    }

    public void setChildMainImageRule(String childMainImageRule) {
        this.childMainImageRule = childMainImageRule == null ? null : childMainImageRule.trim();
    }

    public String getChildAdditionImageRule() {
        return childAdditionImageRule;
    }

    public void setChildAdditionImageRule(String childAdditionImageRule) {
        this.childAdditionImageRule = childAdditionImageRule == null ? null : childAdditionImageRule.trim();
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	public String getEmpowerAccount() {
		return empowerAccount;
	}

	public void setEmpowerAccount(String empowerAccount) {
		this.empowerAccount = empowerAccount;
	}

	public String getThirdPartyName() {
		return thirdPartyName;
	}

	public void setThirdPartyName(String thirdPartyName) {
		this.thirdPartyName = thirdPartyName;
	}

	public String getComputeTemplate() {
		return computeTemplate;
	}

	public void setComputeTemplate(String computeTemplate) {
		this.computeTemplate = computeTemplate;
	}

	@Override
	public String toString() {
		return "AmazonTemplateRule [id=" + id + ", empowerId=" + empowerId + ", templateName=" + templateName
				+ ", defaultTemplate=" + defaultTemplate + ", categoryFirstRule=" + categoryFirstRule
				+ ", categorySecondRule=" + categorySecondRule + ", publishType=" + publishType
				+ ", fulfillmentLatency=" + fulfillmentLatency + ", platformSkuRule=" + platformSkuRule + ", brandRule="
				+ brandRule + ", productTitleRule=" + productTitleRule + ", productNoRule=" + productNoRule
				+ ", productPriceRule=" + productPriceRule + ", quantityRule=" + quantityRule + ", manufacturerRule="
				+ manufacturerRule + ", partNumber=" + partNumber + ", descriptionRule=" + descriptionRule
				+ ", parentMainImageRule=" + parentMainImageRule + ", parentAdditionImageRule="
				+ parentAdditionImageRule + ", childMainImageRule=" + childMainImageRule + ", childAdditionImageRule="
				+ childAdditionImageRule + ", computeTemplate=" + computeTemplate + ", createUserId=" + createUserId
				+ ", createTime=" + createTime + ", updateTime=" + updateTime + ", empowerAccount=" + empowerAccount
				+ ", createUserName=" + createUserName + ", timeType=" + timeType + ", startCreateTime="
				+ startCreateTime + ", endCreateTime=" + endCreateTime + ", updateUserName=" + updateUserName
				+ ", updateUserId=" + updateUserId + ", webName=" + webName + ", beforeTemplateName="
				+ beforeTemplateName + ", thirdPartyName=" + thirdPartyName + ", isPage=" + isPage + ", empowerIds="
				+ empowerIds + ", topUserId=" + topUserId + "]";
	}

}