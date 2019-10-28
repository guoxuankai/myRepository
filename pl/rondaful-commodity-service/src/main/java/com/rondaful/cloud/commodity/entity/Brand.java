package com.rondaful.cloud.commodity.entity;

import java.io.Serializable;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * 品牌表
 * 实体类对应的数据表为：  t_brand
 * @author zzx
 * @date 2018-12-07 15:30:14
 */
@ApiModel(value ="Brand")
public class Brand implements Serializable {
	private static final long serialVersionUID = 1L;
	 
    @ApiModelProperty(value = "唯一id")
    private Long id;

    @ApiModelProperty(value = "所属供应商id")
    private Long supplierId;

    @ApiModelProperty(value = "所属供应商名称")
    private String supplierName;

    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    @ApiModelProperty(value = "备用名称")
    private String name;

    @ApiModelProperty(value = "品牌网站")
    private String brandWebsite;

    @ApiModelProperty(value = "品牌描述")
    private String brandDescribe;

    @ApiModelProperty(value = "品牌LOGO")
    private String brandLogo;

    @ApiModelProperty(value = "版本号")
    private Long version;

    @ApiModelProperty(value = "审核状态，0：待审核，1：审核通过，2：审核失败")
    private Integer state;

    @ApiModelProperty(value = "审核描述")
    private String auditDescription;

    @ApiModelProperty(value = "创建时间")
    private String creatTime;
    
    @ApiModelProperty(value = "品牌英文名称")
    private String brandNameEn;
    
    @ApiModelProperty(value = "国家，多个用英文逗号分隔")
    private String country;
    
    @ApiModelProperty(value = "资质证书")
    private String proveFile;

    @ApiModelProperty(value = "是否查询绑定有商品的，1：是，其它不是")
    private Integer bindSku;
    
    
    private Long category_level_1;

    private Long category_level_2;

    private Long category_level_3;
    
   
    
    
    

	public String getBrandNameEn() {
		return brandNameEn;
	}

	public void setBrandNameEn(String brandNameEn) {
		this.brandNameEn = brandNameEn;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProveFile() {
		return proveFile;
	}

	public void setProveFile(String proveFile) {
		this.proveFile = proveFile;
	}

	public Long getCategory_level_1() {
		return category_level_1;
	}

	public void setCategory_level_1(Long category_level_1) {
		this.category_level_1 = category_level_1;
	}

	public Long getCategory_level_2() {
		return category_level_2;
	}

	public void setCategory_level_2(Long category_level_2) {
		this.category_level_2 = category_level_2;
	}

	public Long getCategory_level_3() {
		return category_level_3;
	}

	public void setCategory_level_3(Long category_level_3) {
		this.category_level_3 = category_level_3;
	}

	public Integer getBindSku() {
		return bindSku;
	}

	public void setBindSku(Integer bindSku) {
		this.bindSku = bindSku;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName == null ? null : brandName.trim();
    }

    public String getBrandWebsite() {
        return brandWebsite;
    }

    public void setBrandWebsite(String brandWebsite) {
        this.brandWebsite = brandWebsite == null ? null : brandWebsite.trim();
    }

    public String getBrandDescribe() {
        return brandDescribe;
    }

    public void setBrandDescribe(String brandDescribe) {
        this.brandDescribe = brandDescribe == null ? null : brandDescribe.trim();
    }

    public String getBrandLogo() {
        return brandLogo;
    }

    public void setBrandLogo(String brandLogo) {
        this.brandLogo = brandLogo == null ? null : brandLogo.trim();
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getAuditDescription() {
        return auditDescription;
    }

    public void setAuditDescription(String auditDescription) {
        this.auditDescription = auditDescription == null ? null : auditDescription.trim();
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}