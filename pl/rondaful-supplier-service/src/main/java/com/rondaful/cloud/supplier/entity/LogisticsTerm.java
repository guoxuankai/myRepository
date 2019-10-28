package com.rondaful.cloud.supplier.entity;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * 
 * 管理后台物流管理查询参数
 * @author xieyanbin
 *
 * @2018年12月13日 
 * @version v1.0
 */
public class LogisticsTerm implements Serializable {

	//物流服务商
	private String carrierName;
	
    private String foreignsCarrierName;
	//物流方式
	private String type;
	public String getCarrierName() {
		return carrierName;
	}
	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}
	public String getForeignsCarrierName() {
		return foreignsCarrierName;
	}
	public void setForeignsCarrierName(String foreignsCarrierName) {
		this.foreignsCarrierName = foreignsCarrierName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	

}
