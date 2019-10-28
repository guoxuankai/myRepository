package com.rondaful.cloud.supplier.model.request.third;

import io.swagger.annotations.ApiModelProperty;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;

public class ThirdFreightReq implements Serializable {

	private static final long serialVersionUID = -7598824742338409737L;

	@ApiModelProperty(value = "仓库id", required = true)
	private Integer warehouseId;

	@ApiModelProperty(value = "国家简码", required = true)
	private String countryCode;

	@ApiModelProperty(value = "邮政编码 | 谷仓必传")
	private String postCode;

	@ApiModelProperty(value = "物流方式code | 计算预估物流费必传")
	private String logisticsCode;

	@ApiModelProperty(value = "所属平台 1(eBay) 2(Amazon) 3(Wish) 4(AliExpress)")
	private String platformType;

	@ApiModelProperty(value = "sku集合",required = true)
	private List<SkuDetail> skuList;

	@ApiModelProperty(value = "城市")
	private String city;

	private List list;

	private HttpServletRequest request;

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getLogisticsCode() {
		return logisticsCode;
	}

	public void setLogisticsCode(String logisticsCode) {
		this.logisticsCode = logisticsCode;
	}

	public String getPlatformType() {
		return platformType;
	}

	public void setPlatformType(String platformType) {
		this.platformType = platformType;
	}

	public List<SkuDetail> getSkuList() {
		return skuList;
	}

	public void setSkuList(List<SkuDetail> skuList) {
		this.skuList = skuList;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public String toString() {
		return "ThirdFreightReq{" +
				"warehouseId=" + warehouseId +
				", countryCode='" + countryCode + '\'' +
				", postCode='" + postCode + '\'' +
				", logisticsCode='" + logisticsCode + '\'' +
				", platformType='" + platformType + '\'' +
				", skuList=" + skuList +
				", city='" + city + '\'' +
				", list=" + list +
				", request=" + request +
				'}';
	}
}
