package com.rondaful.cloud.user.model.dto.user;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/5/4
 * @Description:
 */
public class QueryBackSupplierDTO implements Serializable {
    private static final long serialVersionUID = 9185644601072922961L;

    @ApiModelProperty(value = "状态,目前只有(1:有效)一个状态",name = "status",dataType = "string")
    private Integer status;

    @ApiModelProperty(value = "供应商id",name = "id",dataType = "Integer")
    private Integer supplierId;

    @ApiModelProperty(value = "供应商名称",name = "supplierName",dataType = "String")
    private String supplierName;

    @ApiModelProperty(value = "供应链公司",name = "supplyChainCompany",dataType = "string")
    private String supplyChainCompany;

    @ApiModelProperty(value = "开始时间(yyyy-MM-dd HH:mm:ss)",name = "startTime",dataType = "string")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty(value = "结束时间(yyyy-MM-dd HH:mm:ss)",name = "endTime",dataType = "string")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @ApiModelProperty(value = "展示条数",name = "totalCount",dataType = "Long")
    private Integer pageSize;
    @ApiModelProperty(value = "当前页",name = "currentPage",dataType = "Long")
    private Integer currentPage;
    @ApiModelProperty(value = "查询时间类型:1新增  2  修改",name = "Integer",dataType = "Long")
    private Integer dateType;

    private List<Integer> userIds;

    public List<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Integer> userIds) {
        this.userIds = userIds;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplyChainCompany() {
        return supplyChainCompany;
    }

    public void setSupplyChainCompany(String supplyChainCompany) {
        this.supplyChainCompany = supplyChainCompany;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getDateType() {
        return dateType;
    }

    public void setDateType(Integer dateType) {
        this.dateType = dateType;
    }
}
