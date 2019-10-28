package com.rondaful.cloud.commodity.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;

/**
* @Description:sku操作日志
* @author:范津 
* @date:2019年8月27日 下午3:55:22
 */
public class SkuOperateLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "唯一id")
    private Long id;
	
	@ApiModelProperty(value = "品连sku")
	private String systemSku;

    @ApiModelProperty(value = "操作人")
    private String operateBy;

    @ApiModelProperty(value = "操作信息")
    private String operateInfo;
    
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date creatTime;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}

	public String getOperateBy() {
        return operateBy;
    }

    public void setOperateBy(String operateBy) {
        this.operateBy = operateBy == null ? null : operateBy.trim();
    }

    public String getOperateInfo() {
        return operateInfo;
    }

    public void setOperateInfo(String operateInfo) {
        this.operateInfo = operateInfo == null ? null : operateInfo.trim();
    }

	public String getSystemSku() {
		return systemSku;
	}

	public void setSystemSku(String systemSku) {
		this.systemSku = systemSku;
	}
  
}