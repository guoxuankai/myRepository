package com.rondaful.cloud.supplier.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 入库单查询Request
 *
 * @ClassName WarehouseWarrantRequest
 * @Author tianye
 * @Date 2019/4/25 20:29
 * @Version 1.0
 */
@ApiModel(value = "入库单查询Request")
public class WarehouseWarrantRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 服务调用标识 1：供应商服务调用  2：后台服务调用 -> 不对外
     */
    private String serviceStatus;

    @ApiModelProperty(value = "页码")
    private String page;

    @ApiModelProperty(value = "每页显示行数")
    private String row;

    @ApiModelProperty(value = "入库单状态")
    private String status;

    @ApiModelProperty(value = "仓库服务商代码")
    private String facilitatorCode;

    @ApiModelProperty(value = "目的仓代码")
    private String warehouseCode;

    @ApiModelProperty(value = "入库单号")
    private String receivingCode;

    @ApiModelProperty(value = "供应商名称")
    private String supplier;


    /**
     * 是否主账号标识：0：主账号，1：子账号
     */
    private Integer topFlag;
    private Integer supplierId;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public Integer getTopFlag() {
        return topFlag;
    }

    public void setTopFlag(Integer topFlag) {
        this.topFlag = topFlag;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getIsParent() {
        return isParent;
    }

    public void setIsParent(String isParent) {
        this.isParent = isParent;
    }

    /**
     * 是否为主账户 -> 不对外
     */
    private String isParent = "false";


    /**
     * 仓库编码列表 -> 不对外
     */
    private List<String> codeList;

    /**
     * 仓库编码列表 -> 不对外 用于谷仓多账号数据权限
     */
    private List<String> wareHouseCodeList;

    public List<String> getWareHouseCodeList() {
        return wareHouseCodeList;
    }

    public void setWareHouseCodeList(List<String> wareHouseCodeList) {
        this.wareHouseCodeList = wareHouseCodeList;
    }

    /**
     * 供应商列表 -> 不对外，用于后台服务调用
     */
    private List<String> supplies;

    public List<String> getSupplies() {
        return supplies;
    }

    public void setSupplies(List<String> supplies) {
        this.supplies = supplies;
    }

    public List<String> getCodeList() {
        return codeList;
    }

    public void setCodeList(List<String> codeList) {
        this.codeList = codeList;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFacilitatorCode() {
        return facilitatorCode;
    }

    public void setFacilitatorCode(String facilitatorCode) {
        this.facilitatorCode = facilitatorCode;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getReceivingCode() {
        return receivingCode;
    }

    public void setReceivingCode(String receivingCode) {
        this.receivingCode = receivingCode;
    }
}
