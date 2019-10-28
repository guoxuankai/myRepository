package com.rondaful.cloud.user.controller.model.hystrix;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

import java.io.Serializable;

public class UserFananceVo implements Serializable {
    private static final long serialVersionUID = -8073616254269949852L;

    @ApiModelProperty(value = "事件类型  1:卖家初始化  2：卖家修改  3：供应商初始化 4：供应商修改  5：结算注册 6：结算修改")
    private Integer dataType;

    //卖家初始化及卖家修改
    @ApiModelProperty(value = "卖家ID")
    private Integer sellerId;
    @ApiModelProperty(value = "卖家名称")
    private String sellerName;
    @ApiModelProperty(value = "供应链公司ID")
    private Integer supplyCompanyId;
    @ApiModelProperty(value = "供应链公司名称")
    private String supplyCompanyName;
    @ApiModelProperty(value = "联系人")
    private String contracts;
    @ApiModelProperty(value = "联系人电话")
    private String contractNumber;
    @ApiModelProperty(value = "国家/地区")
    private String country;
    @ApiModelProperty(value = "省/市")
    private String province;

    //供应商初始化及供应商修改
    @ApiModelProperty(value = "供应商ID")
    private Integer supplierId;
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;
    @ApiModelProperty(value = "供应商账号")
    private String supplierAccount;

    //结算注册及修改
    @ApiModelProperty(value = "结算周期")
    private String settlementCycle;

    public UserFananceVo(Integer sellerId, String sellerName, Integer supplyCompanyId, String supplyCompanyName, String contracts, String contractNumber, String country, String province) {
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.supplyCompanyId = supplyCompanyId;
        this.supplyCompanyName = supplyCompanyName;
        this.contracts = contracts;
        this.contractNumber = contractNumber;
        this.country = country;
        this.province = province;
    }

    public UserFananceVo(Integer supplierId, String supplierName, String supplierAccount, Integer supplyCompanyId, String supplyCompanyName, String contracts, String contractNumber, String country, String province) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.supplierAccount = supplierAccount;
        this.supplyCompanyId = supplyCompanyId;
        this.supplyCompanyName = supplyCompanyName;
        this.contracts = contracts;
        this.contractNumber = contractNumber;
        this.country = country;
        this.province = province;
    }

    public UserFananceVo(Integer supplierId, String supplierName, String settlementCycle) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.settlementCycle = settlementCycle;
    }

    public UserFananceVo() {
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
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

    public String getSupplierAccount() {
        return supplierAccount;
    }

    public void setSupplierAccount(String supplierAccount) {
        this.supplierAccount = supplierAccount;
    }

    public String getSettlementCycle() {
        return settlementCycle;
    }

    public void setSettlementCycle(String settlementCycle) {
        this.settlementCycle = settlementCycle;
    }

    public Integer getSellerId() {
        return sellerId;
    }
    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }
    public String getSellerName() {
        return sellerName;
    }
    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }
    public Integer getSupplyCompanyId() {
        return supplyCompanyId;
    }
    public void setSupplyCompanyId(Integer supplyCompanyId) {
        this.supplyCompanyId = supplyCompanyId;
    }
    public String getSupplyCompanyName() {
        return supplyCompanyName;
    }
    public void setSupplyCompanyName(String supplyCompanyName) {
        this.supplyCompanyName = supplyCompanyName;
    }
    public String getContracts() {
        return contracts;
    }
    public void setContracts(String contracts) {
        this.contracts = contracts;
    }
    public String getContractNumber() {
        return contractNumber;
    }
    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }

}
