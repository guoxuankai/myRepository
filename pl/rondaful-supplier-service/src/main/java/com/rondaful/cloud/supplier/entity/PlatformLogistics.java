package com.rondaful.cloud.supplier.entity;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * 
 *  t_platform_logistics
 * @author xieyanbin
 * @date 2018-12-14 10:15:24
 */
@ApiModel(value ="t_platform_logistics")
public class PlatformLogistics implements Serializable {
    @ApiModelProperty(value = "")
    private Long id;

    @ApiModelProperty(value = "ebay物流商")
    private String ebayCarrier;

    @ApiModelProperty(value = "amazon物流商")
    private String amazonCarrier;

    @ApiModelProperty(value = "amazon物流方式代码")
    private String amazonCode;
    
    @ApiModelProperty(value = "速卖通物流方式code")
    private List<AliexpressLogistics> aliexpressCode;

    private static final long serialVersionUID = 1L;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEbayCarrier() {
		return ebayCarrier;
	}

	public void setEbayCarrier(String ebayCarrier) {
		this.ebayCarrier = ebayCarrier;
	}

	public String getAmazonCarrier() {
		return amazonCarrier;
	}

	public void setAmazonCarrier(String amazonCarrier) {
		this.amazonCarrier = amazonCarrier;
	}

	public String getAmazonCode() {
		return amazonCode;
	}

	public void setAmazonCode(String amazonCode) {
		this.amazonCode = amazonCode;
	}

	public List<AliexpressLogistics> getAliexpressCode() {
		return aliexpressCode;
	}

	public void setAliexpressCode(List<AliexpressLogistics> aliexpressCode) {
		this.aliexpressCode = aliexpressCode;
	}

	@Override
	public String toString() {
		return "PlatformLogistics [id=" + id + ", ebayCarrier=" + ebayCarrier + ", amazonCarrier=" + amazonCarrier
				+ ", amazonCode=" + amazonCode + "]";
	}

	
    
}