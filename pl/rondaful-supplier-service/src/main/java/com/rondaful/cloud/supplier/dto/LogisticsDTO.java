package com.rondaful.cloud.supplier.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 *   物流方式实体类
 * @author xieyanbin
 * @date 2018-12-12 19:28:58
 */
@ApiModel(value ="LogisticsDTO")
public class LogisticsDTO implements Serializable {
    @ApiModelProperty(value = "")
    private String id;

    @ApiModelProperty(value = "物流方式简称")
    private String shortName;

    @ApiModelProperty(value = "物流方式类型 默认0 0自营仓库物流 1品连仓库物流")
    private Integer type;

    @ApiModelProperty(value = "物流方式代码")
    private String code;

    @ApiModelProperty(value = "物流商代码")
    private String carrierCode;

    @ApiModelProperty(value = "物流商名称")
    private String carrierName;

    @ApiModelProperty(value = "状态 默认0 0停用 1启用")
    private Integer status;

	@ApiModelProperty(value = "仓库id")
	private Integer warehouseId;

	@ApiModelProperty(value = "仓库code")
	private String warehouseCode;

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
    
    @ApiModelProperty(value = "其他amazon物流商")
    private String otherAmazonCarrier;
    
    @ApiModelProperty(value = "其他amazon物流方式代码")
    private String otherAmazonCode;
    
    @ApiModelProperty(value = "其他ebay物流商")
    private String otherEbayCarrier;

    @ApiModelProperty(value = "其他语言物流方式简称")
    private String foreignShortName;
    
    @ApiModelProperty(value = "其他语言物流服务商名称")
    private String foreignCarrierName;
    
    @ApiModelProperty(value = "其他语言仓库名称")
    private String foreignWarehouseName;

	@ApiModelProperty(value = "总费用")
    private BigDecimal totalCost;

	@ApiModelProperty(value = "线上物流类型")
	private String onlineLogistics;

    private static final long serialVersionUID = 1L;
    
    private LogisticsDTO() {}

	public LogisticsDTO(String code, Integer warehouseId) {
		super();
		this.code = code;
		this.warehouseId = warehouseId;
	}

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

	public String getCarrierCode() {
		return carrierCode;
	}

	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
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

	public String getAliexpressCode() {
		return aliexpressCode;
	}

	public void setAliexpressCode(String aliexpressCode) {
		this.aliexpressCode = aliexpressCode;
	}

	public String getOtherAmazonCarrier() {
		return otherAmazonCarrier;
	}

	public void setOtherAmazonCarrier(String otherAmazonCarrier) {
		this.otherAmazonCarrier = otherAmazonCarrier;
	}

	public String getOtherAmazonCode() {
		return otherAmazonCode;
	}

	public void setOtherAmazonCode(String otherAmazonCode) {
		this.otherAmazonCode = otherAmazonCode;
	}

	public String getOtherEbayCarrier() {
		return otherEbayCarrier;
	}

	public void setOtherEbayCarrier(String otherEbayCarrier) {
		this.otherEbayCarrier = otherEbayCarrier;
	}

	public String getForeignShortName() {
		return foreignShortName;
	}

	public void setForeignShortName(String foreignShortName) {
		this.foreignShortName = foreignShortName;
	}

	public String getForeignCarrierName() {
		return foreignCarrierName;
	}

	public void setForeignCarrierName(String foreignCarrierName) {
		this.foreignCarrierName = foreignCarrierName;
	}

	public String getForeignWarehouseName() {
		return foreignWarehouseName;
	}

	public void setForeignWarehouseName(String foreignWarehouseName) {
		this.foreignWarehouseName = foreignWarehouseName;
	}

	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseCode() {
		return warehouseCode;
	}

	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}

	public String getOnlineLogistics() {
		return onlineLogistics;
	}

	public void setOnlineLogistics(String onlineLogistics) {
		this.onlineLogistics = onlineLogistics;
	}

	@Override
	public String toString() {
		return "LogisticsDTO{" +
				"id='" + id + '\'' +
				", shortName='" + shortName + '\'' +
				", type=" + type +
				", code='" + code + '\'' +
				", carrierCode='" + carrierCode + '\'' +
				", carrierName='" + carrierName + '\'' +
				", status=" + status +
				", warehouseId=" + warehouseId +
				", warehouseCode='" + warehouseCode + '\'' +
				", lastUpdateBy=" + lastUpdateBy +
				", ebayCarrier='" + ebayCarrier + '\'' +
				", amazonCarrier='" + amazonCarrier + '\'' +
				", amazonCode='" + amazonCode + '\'' +
				", aliexpressCode='" + aliexpressCode + '\'' +
				", otherAmazonCarrier='" + otherAmazonCarrier + '\'' +
				", otherAmazonCode='" + otherAmazonCode + '\'' +
				", otherEbayCarrier='" + otherEbayCarrier + '\'' +
				", foreignShortName='" + foreignShortName + '\'' +
				", foreignCarrierName='" + foreignCarrierName + '\'' +
				", foreignWarehouseName='" + foreignWarehouseName + '\'' +
				", totalCost=" + totalCost +
				", onlineLogistics='" + onlineLogistics + '\'' +
				'}';
	}
}