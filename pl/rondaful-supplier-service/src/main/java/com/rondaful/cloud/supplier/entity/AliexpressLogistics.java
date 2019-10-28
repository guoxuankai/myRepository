package com.rondaful.cloud.supplier.entity;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * Aliexpress所支持的物流方式实体类
 * 
 * @author xieyanbin
 *
 * @2019年4月11日 
 * @version v2.1
 * 
 */
public class AliexpressLogistics implements Serializable {

	@ApiModelProperty(value = "推荐显示排序")
	private String recommend_order;
	
	@ApiModelProperty(value = "物流追踪号码校验规则，采用正则表达")
	private String tracking_no_regex;
	
	@ApiModelProperty(value = "最小处理时间")
	private String min_process_day;
	
	@ApiModelProperty(value = "物流公司")
	private String logistics_company;
	
	@ApiModelProperty(value = "最大处理时间")
	private String max_process_day;
	
	@ApiModelProperty(value = "展示名称")
	private String display_name;
	
	@ApiModelProperty(value = "物流服务key")
	private String service_name;
	
	private String aliexpressCode;
	
	public String getRecommend_order() {
		return recommend_order;
	}

	public void setRecommend_order(String recommend_order) {
		this.recommend_order = recommend_order;
	}

	public String getTracking_no_regex() {
		return tracking_no_regex;
	}

	public void setTracking_no_regex(String tracking_no_regex) {
		this.tracking_no_regex = tracking_no_regex;
	}

	public String getMin_process_day() {
		return min_process_day;
	}

	public void setMin_process_day(String min_process_day) {
		this.min_process_day = min_process_day;
	}

	public String getLogistics_company() {
		return logistics_company;
	}

	public void setLogistics_company(String logistics_company) {
		this.logistics_company = logistics_company;
	}

	public String getMax_process_day() {
		return max_process_day;
	}

	public void setMax_process_day(String max_process_day) {
		this.max_process_day = max_process_day;
	}

	public String getDisplay_name() {
		return display_name;
	}

	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}

	public String getService_name() {
		return service_name;
	}

	public void setService_name(String service_name) {
		this.service_name = service_name;
	}
	
	public String getAliexpressCode() {
		return aliexpressCode;
	}

	public void setAliexpressCode(String aliexpressCode) {
		this.aliexpressCode = aliexpressCode;
	}

	@Override
	public String toString() {
		return "AliexpressLogistics [recommend_order=" + recommend_order + ", tracking_no_regex=" + tracking_no_regex
				+ ", min_process_day=" + min_process_day + ", logistics_company=" + logistics_company
				+ ", max_process_day=" + max_process_day + ", display_name=" + display_name + ", service_name="
				+ service_name + ", aliexpressCode=" + aliexpressCode + "]";
	}

}
