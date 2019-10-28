package com.rondaful.cloud.user.model.request.user;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/6/1
 * @Description:
 */
public class SellerUserCenterReq implements Serializable {
    private static final long serialVersionUID = 8213651872077237735L;

    @ApiModelProperty(value = "用户名",name = "userName",dataType = "String")
    private String userName;

    @ApiModelProperty(value = "手机区号",name = "phoneCode",dataType = "String")
    private String phoneCode;

    @ApiModelProperty(value = "手机",name = "phone",dataType = "String")
    private String phone;

    @ApiModelProperty(value = "职位",name = "jobNames",dataType = "String")
    private String jobNames;

    @ApiModelProperty(value = "验证码",name = "verifyCode",dataType = "String")
    private String verifyCode;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getJobNames() {
        return jobNames;
    }

    public void setJobNames(String jobNames) {
        this.jobNames = jobNames;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }
}
