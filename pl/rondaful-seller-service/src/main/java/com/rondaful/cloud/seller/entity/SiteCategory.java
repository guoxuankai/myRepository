package com.rondaful.cloud.seller.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @Description:站点分类映射表t_site_category实体类
 * @author:范津
 * @date:2019年3月9日 上午10:05:12
 */
@ApiModel(value ="SiteCategory")
public class SiteCategory {

    @ApiModelProperty(value = "唯一id")
    private Long id;

    @ApiModelProperty(value = "平台名称")
    private String platform;

    @ApiModelProperty(value = "站点名称")
    private String siteName;

    @ApiModelProperty(value = "最后一级分类ID")
    private Long categoryLevel3;

    @ApiModelProperty(value = "分类id")
    private Long platCategoryID;

    @ApiModelProperty(value = "分类路径")
    private String categoryPath;

    @ApiModelProperty(value = "分类模板1")
    private String categoryTemplate1;

    @ApiModelProperty(value = "分类模板2")
    private String categoryTemplate2;

    @ApiModelProperty(value = "版本号")
    private Long version;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Long getCategoryLevel3() {
        return categoryLevel3;
    }

    public void setCategoryLevel3(Long categoryLevel3) {
        this.categoryLevel3 = categoryLevel3;
    }

    public String getCategoryPath() {
        return categoryPath;
    }

    public void setCategoryPath(String categoryPath) {
        this.categoryPath = categoryPath;
    }

    public String getCategoryTemplate1() {
        return categoryTemplate1;
    }

    public void setCategoryTemplate1(String categoryTemplate1) {
        this.categoryTemplate1 = categoryTemplate1;
    }

    public String getCategoryTemplate2() {
        return categoryTemplate2;
    }

    public void setCategoryTemplate2(String categoryTemplate2) {
        this.categoryTemplate2 = categoryTemplate2;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getPlatCategoryID() {
        return platCategoryID;
    }

    public void setPlatCategoryID(Long platCategoryID) {
        this.platCategoryID = platCategoryID;
    }
}
