package com.rondaful.cloud.seller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


import java.io.Serializable;
import java.util.List;

@ApiModel(value = "AliexpressPublishListingSearch")
public class AliexpressPublishListingSearchVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//导出
	@ApiModelProperty(value = "导出id")
	private List<Long> ids;

	@ApiModelProperty(value = "登录账号")
	private String plAccount;

	@ApiModelProperty(value = "刊登状态 1: 草稿  2: 刊登中 3: 刊登失败 4:审核中  5: 审核失败 6:正在销售 7 已下架")
	private Integer publishStatus;

	@ApiModelProperty(value = "更新状态 1更新中2更新成功3更新失败")
	private Integer updateStatus;
	
	@ApiModelProperty(value = "刊登站点")
	private String site;
	
    @ApiModelProperty(value = "刊登帐号")
    private String publishAccount;

	@ApiModelProperty(value = "刊登帐号id")
	private String empowerId;
    
    @ApiModelProperty(value = "平台sku")
    private String platformSku;

    @ApiModelProperty(value = "品莲sku")
    private String plSku;
    
    @ApiModelProperty(value = "物品刊登成功后的id")
    private String itemId;

    @ApiModelProperty(value = "标题")
    private String title;
    
    @ApiModelProperty(value = "时间类型 [1==创建时间  2==发布时间  3==上线时间]")
    private Integer timeType;
	
    @ApiModelProperty(value = "卖家")
    private String sellerId;
	//数据权限
	private List<Integer> empowerIds;
    
	@ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;
    
    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;

	@ApiModelProperty(value = "分页页数")
	private Integer page;

	@ApiModelProperty(value = "每页条数")
	private Integer row;


	private String language;//语言

	public String getSellerId() {
		return sellerId;
	}

	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	public String getStartTime() {
		if (StringUtils.isNotBlank(startTime))
			return startTime +" 00:00:00";
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		if (StringUtils.isNotBlank(endTime))
			return endTime +" 23:59:59";
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getRow() {
		return row;
	}

	public void setRow(Integer row) {
		this.row = row;
	}
    public Integer getPublishStatus() {
		return publishStatus;
	}

	public void setPublishStatus(Integer publishStatus) {
		this.publishStatus = publishStatus;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getPublishAccount() {
		return publishAccount;
	}

	public void setPublishAccount(String publishAccount) {
		this.publishAccount = publishAccount;
	}

	public String getPlatformSku() {
		return platformSku;
	}

	public void setPlatformSku(String platformSku) {
		this.platformSku = platformSku;
	}

	public String getPlSku() {
		return plSku;
	}

	public void setPlSku(String plSku) {
		this.plSku = plSku;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getTimeType() {
		return timeType;
	}

	public void setTimeType(Integer timeType) {
		this.timeType = timeType;
	}

	public String getPlAccount() {
		return plAccount;
	}

	public void setPlAccount(String plAccount) {
		this.plAccount = plAccount;
	}

	public String getEmpowerId() {
		return empowerId;
	}

	public void setEmpowerId(String empowerId) {
		this.empowerId = empowerId;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public List<Integer> getEmpowerIds() {
		return empowerIds;
	}

	public void setEmpowerIds(List<Integer> empowerIds) {
		this.empowerIds = empowerIds;
	}

	public Integer getUpdateStatus() {
		return updateStatus;
	}

	public void setUpdateStatus(Integer updateStatus) {
		this.updateStatus = updateStatus;
	}

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}
}
