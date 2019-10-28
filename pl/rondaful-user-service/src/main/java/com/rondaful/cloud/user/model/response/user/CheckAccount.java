package com.rondaful.cloud.user.model.response.user;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/5/23
 * @Description:
 */
public class CheckAccount implements Serializable {

    @ApiModelProperty(value = "状态:1-审核通过,3-待激活,2-审核中,3-审核失败,0-待激活")
    private Integer status;
    @ApiModelProperty(value = "备注:")
    private String remark;

    public CheckAccount(){}

    public CheckAccount(Integer status, String remark) {
        this.status = status;
        this.remark = remark;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
