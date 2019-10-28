package com.rondaful.cloud.order.model.xingShang.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 第三方供应商接口--包裹类
 *
 * @author Blade
 * @date 2019-08-05 09:22:31
 **/
public class SysOrderPackageXS implements Serializable {
    private static final long serialVersionUID = -8669915944020558257L;

    @ApiModelProperty(value = "订单包裹详情集合")
    private List<SysOrderPackageDetailXS> sysOrderPackageDetailList;

    @ApiModelProperty(value = "来源订单号(合并包裹会用#的方式进行切割)")
    private String sourceOrderId;

    @ApiModelProperty(value = "订单跟踪号(包裹号)")
    private String orderTrackId;

    @ApiModelProperty(value = "包裹状态(1、wait_push,2、wait_deliver,3、intercepted,4、delivered,5、push_fail)")
    private String packageStatus;

    @ApiModelProperty(value = "发货时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deliveryTime;

    @ApiModelProperty(value = "发货仓库ID")
    private Integer deliveryWarehouseId;

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

    @ApiModelProperty(value = "跟踪单号")
    private String shipTrackNumber;

    @ApiModelProperty(value = "物流商单号")
    private String shipOrderId;

    @ApiModelProperty(value = "仓库发货异常信息")
    private String warehouseShipException;

    public List<SysOrderPackageDetailXS> getSysOrderPackageDetailList() {
        return sysOrderPackageDetailList;
    }

    public void setSysOrderPackageDetailList(List<SysOrderPackageDetailXS> sysOrderPackageDetailList) {
        this.sysOrderPackageDetailList = sysOrderPackageDetailList;
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

    public String getWarehouseShipException() {
        return warehouseShipException;
    }

    public void setWarehouseShipException(String warehouseShipException) {
        this.warehouseShipException = warehouseShipException;
    }
}
