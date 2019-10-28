package com.rondaful.cloud.seller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.List;

@ApiModel(value = "PublishStyleSearchVO")
public class EmpowerSearchVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	private Integer empowerId;
	@ApiModelProperty(value = "账号（自定义名称）,非amazon上的卖家账户，是用户自定义写的，与amazon账号无关")
	private String account;
	@ApiModelProperty(value = "状态  （0未授权  1 正常授权  2授权过期 3停用4迁移）")
	private Integer status;
	@ApiModelProperty(value = "平台 (1 ebay   2 amazon 3 aliexpress 4other)")
	private Integer platform;
	@ApiModelProperty(value = "品连账号")
	private String pinlianAccount;
	@ApiModelProperty(value = "第三方的账号或id  (sellerId)")
	private String thirdPartyName;
	@ApiModelProperty(value = "站点名称   (MarketplaceId)")
	private String webName;
	@ApiModelProperty(value = "品连id")
	private Integer pinlianId;
	@ApiModelProperty(value = "店铺租赁状态( 0 个人店铺 1 租赁店铺 )")
	private Integer rentStatus;
	@ApiModelProperty(value = "租聘状态0未分配1分配")
	private Integer rentType;
	@ApiModelProperty(value = "供应链公司")
	private String company;
	@ApiModelProperty(value = "0 为授权时间   1 为更新时间 2创建时间10没有数据权限")
	private String dataType;   //0 为授权时间   1 为更新时间

	@ApiModelProperty(value = "开始时间")
	private String beginTime;
	@ApiModelProperty(value = "结束时间")
	private String afterTime;

	@ApiModelProperty(value = "卖家品连ID列表（部分查询可用）")
	private List<Integer> pinlianIds;

	@ApiModelProperty(value = "店铺绑定ID列表")
	private List<Integer> bindCode;

	@ApiModelProperty(value = "后台绑定用户ID列表")
	private List<String> cmsBindCode;


	@ApiModelProperty(value = "分页页数")
	private Integer page;

	@ApiModelProperty(value = "每页条数")
	private Integer row;

	@ApiModelProperty(value = "是否需要其他")
	private Integer other;

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

	public Integer getRentStatus() {
		return rentStatus;
	}

	public void setRentStatus(Integer rentStatus) {
		this.rentStatus = rentStatus;
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

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
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

	public String getPinlianAccount() {
		return pinlianAccount;
	}

	public void setPinlianAccount(String pinlianAccount) {
		this.pinlianAccount = pinlianAccount;
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

	public Integer getPinlianId() {
		return pinlianId;
	}

	public void setPinlianId(Integer pinlianId) {
		this.pinlianId = pinlianId;
	}

	public Integer getRentType() {
		return rentType;
	}

	public void setRentType(Integer rentType) {
		this.rentType = rentType;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Integer getOther() {
		return other;
	}

	public void setOther(Integer other) {
		this.other = other;
	}
}
