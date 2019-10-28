package com.rondaful.cloud.order.entity.goodcang;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class GoodCangOrderList {

    @ApiModelProperty(value = "订单号")
    private String orderCode;

    @ApiModelProperty(value = "客户参考号")
    private String referenceNo;

    @ApiModelProperty(value = "平台")
    private String platform;

    @ApiModelProperty(value = "订单状态 C:待发货审核W:待发货D:已发货H:暂存N:异常订单P:问题件X:废弃")
    private String orderStatus;

    @ApiModelProperty(value = "异常问题原因")
    private String abnormalProblemReason;

    @ApiModelProperty(value = "配送方式")
    private String shippingMethod;

    @ApiModelProperty(value = "跟踪号")
    private String trackingNo;

    @ApiModelProperty(value = "配送仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "订单重量")
    private String orderWeight;

    @ApiModelProperty(value = "订单说明")
    private String orderDesc;

    @ApiModelProperty(value = "创建时间")
    private String dateCreate;

    @ApiModelProperty(value = "审核时间")
    private String dateRelease;

    @ApiModelProperty(value = "出库时间")
    private String dateShipping;

    @ApiModelProperty(value = "修改时间")
    private String dateModify;

    @ApiModelProperty(value = "收件人国家二字码")
    private String consigneeCountryCode;

    @ApiModelProperty(value = "收件人国家")
    private String consigneeCountryName;

    @ApiModelProperty(value = "省")
    private String consigneeState;

    @ApiModelProperty(value = "城市")
    private String consigneeCity;

    @ApiModelProperty(value = "区域")
    private String consigneeDistrict;

    @ApiModelProperty(value = "地址1")
    private String consigneeAddress1;

    @ApiModelProperty(value = "地址2")
    private String consigneeAddress2;

    @ApiModelProperty(value = "地址3")
    private String consigneeAddress3;

    @ApiModelProperty(value = "邮编")
    private String consigneZipcode;

    @ApiModelProperty(value = "门牌号")
    private String consigneeDoorplate;

    @ApiModelProperty(value = "公司")
    private String consigneeCompany;

    @ApiModelProperty(value = "收件人姓名")
    private String consigneeName;

    @ApiModelProperty(value = "收件人电话")
    private String consigneePhone;

    @ApiModelProperty(value = "收件人邮箱")
    private String consigneeEmail;

    @ApiModelProperty(value = "年龄检测服务0 - 否16 - 对应16岁18 - 对应18岁")
    private String ageDetection;

    @ApiModelProperty(value = "订单明细")
    private List<GoodCangItem> items;

    @ApiModelProperty(value = "订单费用")
    private List<GoodCangFeeDetails> feeDetails;

    @ApiModelProperty(value = "一票多箱装箱明细，一票一箱则返回空")
    private List<GoodCangOrderBoxInfo> orderBoxInfo;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getAbnormalProblemReason() {
        return abnormalProblemReason;
    }

    public void setAbnormalProblemReason(String abnormalProblemReason) {
        this.abnormalProblemReason = abnormalProblemReason;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getOrderWeight() {
        return orderWeight;
    }

    public void setOrderWeight(String orderWeight) {
        this.orderWeight = orderWeight;
    }

    public String getOrderDesc() {
        return orderDesc;
    }

    public void setOrderDesc(String orderDesc) {
        this.orderDesc = orderDesc;
    }

    public String getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(String dateCreate) {
        this.dateCreate = dateCreate;
    }

    public String getDateRelease() {
        return dateRelease;
    }

    public void setDateRelease(String dateRelease) {
        this.dateRelease = dateRelease;
    }

    public String getDateShipping() {
        return dateShipping;
    }

    public void setDateShipping(String dateShipping) {
        this.dateShipping = dateShipping;
    }

    public String getDateModify() {
        return dateModify;
    }

    public void setDateModify(String dateModify) {
        this.dateModify = dateModify;
    }

    public String getConsigneeCountryCode() {
        return consigneeCountryCode;
    }

    public void setConsigneeCountryCode(String consigneeCountryCode) {
        this.consigneeCountryCode = consigneeCountryCode;
    }

    public String getConsigneeCountryName() {
        return consigneeCountryName;
    }

    public void setConsigneeCountryName(String consigneeCountryName) {
        this.consigneeCountryName = consigneeCountryName;
    }

    public String getConsigneeState() {
        return consigneeState;
    }

    public void setConsigneeState(String consigneeState) {
        this.consigneeState = consigneeState;
    }

    public String getConsigneeCity() {
        return consigneeCity;
    }

    public void setConsigneeCity(String consigneeCity) {
        this.consigneeCity = consigneeCity;
    }

    public String getConsigneeDistrict() {
        return consigneeDistrict;
    }

    public void setConsigneeDistrict(String consigneeDistrict) {
        this.consigneeDistrict = consigneeDistrict;
    }

    public String getConsigneeAddress1() {
        return consigneeAddress1;
    }

    public void setConsigneeAddress1(String consigneeAddress1) {
        this.consigneeAddress1 = consigneeAddress1;
    }

    public String getConsigneeAddress2() {
        return consigneeAddress2;
    }

    public void setConsigneeAddress2(String consigneeAddress2) {
        this.consigneeAddress2 = consigneeAddress2;
    }

    public String getConsigneeAddress3() {
        return consigneeAddress3;
    }

    public void setConsigneeAddress3(String consigneeAddress3) {
        this.consigneeAddress3 = consigneeAddress3;
    }

    public String getConsigneZipcode() {
        return consigneZipcode;
    }

    public void setConsigneZipcode(String consigneZipcode) {
        this.consigneZipcode = consigneZipcode;
    }

    public String getConsigneeDoorplate() {
        return consigneeDoorplate;
    }

    public void setConsigneeDoorplate(String consigneeDoorplate) {
        this.consigneeDoorplate = consigneeDoorplate;
    }

    public String getConsigneeCompany() {
        return consigneeCompany;
    }

    public void setConsigneeCompany(String consigneeCompany) {
        this.consigneeCompany = consigneeCompany;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public String getConsigneePhone() {
        return consigneePhone;
    }

    public void setConsigneePhone(String consigneePhone) {
        this.consigneePhone = consigneePhone;
    }

    public String getConsigneeEmail() {
        return consigneeEmail;
    }

    public void setConsigneeEmail(String consigneeEmail) {
        this.consigneeEmail = consigneeEmail;
    }

    public String getAgeDetection() {
        return ageDetection;
    }

    public void setAgeDetection(String ageDetection) {
        this.ageDetection = ageDetection;
    }

    public List<GoodCangItem> getItems() {
        return items;
    }

    public void setItems(List<GoodCangItem> items) {
        this.items = items;
    }

    public List<GoodCangFeeDetails> getFeeDetails() {
        return feeDetails;
    }

    public void setFeeDetails(List<GoodCangFeeDetails> feeDetails) {
        this.feeDetails = feeDetails;
    }

    public List<GoodCangOrderBoxInfo> getOrderBoxInfo() {
        return orderBoxInfo;
    }

    public void setOrderBoxInfo(List<GoodCangOrderBoxInfo> orderBoxInfo) {
        this.orderBoxInfo = orderBoxInfo;
    }
}
