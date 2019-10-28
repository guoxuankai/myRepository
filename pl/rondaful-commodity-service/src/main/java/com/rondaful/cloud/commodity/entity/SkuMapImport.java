package com.rondaful.cloud.commodity.entity;

import java.io.Serializable;
import java.util.Date;

/**
* @Description:平台sku映射导入记录
* @author:范津 
* @date:2019年8月9日 上午11:55:29
 */
public class SkuMapImport implements Serializable{
	private static final long serialVersionUID = 1L;

	private Long id;

    private String fileName;

    private String fileUrl;

    private String optUser;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl == null ? null : fileUrl.trim();
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
}