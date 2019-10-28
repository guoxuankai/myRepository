package com.rondaful.cloud.seller.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "收货地址VO")
public class ReceivingAddressVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "电话", required = true)
	private String phone;
	
	@ApiModelProperty(value = "手机", required = true)
	private String cellphone;
	
	@ApiModelProperty(value = "收件人", required = true)
	private String addressee;
	
	@ApiModelProperty(value = "邮编", required = true)
	private String zcode;
	
	@ApiModelProperty(value = "国家", required = true)
	private String country;
	
	@ApiModelProperty(value = "州/省", required = true)
	private String province;
	
	@ApiModelProperty(value = "城市", required = true)
	private String city;
	
	@ApiModelProperty(value = "地址1", required = true)
	private String address1;
	
	@ApiModelProperty(value = "地址2", required = true)
	private String address2;
	
	@ApiModelProperty(value = "仓库", required = true)
	private String warehouse;
	
	@ApiModelProperty(value = "邮寄方式", required = true)
	private String mailingMethod;
	
	private String shippingCarrier;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCellphone() {
		return cellphone;
	}

	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}

	public String getAddressee() {
		return addressee;
	}

	public void setAddressee(String addressee) {
		this.addressee = addressee;
	}

	public String getZcode() {
		return zcode;
	}

	public void setZcode(String zcode) {
		this.zcode = zcode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}

	public String getMailingMethod() {
		return mailingMethod;
	}

	public void setMailingMethod(String mailingMethod) {
		this.mailingMethod = mailingMethod;
	}

	public String getShippingCarrier() {
		return shippingCarrier;
	}

	public void setShippingCarrier(String shippingCarrier) {
		this.shippingCarrier = shippingCarrier;
	}
}
