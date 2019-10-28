package com.rondaful.cloud.order.model.vo.invoiceTemplate;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 发票模板列表视图
 * @author Blade
 * @date 2019-06-17 18:47:11
 **/
public class InvoiceTemplateListVO implements Serializable {
    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "模板名称")
    private String templateName;

    @ApiModelProperty(value = "导出语言, EN-英语，DE-德国)")
    private String exportLanguage;

    @ApiModelProperty(value = "公司名称/个人姓名")
    private String title;

    @ApiModelProperty(value = "联络信息,邮箱或手机等")
    private String contactInfo;

    @ApiModelProperty(value = "电话")
    private String telPhone;

    @ApiModelProperty(value = "国家，二字编码，例如：中国-CN")
    private String countryCode;

    @ApiModelProperty(value = "国家中文名")
    private String countryCnName;

    @ApiModelProperty(value = "VAT_税号")
    private String vatTaxNumber;

    @ApiModelProperty(value = "VAT_税率")
    private BigDecimal vatTaxRate;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private static final long serialVersionUID = -613670808546026050L;

    public Integer getId() {
        return id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getExportLanguage() {
        return exportLanguage;
    }

    public void setExportLanguage(String exportLanguage) {
        this.exportLanguage = exportLanguage;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getTelPhone() {
        return telPhone;
    }

    public void setTelPhone(String telPhone) {
        this.telPhone = telPhone;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getVatTaxNumber() {
        return vatTaxNumber;
    }

    public void setVatTaxNumber(String vatTaxNumber) {
        this.vatTaxNumber = vatTaxNumber;
    }

    public BigDecimal getVatTaxRate() {
        return vatTaxRate;
    }

    public void setVatTaxRate(BigDecimal vatTaxRate) {
        this.vatTaxRate = vatTaxRate;
    }

    public String getCountryCnName() {
        return countryCnName;
    }

    public void setCountryCnName(String countryCnName) {
        this.countryCnName = countryCnName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
