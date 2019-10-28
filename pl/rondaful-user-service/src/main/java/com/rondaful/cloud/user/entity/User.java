package com.rondaful.cloud.user.entity;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class User implements Serializable {
	/**
	 * 用户表
	 */
	private static final long serialVersionUID = -602040234618817821L;

	@ApiModelProperty(value = "主键id", required = false)
	private Integer userid;

	@ApiModelProperty(value = "指的是卖家或者供应商账号的主键id,卖家和供应商注册时不填，在登陆供应商和卖家账户后创建子账户时需要填")
	private Integer parentId;// 父级id
	
	@NotBlank(message="用户/账号不能为空!")
	@ApiModelProperty(value = "用户名", required = true)
	private String username;// 用户名

	@NotBlank(message="密码不能为空!")
	@ApiModelProperty(value = "密码", required = true)
	private String password;// 密码

	@NotBlank(message="邮箱不能为空!")
	@ApiModelProperty(value = "邮箱", required = false)
	private String email;// 邮箱

	@NotBlank(message="手机号码不能为空!")
	@ApiModelProperty(value = "手机", required = false)
	private String phone;// 手机

	@ApiModelProperty(value = "座机", required = false)
	private String mobile;// 座机

	@ApiModelProperty(value = "更新人员", required = false)
	private String remarks;// 备注

	@ApiModelProperty(value = "创建日期", required = false)
	private Date createDate;// 创建日期

	@ApiModelProperty(value = "修改时间", required = false)
	private Date updateDate;// 修改时间

	@ApiModelProperty(value = "账号状态：1 启动  2 停止", required = false)
	private Integer status;// 状态代码

	@ApiModelProperty(value = "用户昵称", required = false)
	private String loginName;// 登录名称

	@ApiModelProperty(value = "新增人员", required = false)
	private String enabled;// 授权

	@ApiModelProperty(value = "账号是否属于激活状态  1：激活  0：未激活  2：审核中 3:已拒绝", required = false)
	private Integer delFlag;// 是否已删除

	@ApiModelProperty(value = "财务是否初始化确认  1：已初始化  0 未初始化", required = false)
	private String employeeId;// 员工id

	@ApiModelProperty(value = "审核备注", required = false)
	private String orgId;// 部门id

	@ApiModelProperty(value = "凭证盐", required = false)
	private String credentialsSalt;// 凭证盐

	@ApiModelProperty(value = "平台类型   0供应商平台  1卖家平台  2管理平台", required = true)
	private Integer platformType;// 平台类型

	@ApiModelProperty(value = "结束时间", required = false)
	private Date dateClosed;

	@ApiModelProperty(value = "结算周期 :  1：周结  2：半月结  3：月结',", required = false)
	private Integer closedCircle;// 结算周期

	@ApiModelProperty(value = "联系人 ,新增供应链公司时不能为空",required = false)
	private String linkman;
	
	@NotBlank(message="联系地址不能为空!")
	@ApiModelProperty(value = "地址",required = false)
	private String site;
	
	@NotBlank(message="供应链公司不能为空!")
	@ApiModelProperty(value = "供应链公司",required = false)
	private String supplyChainCompany;
	
	@ApiModelProperty(value = "头像地址",required = false)
	private String imageSite;

	@ApiModelProperty(value = "公司名称",required = false)
	private String companyNameUser;

	@ApiModelProperty(value = "qq",required = false)
	private String qq;

	@ApiModelProperty(value = "联系人邮编",required = false)
	private String postcode;

	private String dateType;
	
	private String beginTime;
	
	private String afterTime;
	
	
	public User() {
		super();
	}


	public String getImageSite() {
		return imageSite;
	}



	public void setImageSite(String imageSite) {
		this.imageSite = imageSite;
	}



	public User(String username, String phone, Date createDate, Date dateClosed, Integer status, Integer platformType) {
		super();
		this.username = username;
		this.phone = phone;
		this.createDate = createDate;
		this.dateClosed = dateClosed;
		this.status = status;
		this.platformType = platformType;
	}
	
	public User(String username, String password, String email, String phone,
			Integer closedCircle, String linkman, String site,String supplyChainCompany) {
		super();
		this.username = username;
		this.password = password;
		this.email = email;
		this.phone = phone;
		this.closedCircle = closedCircle;
		this.linkman = linkman;
		this.site = site;
		this.supplyChainCompany = supplyChainCompany;
	}

	public User(String username, String phone, Date createDate, Date dateClosed, Integer status, Integer platformType, String supplyChainCompany) {
		super();
		this.username = username;
		this.phone = phone;
		this.createDate = createDate;
		this.dateClosed = dateClosed;
		this.status = status;
		this.platformType = platformType;
		this.supplyChainCompany = supplyChainCompany;
	}

	public User(Integer userid, Integer parentId, String username, String password, String email, String phone, String mobile, String remarks, Date createDate,
				Date updateDate, Integer status, List<String> roleNames, String loginName, String enabled, Integer delFlag, String employeeId, String orgId,
				String credentialsSalt, Integer platformType, Date dateClosed, Integer closedCircle, String linkman, String site, String supplyChainCompany,
				String imageSite, String companyNameUser) {
		this.userid = userid;
		this.parentId = parentId;
		this.username = username;
		this.password = password;
		this.email = email;
		this.phone = phone;
		this.mobile = mobile;
		this.remarks = remarks;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.status = status;
		this.loginName = loginName;
		this.enabled = enabled;
		this.delFlag = delFlag;
		this.employeeId = employeeId;
		this.orgId = orgId;
		this.credentialsSalt = credentialsSalt;
		this.platformType = platformType;
		this.dateClosed = dateClosed;
		this.closedCircle = closedCircle;
		this.linkman = linkman;
		this.site = site;
		this.supplyChainCompany = supplyChainCompany;
		this.imageSite = imageSite;
		this.companyNameUser = companyNameUser;
	}

	/*
	 * public List<Integer> getRoleIds() { return roleIds; }
	 * 
	 * public void setRoleIds(List<Integer> roleIds) { this.roleIds = roleIds; }
	 */

	public String getCompanyNameUser() {
		return companyNameUser;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public void setCompanyNameUser(String companyNameUser) {
		this.companyNameUser = companyNameUser;
	}

	public Integer getClosedCircle() {
		return closedCircle;
	}

	public Date getDateClosed() {
		return dateClosed;
	}

	public void setDateClosed(Date dateClosed) {
		this.dateClosed = dateClosed;
	}

	public String getLinkman() {
		return linkman;
	}

	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public Integer getUserid() {
		return userid;
	}

	public void setClosedCircle(Integer closedCircle) {
		this.closedCircle = closedCircle;
	}

	public Integer getPlatformType() {
		return platformType;
	}

	public void setPlatformType(Integer platformType) {
		this.platformType = platformType;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getEnabled() {
		return enabled;
	}

	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getCredentialsSalt() {
		return credentialsSalt;
	}

	public void setCredentialsSalt(String credentialsSalt) {
		this.credentialsSalt = credentialsSalt;
	}

	public String getSupplyChainCompany() {
		return supplyChainCompany;
	}

	public void setSupplyChainCompany(String supplyChainCompany) {
		this.supplyChainCompany = supplyChainCompany;
	}



	public String getDateType() {
		return dateType;
	}



	public void setDateType(String dateType) {
		this.dateType = dateType;
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
	
	
	
	
	
	
}