package com.rondaful.cloud.order.entity.supplier;


import java.io.Serializable;
import java.util.List;

/**
 *  登录用户信息
 *
 * @ClassName UserInfoVO
 * @Author Lxx
 * @Date 2019/4/26 16:16
 * @Version 1.0
 */
public class UserInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
	 * 账号平台类型   0供应商平台  1卖家平台  2管理平台
	 */
	private Integer platformType;


	/**
	 * 当前用户id
	 */
	private Integer userId;
	/**
	 * 当前用户登录名称
	 */
	private String loginName;
	/**
	 * 当前用户顶级父节点id
	 */
	private Integer topUserId;
	/**
	 * 当前用户顶级父节点登录名
	 */
	private String topUserLoginName;

	/**
	 * 供应商公司名称
	 */
	private String supplierCompanyName;
	/**
	 * 是否主账号标识：0：主账号，1：子账号
	 */
	private Integer topFlag;

	/**
	  * 仓库id列表
	  */

	private List<String> wIds;

	/**
	 * 供应商列表
	 */
	private List<String> suppliers;


	public Integer getPlatformType() {
		return platformType;
	}
	public void setPlatformType(Integer platformType) {
		this.platformType = platformType;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public Integer getTopUserId() {
		return topUserId;
	}
	public void setTopUserId(Integer topUserId) {
		this.topUserId = topUserId;
	}
	public String getTopUserLoginName() { return topUserLoginName; }
	public void setTopUserLoginName(String topUserLoginName) {
		this.topUserLoginName = topUserLoginName;
	}
	public String getSupplierCompanyName() { return supplierCompanyName; }
	public void setSupplierCompanyName(String supplierCompanyName) {
		this.supplierCompanyName = supplierCompanyName;
	}
	public Integer getTopFlag() {
		return topFlag;
	}
	public void setTopFlag(Integer topFlag) {
		this.topFlag = topFlag;
	}
	public List<String> getwIds() {
		return wIds;
	}

	public void setwIds(List<String> wIds) {
		this.wIds = wIds;
	}
	public List<String> getSuppliers() {
		return suppliers;
	}
	public void setSuppliers(List<String> suppliers) {
		this.suppliers = suppliers;
	}
}
