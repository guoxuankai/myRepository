package com.rondaful.cloud.seller.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

@ApiModel(description = "亚马逊刊登信息表")
public class AmazonPublishListing/* extends VersionBean*/{

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "卖家在品连的账号")
    private String plAccount;

    @ApiModelProperty(value = "产品标题")
    private String title;

    @ApiModelProperty(value = "品连sku")
    private String plSku;

    @ApiModelProperty(value = "在亚马逊平台上的sku")
    private String platformSku;

    @ApiModelProperty(value = "产品说这是亚马逊上的唯一标识")
    private String asin;

    @ApiModelProperty(value = "刊登站点")
    private String publishSite;

    @ApiModelProperty(value = "卖家账号(自定义账号)")
    private String publishAccount;
    
    @ApiModelProperty(value = "卖家ID")
    private String merchantIdentifier;

    @ApiModelProperty(value = "刊登类型 1：单属性 2：多属性")
    private Integer publishType;

    @ApiModelProperty(value = "刊登状态 1: 草稿  2: 刊登中 3: 在线 4: 刊登失败 5: 已下线 , 6：等待刊登7:在线状态图片更新")
    private Integer publishStatus;
 
    @ApiModelProperty(value = "刊登状态，传入多个状态查询，仅作查询使用")
    private List<Integer> publishStatusList;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    
    @ApiModelProperty(value = "上线时间")
    private Date onlineTime;

    @ApiModelProperty(value = "刊登信息")
    private String publishMessage;

    @ApiModelProperty(value = "开始时间[yyyy-MM-dd] 查询参数")
    private String startCreateTime;

    @ApiModelProperty(value = "结束时间[yyyy-MM-dd] 查询参数")
    private String endCreateTime;

    @ApiModelProperty(value = "时间范围查询类型[0:创建时间 1:发布时间　2：上线时间 3：更新时间 ] 查询参数")
    private Integer timeType;

    @ApiModelProperty(value = "刊登成功时间")
    private Date successTime;

    @ApiModelProperty(value = "刊登主图")
    private String productImage;

    //数据来源1品连2亚马逊
    @ApiModelProperty(value = "数据来源1品连2亚马逊")
    private Integer dataSource;

    //是否有必填项字段用于解决保存草稿
    @ApiModelProperty(value = "是否有必填项字段,0没有1有")
    private Integer hasRequired;
    
    @ApiModelProperty(value = "0没有绑定品连sku,1绑定品连sku")
    private Integer listingType;
    
    private String temp;
    
    //发货仓库
    private Integer warehouseId;
    
    //1价格最低  2综合排序  3物流速度
    private Integer logisticsType;

    @ApiModelProperty(value = "0正常 1下架 2缺货 3少货 4其他")
    private Integer supplyStatus;
    
    @ApiModelProperty(value = "物流方式code")
    private String logisticsCode;

    @ApiModelProperty(value = "在线修改状态0初始值1在线未修改2修改成功3修改失败4修改中")
    private Integer updateStatus;

    @ApiModelProperty(value = "ids仅做导出用")
    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public Integer getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(Integer updateStatus) {
        this.updateStatus = updateStatus;
    }

    public String getLogisticsCode() {
		return logisticsCode;
	}

	public void setLogisticsCode(String logisticsCode) {
		this.logisticsCode = logisticsCode;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public Integer getListingType() {
		return listingType;
	}

	public void setListingType(Integer listingType) {
		this.listingType = listingType;
	}

	public Integer getHasRequired() {
		return hasRequired;
	}

	public void setHasRequired(Integer hasRequired) {
		this.hasRequired = hasRequired;
	}

	public Integer getDataSource() {
        return dataSource;
    }

    public void setDataSource(Integer dataSource) {
        this.dataSource = dataSource;
    }
    
    /**
     * 刊登授权的token
     */
    private String amwToken;

    
    
    // 数据的版本号，针对数据
    private String version;
    private String versionData;
    
    // 批次号
    private String batchNo;
    
    // submitfeedID
    private String submitfeedid;
    
    // 备注
    private String remark;
    
    // 前端要求，主要用于编辑时加载各个项
    private String ext;
    
    //处理数据权限
    private List<String> pinlianAccounts;

    //处理数据权限
    private List<String> publishAccounts;

    //销售人员Id
    private Integer saleUserId;
    
    //销售人员名字
    private String saleName;
    
    //捆绑销售数量
    private Integer plSkuSaleNum;
    
    private Integer plSkuTort;


    public Integer getPlSkuTort() {
        return plSkuTort;
    }

    public void setPlSkuTort(Integer plSkuTort) {
        this.plSkuTort = plSkuTort;
    }

    public Integer getPlSkuSaleNum() {
		return plSkuSaleNum;
	}

	public void setPlSkuSaleNum(Integer plSkuSaleNum) {
		this.plSkuSaleNum = plSkuSaleNum;
	}

	public Integer getSaleUserId() {
		return saleUserId;
	}

	public void setSaleUserId(Integer saleUserId) {
		this.saleUserId = saleUserId;
	}

	public String getSaleName() {
		return saleName;
	}

	public void setSaleName(String saleName) {
		this.saleName = saleName;
	}

	public Date getOnlineTime() {
		return onlineTime;
	}

	public void setOnlineTime(Date onlineTime) {
		this.onlineTime = onlineTime;
	}

	public List<String> getPublishAccounts() {
        return publishAccounts;
    }

    public void setPublishAccounts(List<String> publishAccounts) {
        this.publishAccounts = publishAccounts;
    }

    public List<String> getPinlianAccounts() {
		return pinlianAccounts;
	}

	public void setPinlianAccounts(List<String> pinlianAccounts) {
		this.pinlianAccounts = pinlianAccounts;
	}

	public String getMerchantIdentifier() {
		return merchantIdentifier;
	}

	public void setMerchantIdentifier(String merchantIdentifier) {
		this.merchantIdentifier = merchantIdentifier;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAmwToken() {
		return amwToken;
	}

	public void setAmwToken(String amwToken) {
		this.amwToken = amwToken;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSubmitfeedid() {
		return submitfeedid;
	}

	public void setSubmitfeedid(String submitfeedid) {
		this.submitfeedid = submitfeedid;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getVersionData() {
		return versionData;
	}

	public void setVersionData(String versionData) {
		this.versionData = versionData;
	}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getPlSku() {
        return plSku;
    }

    public void setPlSku(String plSku) {
        this.plSku = plSku == null ? null : plSku.trim();
    }

    public String getPlatformSku() {
        return platformSku;
    }

    public void setPlatformSku(String platformSku) {
        this.platformSku = platformSku == null ? null : platformSku.trim();
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin == null ? null : asin.trim();
    }

    public String getPublishSite() {
        return publishSite;
    }

    public void setPublishSite(String publishSite) {
        this.publishSite = publishSite == null ? null : publishSite.trim();
    }

    public String getPublishAccount() {
        return publishAccount;
    }

    public void setPublishAccount(String publishAccount) {
        this.publishAccount = publishAccount == null ? null : publishAccount.trim();
    }

    public Integer getPublishType() {
        return publishType;
    }

    public void setPublishType(Integer publishType) {
        this.publishType = publishType;
    }

    public Integer getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(Integer publishStatus) {
        this.publishStatus = publishStatus;
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

    public String getPublishMessage() {
        return publishMessage;
    }

    public void setPublishMessage(String publishMessage) {
        this.publishMessage = publishMessage == null ? null : publishMessage.trim();
    }

    public String getStartCreateTime() {
        return startCreateTime;
    }

    public void setStartCreateTime(String startCreateTime) {
        this.startCreateTime = startCreateTime == null ? null : startCreateTime.trim();
    }

    public String getEndCreateTime() {
        return endCreateTime;
    }

    public void setEndCreateTime(String endCreateTime) {
        this.endCreateTime = endCreateTime == null ? null : endCreateTime.trim();
    }

    public String getPlAccount() {
        return plAccount;
    }

    public void setPlAccount(String plAccount) {
        this.plAccount = plAccount == null ? null:plAccount.trim();
    }

    public Date getSuccessTime() {
        return successTime;
    }

    public void setSuccessTime(Date successTime) {
        this.successTime = successTime;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public Integer getTimeType() {
        return timeType;
    }

    public void setTimeType(Integer timeType) {
        this.timeType = timeType;
    }

    public List<Integer> getPublishStatusList() {
        return publishStatusList;
    }

    public void setPublishStatusList(List<Integer> publishStatusList) {
        this.publishStatusList = publishStatusList;
    }

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Integer getLogisticsType() {
		return logisticsType;
	}

	public void setLogisticsType(Integer logisticsType) {
		this.logisticsType = logisticsType;
	}

    public Integer getSupplyStatus() {
        return supplyStatus;
    }

    public void setSupplyStatus(Integer supplyStatus) {
        this.supplyStatus = supplyStatus;
    }
}