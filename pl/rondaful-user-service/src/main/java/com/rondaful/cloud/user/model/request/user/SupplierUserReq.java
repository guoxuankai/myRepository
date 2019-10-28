package com.rondaful.cloud.user.model.request.user;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: xqq
 * @Date: 2019/5/4
 * @Description:
 */
public class SupplierUserReq implements Serializable {
    private static final long serialVersionUID = 3659854906323960022L;

    @ApiModelProperty(value = "")
    private Integer userId;

    @ApiModelProperty(value = "登录名")
    private String loginName;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "供应链公司")
    private String supplyChainCompany;

    @ApiModelProperty(value = "公司名称")
    private String companyNameUser;

    @ApiModelProperty(value = "手机区号")
    private String phoneCode;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "密码")
    private String passWord;

    @ApiModelProperty(value = "电话")
    private String mobile;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "账号状态 1 启用  0 停用")
    private Integer status;

    @ApiModelProperty(value = "国家")
    private Integer country;

    @ApiModelProperty(value = "省/州")
    private Integer province;

    @ApiModelProperty(value = "城市")
    private Integer city;

    @ApiModelProperty(value = "部门id")
    private Integer departmentId;

    @ApiModelProperty(value = "职位")
    private String jobName;

    @ApiModelProperty(value = "结算周期   1：周结  2：半月结  3：月结 4:实时结算 5:周期结算 ")
    private String closedCircle;

    @ApiModelProperty(value = "联系地址")
    private String address;

    @ApiModelProperty(value = "头像地址")
    private String imageSite;

    @ApiModelProperty(value = "")
    private String qq;

    @ApiModelProperty(value = "联系人邮编")
    private String postCode;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "绑定类型")
    private String binds;

    @ApiModelProperty(value = "角色")
    private String roles;

    @ApiModelProperty(value = "职位")
    private String jobNames;

    @ApiModelProperty(value = "顶级账号id")
    private Integer topUserId;

    @ApiModelProperty(value = "最大商品数  -1  不设限")
    private Integer maxCommodity;

    @ApiModelProperty(value = "结算比例")
    private String proportion;

    @ApiModelProperty(value = "推荐人")
    private String recommend;

    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }

    public Integer getTopUserId() {
        return topUserId;
    }

    public void setTopUserId(Integer topUserId) {
        this.topUserId = topUserId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public String getSupplyChainCompany() {
        return supplyChainCompany;
    }

    public void setSupplyChainCompany(String supplyChainCompany) {
        this.supplyChainCompany = supplyChainCompany;
    }

    public String getCompanyNameUser() {
        return companyNameUser;
    }

    public void setCompanyNameUser(String companyNameUser) {
        this.companyNameUser = companyNameUser;
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

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getClosedCircle() {
        return closedCircle;
    }

    public void setClosedCircle(String closedCircle) {
        this.closedCircle = closedCircle;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageSite() {
        return imageSite;
    }

    public void setImageSite(String imageSite) {
        this.imageSite = imageSite;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBinds() {
        return binds;
    }

    public void setBinds(String binds) {
        this.binds = binds;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getJobNames() {
        return jobNames;
    }

    public void setJobNames(String jobNames) {
        this.jobNames = jobNames;
    }

    public Integer getMaxCommodity() {
        return maxCommodity;
    }

    public void setMaxCommodity(Integer maxCommodity) {
        this.maxCommodity = maxCommodity;
    }

    public String getProportion() {
        return proportion;
    }

    public void setProportion(String proportion) {
        this.proportion = proportion;
    }
}
