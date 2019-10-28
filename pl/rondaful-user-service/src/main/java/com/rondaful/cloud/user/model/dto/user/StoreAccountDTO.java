package com.rondaful.cloud.user.model.dto.user;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: xqq
 * @Date: 2019/4/30
 * @Description:
 */
public class StoreAccountDTO implements Serializable {

    private static final long serialVersionUID = -341022663696778682L;
    @ApiModelProperty(value = "个人额度类型")
    private String personal;
    @ApiModelProperty(value = "自营额度类型")
    private String rent;
    @ApiModelProperty(value = "申请额度状态")
    private String applyStatus;
    @ApiModelProperty(value = "审核备注")
    private String remark;

    public String getPersonal() {
        return personal;
    }

    public void setPersonal(String personal) {
        this.personal = personal;
    }

    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }

    public String getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(String applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
