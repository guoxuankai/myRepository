package com.rondaful.cloud.commodity.entity;

import java.io.Serializable;
import java.util.Date;

/**
* @Description:商品禁售表实体类
* @author:范津 
* @date:2019年5月23日 上午10:08:05
 */
public class CommodityLimitSale implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Long id;

    private Long commodityId;

    private String code;
    
    //1：code为国家编号，2：code为卖家ID
    private Integer codeType;

    private Long version;

    private Date createTime;

    private Date updateTime;

   
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(Long commodityId) {
        this.commodityId = commodityId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Integer getCodeType() {
		return codeType;
	}

	public void setCodeType(Integer codeType) {
		this.codeType = codeType;
	}

	public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}