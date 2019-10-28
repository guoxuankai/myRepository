package com.rondaful.cloud.user.model.request.user;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/9/2
 * @Description:
 */
public class SellerUpdateStatusReq implements Serializable {
    private static final long serialVersionUID = 6491096239597302363L;

    @ApiModelProperty(value = "状态:1-启用;4-禁用;3-审核失败",name = "status",dataType = "Integer")
    private Integer status;

    @ApiModelProperty(value = "卖家id",name = "userId",dataType = "Integer",required = true)
    private Integer userId;

    @ApiModelProperty(value = "供应链公司id",name = "supplyChainCompany",dataType = "String")
    private String supplyChainCompany;

    @ApiModelProperty(value = "备注",name = "remark",dataType = "String")
    private String remark;

    @ApiModelProperty(value = "upc:1-启用,2-仅租用,0-禁用",name = "upc",dataType = "Integer")
    private Integer upc;

    @ApiModelProperty(value = "主营类目",name = "mainCategory",dataType = "String")
    private String mainCategory;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getSupplyChainCompany() {
        return supplyChainCompany;
    }

    public void setSupplyChainCompany(String supplyChainCompany) {
        this.supplyChainCompany = supplyChainCompany;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getUpc() {
        return upc;
    }

    public void setUpc(Integer upc) {
        this.upc = upc;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }
}
