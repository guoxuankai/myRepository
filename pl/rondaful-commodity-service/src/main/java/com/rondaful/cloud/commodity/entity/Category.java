package com.rondaful.cloud.commodity.entity;

import java.io.Serializable;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * 分类表
 * 实体类对应的数据表为：  t_category
 * @author zzx
 * @date 2018-12-03 14:05:26
 */
@ApiModel(value ="Category")
public class Category implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	
    @ApiModelProperty(value = "唯一id")
    private Long id;

    @ApiModelProperty(value = "分类名称")
    private String categoryName;

    @ApiModelProperty(value = "分类英文名称")
    private String categoryNameEn;

    @ApiModelProperty(value = "分类级别，1、2、3级")
    private Integer categoryLevel;

    @ApiModelProperty(value = "上级分类id")
    private Long categoryParentId;

    @ApiModelProperty(value = "是否绑定属性，0：不绑定，1：绑定")
    private Integer isBindAttribute;

    @ApiModelProperty(value = "绑定属性id，多个以逗号隔开")
    private String bindAttributeIds;

    @ApiModelProperty(value = "是否绑定仓库类目，0：不绑定，1：绑定")
    private Integer isBindWarehouse;

    @ApiModelProperty(value = "描述")
    private String describe;

    @ApiModelProperty(value = "分类编码")
    private String categoryCode;

    @ApiModelProperty(value = "版本号")
    private Long version;

    private String sortKey;

    private String sort;

    private List<Category> children;

    @ApiModelProperty(value = "1：开启，0：关闭")
    private Integer status;
    
    @ApiModelProperty(value = "排序，值小排前")
    private Integer sortNum;
    
    @ApiModelProperty(value = "佣金百分比")
    private Integer feeRate;
    

    
    
    public Integer getFeeRate() {
		return feeRate;
	}

	public void setFeeRate(Integer feeRate) {
		this.feeRate = feeRate;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName == null ? null : categoryName.trim();
    }

    public Integer getCategoryLevel() {
        return categoryLevel;
    }

    public void setCategoryLevel(Integer categoryLevel) {
        this.categoryLevel = categoryLevel;
    }

    public Long getCategoryParentId() {
        return categoryParentId;
    }

    public void setCategoryParentId(Long categoryParentId) {
        this.categoryParentId = categoryParentId;
    }

    public Integer getIsBindAttribute() {
        return isBindAttribute;
    }

    public void setIsBindAttribute(Integer isBindAttribute) {
        this.isBindAttribute = isBindAttribute;
    }

    public String getBindAttributeIds() {
        return bindAttributeIds;
    }

    public void setBindAttributeIds(String bindAttributeIds) {
        this.bindAttributeIds = bindAttributeIds == null ? null : bindAttributeIds.trim();
    }

    public Integer getIsBindWarehouse() {
        return isBindWarehouse;
    }

    public void setIsBindWarehouse(Integer isBindWarehouse) {
        this.isBindWarehouse = isBindWarehouse;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe == null ? null : describe.trim();
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode == null ? null : categoryCode.trim();
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    public String getCategoryNameEn() {
        return categoryNameEn;
    }

    public void setCategoryNameEn(String categoryNameEn) {
        this.categoryNameEn = categoryNameEn;
    }

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getSortNum() {
		return sortNum;
	}

	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}
    
}