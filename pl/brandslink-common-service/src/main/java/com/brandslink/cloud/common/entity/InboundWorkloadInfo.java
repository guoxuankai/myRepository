package com.brandslink.cloud.common.entity;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 入库工作量信息
 */
public class InboundWorkloadInfo implements Serializable{

    @ApiModelProperty(value = "仓库code")
    private String warehouseCode;

    @ApiModelProperty(value = "客户")
    private String customer;

    @ApiModelProperty(value = "入库类型(01-采购入库 02调拨入库 03销退入库)")
    private Integer putType;

    @ApiModelProperty(value = "操作类型( 1-收货 2-质检 3-上架)")
    private Integer operationType;

    @ApiModelProperty(value = "作业人")
    private String workingPeople;

    @ApiModelProperty(value = "协同人")
    private String synergyPeople;

    @ApiModelProperty(value = "包裹签到数")
    private Integer packetCheckinNum;

    @ApiModelProperty(value = "码盘数")
    private Integer encoderNum;

    @ApiModelProperty(value = "包裹接收数")
    private Integer packetEceiveNum;

    @ApiModelProperty(value = "运单号")
    private String waybillId;

    @ApiModelProperty(value = "质检次数")
    private Integer qcTimeNum;

    @ApiModelProperty(value = "质检个数")
    private Integer qcNum;

    @ApiModelProperty(value = "上架次数")
    private Integer putawayTimeNum;

    @ApiModelProperty(value = "上架个数")
    private Integer putawayNum;

    @ApiModelProperty(value = "操作时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Integer getPutType() {
        return putType;
    }

    public void setPutType(Integer putType) {
        this.putType = putType;
    }

    public Integer getOperationType() {
        return operationType;
    }

    public void setOperationType(Integer operationType) {
        this.operationType = operationType;
    }

    public String getWorkingPeople() {
        return workingPeople;
    }

    public void setWorkingPeople(String workingPeople) {
        this.workingPeople = workingPeople;
    }

    public String getSynergyPeople() {
        return synergyPeople;
    }

    public void setSynergyPeople(String synergyPeople) {
        this.synergyPeople = synergyPeople;
    }

    public Integer getPacketCheckinNum() {
        return packetCheckinNum;
    }

    public void setPacketCheckinNum(Integer packetCheckinNum) {
        this.packetCheckinNum = packetCheckinNum;
    }

    public Integer getEncoderNum() {
        return encoderNum;
    }

    public void setEncoderNum(Integer encoderNum) {
        this.encoderNum = encoderNum;
    }

    public Integer getPacketEceiveNum() {
        return packetEceiveNum;
    }

    public void setPacketEceiveNum(Integer packetEceiveNum) {
        this.packetEceiveNum = packetEceiveNum;
    }

    public String getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(String waybillId) {
        this.waybillId = waybillId;
    }

    public Integer getQcTimeNum() {
        return qcTimeNum;
    }

    public void setQcTimeNum(Integer qcTimeNum) {
        this.qcTimeNum = qcTimeNum;
    }

    public Integer getQcNum() {
        return qcNum;
    }

    public void setQcNum(Integer qcNum) {
        this.qcNum = qcNum;
    }

    public Integer getPutawayTimeNum() {
        return putawayTimeNum;
    }

    public void setPutawayTimeNum(Integer putawayTimeNum) {
        this.putawayTimeNum = putawayTimeNum;
    }

    public Integer getPutawayNum() {
        return putawayNum;
    }

    public void setPutawayNum(Integer putawayNum) {
        this.putawayNum = putawayNum;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
