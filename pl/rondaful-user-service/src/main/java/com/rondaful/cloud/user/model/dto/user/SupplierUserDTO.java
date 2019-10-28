package com.rondaful.cloud.user.model.dto.user;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/5/2
 * @Description:
 */
public class SupplierUserDTO implements Serializable {
    private static final long serialVersionUID = -7135782030922817451L;

    @ApiModelProperty(value = "")
    private Integer id;

    @ApiModelProperty(value = "顶级账号id")
    private Integer topUserId;

    @ApiModelProperty(value = "账号上一级父id")
    private Integer parentId;

    @ApiModelProperty(value = "登录名")
    private String loginName;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "供应链公司")
    private String supplyChainCompany;

    @ApiModelProperty(value = "供应链公司名称")
    private String supplyChainCompanyName;

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

    @ApiModelProperty(value = "结算周期   1：周结  2：半月结  3：月结")
    private String closedCircle;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "新增时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    @ApiModelProperty(value = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

    @ApiModelProperty(value = "最大商品数  -1  不设限")
    private Integer maxCommodity;

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
    private List<UserOrgDTO> binds;

    @ApiModelProperty(value = "角色")
    private List<Integer> roleIds;

    @ApiModelProperty(value = "职位")
    private List<String> jobs;

    private List<Integer> menuIds;

    private String recommend;

    @ApiModelProperty(value = "推荐人")
    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTopUserId() {
        return topUserId;
    }

    public void setTopUserId(Integer topUserId) {
        this.topUserId = topUserId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
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

    public String getClosedCircle() {
        return closedCircle;
    }

    public void setClosedCircle(String closedCircle) {
        this.closedCircle = closedCircle;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getMaxCommodity() {
        return maxCommodity;
    }

    public void setMaxCommodity(Integer maxCommodity) {
        this.maxCommodity = maxCommodity;
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

    public List<UserOrgDTO> getBinds() {
        return binds;
    }

    public void setBinds(List<UserOrgDTO> binds) {
        this.binds = binds;
    }

    public List<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
    }

    public List<String> getJobs() {
        return jobs;
    }

    public void setJobs(List<String> jobs) {
        this.jobs = jobs;
    }

    public List<Integer> getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(List<Integer> menuIds) {
        this.menuIds = menuIds;
    }

    public String getSupplyChainCompanyName() {
        return supplyChainCompanyName;
    }

    public void setSupplyChainCompanyName(String supplyChainCompanyName) {
        this.supplyChainCompanyName = supplyChainCompanyName;
    }
}
