package com.rondaful.cloud.seller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

@ApiModel(description = "商品库存状态(判断顺序 上下架  是否侵权 ， 是否缺货  是否低于预警)")
public class CommodityStatusVO implements Serializable {

	private static final long serialVersionUID = 7128794523134041661L;

    @ApiModelProperty(value = "sku")
    private String plSku;

	@ApiModelProperty(value = "美元价格")
	private String commodityPriceUs;

	@ApiModelProperty(value = "库存数量")
	private Long availableQty;

	@ApiModelProperty(value = "预警数量 -1或者空  表示不设限制")
	private Long warnVal;

	@ApiModelProperty(value = "状态 1下架 3正常 ")
	private Integer status;

	@ApiModelProperty(value = "侵权状态 1侵权，0不侵权")
	private Integer tortFlag;

	@ApiModelProperty(value = "显示状态 0正常1下架2侵权3缺货4低于预警")
	private Integer showStatus;

	public Long getAvailableQty() {
		return availableQty;
	}

	public void setAvailableQty(Long availableQty) {
		this.availableQty = availableQty;
	}

	public Long getWarnVal() {
		return warnVal;
	}

	public void setWarnVal(Long warnVal) {
		this.warnVal = warnVal;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getTortFlag() {
		return tortFlag;
	}

	public void setTortFlag(Integer tortFlag) {
		this.tortFlag = tortFlag;
	}

	public String getCommodityPriceUs() {
		return commodityPriceUs;
	}

	public void setCommodityPriceUs(String commodityPriceUs) {
		this.commodityPriceUs = commodityPriceUs;
	}

	public Integer getShowStatus() {
		return showStatus;
	}

	public void setShowStatus(Integer showStatus) {
		this.showStatus = showStatus;
	}

    public String getPlSku() {
        return plSku;
    }

    public void setPlSku(String plSku) {
        this.plSku = plSku;
    }
}
