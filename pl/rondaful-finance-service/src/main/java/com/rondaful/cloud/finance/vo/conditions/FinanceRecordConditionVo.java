package com.rondaful.cloud.finance.vo.conditions;

import io.swagger.annotations.ApiModelProperty;

public class FinanceRecordConditionVo extends BaseConditionVo {

	@ApiModelProperty("交易类型")
	private String tradeType;
	@ApiModelProperty("收支类型")
	private String type;

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
