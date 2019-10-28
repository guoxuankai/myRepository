package com.rondaful.cloud.commodity.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
* @Description:sku推送记录
* @author:范津 
* @date:2019年4月26日 上午10:54:48
 */
@ApiModel(value ="SkuPushRecord")
public class SkuPushRecord implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "唯一id")
	private Long id;

	@ApiModelProperty(value = "仓库服务商编码")
    private String warehouseProviderCode;
	
	@ApiModelProperty(value = "仓库服务商名称")
    private String warehouseProviderName;
	
	@ApiModelProperty(value = "账号ID")
	private Integer accountId;

	@ApiModelProperty(value = "仓库服务商账号")
    private String account;

	@ApiModelProperty(value = "品连sku")
    private String systemSku;
	
	@ApiModelProperty(value = "商品名称")
	private String commodityName;

	@ApiModelProperty(value = "商品状态，X:废弃， D:草稿，S:可用，W:审核中，R:审核不通过")
    private String productState;

	@ApiModelProperty(value = "推送结果，0：推送失败，1：推送成功")
    private Integer pushState;

	@ApiModelProperty(value = "创建时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
	
	@ApiModelProperty(value = "版本号")
    private Long version;
	
	@ApiModelProperty(value = "开始时间")
	private String startTime;
	
	@ApiModelProperty(value = "结束时间")
	private String endTime;
	
	@ApiModelProperty(value = "更新时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
	
	@ApiModelProperty(value = "查询时间类型，1：创建时间，2：更新时间")
	private Integer queryTimeType;
	
	@ApiModelProperty(value = "sku主图")
	private String masterPicture;
	
	@ApiModelProperty(value = "sku英文名")
	private String commodityNameEn;
	
	@ApiModelProperty(value = "供应商ID")
	private Long supplierId;
	

	
	
    public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    public String getSystemSku() {
        return systemSku;
    }

    public void setSystemSku(String systemSku) {
        this.systemSku = systemSku == null ? null : systemSku.trim();
    }

    public String getProductState() {
        return productState;
    }

    public void setProductState(String productState) {
        this.productState = productState == null ? null : productState.trim();
    }

    public Integer getPushState() {
        return pushState;
    }

    public void setPushState(Integer pushState) {
        this.pushState = pushState;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getQueryTimeType() {
		return queryTimeType;
	}

	public void setQueryTimeType(Integer queryTimeType) {
		this.queryTimeType = queryTimeType;
	}

	public String getMasterPicture() {
		return masterPicture;
	}

	public void setMasterPicture(String masterPicture) {
		this.masterPicture = masterPicture;
	}

	public String getCommodityNameEn() {
		return commodityNameEn;
	}

	public void setCommodityNameEn(String commodityNameEn) {
		this.commodityNameEn = commodityNameEn;
	}

	public String getWarehouseProviderName() {
		return warehouseProviderName;
	}

	public void setWarehouseProviderName(String warehouseProviderName) {
		this.warehouseProviderName = warehouseProviderName;
	}

	public String getWarehouseProviderCode() {
		return warehouseProviderCode;
	}

	public void setWarehouseProviderCode(String warehouseProviderCode) {
		this.warehouseProviderCode = warehouseProviderCode;
	}

	public Long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}
	
}