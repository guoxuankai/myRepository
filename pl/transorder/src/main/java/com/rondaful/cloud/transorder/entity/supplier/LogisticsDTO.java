package com.rondaful.cloud.transorder.entity.supplier;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value ="LogisticsDTO")
public class LogisticsDTO implements Serializable {
    @ApiModelProperty(value = "")
    private Long id;

    @ApiModelProperty(value = "物流方式简称")
    private String shortName;

    @ApiModelProperty(value = "物流方式类型 默认0 0自营仓库物流")
    private Byte type;

    @ApiModelProperty(value = "物流方式代码")
    private String code;

    @ApiModelProperty(value = "物流商代码")//对应系统订单：shipping_carrier_used_code邮寄方式(货物承运公司)CODE
    private String carrierCode;
    
    @ApiModelProperty(value = "供应商")
    private String supplier;

    @ApiModelProperty(value = "物流商名称")
    private String carrierName;

    @ApiModelProperty(value = "状态 默认0 0停用 1启用")
    private Integer status;

    @ApiModelProperty(value = "所属平台 A供应商平台 B管理后台")
    private Integer sysCode;

    @ApiModelProperty(value = "最后更新人id")
    private Long lastUpdateBy;
    
    @ApiModelProperty(value = "ebay物流商代码")
    private String ebayCarrier;
    
    @ApiModelProperty(value = "amazon物流商代码")
    private String amazonCarrier;

    @ApiModelProperty(value = "amazon物流方式")
    private String amazonCode;

    @ApiModelProperty(value = "速卖通物流方式code")
    private String aliexpressCode;
    
    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;
    
//    @ApiModelProperty(value = "仓库代码")
//    private String warehouseCode;

    @ApiModelProperty(value = "仓库Id")
    private String warehouseId;


    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCarrierCode() {
        return carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSysCode() {
        return sysCode;
    }

    public void setSysCode(Integer sysCode) {
        this.sysCode = sysCode;
    }

    public Long getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(Long lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public String getEbayCarrier() {
        return ebayCarrier;
    }

    public void setEbayCarrier(String ebayCarrier) {
        this.ebayCarrier = ebayCarrier;
    }

    public String getAmazonCarrier() {
        return amazonCarrier;
    }

    public void setAmazonCarrier(String amazonCarrier) {
        this.amazonCarrier = amazonCarrier;
    }

    public String getAmazonCode() {
        return amazonCode;
    }

    public void setAmazonCode(String amazonCode) {
        this.amazonCode = amazonCode;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

//    public String getWarehouseCode() {
//        return warehouseCode;
//    }
//
//    public void setWarehouseCode(String warehouseCode) {
//        this.warehouseCode = warehouseCode;
//    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getAliexpressCode() {
        return aliexpressCode;
    }

    public void setAliexpressCode(String aliexpressCode) {
        this.aliexpressCode = aliexpressCode;
    }
}