package com.rondaful.cloud.commodity.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.afterturn.easypoi.excel.annotation.Excel;

public class SkuImportErrorRecord {
    private Long id;

    private Long importId;
    
    private String supplierSpu;

    @Excel(name = "供应商SKU",width=26)
    private String supplierSku;
    
    @Excel(name = "商品导入状态",width=26)
    private String state;

    @Excel(name = "导入日志",width=26)
    private String content;

    @Excel(name = "操作人",width=26)
    private String optUser;
    
    @Excel(name = "创建时间",width=26,format="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getImportId() {
		return importId;
	}

	public void setImportId(Long importId) {
		this.importId = importId;
	}

	public String getSupplierSpu() {
        return supplierSpu;
    }

    public void setSupplierSpu(String supplierSpu) {
        this.supplierSpu = supplierSpu == null ? null : supplierSpu.trim();
    }

    public String getSupplierSku() {
        return supplierSku;
    }

    public void setSupplierSku(String supplierSku) {
        this.supplierSku = supplierSku == null ? null : supplierSku.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getOptUser() {
        return optUser;
    }

    public void setOptUser(String optUser) {
        this.optUser = optUser == null ? null : optUser.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
    
}