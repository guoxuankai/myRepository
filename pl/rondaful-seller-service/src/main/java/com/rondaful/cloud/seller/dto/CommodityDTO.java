package com.rondaful.cloud.seller.dto;

import java.math.BigDecimal;

public class CommodityDTO {

	private BigDecimal commodityPriceUs;
	private BigDecimal commodityPrice;
	private String plSku;
	
	private BigDecimal computeResultPriceUs;
	private BigDecimal computeResultPrice;
	
	
	
	
	public BigDecimal getComputeResultPriceUs() {
		return computeResultPriceUs;
	}
	public void setComputeResultPriceUs(BigDecimal computeResultPriceUs) {
		this.computeResultPriceUs = computeResultPriceUs;
	}
	public BigDecimal getComputeResultPrice() {
		return computeResultPrice;
	}
	public void setComputeResultPrice(BigDecimal computeResultPrice) {
		this.computeResultPrice = computeResultPrice;
	}
	public BigDecimal getCommodityPriceUs() {
		return commodityPriceUs;
	}
	public void setCommodityPriceUs(BigDecimal commodityPriceUs) {
		this.commodityPriceUs = commodityPriceUs;
	}
	public BigDecimal getCommodityPrice() {
		return commodityPrice;
	}
	public void setCommodityPrice(BigDecimal commodityPrice) {
		this.commodityPrice = commodityPrice;
	}
	public String getPlSku() {
		return plSku;
	}
	public void setPlSku(String plSku) {
		this.plSku = plSku;
	}
	@Override
	public String toString() {
		return "CommodityDTO [commodityPriceUs=" + commodityPriceUs + ", commodityPrice=" + commodityPrice + ", plSku="
				+ plSku + ", computeResultPriceUs=" + computeResultPriceUs + ", computeResultPrice="
				+ computeResultPrice + "]";
	}
	
	
	
}
