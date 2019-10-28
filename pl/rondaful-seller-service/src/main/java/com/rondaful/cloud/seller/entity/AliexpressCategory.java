package com.rondaful.cloud.seller.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * 速卖通分类表
 * 实体类对应的数据表为：  aliexpress_category
 * @author chenhan
 * @date 2019-04-10 13:36:20
 */
@ApiModel(value ="AliexpressCategory")
public class AliexpressCategory implements Serializable {
    private static final long serialVersionUID = -4697415534163157793L;
    @ApiModelProperty(value = "")
    private Long id;

    @ApiModelProperty(value = "类别id")
    private Long categoryId;

    @ApiModelProperty(value = "发布类目层级")
    private Integer categoryLevel;

    @ApiModelProperty(value = "类型名称")
    private String categoryName;

    @ApiModelProperty(value = "英文类型名称")
    private String categoryNameEn;

    @ApiModelProperty(value = "all类型名称,类目多语言名称")
    private String categoryNameAll;

    @ApiModelProperty(value = "类型父id")
    private Long categoryParentId;

    @ApiModelProperty(value = "是否叶子发布类目")
    private Boolean isleaf;

    @ApiModelProperty(value = "分类是否需要尺寸属性")
    private Boolean sizeis;

    @ApiModelProperty(value = "创建者")
    private Long creatorId;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date creationTime;

    @ApiModelProperty(value = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "0  有效  1 无效")
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getCategoryLevel() {
        return categoryLevel;
    }

    public void setCategoryLevel(Integer categoryLevel) {
        this.categoryLevel = categoryLevel;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName == null ? null : categoryName.trim();
    }

    public String getCategoryNameEn() {
        return categoryNameEn;
    }

    public void setCategoryNameEn(String categoryNameEn) {
        this.categoryNameEn = categoryNameEn == null ? null : categoryNameEn.trim();
    }

    public String getCategoryNameAll() {
        return categoryNameAll;
    }

    public void setCategoryNameAll(String categoryNameAll) {
        this.categoryNameAll = categoryNameAll == null ? null : categoryNameAll.trim();
    }

    public Long getCategoryParentId() {
        return categoryParentId;
    }

    public void setCategoryParentId(Long categoryParentId) {
        this.categoryParentId = categoryParentId;
    }

    public Boolean getIsleaf() {
        return isleaf;
    }

    public void setIsleaf(Boolean isleaf) {
        this.isleaf = isleaf;
    }

    public Boolean getSizeis() {
        return sizeis;
    }

    public void setSizeis(Boolean sizeis) {
        this.sizeis = sizeis;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}