package com.rondaful.cloud.commodity.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
* @Description:SPU分类映射表t_spu_category实体类
* @author:范津 
* @date:2019年3月8日 下午3:49:25
 */
@ApiModel(value ="SpuCategory")
public class SpuCategory {
	
	@ApiModelProperty(value = "唯一id")
    private Long id;

	@ApiModelProperty(value = "商品spu码")
    private String spu;

	@ApiModelProperty(value = "平台名称")
    private String platform;
	
	@ApiModelProperty(value = "站点编码")
	private String siteCode;

	@ApiModelProperty(value = "站点名称")
    private String siteName;

	@ApiModelProperty(value = "平台商品分类ID")
	private Long platCategoryId;
	
	@ApiModelProperty(value = "最后一级分类ID")
    private Long categoryLevel3;

    @ApiModelProperty(value = "分类路径")
    private String categoryPath;

    @ApiModelProperty(value = "版本号")
    private Long version;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date creatTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpu() {
		return spu;
	}

	public void setSpu(String spu) {
		this.spu = spu;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

	public String getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

	public Long getPlatCategoryId() {
		return platCategoryId;
	}

	public void setPlatCategoryId(Long platCategoryId) {
		this.platCategoryId = platCategoryId;
	}
    
}