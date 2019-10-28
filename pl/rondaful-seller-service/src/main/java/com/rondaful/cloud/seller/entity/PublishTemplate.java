package com.rondaful.cloud.seller.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * 刊登模板
 * 实体类对应的数据表为：  publish_template
 * @author chenhan
 * @date 2019-04-19 14:15:18
 */
@ApiModel(value ="PublishTemplate")
public class PublishTemplate implements Serializable {

    private static final long serialVersionUID = -2927881543097097676L;

    @ApiModelProperty(value = "")
    private Long id;

    @ApiModelProperty(value = "模板类型 1:物流设置2:买家限制3:商品所在地4:议价5:退货政策6:收款说明7:橱窗展示8:屏蔽目的地10生成平台sku模板({'ruleOne':'name 授权店铺名称 或者sku ','ruleTwo':'@ 符号','ruleThree':'8 长度'})  aliexpress速卖通40:产品模板 41区域调价模板")
    private Integer templateType;

    @ApiModelProperty(value = "平台站点")
    private String site;

    @ApiModelProperty(value = "平台 1:amazon 2:eBay 3:wish 4:aliexpress")
    private Integer platform;

    @ApiModelProperty(value = "店铺账号id")
    private String empowerId;

    @ApiModelProperty(value = "店铺账号名称")
    private String empowerName;

    @ApiModelProperty(value = "名称")
    private String templateName;

    @ApiModelProperty(value = "是否默认")
    private Boolean defaultIs;

    @ApiModelProperty(value = "是否系统模板")
    private Boolean systemIs;

    @ApiModelProperty(value = "内容")
    private String contentExt;

    @ApiModelProperty(value = "品连账号")
    private String plAccount;

    @ApiModelProperty(value = "创建者")
    private Long createId;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
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

    public Integer getTemplateType() {
        return templateType;
    }

    public void setTemplateType(Integer templateType) {
        this.templateType = templateType;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site == null ? null : site.trim();
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public String getEmpowerId() {
        return empowerId;
    }

    public void setEmpowerId(String empowerId) {
        this.empowerId = empowerId == null ? null : empowerId.trim();
    }

    public String getEmpowerName() {
        return empowerName;
    }

    public void setEmpowerName(String empowerName) {
        this.empowerName = empowerName == null ? null : empowerName.trim();
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName == null ? null : templateName.trim();
    }

    public Boolean getDefaultIs() {
        return defaultIs;
    }

    public void setDefaultIs(Boolean defaultIs) {
        this.defaultIs = defaultIs;
    }

    public Boolean getSystemIs() {
        return systemIs;
    }

    public void setSystemIs(Boolean systemIs) {
        this.systemIs = systemIs;
    }

    public String getContentExt() {
        return contentExt;
    }

    public void setContentExt(String contentExt) {
        this.contentExt = contentExt == null ? null : contentExt.trim();
    }

    public String getPlAccount() {
        return plAccount;
    }

    public void setPlAccount(String plAccount) {
        this.plAccount = plAccount == null ? null : plAccount.trim();
    }

    public Long getCreateId() {
        return createId;
    }

    public void setCreateId(Long createId) {
        this.createId = createId;
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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