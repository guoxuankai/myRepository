package com.rondaful.cloud.order.model.dto.sysOrderInvoice;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 导出发票信息的VO
 * @author Blade
 * @date 2019-06-18 19:35:31
 **/
public class SysOrderInvoiceExportInfoVO implements Serializable {

    @ApiModelProperty(value = "系统订单ID")
    private String sysOrderId;

    @ApiModelProperty(value = "导出语言, EN-英语，DE-德国)")
    private String exportLanguageCode;

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

    @ApiModelProperty(value = "国家英文名称")
    private String countryEnName;

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

    @ApiModelProperty(value = "系统订单项实体类")
    private List<SysOrderInvoiceExportSkuDetailVO> sysOrderInvoiceExportSkuDetailList;

    @ApiModelProperty(value = "买家姓名")
    private String buyerName;

    @ApiModelProperty(value = "收货人姓名")
    private String shipToName;

    @ApiModelProperty(value = "收货人电话")
    private String shipToPhone;

    @ApiModelProperty(value = "收货目的地/国家名称")
    private String shipToCountryName;

    @ApiModelProperty(value = "收货省/州名")
    private String shipToState;

    @ApiModelProperty(value = "收货城市")
    private String shipToCity;

    @ApiModelProperty(value = "收货地址1")
    private String shipToAddrStreet1;

    @ApiModelProperty(value = "收货地址2")
    private String shipToAddrStreet2;

    @ApiModelProperty(value = "收货地址3")
    private String shipToAddrStreet3;

    @ApiModelProperty(value = "收货邮编")
    private String shipToPostalCode;

    @ApiModelProperty(value = "来源订单ID")
    private String sourceOrderId;

    @ApiModelProperty(value = "实际物流费")
    private BigDecimal actualShipCost;

    @ApiModelProperty(value = "系统商品总价(商品单价X数量)")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "所有商品的总价格相加")
    private BigDecimal subTotal;

    @ApiModelProperty(value = "运费")
    private BigDecimal shippingFee;

    @ApiModelProperty(value = "税费: subTotal * taxRate, taxRate由taxRateType决定")
    private BigDecimal taxFee;

    @ApiModelProperty(value = "其他费用")
    private BigDecimal otherFee;

    @ApiModelProperty(value = "发票总价: 商品总价格+运费+税费+otherFee")
    private BigDecimal invoiceTotal;

    @ApiModelProperty(value = "日期")
    private String date;

    @ApiModelProperty(value = "生成日期")
    private String createDate;

    private static final long serialVersionUID = 4278403251848510406L;

    public String getSysOrderId() {
        return sysOrderId;
    }

    public void setSysOrderId(String sysOrderId) {
        this.sysOrderId = sysOrderId;
    }

    public String getExportLanguageCode() {
        return exportLanguageCode;
    }

    public void setExportLanguageCode(String exportLanguageCode) {
        this.exportLanguageCode = exportLanguageCode;
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

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getShipToName() {
        return shipToName;
    }

    public void setShipToName(String shipToName) {
        this.shipToName = shipToName;
    }

    public String getShipToCountryName() {
        return shipToCountryName;
    }

    public void setShipToCountryName(String shipToCountryName) {
        this.shipToCountryName = shipToCountryName;
    }

    public String getShipToState() {
        return shipToState;
    }

    public void setShipToState(String shipToState) {
        this.shipToState = shipToState;
    }

    public String getShipToCity() {
        return shipToCity;
    }

    public void setShipToCity(String shipToCity) {
        this.shipToCity = shipToCity;
    }

    public String getShipToAddrStreet1() {
        return shipToAddrStreet1;
    }

    public void setShipToAddrStreet1(String shipToAddrStreet1) {
        this.shipToAddrStreet1 = shipToAddrStreet1;
    }

    public String getShipToAddrStreet2() {
        return shipToAddrStreet2;
    }

    public void setShipToAddrStreet2(String shipToAddrStreet2) {
        this.shipToAddrStreet2 = shipToAddrStreet2;
    }

    public String getShipToAddrStreet3() {
        return shipToAddrStreet3;
    }

    public void setShipToAddrStreet3(String shipToAddrStreet3) {
        this.shipToAddrStreet3 = shipToAddrStreet3;
    }

    public String getShipToPostalCode() {
        return shipToPostalCode;
    }

    public void setShipToPostalCode(String shipToPostalCode) {
        this.shipToPostalCode = shipToPostalCode;
    }

    public String getSourceOrderId() {
        return sourceOrderId;
    }

    public void setSourceOrderId(String sourceOrderId) {
        this.sourceOrderId = sourceOrderId;
    }

    public BigDecimal getActualShipCost() {
        return actualShipCost;
    }

    public void setActualShipCost(BigDecimal actualShipCost) {
        this.actualShipCost = actualShipCost;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getTaxFee() {
        return taxFee;
    }

    public void setTaxFee(BigDecimal taxFee) {
        this.taxFee = taxFee;
    }

    public BigDecimal getOtherFee() {
        return otherFee;
    }

    public void setOtherFee(BigDecimal otherFee) {
        this.otherFee = otherFee;
    }

    public BigDecimal getInvoiceTotal() {
        return invoiceTotal;
    }

    public void setInvoiceTotal(BigDecimal invoiceTotal) {
        this.invoiceTotal = invoiceTotal;
    }

    public List<SysOrderInvoiceExportSkuDetailVO> getSysOrderInvoiceExportSkuDetailList() {
        return sysOrderInvoiceExportSkuDetailList;
    }

    public void setSysOrderInvoiceExportSkuDetailList(List<SysOrderInvoiceExportSkuDetailVO> sysOrderInvoiceExportSkuDetailList) {
        this.sysOrderInvoiceExportSkuDetailList = sysOrderInvoiceExportSkuDetailList;
    }

    public String getShipToPhone() {
        return shipToPhone;
    }

    public void setShipToPhone(String shipToPhone) {
        this.shipToPhone = shipToPhone;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCountryEnName() {
        return countryEnName;
    }

    public void setCountryEnName(String countryEnName) {
        this.countryEnName = countryEnName;
    }
}
