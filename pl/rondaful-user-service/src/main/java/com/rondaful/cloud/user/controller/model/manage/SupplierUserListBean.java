package com.rondaful.cloud.user.controller.model.manage;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class SupplierUserListBean implements Serializable {
    private static final long serialVersionUID = 8144211665610492167L;

    @ApiModelProperty(name = "delFlag", value = "账号审核状态", dataType = "string",  required = false)
    private String delFlag;
    @ApiModelProperty(name = "supplierUsername", value = "供应商账号", dataType = "string", required = false)
    private String supplierUsername;
    @ApiModelProperty(name = "supplierCompany", value = "供应商公司", dataType = "string",required = false)
    private String supplierCompany;
    @ApiModelProperty(name = "supplyChainCompanyId", value = "供应链公司id", dataType = "string", required = false)
    private String supplyChainCompanyId;
    @ApiModelProperty(name = "createDateStart", value = "起始新增时间", dataType = "string", required = false)
    private String createDateStart;
    @ApiModelProperty(name = "createDateClose", value = "结束新增时间", dataType = "string",  required = false)
    private String createDateClose;
    @ApiModelProperty(name = "updateDateStart", value = "起始更新时间", dataType = "string", required = false)
    private String updateDateStart;
    @ApiModelProperty(name = "updateDateClose", value = "结束更新时间", dataType = "string", required = false)
    private String updateDateClose;
    @ApiModelProperty(name = "currPage", value = "数据页数", dataType = "string", required = true)
    private String currPage;
    @ApiModelProperty(name = "row", value = "数据数量", dataType = "string", required = true)
    private String row;

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getSupplierUsername() {
        return supplierUsername;
    }

    public void setSupplierUsername(String supplierUsername) {
        this.supplierUsername = supplierUsername;
    }

    public String getSupplierCompany() {
        return supplierCompany;
    }

    public void setSupplierCompany(String supplierCompany) {
        this.supplierCompany = supplierCompany;
    }

    public String getSupplyChainCompanyId() {
        return supplyChainCompanyId;
    }

    public void setSupplyChainCompanyId(String supplyChainCompanyId) {
        this.supplyChainCompanyId = supplyChainCompanyId;
    }

    public String getCreateDateStart() {
        return createDateStart;
    }

    public void setCreateDateStart(String createDateStart) {
        this.createDateStart = createDateStart;
    }

    public String getCreateDateClose() {
        return createDateClose;
    }

    public void setCreateDateClose(String createDateClose) {
        this.createDateClose = createDateClose;
    }

    public String getUpdateDateStart() {
        return updateDateStart;
    }

    public void setUpdateDateStart(String updateDateStart) {
        this.updateDateStart = updateDateStart;
    }

    public String getUpdateDateClose() {
        return updateDateClose;
    }

    public void setUpdateDateClose(String updateDateClose) {
        this.updateDateClose = updateDateClose;
    }

    public String getCurrPage() {
        return currPage;
    }

    public void setCurrPage(String currPage) {
        this.currPage = currPage;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }
}
