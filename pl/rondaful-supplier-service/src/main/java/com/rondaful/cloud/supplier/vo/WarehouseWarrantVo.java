package com.rondaful.cloud.supplier.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 创建入库单 Vo
 *
 * @ClassName WarehouseWarrantVo
 * @Author tianye
 * @Date 2019/4/26 11:43
 * @Version 1.0
 */
public class WarehouseWarrantVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "创建入库单保存草稿or提交 true:提交 false:保存草稿", required = true)
    private boolean commitFlag;

    // 入库单明细
    @ApiModelProperty(value = "目的仓服务商名称", required = true)
    private String warehouseFacilitatorName;

    @ApiModelProperty(value = "目的仓服务商代码", required = true)
    private String warehouseFacilitatorCode;

    @ApiModelProperty(value = "目的仓名称", required = true)
    private String warehouseName;

    @ApiModelProperty(value = "目的仓代码", required = true)
    private String warehouseCode;

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    @ApiModelProperty(value = "入库单类型 0:标准入库单 3-中转入库单 5-FBA入库单", required = true)
    private String transitType;

    @ApiModelProperty(value = "运输方式 0：空运，1：海运散货 2：快递，3铁运 ，4海运整柜", required = true)
    private String receivingShippingType;

    @ApiModelProperty(value = "追踪号")
    private String trackingNumber;

    @ApiModelProperty(value = "计划到货时间 yyyy-MM-dd HH:mm:ss")
    private String etaDate;

    @ApiModelProperty(value = "增值税号")
    private String vatNumber;

    @ApiModelProperty(value = "增值税豁免号")
    private String exemptionNumber;

    @ApiModelProperty(value = "EORI")
    private String eori;

    @ApiModelProperty(value = "中转服务方式(物流产品代码)", required = true)
    private String smCode;

    @ApiModelProperty(value = "中转仓库编号", required = true)
    private String transitWarehouseCode;

    @ApiModelProperty(value = "中转仓库名称", required = true)
    private String transitWarehouseName;

    @ApiModelProperty(value = "商品总重量(kg)", required = true)
    private BigDecimal weight;

    @ApiModelProperty(value = "商品总体积(立方米)", required = true)
    private BigDecimal volume;

    @ApiModelProperty(value = "提单类型,0:电放;1:正本", required = true)
    private String pickupForm;

    @ApiModelProperty(value = "报关类型,0:EDI报关,1:委托报关,2:报关自理", required = true)
    private String customsType;

    @ApiModelProperty(value = "清关附件名称")
    private String clearanceAttached;

    @ApiModelProperty(value = "清关附件")
    private String clearanceAttachedName;

    @ApiModelProperty(value = "发票文件", required = true)
    private String invoiceAttached;

    public String getClearanceAttachedName() {
        return clearanceAttachedName;
    }

    public void setClearanceAttachedName(String clearanceAttachedName) {
        this.clearanceAttachedName = clearanceAttachedName;
    }

    public String getWarehouseFacilitatorCode() {
        return warehouseFacilitatorCode;
    }

    public void setWarehouseFacilitatorCode(String warehouseFacilitatorCode) {
        this.warehouseFacilitatorCode = warehouseFacilitatorCode;
    }

    public String getInvoiceAttachedName() {
        return invoiceAttachedName;
    }

    public void setInvoiceAttachedName(String invoiceAttachedName) {
        this.invoiceAttachedName = invoiceAttachedName;
    }

    @ApiModelProperty(value = "发票文件名称", required = true)
    private String invoiceAttachedName;

    @ApiModelProperty(value = "揽收服务(送货方式),0:自送货物,1:上门提货", required = true)
    private String collectingService;

    @ApiModelProperty(value = "增值服务,0:world_ease(worldease服务), 1:origin_crt(产地证), 2:fumigation(熏蒸)")
    private String valueAddedService;

    @ApiModelProperty(value = "是否自有税号清关,0:否,1:是", required = true)
    private String clearanceService;

    @ApiModelProperty(value = "进口商代码")
    private Integer importCompany;

    @ApiModelProperty(value = "出口商代码")
    private Integer exportCompany;

    @ApiModelProperty(value = "备注")
    private String receivingDesc;

    //  商品明细
    @ApiModelProperty(value = "商品明细")
    private List<ProductInfoVo> productInfos;

    // 揽收信息明细
    @ApiModelProperty(value = "揽收联系人-名")
    private String caFirstName;

    @ApiModelProperty(value = "揽收联系人-姓")
    private String caLastName;

    @ApiModelProperty(value = "揽收联系人电话")
    private String caContactPhone;

    @ApiModelProperty(value = "揽收地址州/省份")
    private String caState;

    @ApiModelProperty(value = "揽收地址城市")
    private String caCity;

    @ApiModelProperty(value = "揽收地址国家")
    private String caCountryCode;

    @ApiModelProperty(value = "揽收地址邮编")
    private String caZipcode;

    @ApiModelProperty(value = "揽收地址1")
    private String caAddress1;

    @ApiModelProperty(value = "揽收地址2")
    private String caAddress2;

    @ApiModelProperty(value = "揽收时间 yyyy-MM-dd HH:mm:ss")
    private String caDate;

    public List<ProductInfoVo> getProductInfos() {
        return productInfos;
    }

    public void setProductInfos(List<ProductInfoVo> productInfos) {
        this.productInfos = productInfos;
    }

    public String getWarehouseFacilitatorName() {
        return warehouseFacilitatorName;
    }

    public void setWarehouseFacilitatorName(String warehouseFacilitatorName) {
        this.warehouseFacilitatorName = warehouseFacilitatorName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getTransitType() {
        return transitType;
    }

    public void setTransitType(String transitType) {
        this.transitType = transitType;
    }

    public String getReceivingShippingType() {
        return receivingShippingType;
    }

    public void setReceivingShippingType(String receivingShippingType) {
        this.receivingShippingType = receivingShippingType;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getEtaDate() {
        return etaDate;
    }

    public void setEtaDate(String etaDate) {
        this.etaDate = etaDate;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public String getExemptionNumber() {
        return exemptionNumber;
    }

    public void setExemptionNumber(String exemptionNumber) {
        this.exemptionNumber = exemptionNumber;
    }

    public String getEori() {
        return eori;
    }

    public void setEori(String eori) {
        this.eori = eori;
    }

    public String getSmCode() {
        return smCode;
    }

    public void setSmCode(String smCode) {
        this.smCode = smCode;
    }

    public String getTransitWarehouseCode() {
        return transitWarehouseCode;
    }

    public void setTransitWarehouseCode(String transitWarehouseCode) {
        this.transitWarehouseCode = transitWarehouseCode;
    }

    public String getTransitWarehouseName() {
        return transitWarehouseName;
    }

    public void setTransitWarehouseName(String transitWarehouseName) {
        this.transitWarehouseName = transitWarehouseName;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public String getPickupForm() {
        return pickupForm;
    }

    public void setPickupForm(String pickupForm) {
        this.pickupForm = pickupForm;
    }

    public String getCustomsType() {
        return customsType;
    }

    public void setCustomsType(String customsType) {
        this.customsType = customsType;
    }

    public String getClearanceAttached() {
        return clearanceAttached;
    }

    public void setClearanceAttached(String clearanceAttached) {
        this.clearanceAttached = clearanceAttached;
    }

    public String getInvoiceAttached() {
        return invoiceAttached;
    }

    public void setInvoiceAttached(String invoiceAttached) {
        this.invoiceAttached = invoiceAttached;
    }

    public String getCollectingService() {
        return collectingService;
    }

    public void setCollectingService(String collectingService) {
        this.collectingService = collectingService;
    }

    public String getValueAddedService() {
        return valueAddedService;
    }

    public void setValueAddedService(String valueAddedService) {
        this.valueAddedService = valueAddedService;
    }

    public String getClearanceService() {
        return clearanceService;
    }

    public void setClearanceService(String clearanceService) {
        this.clearanceService = clearanceService;
    }

    public Integer getImportCompany() {
        return importCompany;
    }

    public void setImportCompany(Integer importCompany) {
        this.importCompany = importCompany;
    }

    public Integer getExportCompany() {
        return exportCompany;
    }

    public void setExportCompany(Integer exportCompany) {
        this.exportCompany = exportCompany;
    }

    public String getReceivingDesc() {
        return receivingDesc;
    }

    public void setReceivingDesc(String receivingDesc) {
        this.receivingDesc = receivingDesc;
    }

    public String getCaFirstName() {
        return caFirstName;
    }

    public void setCaFirstName(String caFirstName) {
        this.caFirstName = caFirstName;
    }

    public String getCaLastName() {
        return caLastName;
    }

    public void setCaLastName(String caLastName) {
        this.caLastName = caLastName;
    }

    public String getCaContactPhone() {
        return caContactPhone;
    }

    public void setCaContactPhone(String caContactPhone) {
        this.caContactPhone = caContactPhone;
    }

    public String getCaState() {
        return caState;
    }

    public void setCaState(String caState) {
        this.caState = caState;
    }

    public String getCaCity() {
        return caCity;
    }

    public void setCaCity(String caCity) {
        this.caCity = caCity;
    }

    public String getCaCountryCode() {
        return caCountryCode;
    }

    public void setCaCountryCode(String caCountryCode) {
        this.caCountryCode = caCountryCode;
    }

    public String getCaZipcode() {
        return caZipcode;
    }

    public void setCaZipcode(String caZipcode) {
        this.caZipcode = caZipcode;
    }

    public String getCaAddress1() {
        return caAddress1;
    }

    public void setCaAddress1(String caAddress1) {
        this.caAddress1 = caAddress1;
    }

    public String getCaAddress2() {
        return caAddress2;
    }

    public void setCaAddress2(String caAddress2) {
        this.caAddress2 = caAddress2;
    }

    public String getCaDate() {
        return caDate;
    }

    public void setCaDate(String caDate) {
        this.caDate = caDate;
    }

    public boolean isCommitFlag() {
        return commitFlag;
    }

    public void setCommitFlag(boolean commitFlag) {
        this.commitFlag = commitFlag;
    }
}
