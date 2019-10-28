package com.rondaful.cloud.user.entity;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

/**
 * 供应商及卖家注册所提交的数据
 * @author Administrator
 *
 */
public class SupplierUser implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1565300987585734091L;

	@ApiModelProperty(value="主键id",required = false)
	private Integer id;
	
	@ApiModelProperty(value="供应商账号",required = true)
	private String account;
	
	@ApiModelProperty(value="密码",required = true)
	private String password;
	
	@ApiModelProperty(value="公司名称",required = true)
	private String companyName;//公司名称
	
	@ApiModelProperty(value="电话",required = true)
	private String phone;
	
	@ApiModelProperty(value="邮件",required = true)
	private String email;
	
	@ApiModelProperty(value="用户id-预留外键",required = true)
	private Integer userId;//用户id

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	
	
	
	
	
}
