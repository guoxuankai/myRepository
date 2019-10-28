package com.rondaful.cloud.commodity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value ="CodeAndValueVo")
public class CodeAndValueVo {
	
	@ApiModelProperty(value = "code")
	private String code;
	
	@ApiModelProperty(value = "value")
	private String value;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
