package com.rondaful.cloud.commodity.entity;

import java.util.Date;

public class BindCategoryAliexpress {
    private Long id;

    private Long pinlianCategoty3Id;

    //速卖通分类ID,多级用逗号分隔
    private String aliCategoryIds;

    private Date updateTime;

    private Long version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPinlianCategoty3Id() {
        return pinlianCategoty3Id;
    }

    public void setPinlianCategoty3Id(Long pinlianCategoty3Id) {
        this.pinlianCategoty3Id = pinlianCategoty3Id;
    }

    public String getAliCategoryIds() {
		return aliCategoryIds;
	}

	public void setAliCategoryIds(String aliCategoryIds) {
		this.aliCategoryIds = aliCategoryIds;
	}

	public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}