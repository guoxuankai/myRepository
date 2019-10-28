package com.rondaful.cloud.supplier.model.dto.storage;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/6/18
 * @Description:
 */
public class StorageRecordDTO implements Serializable {
    private static final long serialVersionUID = -8740127501898113202L;

    @ApiModelProperty(value = "")
    private Long id;

    @ApiModelProperty(value = "主账号id")
    private Integer topUserId;

    @ApiModelProperty(value = "主账号id")
    private String firmCode;

    @ApiModelProperty(value = "目的仓库id")
    private Integer warehouseId;

    @ApiModelProperty(value = "仓库服务商名字")
    private String serviceName;

    private Integer serviceId;

    @ApiModelProperty(value = "仓库服务商名字")
    private String warehouseName;

    @ApiModelProperty(value = "参考号")
    private String referenceNo;

    @ApiModelProperty(value = "入库单类型：0:标准入库单 3-中转入库单 5-FBA入库单")
    private Integer transitType;

    @ApiModelProperty(value = "运输方式：0：空运，1：海运散货 2：快递，3：铁运 ，4：海运整柜")
    private String receivingShippingType;

    @ApiModelProperty(value = "跟踪号/海柜号。中转选填，verify=1（入库单提交审核）时自发，FBA转仓必填")
    private String trackingNumber;

    @ApiModelProperty(value = "海外仓仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "预计到达时间（格式：YYYY-MM-DD）")
    private String etaDate;

    @ApiModelProperty(value = "备注")
    private String receivingDesc;

    @ApiModelProperty(value = "是否审核值0：新建不审核(草稿状态)，值1：新建并审核，默认为0 ，审核通过之后，不可编辑")
    private Integer verify;

    @ApiModelProperty(value = "进口商编码。必填条件：clearance_service=1或者目的仓库对应的国家需要提供VAT(目前英国需要提供)")
    private String importCompany;

    @ApiModelProperty(value = "联系人")
    private String saContacter;

    @ApiModelProperty(value = "联系电话（手机号）")
    private String saContactPhone;

    @ApiModelProperty(value = "发件国家简称")
    private String saCountryCode;

    @ApiModelProperty(value = "省/州")
    private String saState;

    @ApiModelProperty(value = "城市")
    private String saCity;

    @ApiModelProperty(value = "区")
    private String saRegion;

    @ApiModelProperty(value = "地址1")
    private String saAddress1;

    @ApiModelProperty(value = "地址2")
    private String saAddress2;

    @ApiModelProperty(value = "入库单单号")
    private String receivingCode;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "入库单明细")
    private List<StoregeItemDTO> items;

    @ApiModelProperty(value = "transit_type=3特有属性")
    private StorageSpecificDTO specificDTO;

    @ApiModelProperty(value = "transit_type=3特有。揽收资料。必填条件：当collection_service=1（上门揽收）时必填")
    private List<StorageCollectingDTO> collectings;

    @ApiModelProperty(value = "质检方式")
    private Integer qualityType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public Integer getTransitType() {
        return transitType;
    }

    public void setTransitType(Integer transitType) {
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

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getEtaDate() {
        return etaDate;
    }

    public void setEtaDate(String etaDate) {
        this.etaDate = etaDate;
    }

    public String getReceivingDesc() {
        return receivingDesc;
    }

    public void setReceivingDesc(String receivingDesc) {
        this.receivingDesc = receivingDesc;
    }

    public Integer getVerify() {
        return verify;
    }

    public void setVerify(Integer verify) {
        this.verify = verify;
    }

    public String getImportCompany() {
        return importCompany;
    }

    public void setImportCompany(String importCompany) {
        this.importCompany = importCompany;
    }

    public String getSaContacter() {
        return saContacter;
    }

    public void setSaContacter(String saContacter) {
        this.saContacter = saContacter;
    }

    public String getSaContactPhone() {
        return saContactPhone;
    }

    public void setSaContactPhone(String saContactPhone) {
        this.saContactPhone = saContactPhone;
    }

    public String getSaCountryCode() {
        return saCountryCode;
    }

    public void setSaCountryCode(String saCountryCode) {
        this.saCountryCode = saCountryCode;
    }

    public String getSaState() {
        return saState;
    }

    public void setSaState(String saState) {
        this.saState = saState;
    }

    public String getSaCity() {
        return saCity;
    }

    public void setSaCity(String saCity) {
        this.saCity = saCity;
    }

    public String getSaRegion() {
        return saRegion;
    }

    public void setSaRegion(String saRegion) {
        this.saRegion = saRegion;
    }

    public String getSaAddress1() {
        return saAddress1;
    }

    public void setSaAddress1(String saAddress1) {
        this.saAddress1 = saAddress1;
    }

    public String getSaAddress2() {
        return saAddress2;
    }

    public void setSaAddress2(String saAddress2) {
        this.saAddress2 = saAddress2;
    }

    public String getReceivingCode() {
        return receivingCode;
    }

    public void setReceivingCode(String receivingCode) {
        this.receivingCode = receivingCode;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public List<StoregeItemDTO> getItems() {
        return items;
    }

    public void setItems(List<StoregeItemDTO> items) {
        this.items = items;
    }

    public StorageSpecificDTO getSpecificDTO() {
        return specificDTO;
    }

    public void setSpecificDTO(StorageSpecificDTO specificDTO) {
        this.specificDTO = specificDTO;
    }

    public List<StorageCollectingDTO> getCollectings() {
        return collectings;
    }

    public void setCollectings(List<StorageCollectingDTO> collectings) {
        this.collectings = collectings;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public Integer getTopUserId() {
        return topUserId;
    }

    public void setTopUserId(Integer topUserId) {
        this.topUserId = topUserId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getQualityType() {
        return qualityType;
    }

    public void setQualityType(Integer qualityType) {
        this.qualityType = qualityType;
    }

    public String getFirmCode() {
        return firmCode;
    }

    public void setFirmCode(String firmCode) {
        this.firmCode = firmCode;
    }
}
