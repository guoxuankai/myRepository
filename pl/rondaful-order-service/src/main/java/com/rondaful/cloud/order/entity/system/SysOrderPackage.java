package com.rondaful.cloud.order.entity.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单包裹表
 * 实体类对应的数据表为：  tb_sys_order_package
 *
 * @author chenjiangxin
 * @date 2019-07-18 17:20:20
 */
@ApiModel(value = "SysOrderPackage")
public class SysOrderPackage implements Serializable {

    private static final long serialVersionUID = 2333793069860151076L;
    //内部标记，拦截包裹用
    @ApiModelProperty(value = "是否拦截成功")
    private boolean isInterceptSuccessful = true;

    @ApiModelProperty(value = "被合并的订单号(有多个则用#号拼接)")
    private String operateSysOrderId;

    @ApiModelProperty(value = "订单包裹详情集合")
    private List<SysOrderPackageDetail> sysOrderPackageDetailList;

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "系统订单ID")
    private String sysOrderId;

    @ApiModelProperty(value = "来源订单号(合并包裹会用#的方式进行切割)")
    private String sourceOrderId;

    @ApiModelProperty(value = "订单跟踪号(包裹号)")
    private String orderTrackId;

    @ApiModelProperty(value = "包裹状态(1、wait_push,2、wait_deliver,3、intercepted,4、delivered,5、push_fail)")
    private String packageStatus;

    @ApiModelProperty(value = "发货时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date deliveryTime;

    @ApiModelProperty(value = "发货仓库ID")
    private Integer deliveryWarehouseId;

    @ApiModelProperty(value = "发货仓库code")
    private String deliveryWarehouseCode;

    @ApiModelProperty(value = "订单发货仓库名称")
    private String deliveryWarehouse;

    @ApiModelProperty(value = "物流商CODE")
    private String shippingCarrierUsedCode;

    @ApiModelProperty(value = "物流商名称")
    private String shippingCarrierUsed;

    @ApiModelProperty(value = "邮寄方式CODE")
    private String deliveryMethodCode;

    @ApiModelProperty(value = "物流类型(策略)：1.cheapest 2.integrated_optimal 3.fastest")
    private String logisticsStrategy;

    @ApiModelProperty(value = "邮寄方式名称")
    private String deliveryMethod;

    @ApiModelProperty(value = "映射后的Amazon物流商名称")
    private String amazonCarrierName;

    @ApiModelProperty(value = "映射后的Amazon配送方式")
    private String amazonShippingMethod;

    @ApiModelProperty(value = "映射后的Ebay物流商名称")
    private String ebayCarrierName;

    @ApiModelProperty(value = "实际物流费")
    private BigDecimal actualShipCost;

    @ApiModelProperty(value = "附加运费比例")
    private BigDecimal additionalFreightRate;

    @ApiModelProperty(value = "预估物流费")
    private BigDecimal estimateShipCost;

    @ApiModelProperty(value = "跟踪单号")
    private String shipTrackNumber;

    @ApiModelProperty(value = "物流商单号")
    private String shipOrderId;

    @ApiModelProperty(value = "谷仓返回订单号")
    private String referenceId;

    @ApiModelProperty(value = "仓库发货异常信息")
    private String warehouseShipException;

    @ApiModelProperty(value = "操作状态(1、split(拆分)，2、merged(合并))")
    private String operateStatus;

    @ApiModelProperty(value = "操作的包裹号(如果是合单，则这里面存放的是所有的包裹号，用#分割。如果是拆单，则存放的主包裹号)")
    private String operateOrderTrackId;

    @ApiModelProperty(value = "是否展示(1、show，2、no_show)")
    private String isShow;

    @ApiModelProperty(value = "计算费用的信息,json格式")
    private String calculateFeeInfo;

    @ApiModelProperty(value = "创建人")
    private String creater;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "修改人")
    private String modifier;

    @ApiModelProperty(value = "修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifiedTime;

    @ApiModelProperty(value = "包裹发货状态")
    private byte packageDeliverStatus;

    private boolean deliveryGood;

    private List<String> skus;

    public boolean isInterceptSuccessful() {
        return isInterceptSuccessful;
    }

    public void setInterceptSuccessful(boolean interceptSuccessful) {
        isInterceptSuccessful = interceptSuccessful;
    }

    public String getOperateSysOrderId() {
        return operateSysOrderId;
    }

    public void setOperateSysOrderId(String operateSysOrderId) {
        this.operateSysOrderId = operateSysOrderId;
    }

    public List<SysOrderPackageDetail> getSysOrderPackageDetailList() {
        return sysOrderPackageDetailList;
    }

    public void setSysOrderPackageDetailList(List<SysOrderPackageDetail> sysOrderPackageDetailList) {
        this.sysOrderPackageDetailList = sysOrderPackageDetailList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSysOrderId() {
        return sysOrderId;
    }

    public void setSysOrderId(String sysOrderId) {
        this.sysOrderId = sysOrderId;
    }

    public String getSourceOrderId() {
        return sourceOrderId;
    }

    public void setSourceOrderId(String sourceOrderId) {
        this.sourceOrderId = sourceOrderId;
    }

    public String getOrderTrackId() {
        return orderTrackId;
    }

    public void setOrderTrackId(String orderTrackId) {
        this.orderTrackId = orderTrackId;
    }

    public String getPackageStatus() {
        return packageStatus;
    }

    public void setPackageStatus(String packageStatus) {
        this.packageStatus = packageStatus;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Integer getDeliveryWarehouseId() {
        return deliveryWarehouseId;
    }

    public void setDeliveryWarehouseId(Integer deliveryWarehouseId) {
        this.deliveryWarehouseId = deliveryWarehouseId;
    }

    public String getDeliveryWarehouseCode() {
        return deliveryWarehouseCode;
    }

    public void setDeliveryWarehouseCode(String deliveryWarehouseCode) {
        this.deliveryWarehouseCode = deliveryWarehouseCode;
    }

    public String getDeliveryWarehouse() {
        return deliveryWarehouse;
    }

    public void setDeliveryWarehouse(String deliveryWarehouse) {
        this.deliveryWarehouse = deliveryWarehouse;
    }

    public String getShippingCarrierUsedCode() {
        return shippingCarrierUsedCode;
    }

    public void setShippingCarrierUsedCode(String shippingCarrierUsedCode) {
        this.shippingCarrierUsedCode = shippingCarrierUsedCode;
    }

    public String getShippingCarrierUsed() {
        return shippingCarrierUsed;
    }

    public void setShippingCarrierUsed(String shippingCarrierUsed) {
        this.shippingCarrierUsed = shippingCarrierUsed;
    }

    public String getDeliveryMethodCode() {
        return deliveryMethodCode;
    }

    public void setDeliveryMethodCode(String deliveryMethodCode) {
        this.deliveryMethodCode = deliveryMethodCode;
    }

    public String getLogisticsStrategy() {
        return logisticsStrategy;
    }

    public void setLogisticsStrategy(String logisticsStrategy) {
        this.logisticsStrategy = logisticsStrategy;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public String getAmazonCarrierName() {
        return amazonCarrierName;
    }

    public void setAmazonCarrierName(String amazonCarrierName) {
        this.amazonCarrierName = amazonCarrierName;
    }

    public String getAmazonShippingMethod() {
        return amazonShippingMethod;
    }

    public void setAmazonShippingMethod(String amazonShippingMethod) {
        this.amazonShippingMethod = amazonShippingMethod;
    }

    public String getEbayCarrierName() {
        return ebayCarrierName;
    }

    public void setEbayCarrierName(String ebayCarrierName) {
        this.ebayCarrierName = ebayCarrierName;
    }

    public BigDecimal getActualShipCost() {
        return actualShipCost;
    }

    public void setActualShipCost(BigDecimal actualShipCost) {
        this.actualShipCost = actualShipCost;
    }

    public BigDecimal getAdditionalFreightRate() {
        return additionalFreightRate;
    }

    public void setAdditionalFreightRate(BigDecimal additionalFreightRate) {
        this.additionalFreightRate = additionalFreightRate;
    }

    public BigDecimal getEstimateShipCost() {
        return estimateShipCost;
    }

    public void setEstimateShipCost(BigDecimal estimateShipCost) {
        this.estimateShipCost = estimateShipCost;
    }

    public String getShipTrackNumber() {
        return shipTrackNumber;
    }

    public void setShipTrackNumber(String shipTrackNumber) {
        this.shipTrackNumber = shipTrackNumber;
    }

    public String getShipOrderId() {
        return shipOrderId;
    }

    public void setShipOrderId(String shipOrderId) {
        this.shipOrderId = shipOrderId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getWarehouseShipException() {
        return warehouseShipException;
    }

    public void setWarehouseShipException(String warehouseShipException) {
        this.warehouseShipException = warehouseShipException;
    }

    public String getOperateStatus() {
        return operateStatus;
    }

    public void setOperateStatus(String operateStatus) {
        this.operateStatus = operateStatus;
    }

    public String getOperateOrderTrackId() {
        return operateOrderTrackId;
    }

    public void setOperateOrderTrackId(String operateOrderTrackId) {
        this.operateOrderTrackId = operateOrderTrackId;
    }

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }

    public String getCalculateFeeInfo() {
        return calculateFeeInfo;
    }

    public void setCalculateFeeInfo(String calculateFeeInfo) {
        this.calculateFeeInfo = calculateFeeInfo;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public byte getPackageDeliverStatus() {
        return packageDeliverStatus;
    }

    public void setPackageDeliverStatus(byte packageDeliverStatus) {
        this.packageDeliverStatus = packageDeliverStatus;
    }

    public boolean isDeliveryGood() {
        return deliveryGood;
    }

    public void setDeliveryGood(boolean deliveryGood) {
        this.deliveryGood = deliveryGood;
    }

    public List<String> getSkus() {
        return skus;
    }

    public void setSkus(List<String> skus) {
        this.skus = skus;
    }
}