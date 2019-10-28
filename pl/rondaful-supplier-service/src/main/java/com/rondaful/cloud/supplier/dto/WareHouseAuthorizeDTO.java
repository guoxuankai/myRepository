package com.rondaful.cloud.supplier.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 数据库返回对像
 * @author Administrator
 *
 */
public class WareHouseAuthorizeDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer authorizeId;
	private String customName;
	private String companyCode;
	private Integer authorizeStatus;
	private String appKey;
	private String appToken;
	
	private List<WareHouseDetailDTO>  wareHouseDetailDTOList;
	
	public Integer getAuthorizeId() {
		return authorizeId;
	}
	public void setAuthorizeId(Integer authorizeId) {
		this.authorizeId = authorizeId;
	}
	public String getCustomName() {
		return customName;
	}
	public void setCustomName(String customName) {
		this.customName = customName;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public Integer getAuthorizeStatus() {
		return authorizeStatus;
	}
	public void setAuthorizeStatus(Integer authorizeStatus) {
		this.authorizeStatus = authorizeStatus;
	}
	public List<WareHouseDetailDTO> getWareHouseDetailDTOList() {
		return wareHouseDetailDTOList;
	}
	public void setWareHouseDetailDTOList(List<WareHouseDetailDTO> wareHouseDetailDTOList) {
		this.wareHouseDetailDTOList = wareHouseDetailDTOList;
	}
	public String getAppKey() {
		return appKey;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	public String getAppToken() {
		return appToken;
	}
	public void setAppToken(String appToken) {
		this.appToken = appToken;
	}
}
