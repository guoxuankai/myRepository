package com.rondaful.cloud.order.entity.supplier;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 
 *   物流方式实体类
 * @author xieyanbin
 * @date 2018-12-12 19:28:58
 */
@ApiModel(value ="Logistics")
public class Logistics implements Serializable {
    @ApiModelProperty(value = "")
    private String id;

    @ApiModelProperty(value = "物流方式简称")
    private String shortName;

    @ApiModelProperty(value = "物流方式全称")
    private String fullName;

    @ApiModelProperty(value = "物流方式类型 默认0 0自营仓库物流")
    private Integer type;

    @ApiModelProperty(value = "物流方式代码")
    private String code;

    @ApiModelProperty(value = "amazon物流商")
    private String amazonCarrier;
    
    @ApiModelProperty(value = "amazon物流方式代码ʽ")
    private String amazonCode;
    
    @ApiModelProperty(value = "ebay物流商")
    private String ebayCarrier;
    
    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;
    
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;
    
    @ApiModelProperty(value = "物流服务商名称")
    private String carrierName;
    
    @ApiModelProperty(value = "物流服务商代码")
    private String carrierCode;

    @ApiModelProperty(value = "状态 默认0 0停用 1启用")
    private String status;

    @ApiModelProperty(value = "所属平台 A供应商平台 B管理后台")
    private Integer belongSys;

    @ApiModelProperty(value = "最后更新人id")
    private Long lastUpdateBy;

    private Integer[] idList;

    private String lastUpdateTime;


    private String createTime;
    
    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;
    //供应商名称
    private String supplier;

    private static final long serialVersionUID = 1L;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public String getEbayCarrier() {
		return ebayCarrier;
	}

	public void setEbayCarrier(String ebayCarrier) {
		this.ebayCarrier = ebayCarrier;
	}

	public String getWarehouseCode() {
		return warehouseCode;
	}

	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getCarrierName() {
		return carrierName;
	}

	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}

	public String getCarrierCode() {
		return carrierCode;
	}

	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getBelongSys() {
		return belongSys;
	}

	public void setBelongSys(Integer belongSys) {
		this.belongSys = belongSys;
	}

	public Long getLastUpdateBy() {
		return lastUpdateBy;
	}

	public void setLastUpdateBy(Long lastUpdateBy) {
		this.lastUpdateBy = lastUpdateBy;
	}

	public Integer[] getIdList() {
		return idList;
	}

	public void setIdList(Integer[] idList) {
		this.idList = idList;
	}

	public String getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getWareHouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	@Override
	public String toString() {
		return "Logistics [id=" + id + ", shortName=" + shortName + ", fullName=" + fullName + ", type=" + type
				+ ", code=" + code + ", amazonCarrier=" + amazonCarrier + ", amazonCode=" + amazonCode
				+ ", ebayCarrier=" + ebayCarrier + ", warehouseCode=" + warehouseCode + ", supplierName=" + supplierName
				+ ", carrierName=" + carrierName + ", carrierCode=" + carrierCode + ", status=" + status
				+ ", belongSys=" + belongSys + ", lastUpdateBy=" + lastUpdateBy + ", idList=" + Arrays.toString(idList)
				+ ", lastUpdateTime=" + lastUpdateTime + ", createTime=" + createTime + ", warehouseName="
				+ warehouseName + ", supplier=" + supplier + "]";
	}
    
	
	
}