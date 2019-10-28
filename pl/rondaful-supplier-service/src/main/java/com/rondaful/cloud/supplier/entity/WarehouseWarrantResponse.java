package com.rondaful.cloud.supplier.entity;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 入库单列表查询DTO
 *
 * @ClassName WarehouseWarrantResponse
 * @Author tianye
 * @Date 2019/4/25 20:52
 * @Version 1.0
 */
public class WarehouseWarrantResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "入库单号")
    private String receivingCode;

    @ApiModelProperty(value = "入库单状态(0:草稿,1:审核中,2:待收货,3:已入库,4:已取消,5:异常)")
    private Byte receivingStatus;

    @ApiModelProperty(value = "目的仓服务商名称")
    private String warehouseFacilitatorName;

    @ApiModelProperty(value = "目的仓名称")
    private String warehouseName;

    @ApiModelProperty(value = "备注")
    private String receivingDesc;

    @ApiModelProperty(value = "创建的账户")
    private String createBy;

    @ApiModelProperty(value = "最后一次修改人")
    private String lastUpdateBy;

    @ApiModelProperty(value = "创建时间")
    private Date creatTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "供应商名称")
    private String supplier;

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReceivingCode() {
        return receivingCode;
    }

    public void setReceivingCode(String receivingCode) {
        this.receivingCode = receivingCode;
    }

    public Byte getReceivingStatus() {
        return receivingStatus;
    }

    public void setReceivingStatus(Byte receivingStatus) {
        this.receivingStatus = receivingStatus;
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

    public String getReceivingDesc() {
        return receivingDesc;
    }

    public void setReceivingDesc(String receivingDesc) {
        this.receivingDesc = receivingDesc;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
