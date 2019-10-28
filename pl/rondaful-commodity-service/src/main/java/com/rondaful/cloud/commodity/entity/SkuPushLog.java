package com.rondaful.cloud.commodity.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
* @Description:sku推送操作日志
* @author:范津 
* @date:2019年4月26日 上午10:50:45
 */
@ApiModel(value ="SkuPushLog")
public class SkuPushLog implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "唯一id")
    private Long id;

	@ApiModelProperty(value = "sku推送记录ID")
    private Long recordId;

	@ApiModelProperty(value = "操作人账号")
    private String optUser;
	
	@ApiModelProperty(value = "操作类型")
    private Integer optType;

	@ApiModelProperty(value = "操作内容")
    private String content;

	@ApiModelProperty(value = "创建时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getOptUser() {
        return optUser;
    }

    public void setOptUser(String optUser) {
        this.optUser = optUser == null ? null : optUser.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

	public Integer getOptType() {
		return optType;
	}

	public void setOptType(Integer optType) {
		this.optType = optType;
	}

    
}