package com.rondaful.cloud.seller.vo;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "EbayPublishListingSearch")
public class PublishListingSearchVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "1=草稿,2=刊登中,3=已下线,4=在线,5=刊登失败 8平台删除")
	private Integer status;

	@ApiModelProperty(value = "更新状态 1更新中2更新成功3更新失败")
	private Integer updateStatus;//1更新中2更新成功3更新失败
	
	@ApiModelProperty(value = "刊登站点")
	private String site;
	
    @ApiModelProperty(value = "刊登帐号")
    private String publishAccount;
    
    @ApiModelProperty(value = "平台sku")
    private String platformSku;

    @ApiModelProperty(value = "品莲sku")
    private String plSku;
    
    @ApiModelProperty(value = "物品刊登成功后的id")
    private String itemId;

    @ApiModelProperty(value = "标题")
    private String title;

	@ApiModelProperty(value = "创建刊登人id")
	private Long createId;

	@ApiModelProperty(value = "创建人刊登人名称")
	private String createName;

	@ApiModelProperty(value = "售卖形式 刊登类型(1=单属性一口价 2=多属性一口价 3=拍卖)")
	private Integer listingType;

	@ApiModelProperty(value = "时间类型 [1==创建时间  2==发布时间  3==上线时间  4==下线时间 5更新时间]")
    private Integer timeType;
	
    @ApiModelProperty(value = "卖家")
    private String seller;
	//数据权限
	private List<Integer> empowerIds;
    
	@ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;
    
    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;

	@ApiModelProperty(value = "品莲Spu")
	private String plSpu;
    
	@ApiModelProperty(value = "分页页数")
	private String page;

	@ApiModelProperty(value = "每页条数")
	private String row;


	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
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

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getRow() {
		return row;
	}

	public void setRow(String row) {
		this.row = row;
	}
    public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public String getPlSpu() {
		return plSpu;
	}

	public void setPlSpu(String plSpu) {
		this.plSpu = plSpu;
	}

	public Long getCreateId() {
		return createId;
	}

	public void setCreateId(Long createId) {
		this.createId = createId;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public Integer getListingType() {
		return listingType;
	}

	public void setListingType(Integer listingType) {
		this.listingType = listingType;
	}
}
