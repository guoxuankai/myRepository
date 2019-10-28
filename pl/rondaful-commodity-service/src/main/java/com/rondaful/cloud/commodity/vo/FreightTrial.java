package com.rondaful.cloud.commodity.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class FreightTrial implements Serializable {

	private static final long serialVersionUID = -7598824742338409737L;

	@ApiModelProperty(value = "订单号")
	private String sysOrderId;

	@ApiModelProperty(value = "仓库编码", required = true)
	private String warehouseCode;
	
	@ApiModelProperty(value = "国家简码", required = true)
	private String countryCode;

	@ApiModelProperty(value = "邮政编码 | 谷仓必传")
	private String postCode;

	@ApiModelProperty(value = "物流方式code | 计算预估物流费必传")
	private String logisticsCode;

	@ApiModelProperty(value = "所属平台 1(eBay) 2(Amazon) 3(Wish) 4(AliExpress)")
	private String platformType;

	@ApiModelProperty(value = "调用平台 0：erp 1：谷仓", required = true)
	private Integer callPlatform;
	
	@ApiModelProperty(value = "ERP(传供应商SKU)格式为:[{\"sku\":\"DI0302801\",\"num\":\"6\"},]  | 仓库为ERP此字段必传")
	private List<Map<String,Object>> erpSKUList;

	@ApiModelProperty(value = "谷仓(传品连SKU)格式为[\"sku2:1\",\"sku3:1\"] | 仓库为谷仓此字段必传")
	private List<String> gcSKUList;

	public String getSysOrderId() {
		return sysOrderId;
	}

	public void setSysOrderId(String sysOrderId) {
		this.sysOrderId = sysOrderId;
	}

	public String getWarehouseCode() {
		return warehouseCode;
	}

	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
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

	public Integer getCallPlatform() {
		return callPlatform;
	}

	public void setCallPlatform(Integer callPlatform) {
		this.callPlatform = callPlatform;
	}

	public List<Map<String, Object>> getErpSKUList() {
		return erpSKUList;
	}

	public void setErpSKUList(List<Map<String, Object>> erpSKUList) {
		this.erpSKUList = erpSKUList;
	}

	public List<String> getGcSKUList() {
		return gcSKUList;
	}

	public void setGcSKUList(List<String> gcSKUList) {
		this.gcSKUList = gcSKUList;
	}

	@Override
	public String toString() {
		return "FreightTrial{" +
				"sysOrderId='" + sysOrderId + '\'' +
				", warehouseCode='" + warehouseCode + '\'' +
				", countryCode='" + countryCode + '\'' +
				", postCode='" + postCode + '\'' +
				", logisticsCode='" + logisticsCode + '\'' +
				", platformType='" + platformType + '\'' +
				", callPlatform=" + callPlatform +
				", erpSKUList=" + erpSKUList +
				", gcSKUList=" + gcSKUList +
				'}';
	}
}
