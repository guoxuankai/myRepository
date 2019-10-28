package com.rondaful.cloud.order.model.vo.invoiceTemplate;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 发票模板详细信息VO
 *
 * @author Blade
 * @date 2019-06-19 18:02:55
 **/
public class InvoiceTemplateInfoVO implements Serializable {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "id")
    private Integer invoiceTemplateId;

    @ApiModelProperty(value = "模板名称")
    private String templateName;

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

    @ApiModelProperty(value = "国家，二字编码，例如：中国-CN")
    private String countryCode;

    @ApiModelProperty(value = "国家中文名")
    private String countryCnName;

    @ApiModelProperty(value = "导出语言, EN-英语，DE-德国)")
    private String exportLanguage;

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

    private static final long serialVersionUID = 2825481548853615771L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
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

    public String getCountryCnName() {
        return countryCnName;
    }

    public void setCountryCnName(String countryCnName) {
        this.countryCnName = countryCnName;
    }

    public String getExportLanguage() {
        return exportLanguage;
    }

    public void setExportLanguage(String exportLanguage) {
        this.exportLanguage = exportLanguage;
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

    public Integer getInvoiceTemplateId() {
        return invoiceTemplateId;
    }

    public void setInvoiceTemplateId(Integer invoiceTemplateId) {
        this.invoiceTemplateId = invoiceTemplateId;
    }
}
