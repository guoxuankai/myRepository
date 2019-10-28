package com.rondaful.cloud.user.model.dto.user;

import cn.afterturn.easypoi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: xqq
 * @Date: 2019/7/24
 * @Description:
 */
public class SellerUserExportDTO implements Serializable {
    private static final long serialVersionUID = 7147936288887978821L;

    @Excel(name = "账号")
    private String name;
    @Excel(name = "邮箱",width = 20.00D,height = 8.0D)
    private String email;
    @Excel(name = "电话")
    private String phone;
    @Excel(name = "角色")
    private String role;
    @Excel(name = "部门")
    private String department;
    @Excel(name = "职位")
    private String job;
    @Excel(name = "创建时间",databaseFormat = "yyyy/MM/dd",format = "yyyy/MM/dd")
    private Date createDate;
    @Excel(name = "绑定平台店铺(ebay)")
    private String ebay;
    @Excel(name = "绑定平台店铺(amazon)")
    private String amazon;
    @Excel(name = "绑定平台店铺(aliexpress)")
    private String aliexpress;
    @Excel(name = "绑定平台店铺(other)")
    private String other;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getEbay() {
        return ebay;
    }

    public void setEbay(String ebay) {
        this.ebay = ebay;
    }

    public String getAmazon() {
        return amazon;
    }

    public void setAmazon(String amazon) {
        this.amazon = amazon;
    }

    public String getAliexpress() {
        return aliexpress;
    }

    public void setAliexpress(String aliexpress) {
        this.aliexpress = aliexpress;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
}
