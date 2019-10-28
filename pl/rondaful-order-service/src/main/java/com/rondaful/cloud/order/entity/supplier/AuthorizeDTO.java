package com.rondaful.cloud.order.entity.supplier;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class AuthorizeDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "账号自定义名称")
	private String customName;
	@ApiModelProperty(value = "客户编码")
	private String companyCode;
	@ApiModelProperty(value = "授权key")
	private String appKey;
	@ApiModelProperty(value = "授权token")
	private String appToken;
//	private List<String> referenceIDList = new ArrayList<>();
}