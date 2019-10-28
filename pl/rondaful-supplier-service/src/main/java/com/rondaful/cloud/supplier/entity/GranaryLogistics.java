package com.rondaful.cloud.supplier.entity;

import java.io.Serializable;

/**
 * 
 * 谷仓接收实体类
 * @author xieyanbin
 *
 * @2019年4月26日 
 * @version v2.2
 */
public class GranaryLogistics implements Serializable {

	private static final long serialVersionUID = 4569225955408833046L;
	
	//运输方式代码
	private String code;
	
	//运输方式中文名称
	private String name;
	
	//运输方式英文名称
	private String name_en;
	
	//仓库代码
	private String warehouse_code;
	
	//仓库代码
	private String fl_warehouse_code;
	
	//物流产品类型，0-尾程物流产品，1-退件代选物流产品，2-头程物流产品，3-退件自选物流产品，4-未预报退件物流产品
	private String type;
	
	//是否支持签名服务,0-否，1-是
	private String is_signature;
	
	//服务商代码
	private String sp_code;
	
	private String app_key;
	
	private String app_token;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName_en() {
		return name_en;
	}

	public void setName_en(String name_en) {
		this.name_en = name_en;
	}

	public String getWarehouse_code() {
		return warehouse_code;
	}

	public void setWarehouse_code(String warehouse_code) {
		this.warehouse_code = warehouse_code;
	}

	public String getFl_warehouse_code() {
		return fl_warehouse_code;
	}

	public void setFl_warehouse_code(String fl_warehouse_code) {
		this.fl_warehouse_code = fl_warehouse_code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIs_signature() {
		return is_signature;
	}

	public void setIs_signature(String is_signature) {
		this.is_signature = is_signature;
	}

	public String getSp_code() {
		return sp_code;
	}

	public void setSp_code(String sp_code) {
		this.sp_code = sp_code;
	}
	
	public String getApp_key() {
		return app_key;
	}

	public void setApp_key(String app_key) {
		this.app_key = app_key;
	}

	public String getApp_token() {
		return app_token;
	}

	public void setApp_token(String app_token) {
		this.app_token = app_token;
	}

	@Override
	public String toString() {
		return "GranaryLogistics [code=" + code + ", name=" + name + ", name_en=" + name_en + ", warehouse_code="
				+ warehouse_code + ", fl_warehouse_code=" + fl_warehouse_code + ", type=" + type + ", is_signature="
				+ is_signature + ", sp_code=" + sp_code + ", app_key=" + app_key + ", app_token=" + app_token + "]";
	}

	
	
	
	
}
