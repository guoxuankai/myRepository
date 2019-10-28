package com.rondaful.cloud.seller.entity;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

public class AmazonPublishSubListing implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 783421390181462683L;

	private Long id;

    private Long listingId;

    private String processStatus;

    private String msgType;

    private String submitfeedId;

    private String xmls;

    private Date createTime;
    
    /** 调用接口异常信息 */
    private String resultMessage;
    
    /**  1：已完成，2：失败，3：刊登中，4：等待刊登  （注意：主表数据的状态类似类，但不一样）*/
    private Integer completeStatus;
    
    private Date updateTime;
    /** 当前调用的那个接口 */
    private String currInterface;
    
//message_id,parent_type,asin,merchant_id,
    
    /**
     * 	消息id
     */
    private Long messageId;
    
    /**
     * 	是否是父级的xml，0：是，1：否 
     */
    private Integer parentType;
    
    /**
     * asin
     */
    private String asin;
    
    /**
     * 	卖家id
     */
    private String merchantId;
    
    /**
     *	 站点id
     */
    private String marketplaceId;
    
    /** 平台sku */
    private String sku;
    
    /** 品连sku */
    private String plSku;


	/**
	 * sku对应商品的状态 1：待上架（下架），3：已上架 数据库默认上架
	 */
	private Integer plSkuStatus;  //pl_sku_status  INTEGER

	/**
	 * 对应sku到对应仓库的库存数量（当为空的时候代表老数据或者该仓库不存在此sku）
	 */
    private Long plSkuCount;   //pl_sku_count  BIGINT

    /**
     * 捆绑销售数量
     */
    private Integer plSkuSaleNum;
 	private Integer plSkuTort;


	public Integer getPlSkuTort() {
		return plSkuTort;
	}

	public void setPlSkuTort(Integer plSkuTort) {
		this.plSkuTort = plSkuTort;
	}

	public Integer getPlSkuSaleNum() {
		return plSkuSaleNum;
	}

	public void setPlSkuSaleNum(Integer plSkuSaleNum) {
		this.plSkuSaleNum = plSkuSaleNum;
	}

	public String getPlSku() {
		return plSku;
	}

	public void setPlSku(String plSku) {
		this.plSku = plSku;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Long getMessageId() {
		return messageId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}

	public Integer getParentType() {
		return parentType;
	}

	public void setParentType(Integer parentType) {
		this.parentType = parentType;
	}

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMarketplaceId() {
		return marketplaceId;
	}

	public void setMarketplaceId(String marketplaceId) {
		this.marketplaceId = marketplaceId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}

	public Integer getCompleteStatus() {
		return completeStatus;
	}

	public void setCompleteStatus(Integer completeStatus) {
		this.completeStatus = completeStatus;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCurrInterface() {
		return currInterface;
	}

	public void setCurrInterface(String currInterface) {
		this.currInterface = currInterface;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getListingId() {
        return listingId;
    }

    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus == null ? null : processStatus.trim();
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType == null ? null : msgType.trim();
    }

    public String getSubmitfeedId() {
        return submitfeedId;
    }

    public void setSubmitfeedId(String submitfeedId) {
        this.submitfeedId = submitfeedId == null ? null : submitfeedId.trim();
    }

    public String getXmls() {
        return xmls;
    }

    public void setXmls(String xmls) {
        this.xmls = xmls == null ? null : xmls.trim();
    }

	public Integer getPlSkuStatus() {
		return plSkuStatus;
	}

	public void setPlSkuStatus(Integer plSkuStatus) {
		this.plSkuStatus = plSkuStatus;
	}

	public Long getPlSkuCount() {
		return plSkuCount;
	}

	public void setPlSkuCount(Long plSkuCount) {
		this.plSkuCount = plSkuCount;
	}
}