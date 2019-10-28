package com.rondaful.cloud.user.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 供应商列表数据集成
 * @author Administrator
 *
 */
public class SupplyChainCompanyListBean implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 2473742317950776526L;

	@ApiModelProperty(value = "id")
	private Integer id;

	@ApiModelProperty(value = "账号")
	private String username;

	@ApiModelProperty(value = "供应链公司")
	private String supplyChainCompany;

	@ApiModelProperty(value = "公司名称")
	private String supplyChainCompanyName;

	@ApiModelProperty(value = "联系人")
	private String linjman;

	@ApiModelProperty(value = "联系人座机")
	private String linkmanMoble;

	@ApiModelProperty(value = "联系邮箱")
	private String email;

	@ApiModelProperty(value = "联系人手机")
	private String linkmanPhone;

	@ApiModelProperty(value = "绑定卖家数量")
	private Integer bindingSeller;

	@ApiModelProperty(value = "绑定供应商数量")
	private Integer bindingSupplier;

	@ApiModelProperty(value = "账户或公司状态")
	private Integer status;

	@ApiModelProperty(value = "账号审核状态  1：激活  0：未激活  2：审核中 3:已拒绝")
	private Integer delFlag;

	@ApiModelProperty(value = "新增时间")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date createDate;

	@ApiModelProperty(value = "更新时间")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date updateDate;

	@ApiModelProperty(value = "新增操作人")
	private String createOptionPerson;

	@ApiModelProperty(value = "更新操作人")
	private String updateOptionPerson;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSupplyChainCompanyName() {
		return supplyChainCompanyName;
	}

	public void setSupplyChainCompanyName(String supplyChainCompanyName) {
		this.supplyChainCompanyName = supplyChainCompanyName;
	}

	public String getLinjman() {
		return linjman;
	}

	public void setLinjman(String linjman) {
		this.linjman = linjman;
	}

	public String getLinkmanMoble() {
		return linkmanMoble;
	}

	public void setLinkmanMoble(String linkmanMoble) {
		this.linkmanMoble = linkmanMoble;
	}

	public String getLinkmanPhone() {
		return linkmanPhone;
	}

	public void setLinkmanPhone(String linkmanPhone) {
		this.linkmanPhone = linkmanPhone;
	}

	public Integer getBindingSeller() {
		return bindingSeller;
	}

	public void setBindingSeller(Integer bindingSeller) {
		this.bindingSeller = bindingSeller;
	}

	public Integer getBindingSupplier() {
		return bindingSupplier;
	}

	public void setBindingSupplier(Integer bindingSupplier) {
		this.bindingSupplier = bindingSupplier;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getCreateOptionPerson() {
		return createOptionPerson;
	}

	public void setCreateOptionPerson(String createOptionPerson) {
		this.createOptionPerson = createOptionPerson;
	}

	public String getUpdateOptionPerson() {
		return updateOptionPerson;
	}

	public void setUpdateOptionPerson(String updateOptionPerson) {
		this.updateOptionPerson = updateOptionPerson;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSupplyChainCompany() {
		return supplyChainCompany;
	}

	public void setSupplyChainCompany(String supplyChainCompany) {
		this.supplyChainCompany = supplyChainCompany;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

}

