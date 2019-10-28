package com.rondaful.cloud.user.model.request.user;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/4/26
 * @Description:
 */
public class ManageUserReq implements Serializable {
    private static final long serialVersionUID = -1969022483016749095L;

    @ApiModelProperty(value = "id",name = "id",dataType = "Integer")
    private Integer id;

    @ApiModelProperty(value = "登录名",name = "loginName",dataType = "String")
    private String loginName;

    @ApiModelProperty(value = "用户名",name = "userName",dataType = "String")
    private String userName;

    @ApiModelProperty(value = "密码",name = "password",dataType = "String")
    private String passWord;

    @ApiModelProperty(value = "邮箱",name = "email",dataType = "String")
    private String email;

    @ApiModelProperty(value = "区号",name = "phoneCode",dataType = "String")
    private String phoneCode;

    @ApiModelProperty(value = "手机号",name = "phone",dataType = "String")
    private String phone;

    @ApiModelProperty(value = "备注",name = "remark",dataType = "String")
    private String remark;

    @ApiModelProperty(value = "国家",name = "country",dataType = "Integer")
    private Integer country;

    @ApiModelProperty(value = "省/州",name = "province",dataType = "Integer")
    private Integer province;

    @ApiModelProperty(value = "市",name = "city",dataType = "Integer")
    private Integer city;

    @ApiModelProperty(value = "部门id",name = "departmentId",dataType = "Integer")
    private Integer departmentId;

    @ApiModelProperty(value = "头像地址",name = "imageSite",dataType = "String")
    private String imageSite;

    @ApiModelProperty(value = "职位",name = "jobNames",dataType = "String")
    private String jobNames;

    @ApiModelProperty(value = "绑定类型",name = "binds",dataType = "String")
    private String binds;
    @ApiModelProperty(value = "角色",name = "roles",dataType = "String")
    private String roles;

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

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getCountry() {
        return country;
    }

    public void setCountry(Integer country) {
        this.country = country;
    }

    public Integer getProvince() {
        return province;
    }

    public void setProvince(Integer province) {
        this.province = province;
    }

    public Integer getCity() {
        return city;
    }

    public void setCity(Integer city) {
        this.city = city;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getImageSite() {
        return imageSite;
    }

    public void setImageSite(String imageSite) {
        this.imageSite = imageSite;
    }

    public String getJobNames() {
        return jobNames;
    }

    public void setJobNames(String jobNames) {
        this.jobNames = jobNames;
    }

    public String getBinds() {
        return binds;
    }

    public void setBinds(String binds) {
        this.binds = binds;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
