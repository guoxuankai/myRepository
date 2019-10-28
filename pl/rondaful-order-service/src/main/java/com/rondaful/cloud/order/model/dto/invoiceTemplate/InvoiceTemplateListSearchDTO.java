package com.rondaful.cloud.order.model.dto.invoiceTemplate;

import com.rondaful.cloud.order.entity.BasePageSearchDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * 发票模板列表查询条件的DTO
 * @author Blade
 * @date 2019-06-17 18:46:17
 **/
@ApiModel(value ="InvoiceTemplateListSearchDTO")
public class InvoiceTemplateListSearchDTO extends BasePageSearchDTO implements Serializable {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "卖家ID")
    private Integer sellerId;

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

    @ApiModelProperty(value = "国家")
    private String countryCode;

    @ApiModelProperty(value = "开始创建时间,格式：yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startCreateTime;

    @ApiModelProperty(value = "结束创建时间,格式：yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endCreateTime;

    private static final long serialVersionUID = -3057654721097803633L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getStartCreateTime() {
        return startCreateTime;
    }

    public void setStartCreateTime(String startCreateTime) {
        this.startCreateTime = startCreateTime;
    }

    public String getEndCreateTime() {
        return endCreateTime;
    }

    public void setEndCreateTime(String endCreateTime) {
        this.endCreateTime = endCreateTime;
    }
}
