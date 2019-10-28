package com.rondaful.cloud.order.model.dto.sysOrderInvoice;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Blade
 * @date 2019-06-18 16:46:21
 **/
public class SysOrderInvoiceInsertOrUpdateDTO implements Serializable {

    private static final long serialVersionUID = -3748894161498807947L;

    @ApiModelProperty(value = "系统订单ID")
    private String sysOrderId;

    @ApiModelProperty(value = "发票模板ID")
    private Integer invoiceTemplateId;

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

    @ApiModelProperty(value = "网站")
    private String webSite;

    @ApiModelProperty(value = "传真")
    private String fax;

    @ApiModelProperty(value = "国家中文名")
    private String countryCnName;

    @ApiModelProperty(value = "国家，二字编码，例如：中国-CN")
    private String countryCode;

    @ApiModelProperty(value = "州/省")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "详细地址")
    private String detailAddress;

    @ApiModelProperty(value = "邮编")
    private String postcode;

    @ApiModelProperty(value = "VAT_税号")
    private String vatTaxNumber;

    @ApiModelProperty(value = "VAT_税率")
    private BigDecimal vatTaxRate;

    @ApiModelProperty(value = "VAT_应税，1-Total，2-Total（1+VAT税率）")
    private String vatTaxType;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "修改人")
    private String modifier;

    @ApiModelProperty(value = "修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")    private Date modifiedTime;
    public String getCountryCnName() {
        return countryCnName;
    }

    public void setCountryCnName(String countryCnName) {
        this.countryCnName = countryCnName;
    }

    @Override
    public String toString() {
        return "SysOrderInvoiceInsertOrUpdateDTO{" +
                "sysOrderId='" + sysOrderId + '\'' +
                ", invoiceTemplateId=" + invoiceTemplateId +
                ", templateName='" + templateName + '\'' +
                ", exportLanguage='" + exportLanguage + '\'' +
                ", title='" + title + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                ", telPhone='" + telPhone + '\'' +
                ", webSite='" + webSite + '\'' +
                ", fax='" + fax + '\'' +
                ", countryCnName='" + countryCnName + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", detailAddress='" + detailAddress + '\'' +
                ", postcode='" + postcode + '\'' +
                ", vatTaxNumber='" + vatTaxNumber + '\'' +
                ", vatTaxRate=" + vatTaxRate +
                ", vatTaxType='" + vatTaxType + '\'' +
                ", creator='" + creator + '\'' +
                ", createTime=" + createTime +
                ", modifier='" + modifier + '\'' +
                ", modifiedTime=" + modifiedTime +
                '}';
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getSysOrderId() {
        return sysOrderId;
    }

    public void setSysOrderId(String sysOrderId) {
        this.sysOrderId = sysOrderId;
    }

    public Integer getInvoiceTemplateId() {
        return invoiceTemplateId;
    }

    public void setInvoiceTemplateId(Integer invoiceTemplateId) {
        this.invoiceTemplateId = invoiceTemplateId;
    }

    public String getExportLanguage() {
        return exportLanguage;
    }

    public void setExportLanguage(String exportLanguage) {
        this.exportLanguage = exportLanguage;
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

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
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

    public String getVatTaxType() {
        return vatTaxType;
    }

    public void setVatTaxType(String vatTaxType) {
        this.vatTaxType = vatTaxType;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}
