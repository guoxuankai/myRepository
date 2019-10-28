package com.rondaful.cloud.seller.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@ApiModel(description = "订单规则邮寄方式基础对象")
public class OrderRule implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "规则名称")
    private String ruleName;

    @ApiModelProperty(value = "卖家id")
    private String sellerId;

    @ApiModelProperty(value = "品连卖家账号")
    private String sellerAccount ;

    @ApiModelProperty(value = "优先级，越小越优先")
    private Integer priority;


    @ApiModelProperty(value = "状态[1:启用 2:停用 ]")
    private Integer status;

    @ApiModelProperty(value = "有效期开始时间 [yyyy-MM-dd HH:mm:ss]")
    private String effectiveStartTime;

    @ApiModelProperty(value = "有效期结束时间 [yyyy-MM-dd HH:mm:ss]")
    private String effectiveEndTime;

    @ApiModelProperty(value = "邮寄方式id(分配邮寄方式时使用此字段)")
    private String mailTypeCode;

    @ApiModelProperty(value = "订单总价下限RMB(0#1.00,#号前 0比较时代等号，1不带等号)")
    private String priceMin;

    @ApiModelProperty(value = "订单总价上限RMB(0#1.00,#号前 0比较时代等号，1不带等号)")
    private String priceMax;

    @ApiModelProperty(value = "订单总重下限 g(0#1.00,#号前 0比较时代等号，1不带等号)")
    private String weightMin;

    @ApiModelProperty(value = "订单总重上限 g(0#1.00,#号前 0比较时代等号，1不带等号)")
    private String weightMax;

    @ApiModelProperty(value = "订单体积下限 平方米(0#1.00,#号前 0比较时代等号，1不带等号)")
    private String volumeMin;

    @ApiModelProperty(value = "订单体积上限 平方米(0#1.00,#号前 0比较时代等号，1不带等号)")
    private String volumeMax;

    @ApiModelProperty(value = "version")
    private Long version;

    @DateTimeFormat(pattern= "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @DateTimeFormat(pattern= "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "开始创建时间，查询参数 [yyyy-MM-dd HH:mm:ss]" )
    private String startCreateTime;

    @ApiModelProperty(value = "结束创建时间，查询参数 [yyyy-MM-dd HH:mm:ss]")
    private String endCreateTime;

    @ApiModelProperty(value = "发货仓库id(分配发货仓库时使用此字段)")
    private String deliveryWarehouseCode;

    @ApiModelProperty(value = "前端传入的显示数据")
    private String message;

    @ApiModelProperty(value = "平台标志:S-卖家 G-管理后台")
    private String platformMark;

    @ApiModelProperty(value = "发货仓库ID")
    private Integer deliveryWarehouseId;

    private static final long serialVersionUID = 1L;

    public Integer getDeliveryWarehouseId() {
        return deliveryWarehouseId;
    }

    public void setDeliveryWarehouseId(Integer deliveryWarehouseId) {
        this.deliveryWarehouseId = deliveryWarehouseId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId == null ? null : sellerId.trim();
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName == null?null:ruleName.trim();
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getEffectiveStartTime() {
        return effectiveStartTime;
    }

    public void setEffectiveStartTime(String effectiveStartTime) {
        this.effectiveStartTime = effectiveStartTime;
    }

    public String getEffectiveEndTime() {
        return effectiveEndTime;
    }

    public void setEffectiveEndTime(String effectiveEndTime) {
        this.effectiveEndTime = effectiveEndTime;
    }


    public String getPriceMin() {
        return priceMin;
    }

    public void setPriceMin(String priceMin) {
        this.priceMin = priceMin;
    }

    public String getPriceMax() {
        return priceMax;
    }

    public void setPriceMax(String priceMax) {
        this.priceMax = priceMax;
    }

    public String getWeightMin() {
        return weightMin;
    }

    public void setWeightMin(String weightMin) {
        this.weightMin = weightMin;
    }

    public String getWeightMax() {
        return weightMax;
    }

    public void setWeightMax(String weightMax) {
        this.weightMax = weightMax;
    }

    public String getVolumeMin() {
        return volumeMin;
    }

    public void setVolumeMin(String volumeMin) {
        this.volumeMin = volumeMin;
    }

    public String getVolumeMax() {
        return volumeMax;
    }

    public void setVolumeMax(String volumeMax) {
        this.volumeMax = volumeMax;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getStartCreateTime() {
        return startCreateTime;
    }

    public void setStartCreateTime(String startCreateTime) {
        this.startCreateTime = startCreateTime;
    }

    public String getEndCreateTime() {
        return endCreateTime;
    }

    public void setEndCreateTime(String endCreateTime) {
        this.endCreateTime = endCreateTime;
    }

    public String getMailTypeCode() {
        return mailTypeCode;
    }

    public void setMailTypeCode(String mailTypeCode) {
        this.mailTypeCode = mailTypeCode == null?null:mailTypeCode.trim();
    }

    public String getDeliveryWarehouseCode() {
        return deliveryWarehouseCode;
    }

    public void setDeliveryWarehouseCode(String deliveryWarehouseCode) {
        this.deliveryWarehouseCode = deliveryWarehouseCode == null?null:deliveryWarehouseCode.trim();
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getSellerAccount() {
        return sellerAccount;
    }

    public void setSellerAccount(String sellerAccount) {
        this.sellerAccount = sellerAccount == null?null:sellerAccount.trim();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPlatformMark() {
        return platformMark;
    }

    public void setPlatformMark(String platformMark) {
        this.platformMark = platformMark;
    }
}