package com.rondaful.cloud.seller.vo;

import java.math.BigDecimal;
import java.util.List;


import io.swagger.annotations.ApiModelProperty;

public class AmazonDisposePriceVO {

	@ApiModelProperty(value = "最终售价(美元)")
	private BigDecimal priceUSD;

	@ApiModelProperty(value = "计价模板json")
	private String pricingRuleJson;
	
	@ApiModelProperty(value = "品连sku")
	private String[] plSkus;
	
	@ApiModelProperty(value = "站点货币币种")
	private String siteCurrency;
	
	@ApiModelProperty(value = "原货币币种")
	private String sourceCurrency;
	
	
	
	
	
	public String getSourceCurrency() {
		return sourceCurrency;
	}


	public void setSourceCurrency(String sourceCurrency) {
		this.sourceCurrency = sourceCurrency;
	}


	public String getSiteCurrency() {
		return siteCurrency;
	}


	public void setSiteCurrency(String siteCurrency) {
		this.siteCurrency = siteCurrency;
	}


	public BigDecimal getPriceUSD() {
		return priceUSD;
	}


	public void setPriceUSD(BigDecimal priceUSD) {
		this.priceUSD = priceUSD;
	}


	public String getPricingRuleJson() {
		return pricingRuleJson;
	}


	public void setPricingRuleJson(String pricingRuleJson) {
		this.pricingRuleJson = pricingRuleJson;
	}
	public String[] getPlSkus() {
		return plSkus;
	}


	public void setPlSkus(String[] plSkus) {
		this.plSkus = plSkus;
	}



	/**
	 * 计价模板
	 * @author dsl
	 *
	 */
	public static class pricingRule{
		private BigDecimal saleProfit;//销售利润
		private String logisticsAddress;//物流运费地址
		private BigDecimal logisticsPrice;//物流运费
		private BigDecimal brokeragePriceRatio;//佣金百分比
		private String brokeragePriceText;//佣金具体值
		private List<Item> items;//自定义添加的费用
		public BigDecimal getSaleProfit() {
			return saleProfit;
		}
		public void setSaleProfit(BigDecimal saleProfit) {
			this.saleProfit = saleProfit;
		}
		public String getLogisticsAddress() {
			return logisticsAddress;
		}
		public void setLogisticsAddress(String logisticsAddress) {
			this.logisticsAddress = logisticsAddress;
		}
		public BigDecimal getLogisticsPrice() {
			return logisticsPrice;
		}
		public void setLogisticsPrice(BigDecimal logisticsPrice) {
			this.logisticsPrice = logisticsPrice;
		}
		public BigDecimal getBrokeragePriceRatio() {
			return brokeragePriceRatio;
		}
		public void setBrokeragePriceRatio(BigDecimal brokeragePriceRatio) {
			this.brokeragePriceRatio = brokeragePriceRatio;
		}
		public String getBrokeragePriceText() {
			return brokeragePriceText;
		}
		public void setBrokeragePriceText(String brokeragePriceText) {
			this.brokeragePriceText = brokeragePriceText;
		}
		public List<Item> getItems() {
			return items;
		}
		public void setItems(List<Item> items) {
			this.items = items;
		}
		
	}
	
	/**
	 * 自定义项目
	 * @author dsl
	 *
	 */
	public static class Item{
		private String itemName;//费用项目名字
		private BigDecimal itemPriceRatio;//百分比
		private String itemPriceText;//具体值
		public String getItemName() {
			return itemName;
		}
		public void setItemName(String itemName) {
			this.itemName = itemName;
		}
		public BigDecimal getItemPriceRatio() {
			return itemPriceRatio;
		}
		public void setItemPriceRatio(BigDecimal itemPriceRatio) {
			this.itemPriceRatio = itemPriceRatio;
		}
		public String getItemPriceText() {
			return itemPriceText;
		}
		public void setItemPriceText(String itemPriceText) {
			this.itemPriceText = itemPriceText;
		}
		
	}
	
}
