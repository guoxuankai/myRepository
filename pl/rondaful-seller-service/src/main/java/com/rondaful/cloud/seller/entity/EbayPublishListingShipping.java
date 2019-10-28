package com.rondaful.cloud.seller.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.annotations.ApiModelProperty;

public class EbayPublishListingShipping implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "主键")
	private Integer id;

	@ApiModelProperty(value = "列表主键id")
	private Integer listingId;

	@ApiModelProperty(value = "是否全球运输 0=不支持  1=支技")
	private Boolean globalShipping;

	@ApiModelProperty(value = "不运输地区，多个地区用逗号分开")
	private String excludeShipToLocation;

	@ApiModelProperty(value = "")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date creationTime;

	@ApiModelProperty(value = "")
	private String paymentInstructions;

	@ApiModelProperty(value = "国内运输")
	private List<EbayPublishListingShippingTransport> shippingServiceOptions;

	@ApiModelProperty(value = "国际运输")
	private List<EbayPublishListingShippingTransport> internationalShippingServiceOptions;

	public List<EbayPublishListingShippingTransport> getShippingServiceOptions() {
		return shippingServiceOptions;
	}

	public void setShippingServiceOptions(List<EbayPublishListingShippingTransport> shippingServiceOptions) {
		this.shippingServiceOptions = shippingServiceOptions;
	}

	public List<EbayPublishListingShippingTransport> getInternationalShippingServiceOptions() {
		return internationalShippingServiceOptions;
	}

	public void setInternationalShippingServiceOptions(List<EbayPublishListingShippingTransport> internationalShippingServiceOptions) {
		this.internationalShippingServiceOptions = internationalShippingServiceOptions;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getListingId() {
		return listingId;
	}

	public void setListingId(Integer listingId) {
		this.listingId = listingId;
	}

	public Boolean getGlobalShipping() {
		return globalShipping;
	}

	public void setGlobalShipping(Boolean globalShipping) {
		this.globalShipping = globalShipping;
	}

	public String getExcludeShipToLocation() {
		return excludeShipToLocation;
	}

	public void setExcludeShipToLocation(String excludeShipToLocation) {
		this.excludeShipToLocation = excludeShipToLocation;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public String getPaymentInstructions() {
		return paymentInstructions;
	}

	public void setPaymentInstructions(String paymentInstructions) {
		this.paymentInstructions = paymentInstructions;
	}




}