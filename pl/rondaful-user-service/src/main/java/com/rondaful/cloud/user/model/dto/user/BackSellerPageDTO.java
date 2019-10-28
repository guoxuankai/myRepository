package com.rondaful.cloud.user.model.dto.user;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/29
 * @Description:
 */
public class BackSellerPageDTO implements Serializable {
    private static final long serialVersionUID = -4562309485007700669L;

    @ApiModelProperty(value = "")
    private Integer id;

    @ApiModelProperty(value = "供应链公司")
    private String supplyChainCompany;

    @ApiModelProperty(value = "供应链公司名称")
    private String supplyChainCompanyName;

    @ApiModelProperty(value = "公司名称")
    private String companyCame;

    @ApiModelProperty(value = "登录名")
    private String loginName;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "手机")
    private String phone;

    @ApiModelProperty(value = "账号状态 1 启用  0 停用")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    @ApiModelProperty(value = "修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

    @ApiModelProperty(value = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "店铺授信额度")
    private StoreAccountDTO storeAccount;

    @ApiModelProperty(value = "卖家类型  1:个人卖家  2企业卖家")
    private Integer sellerType;

    @ApiModelProperty(value = "审核人")
    private String authBy;

    @ApiModelProperty(value = "审核时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date authDate;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "注册来源")
    private String registerSource;

    public StoreAccountDTO getStoreAccount() {
        return storeAccount;
    }

    public void setStoreAccount(StoreAccountDTO storeAccount) {
        this.storeAccount = storeAccount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSupplyChainCompany() {
        return supplyChainCompany;
    }

    public void setSupplyChainCompany(String supplyChainCompany) {
        this.supplyChainCompany = supplyChainCompany;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getCompanyCame() {
        return companyCame;
    }

    public void setCompanyCame(String companyCame) {
        this.companyCame = companyCame;
    }

    public String getSupplyChainCompanyName() {
        return supplyChainCompanyName;
    }

    public void setSupplyChainCompanyName(String supplyChainCompanyName) {
        this.supplyChainCompanyName = supplyChainCompanyName;
    }

    public Integer getSellerType() {
        return sellerType;
    }

    public void setSellerType(Integer sellerType) {
        this.sellerType = sellerType;
    }

    public String getAuthBy() {
        return authBy;
    }

    public void setAuthBy(String authBy) {
        this.authBy = authBy;
    }

    public Date getAuthDate() {
        return authDate;
    }

    public void setAuthDate(Date authDate) {
        this.authDate = authDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegisterSource() {
        return registerSource;
    }

    public void setRegisterSource(String registerSource) {
        this.registerSource = registerSource;
    }
}
