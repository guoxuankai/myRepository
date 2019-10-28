package com.rondaful.cloud.user.entity;

import io.swagger.annotations.ApiModelProperty;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/30
 * @Description:
 */
public class QuerySellerPageDO implements Serializable {
    private static final long serialVersionUID = -5054820118386406237L;

    private List<Integer> status;
    private String supplyChainCompany;
    private Date startTime;
    private Date endTime;
    private List<Integer> userIds;
    private Integer dateType;
    @ApiModelProperty(value = "手机号",name = "phone",dataType = "String")
    private String phone;

    @ApiModelProperty(value = "邮箱",name = "email",dataType = "String")
    private String email;

    @ApiModelProperty(value = "登录账号",name = "loginName",dataType = "String")
    private String loginName;

    private String userName;

    public List<Integer> getStatus() {
        return status;
    }

    public void setStatus(List<Integer> status) {
        this.status = status;
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

    public List<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Integer> userIds) {
        this.userIds = userIds;
    }

    public Integer getDateType() {
        return dateType;
    }

    public void setDateType(Integer dateType) {
        this.dateType = dateType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
