package com.brandslink.cloud.user.dto.request;

import com.brandslink.cloud.user.dto.response.RoleInfoResponseDTO;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;


/**
 * 编辑客户基本信息请求model
 *
 * @ClassName CustomerForBasicInfoRequestDTO
 * @Author tianye
 * @Date 2019/7/16 16:40
 * @Version 1.0
 */
public class CustomerForBasicInfoRequestDTO implements Serializable {

    @ApiModelProperty(value = "oms系统传账号id，其他系统传客户id")
    private Integer id;

    @ApiModelProperty(value = "客户编码", required = true)
    private String customerCode;

    @ApiModelProperty(value = "中文名称（简称）")
    private String shortenedChineseName;

    @ApiModelProperty(value = "中文名称（全称）")
    private String chineseName;

    @ApiModelProperty(value = "英文名称", required = true)
    private String englishName;

    @ApiModelProperty(value = "联系人", required = true)
    private String contacts;

    @ApiModelProperty(value = "联系方式")
    private String contactWay;

    @ApiModelProperty(value = "销退质检方式代码")
    private String cancellationRefundCode;

    @ApiModelProperty(value = "销退质检方式名称")
    private String cancellationRefundName;

    @ApiModelProperty(value = "客户状态 1：正常  2：作废")
    private String status;

    @ApiModelProperty(value = "省", required = true)
    private String provincial;

    @ApiModelProperty(value = "市", required = true)
    private String city;

    @ApiModelProperty(value = "区", required = true)
    private String district;

    @ApiModelProperty(value = "详细地址", required = true)
    private String address;

    @ApiModelProperty(value = "客户回调接口")
    private String url;

    @ApiModelProperty(value = "商务")
    private String commerce;

    @ApiModelProperty(value = "商务联系方式")
    private String commerceWay;

    @ApiModelProperty(value = "合同日期")
    private String contractDate;

    @ApiModelProperty(value = "所属仓库信息", required = true)
    private List<RoleInfoResponseDTO.WarehouseDetail> warehouseList;

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

    public List<RoleInfoResponseDTO.WarehouseDetail> getWarehouseList() {
        return warehouseList;
    }

    public void setWarehouseList(List<RoleInfoResponseDTO.WarehouseDetail> warehouseList) {
        this.warehouseList = warehouseList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
