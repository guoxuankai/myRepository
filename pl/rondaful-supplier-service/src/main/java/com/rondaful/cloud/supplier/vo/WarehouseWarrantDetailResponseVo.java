package com.rondaful.cloud.supplier.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 查询入库单明细，返回前端vo
 *
 * @ClassName WarehouseWarrantDetailResponseVo
 * @Author tianye
 * @Date 2019/5/5 10:03
 * @Version 1.0
 */
public class WarehouseWarrantDetailResponseVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "目的仓服务商名称")
    private String warehouseFacilitatorName;

    @ApiModelProperty(value = "目的仓服务商代码")
    private String warehouseFacilitatorCode;

    @ApiModelProperty(value = "目的仓名称")
    private String warehouseName;

    @ApiModelProperty(value = "目的仓编码")
    private String warehouseCode;

    @ApiModelProperty(value = "入库单类型 0:标准入库单 3-中转入库单 5-FBA入库单")
    private Byte transitType;

    @ApiModelProperty(value = "运输方式 0：空运，1：海运散货 2：快递，3铁运 ，4海运整柜")
    private Byte receivingShippingType;

    @ApiModelProperty(value = "追踪号")
    private String trackingNumber;

    @ApiModelProperty(value = "计划到货时间")
    private Date etaDate;

    @ApiModelProperty(value = "增值税号")
    private String vatNumber;

    @ApiModelProperty(value = "增值税豁免号")
    private String exemptionNumber;

    @ApiModelProperty(value = "EORI")
    private String eori;

    @ApiModelProperty(value = "中转服务方式(物流产品代码)")
    private String smCode;

    @ApiModelProperty(value = "中转仓库编号")
    private String transitWarehouseCode;

    @ApiModelProperty(value = "中转仓库名称")
    private String transitWarehouseName;

    @ApiModelProperty(value = "商品总重量(kg)")
    private BigDecimal weight;

    @ApiModelProperty(value = "商品总体积(立方米)")
    private BigDecimal volume;

    @ApiModelProperty(value = "提单类型,0:电放;1:正本")
    private Byte pickupForm;

    @ApiModelProperty(value = "报关类型,0:EDI报关,1:委托报关,2:报关自理")
    private Byte customsType;

    @ApiModelProperty(value = "清关附件")
    private String clearanceAttached;

    @ApiModelProperty(value = "发票文件")
    private String invoiceAttached;

    @ApiModelProperty(value = "清关附件名称")
    private String clearanceAttachedName;

    @ApiModelProperty(value = "发票文件名称")
    private String invoiceAttachedName;

    @ApiModelProperty(value = "揽收服务(送货方式),0:自送货物,1:上门提货")
    private Byte collectingService;

    @ApiModelProperty(value = "增值服务,0:world_ease(worldease服务), 1:origin_crt(产地证), 2:fumigation(熏蒸)")
    private String valueAddedService;

    @ApiModelProperty(value = "是否自有税号清关,0:否,1:是")
    private Byte clearanceService;

    @ApiModelProperty(value = "进口商代码")
    private Integer importCompany;

    @ApiModelProperty(value = "出口商代码")
    private Integer exportCompany;

    @ApiModelProperty(value = "供应商ID")
    private Integer supplierId;

    @ApiModelProperty(value = "供应商")
    private String supplier;

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

    @ApiModelProperty(value = "揽收时间 yyyy-MM-dd")
    private Date caDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWarehouseFacilitatorCode() {
        return warehouseFacilitatorCode;
    }

    public void setWarehouseFacilitatorCode(String warehouseFacilitatorCode) {
        this.warehouseFacilitatorCode = warehouseFacilitatorCode;
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

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public Byte getTransitType() {
        return transitType;
    }

    public void setTransitType(Byte transitType) {
        this.transitType = transitType;
    }

    public Byte getReceivingShippingType() {
        return receivingShippingType;
    }

    public void setReceivingShippingType(Byte receivingShippingType) {
        this.receivingShippingType = receivingShippingType;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Date getEtaDate() {
        return etaDate;
    }

    public void setEtaDate(Date etaDate) {
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

    public Byte getPickupForm() {
        return pickupForm;
    }

    public void setPickupForm(Byte pickupForm) {
        this.pickupForm = pickupForm;
    }

    public Byte getCustomsType() {
        return customsType;
    }

    public void setCustomsType(Byte customsType) {
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

    public String getClearanceAttachedName() {
        return clearanceAttachedName;
    }

    public void setClearanceAttachedName(String clearanceAttachedName) {
        this.clearanceAttachedName = clearanceAttachedName;
    }

    public String getInvoiceAttachedName() {
        return invoiceAttachedName;
    }

    public void setInvoiceAttachedName(String invoiceAttachedName) {
        this.invoiceAttachedName = invoiceAttachedName;
    }

    public Byte getCollectingService() {
        return collectingService;
    }

    public void setCollectingService(Byte collectingService) {
        this.collectingService = collectingService;
    }

    public String getValueAddedService() {
        return valueAddedService;
    }

    public void setValueAddedService(String valueAddedService) {
        this.valueAddedService = valueAddedService;
    }

    public Byte getClearanceService() {
        return clearanceService;
    }

    public void setClearanceService(Byte clearanceService) {
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

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public List<ProductInfoVo> getProductInfos() {
        return productInfos;
    }

    public void setProductInfos(List<ProductInfoVo> productInfos) {
        this.productInfos = productInfos;
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

    public Date getCaDate() {
        return caDate;
    }

    public void setCaDate(Date caDate) {
        this.caDate = caDate;
    }
}
