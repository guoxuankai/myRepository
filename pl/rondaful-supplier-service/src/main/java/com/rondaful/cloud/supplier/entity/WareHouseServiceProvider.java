package com.rondaful.cloud.supplier.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 
 * @author songjie
 *
 */
public class WareHouseServiceProvider implements Serializable {
    private Integer id;

    private String serviceProviderName;

    private Byte serviceProviderStatus;
    
    private Integer supplyChainId;
    
    private String supplyChainCompany;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateDate;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServiceProviderName() {
        return serviceProviderName;
    }

    public void setServiceProviderName(String serviceProviderName) {
        this.serviceProviderName = serviceProviderName == null ? null : serviceProviderName.trim();
    }

    public Byte getServiceProviderStatus() {
        return serviceProviderStatus;
    }

    public void setServiceProviderStatus(Byte serviceProviderStatus) {
        this.serviceProviderStatus = serviceProviderStatus;
    }

    public Integer getSupplyChainId() {
  		return supplyChainId;
  	}

  	public void setSupplyChainId(Integer supplyChainId) {
  		this.supplyChainId = supplyChainId;
  	}
  	
  	public String getSupplyChainCompany() {
		return supplyChainCompany;
	}

	public void setSupplyChainCompany(String supplyChainCompany) {
		this.supplyChainCompany = supplyChainCompany;
	}

  	public Date getLastUpdateDate() {
  		return lastUpdateDate;
  	}

  	public void setLastUpdateDate(Date lastUpdateDate) {
  		this.lastUpdateDate = lastUpdateDate;
  	}
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}