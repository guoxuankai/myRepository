package com.rondaful.cloud.seller.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

public class Empower implements Serializable {
	@ApiModelProperty(value = "id")
    private Integer empowerId;
	@ApiModelProperty(value = "账号（自定义名称）,非amazon上的卖家账户，是用户自定义写的，与amazon账号无关")
    private String account;
	@ApiModelProperty(value = "状态  （0未授权  1 正常授权  2授权过期 3停用4迁移）")
    private Integer status;
	@ApiModelProperty(value = "平台 (1 ebay   2 amazon 3 aliexpress 4other)")
    private Integer platform;
	@ApiModelProperty(value = "授权令牌")
    private String token;
	@ApiModelProperty(value = "速卖通账号刷新token,ebay的userid")
	private String refreshToken;
	@ApiModelProperty(value = "第三方的账号或id  (sellerId)")
    private String thirdPartyName;
	
	@ApiModelProperty(value = "站点名称   (MarketplaceId)亚马逊多个用,隔开")
    private String webName;
	
	@ApiModelProperty(value = "paypal账号01")
    private String paypalAccount01;
	
	@ApiModelProperty(value = "paypal账号02")
    private String paypalAccount02;
	
	@ApiModelProperty(value = "授权时间")
    private Date autoTime;
	@ApiModelProperty(value = "授权到期时间")
    private Date endTime;
	@ApiModelProperty(value = "修改时间")
    private Date updateTime;
	@ApiModelProperty(value = "创建时间")
    private Date createTime;
	@ApiModelProperty(value = "品连账号")
    private String pinlianAccount;
	
    private String beginTime;
	
    private String afterTime;
	@ApiModelProperty(value = "0 为授权时间   1 为更新时间 10 确认授权")
    private String dataType;   //0 为授权时间   1 为更新时间

    @ApiModelProperty(value = "品连id")
    private Integer pinlianId;
    @ApiModelProperty(value = "品连导入数据名称")
    private String nickName;
    
    @ApiModelProperty(value = "子账号")
    private String parentAccount;

    @ApiModelProperty(value = "店铺租赁状态( 0 个人店铺 1 租赁店铺 )")
	private Integer rentStatus;


	@ApiModelProperty(value = "供应链公司")
	private String company;

	@ApiModelProperty(value = "供应链公司名称（显示用）")
	private String companyName;

	@ApiModelProperty(value = "租聘状态0未分配1分配")
	private Integer rentType;

	@ApiModelProperty(value = "租聘时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date rentTime;

	@ApiModelProperty(value = "eBay平台走什么平台的单0不启用 1:erp  2:品连 3我有自己的edis账号并且授权给品连")
	private Integer ebayEdis;

	@ApiModelProperty(value = "主营类目(多个用,隔开)")
	private String categoryIds;

    
    @ApiModelProperty(value = "卖家品连ID列表（部分查询可用）")
    private List<Integer> pinlianIds;
    
    @ApiModelProperty(value = "店铺绑定ID列表")
    private List<Integer> bindCode;

    @ApiModelProperty(value = "后台绑定用户ID列表")
    private List<String> cmsBindCode;
    
    
    
    
	public Empower() {
	}

	public Empower(Integer status, Integer platform, String thirdPartyName, String webName) {
		this.status = status;
		this.platform = platform;
		this.thirdPartyName = thirdPartyName;
		this.webName = webName;
	}

	private static final long serialVersionUID = 1L;

	public String getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(String categoryIds) {
		this.categoryIds = categoryIds;
	}

	public Integer getEbayEdis() {
		return ebayEdis;
	}

	public void setEbayEdis(Integer ebayEdis) {
		this.ebayEdis = ebayEdis;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public Integer getEmpowerId() {
		return empowerId;
	}

	public void setEmpowerId(Integer empowerId) {
		this.empowerId = empowerId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getPlatform() {
		return platform;
	}

	public void setPlatform(Integer platform) {
		this.platform = platform;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getThirdPartyName() {
		return thirdPartyName;
	}

	public void setThirdPartyName(String thirdPartyName) {
		this.thirdPartyName = thirdPartyName;
	}

	public String getWebName() {
		return webName;
	}

	public void setWebName(String webName) {
		this.webName = webName;
	}

	public String getPaypalAccount01() {
		return paypalAccount01;
	}

	public void setPaypalAccount01(String paypalAccount01) {
		this.paypalAccount01 = paypalAccount01;
	}

	public String getPaypalAccount02() {
		return paypalAccount02;
	}

	public void setPaypalAccount02(String paypalAccount02) {
		this.paypalAccount02 = paypalAccount02;
	}

	public Date getAutoTime() {
		return autoTime;
	}

	public void setAutoTime(Date autoTime) {
		this.autoTime = autoTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getPinlianAccount() {
		return pinlianAccount;
	}

	public void setPinlianAccount(String pinlianAccount) {
		this.pinlianAccount = pinlianAccount;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getAfterTime() {
		return afterTime;
	}

	public void setAfterTime(String afterTime) {
		this.afterTime = afterTime;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Integer getPinlianId() {
		return pinlianId;
	}

	public void setPinlianId(Integer pinlianId) {
		this.pinlianId = pinlianId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getParentAccount() {
		return parentAccount;
	}

	public void setParentAccount(String parentAccount) {
		this.parentAccount = parentAccount;
	}
	
	
	public Integer getRentStatus() {
		return rentStatus;
	}

	public void setRentStatus(Integer rentStatus) {
		this.rentStatus = rentStatus;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company == null ? null : company.trim();
	}

	public Integer getRentType() {
		return rentType;
	}

	public void setRentType(Integer rentType) {
		this.rentType = rentType;
	}

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getRentTime() {
		return rentTime;
	}

	public void setRentTime(Date rentTime) {
		this.rentTime = rentTime;
	}

	public List<Integer> getPinlianIds() {
		return pinlianIds;
	}

	public void setPinlianIds(List<Integer> pinlianIds) {
		this.pinlianIds = pinlianIds;
	}
	
	public List<Integer> getBindCode() {
		return bindCode;
	}

	public void setBindCode(List<Integer> bindCode) {
		this.bindCode = bindCode;
	}

	
	public List<String> getCmsBindCode() {
		return cmsBindCode;
	}

	public void setCmsBindCode(List<String> cmsBindCode) {
		this.cmsBindCode = cmsBindCode;
	}

	@Override
	public String toString() {
		return "Empower [empowerId=" + empowerId + ", account=" + account + ", status=" + status + ", platform="
				+ platform + ", token=" + token + ", refreshToken=" + refreshToken + ", thirdPartyName="
				+ thirdPartyName + ", webName=" + webName + ", paypalAccount01=" + paypalAccount01
				+ ", paypalAccount02=" + paypalAccount02 + ", autoTime=" + autoTime + ", endTime=" + endTime
				+ ", updateTime=" + updateTime + ", createTime=" + createTime + ", pinlianAccount=" + pinlianAccount
				+ ", beginTime=" + beginTime + ", afterTime=" + afterTime + ", dataType=" + dataType + ", pinlianId="
				+ pinlianId + ", nickName=" + nickName + ", parentAccount=" + parentAccount + ", rentStatus="
				+ rentStatus + ", pinlianIds=" + pinlianIds + ", bindCode=" + bindCode + ", cmsBindCode=" + cmsBindCode
				+ "]";
	}

	

	
	
	
	
}