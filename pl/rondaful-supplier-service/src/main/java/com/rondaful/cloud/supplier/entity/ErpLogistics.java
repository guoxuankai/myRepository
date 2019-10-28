package com.rondaful.cloud.supplier.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 *  接收erp物流渠道信息接口实体
 * @author xieyanbin
 *
 * @2018年12月17日 
 * @version v1.0
 */
public class ErpLogistics implements Serializable {

	//邮寄方式简称
	private String shortname;
	//邮寄方式全称
	private String fullname;
	//邮寄方式代码
	private String code;
	//物流商名称
	private String carrier_name;
	//物流商代码
	private String carrier_code;
	
	private List<ErpWarehouse> use_warehouse_arr;
	
	private String warehouseCode;
 
	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCarrier_name() {
		return carrier_name;
	}

	public void setCarrier_name(String carrier_name) {
		this.carrier_name = carrier_name;
	}

	public String getCarrier_code() {
		return carrier_code;
	}

	public void setCarrier_code(String carrier_code) {
		this.carrier_code = carrier_code;
	}

	public List<ErpWarehouse> getUse_warehouse_arr() {
		return use_warehouse_arr;
	}

	public void setUse_warehouse_arr(List<ErpWarehouse> use_warehouse_arr) {
		this.use_warehouse_arr = use_warehouse_arr;
	}

	public String getWarehouseCode() {
		return warehouseCode;
	}

	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}

	@Override
	public String toString() {
		return "ErpLogistics [shortname=" + shortname + ", fullname=" + fullname + ", code=" + code + ", carrier_name="
				+ carrier_name + ", carrier_code=" + carrier_code + ", use_warehouse_arr=" + use_warehouse_arr
				+ ", warehouseCode=" + warehouseCode + "]";
	}

	

	
}
