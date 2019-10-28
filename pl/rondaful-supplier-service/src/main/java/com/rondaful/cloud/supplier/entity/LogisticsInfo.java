package com.rondaful.cloud.supplier.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;
/**
 * 
 * 实体类对应的数据表为：  t_logistics_info
 * @author xieyanbin
 * @date 2019-04-26 10:07:25
 */
@ApiModel(value ="LogisticsInfo")
public class LogisticsInfo implements Serializable {
    @ApiModelProperty(value = "")
    private String id;
    
    private List<String> idList; 

    @ApiModelProperty(value = "")
    private String shortNameEn;

    @ApiModelProperty(value = "物流方式简称")
    private String shortName;

    @ApiModelProperty(value = "物流方式类型   2供应商仓库物流  1品连仓库物流")
    private String type;

    @ApiModelProperty(value = "物流方式代码")
    private String code;

    @ApiModelProperty(value = "amazon物流商代码")
    private String amazonCarrier;

    @ApiModelProperty(value = "amazon物流方式")
    private String amazonCode;

    @ApiModelProperty(value = "ebay物流商代码")
    private String ebayCarrier;

    @ApiModelProperty(value = "速卖通物流方式code")
    private String aliexpressCode;

    @ApiModelProperty(value = "其他amazon物流商代码")
    private String otherAmazonCarrier;

    @ApiModelProperty(value = "其他amazon物流方式")
    private String otherAmazonCode;

    @ApiModelProperty(value = "其他ebay物流商代码")
    private String otherEbayCarrier;

    @ApiModelProperty(value = "物流服务商名称")
    private String carrierName;

    @ApiModelProperty(value = "物流服务商代码")
    private String carrierCode;

    @ApiModelProperty(value = "物流方式状态 默认0 0停用 1启用")
    private String status;

    @ApiModelProperty(value = "客户唯一标识")
    private String identify;


    @ApiModelProperty(value = "最后更新人id")
    private Long lastUpdateBy;

    @ApiModelProperty(value = "")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String lastUpdateTime;

    @ApiModelProperty(value = "")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

    private String warehouseId;

    private String warehouseCode;

    private HttpServletRequest request;
    
    private List<String> codeList;

    private List<String> warehouseIdList;
  
    private static final long serialVersionUID = 1L;
    
    
    public LogisticsInfo() {}
    

	public LogisticsInfo(String warehouseId, List<String> codeList) {
		super();
		this.warehouseId = warehouseId;
		this.codeList = codeList;
	}



	public LogisticsInfo(String code, String warehouseId, String status) {
		super();
		this.code = code;
		this.warehouseId = warehouseId;
		this.status = status;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public List<String> getIdList() {
		return idList;
	}


	public void setIdList(List<String> idList) {
		this.idList = idList;
	}


	public String getShortNameEn() {
		return shortNameEn;
	}


	public void setShortNameEn(String shortNameEn) {
		this.shortNameEn = shortNameEn;
	}


	public String getShortName() {
		return shortName;
	}


	public void setShortName(String shortName) {
		this.shortName = shortName;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
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


	public String getIdentify() {
		return identify;
	}


	public void setIdentify(String identify) {
		this.identify = identify;
	}



	public Long getLastUpdateBy() {
		return lastUpdateBy;
	}


	public void setLastUpdateBy(Long lastUpdateBy) {
		this.lastUpdateBy = lastUpdateBy;
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


	public HttpServletRequest getRequest() {
		return request;
	}


	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}


	public List<String> getCodeList() {
		return codeList;
	}


	public void setCodeList(List<String> codeList) {
		this.codeList = codeList;
	}


	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}

	public List<String> getWarehouseIdList() {
		return warehouseIdList;
	}

	public void setWarehouseIdList(List<String> warehouseIdList) {
		this.warehouseIdList = warehouseIdList;
	}

	public String getWarehouseCode() {
		return warehouseCode;
	}

	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}

	@Override
	public String toString() {
		return "LogisticsInfo{" +
				"id='" + id + '\'' +
				", idList=" + idList +
				", shortNameEn='" + shortNameEn + '\'' +
				", shortName='" + shortName + '\'' +
				", type='" + type + '\'' +
				", code='" + code + '\'' +
				", amazonCarrier='" + amazonCarrier + '\'' +
				", amazonCode='" + amazonCode + '\'' +
				", ebayCarrier='" + ebayCarrier + '\'' +
				", aliexpressCode='" + aliexpressCode + '\'' +
				", otherAmazonCarrier='" + otherAmazonCarrier + '\'' +
				", otherAmazonCode='" + otherAmazonCode + '\'' +
				", otherEbayCarrier='" + otherEbayCarrier + '\'' +
				", carrierName='" + carrierName + '\'' +
				", carrierCode='" + carrierCode + '\'' +
				", status='" + status + '\'' +
				", identify='" + identify + '\'' +
				", lastUpdateBy=" + lastUpdateBy +
				", lastUpdateTime='" + lastUpdateTime + '\'' +
				", createTime='" + createTime + '\'' +
				", warehouseId='" + warehouseId + '\'' +
				", warehouseCode='" + warehouseCode + '\'' +
				", request=" + request +
				", codeList=" + codeList +
				", warehouseIdList=" + warehouseIdList +
				'}';
	}
}