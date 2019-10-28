package com.rondaful.cloud.seller.entity;

import java.util.Date;

public class PublishLog {
    private Long id;

    private String type;

    private Integer operatorId;

    private String operatorName;

    private Date createTime;

    private String content;

    private Long publishId;
    
    
    
    public Long getPublishId() {
		return publishId;
	}

	public void setPublishId(Long publishId) {
		this.publishId = publishId;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName == null ? null : operatorName.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

	@Override
	public String toString() {
		return "PublishLog [id=" + id + ", type=" + type + ", operatorId=" + operatorId + ", operatorName="
				+ operatorName + ", createTime=" + createTime + ", content=" + content + ", publishId=" + publishId
				+ "]";
	}

}