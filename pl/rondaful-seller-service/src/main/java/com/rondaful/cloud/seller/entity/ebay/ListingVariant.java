package com.rondaful.cloud.seller.entity.ebay;

import java.math.BigDecimal;


public class ListingVariant {
	
    private String plSku;  

    private String platformSku;

    private BigDecimal startPrice;

    private Integer quantity;

	private String ean;

    private String upc;
    
    private String mpn;

	private String isbn;
    
    private String multiattribute;  //添加的多个属性的数据集合
    
    private String picture;
    
	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getPlSku() {
		return plSku;
	}

	public void setPlSku(String plSku) {
		this.plSku = plSku;
	}

	public String getPlatformSku() {
		return platformSku;
	}

	public void setPlatformSku(String platformSku) {
		this.platformSku = platformSku;
	}

	public BigDecimal getStartPrice() {
		return startPrice;
	}

	public void setStartPrice(BigDecimal startPrice) {
		this.startPrice = startPrice;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getUpc() {
		return upc;
	}

	public void setUpc(String upc) {
		this.upc = upc;
	}

	public String getMpn() {
		return mpn;
	}

	public void setMpn(String mpn) {
		this.mpn = mpn;
	}

	public String getMultiattribute() {
		return multiattribute;
	}

	public void setMultiattribute(String multiattribute) {
		this.multiattribute = multiattribute;
	}

	public String getEan() {
		return ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
}
