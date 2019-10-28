package com.rondaful.cloud.supplier.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author songjie
 *
 */
@ApiModel(description = "授权对像VO")
public class WareHouseAuthorizeVO implements Serializable {
    
	@ApiModelProperty(value = "主键id")
	private Integer id;
    
	@ApiModelProperty(value = "自定义名称")
    private String customName;

	@ApiModelProperty(value = "appKey")
    private String appKey;

	@ApiModelProperty(value = "appToken")
    private String appToken;

	@ApiModelProperty(value = "服务商id")
    private Integer serviceId;
    
	@ApiModelProperty(value = "[0=停用,1=启用,2删除]")
    private Integer status;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName == null ? null : customName.trim();
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey == null ? null : appKey.trim();
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken == null ? null : appToken.trim();
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}