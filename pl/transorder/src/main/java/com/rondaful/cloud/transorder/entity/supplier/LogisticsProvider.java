package com.rondaful.cloud.transorder.entity.supplier;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value ="LogisticsProvider")
public class LogisticsProvider implements Serializable {

	private static final long serialVersionUID = -7598824742338409737L;

	@ApiModelProperty(value = "供应商SKU")
	private String supplierSKU;
	
	@ApiModelProperty(value = "所属供应商id(userId用户ID)")
	private String supplierId;
	
	@ApiModelProperty(value = "供应商名称")
	private String supplierName;

	@ApiModelProperty(value = "仓库编码")
	private String warehouseCode;
	
	@ApiModelProperty(value = "仓库名称")
	private String WarehouseName;

	@ApiModelProperty(value = "国家简码")
	private String countryCode;
	
	private List<String> carrierCodeList;
}