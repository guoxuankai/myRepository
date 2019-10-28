package com.brandslink.cloud.user.dto.response;

import com.brandslink.cloud.user.entity.ShipperInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 客户信息表dto
 *
 * @author tianye
 * @date 2019-06-10 10:00:40
 */
@ApiModel(value = "CustomerInfo")
public class CustomerInfoResponseDTO implements Serializable {

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "客户id（最长16位）")
    private String customerAppId;

    @ApiModelProperty(value = "客户秘钥")
    private String customerSecretKey;

    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @ApiModelProperty(value = "中文名称（简称）")
    private String shortenedChineseName;

    @ApiModelProperty(value = "中文名称（全称）")
    private String chineseName;

    @ApiModelProperty(value = "英文名称")
    private String englishName;

    @ApiModelProperty(value = "联系人")
    private String contacts;

    @ApiModelProperty(value = "联系方式")
    private String contactWay;

    @ApiModelProperty(value = "商务")
    private String commerce;

    @ApiModelProperty(value = "商务联系方式")
    private String commerceWay;

    @ApiModelProperty(value = "合同日期")
    private String contractDate;

    @ApiModelProperty(value = "销退质检方式代码")
    private String cancellationRefundCode;

    @ApiModelProperty(value = "销退质检方式名称")
    private String cancellationRefundName;

    @ApiModelProperty(value = "客户状态 1：正常  2：作废")
    private String status;

    @ApiModelProperty(value = "省")
    private String provincial;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "区")
    private String district;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "最后一次修改人")
    private String lastUpdateBy;

    @ApiModelProperty(value = "最后一次修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateTime;

    @ApiModelProperty(value = "货主信息")
    private List<ShipperInfo> shipperInfoList;

    @ApiModelProperty(value = "所属仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "主账号")
    private String account;

    @ApiModelProperty(value = "法定代表人")
    private String legalRepresentative;

    @ApiModelProperty(value = "法定代表人身份证号码")
    private String legalRepresentativeIdentityCard;

    @ApiModelProperty(value = "营业执照")
    private String businessLicense;

    @ApiModelProperty(value = "法定代表人身份证扫描件（正面）")
    private String identityCardFront;

    @ApiModelProperty(value = "法定代表人身份证扫描件（反面）")
    private String identityCardVerso;

    @ApiModelProperty(value = "审核状态 0：待提交 1：待审核 2：审核通过 3：审核不通过")
    private String auditStatus;

    @ApiModelProperty(value = "审核人")
    private String auditor;

    @ApiModelProperty(value = "审核时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditDate;

    @ApiModelProperty(value = "审核不通过原因")
    private String auditFailedCause;

    @ApiModelProperty(value = "客户回调接口")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getShortenedChineseName() {
        return shortenedChineseName;
    }

    public void setShortenedChineseName(String shortenedChineseName) {
        this.shortenedChineseName = shortenedChineseName;
    }

    public String getCommerce() {
        return commerce;
    }

    public void setCommerce(String commerce) {
        this.commerce = commerce;
    }

    public String getCommerceWay() {
        return commerceWay;
    }

    public void setCommerceWay(String commerceWay) {
        this.commerceWay = commerceWay;
    }

    public String getContractDate() {
        return contractDate;
    }

    public void setContractDate(String contractDate) {
        this.contractDate = contractDate;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getLegalRepresentative() {
        return legalRepresentative;
    }

    public void setLegalRepresentative(String legalRepresentative) {
        this.legalRepresentative = legalRepresentative;
    }

    public String getLegalRepresentativeIdentityCard() {
        return legalRepresentativeIdentityCard;
    }

    public void setLegalRepresentativeIdentityCard(String legalRepresentativeIdentityCard) {
        this.legalRepresentativeIdentityCard = legalRepresentativeIdentityCard;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public String getIdentityCardFront() {
        return identityCardFront;
    }

    public void setIdentityCardFront(String identityCardFront) {
        this.identityCardFront = identityCardFront;
    }

    public String getIdentityCardVerso() {
        return identityCardVerso;
    }

    public void setIdentityCardVerso(String identityCardVerso) {
        this.identityCardVerso = identityCardVerso;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    public Date getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    public String getAuditFailedCause() {
        return auditFailedCause;
    }

    public void setAuditFailedCause(String auditFailedCause) {
        this.auditFailedCause = auditFailedCause;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomerAppId() {
        return customerAppId;
    }

    public void setCustomerAppId(String customerAppId) {
        this.customerAppId = customerAppId;
    }

    public String getCustomerSecretKey() {
        return customerSecretKey;
    }

    public void setCustomerSecretKey(String customerSecretKey) {
        this.customerSecretKey = customerSecretKey;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getContactWay() {
        return contactWay;
    }

    public void setContactWay(String contactWay) {
        this.contactWay = contactWay;
    }

    public String getCancellationRefundCode() {
        return cancellationRefundCode;
    }

    public void setCancellationRefundCode(String cancellationRefundCode) {
        this.cancellationRefundCode = cancellationRefundCode;
    }

    public String getCancellationRefundName() {
        return cancellationRefundName;
    }

    public void setCancellationRefundName(String cancellationRefundName) {
        this.cancellationRefundName = cancellationRefundName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProvincial() {
        return provincial;
    }

    public void setProvincial(String provincial) {
        this.provincial = provincial;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public List<ShipperInfo> getShipperInfoList() {
        return shipperInfoList;
    }

    public void setShipperInfoList(List<ShipperInfo> shipperInfoList) {
        this.shipperInfoList = shipperInfoList;
    }
}