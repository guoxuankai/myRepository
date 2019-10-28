package com.rondaful.cloud.commodity.entity;

import java.io.Serializable;

import cn.afterturn.easypoi.excel.annotation.Excel;

/**
* @Description:平台sku映射导入日志
* @author:范津 
* @date:2019年8月9日 上午11:55:54
 */
public class SkuMapImportLog implements Serializable{
	private static final long serialVersionUID = 1L;

	private Long id;

    private Long importId;

    @Excel(name = "平台[platform]",width=26)
    private String platform;

    @Excel(name = "授权店铺账号[empower account]",width=40)
    private String empowerAccount;

    @Excel(name = "平台sku[platform sku]",width=26)
    private String platformSku;

    @Excel(name = "品连sku[pinLian sku]",width=26)
    private String systemSku;

    @Excel(name = "导入状态[state]",width=26)
    private String state;

    @Excel(name = "导入日志[content]",width=26)
    private String content;

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

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform == null ? null : platform.trim();
    }

    public String getEmpowerAccount() {
		return empowerAccount;
	}

	public void setEmpowerAccount(String empowerAccount) {
		this.empowerAccount = empowerAccount;
	}

	public String getPlatformSku() {
        return platformSku;
    }

    public void setPlatformSku(String platformSku) {
        this.platformSku = platformSku == null ? null : platformSku.trim();
    }

    public String getSystemSku() {
        return systemSku;
    }

    public void setSystemSku(String systemSku) {
        this.systemSku = systemSku == null ? null : systemSku.trim();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state == null ? null : state.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}