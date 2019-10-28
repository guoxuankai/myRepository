package com.rondaful.cloud.seller.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 生成的upc码
 * @author guoxuankai
 * @date 2019/6/10
 */
@ApiModel(description = "生成的upc码")
public class UpcGenerate implements Serializable {

    @ApiModelProperty(value = "唯一id")
    private Integer id;

    @ApiModelProperty(value = "号码批次")
    private String numberBatch;

    @ApiModelProperty(value = "号码类型")
    private String numberType;

    @ApiModelProperty(value = "号码")
    private String number;

    @ApiModelProperty(value = "已用平台    1 ebay  2amazon  3 ebay,amazon")
    private Integer usedplatform;

    @ApiModelProperty(value = "创建时间")
    private Date createdTime;

    @ApiModelProperty(value = "启用停用状态   0 启用  1 停用")
    private Integer useStatus;

    @ApiModelProperty(value = "状态  0未使用  1 已使用  2 停用")
    private Integer status;

    @ApiModelProperty(value = "卖家账号")
    private String account;


    @ApiModelProperty(value = "父账号")
    private String parentAccount;


    private static final long serialVersionUID = 1L;


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public String getNumberBatch() {
        return numberBatch;
    }


    public void setNumberBatch(String numberBatch) {
        this.numberBatch = numberBatch == null ? null : numberBatch.trim();
    }


    public String getNumberType() {
        return numberType;
    }


    public void setNumberType(String numberType) {
        this.numberType = numberType == null ? null : numberType.trim();
    }


    public String getNumber() {
        return number;
    }


    public void setNumber(String number) {
        this.number = number == null ? null : number.trim();
    }


    public Integer getUsedplatform() {
        return usedplatform;
    }


    public void setUsedplatform(Integer usedplatform) {
        this.usedplatform = usedplatform;
    }


    public Date getCreatedTime() {
        return createdTime;
    }


    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }


    public Integer getUseStatus() {
        return useStatus;
    }


    public void setUseStatus(Integer useStatus) {
        this.useStatus = useStatus;
    }


    public Integer getStatus() {
        return status;
    }


    public void setStatus(Integer status) {
        this.status = status;
    }


    public String getAccount() {
        return account;
    }


    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }


    public String getParentAccount() {
        return parentAccount;
    }


    public void setParentAccount(String parentAccount) {
        this.parentAccount = parentAccount == null ? null : parentAccount.trim();
    }
}