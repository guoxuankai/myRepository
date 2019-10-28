package com.rondaful.cloud.commodity.entity;

import java.io.Serializable;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * 属性表
 * 实体类对应的数据表为：  t_attribute
 * @author zzx
 * @date 2018-12-03 10:50:41
 */
@ApiModel(value ="Attribute")
public class Attribute implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    @ApiModelProperty(value = "唯一id")
    private Long id;

    @ApiModelProperty(value = "属性中文名称")
    private String attributeNameCn;

    @ApiModelProperty(value = "属性英文名称")
    private String attributeNameEn;

    @ApiModelProperty(value = "属性描述")
    private String attributeDescribe;

    @ApiModelProperty(value = "属性值（中文：英文），多个以|隔开")
    private String attributeValue;

    @ApiModelProperty(value = "版本号")
    private Long version;

    @ApiModelProperty(value = "是否sku，1是，0否")
    private Integer isSku;
    
    @ApiModelProperty(value = "输入方式，1单选，2多选，3文本框")
    private Integer inputType;
    
    
    
    
    public Integer getIsSku() {
		return isSku;
	}

	public void setIsSku(Integer isSku) {
		this.isSku = isSku;
	}

	public Integer getInputType() {
		return inputType;
	}

	public void setInputType(Integer inputType) {
		this.inputType = inputType;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAttributeNameCn() {
        return attributeNameCn;
    }

    public void setAttributeNameCn(String attributeNameCn) {
        this.attributeNameCn = attributeNameCn == null ? null : attributeNameCn.trim();
    }

    public String getAttributeNameEn() {
        return attributeNameEn;
    }

    public void setAttributeNameEn(String attributeNameEn) {
        this.attributeNameEn = attributeNameEn == null ? null : attributeNameEn.trim();
    }

    public String getAttributeDescribe() {
        return attributeDescribe;
    }

    public void setAttributeDescribe(String attributeDescribe) {
        this.attributeDescribe = attributeDescribe == null ? null : attributeDescribe.trim();
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue == null ? null : attributeValue.trim();
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}