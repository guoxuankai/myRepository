package com.example.demo.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "emp")
public class Employee implements Serializable {


    @Id
    @Column(name = "e_id")
    private String id;

    @Column(name = "e_portrait")
    private String portrait;

    @Column(name = "e_name")
    private String name;

    @Column(name = "e_pwd")
    private String pwd;

    @Column(name = "e_uname")
    private String uname;

    @Column(name = "e_birthday")
    private Date birthday;

    @Column(name = "e_level")
    private Integer level;

    @Column(name = "e_six")
    private Integer six;

    @Column(name = "e_wages", nullable = true)
    private Integer wages;

    @Column(name = "e_hobby")
    private String hobby;

    @Column(name = "e_mibiao")
    private String mibiao;

    @Column(name = "e_midaan")
    private String midaan;

    @Column(name = "e_add")
    private Date eAdd;

    @Column(name = "e_update")
    private Date eUpdate;

    @Column(name = "e_login")
    private Date login;

    @Column(name = "e_xzlogin")
    private Date xzlogin;

    @Column(name = "e_remark")
    private String remark;

    @ManyToOne
    @JoinColumn(name = "e_deptid")
    private Department department;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getSix() {
        return six;
    }

    public void setSix(Integer six) {
        this.six = six;
    }

    public Integer getWages() {
        return wages;
    }

    public void setWages(Integer wages) {
        this.wages = wages;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getMibiao() {
        return mibiao;
    }

    public void setMibiao(String mibiao) {
        this.mibiao = mibiao;
    }

    public String getMidaan() {
        return midaan;
    }

    public void setMidaan(String midaan) {
        this.midaan = midaan;
    }

    public Date geteAdd() {
        return eAdd;
    }

    public void seteAdd(Date eAdd) {
        this.eAdd = eAdd;
    }

    public Date geteUpdate() {
        return eUpdate;
    }

    public void seteUpdate(Date eUpdate) {
        this.eUpdate = eUpdate;
    }

    public Date getLogin() {
        return login;
    }

    public void setLogin(Date login) {
        this.login = login;
    }

    public Date getXzlogin() {
        return xzlogin;
    }

    public void setXzlogin(Date xzlogin) {
        this.xzlogin = xzlogin;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }


    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", portrait='" + portrait + '\'' +
                ", name='" + name + '\'' +
                ", pwd='" + pwd + '\'' +
                ", uname='" + uname + '\'' +
                ", birthday=" + birthday +
                ", level=" + level +
                ", six=" + six +
                ", wages=" + wages +
                ", hobby='" + hobby + '\'' +
                ", mibiao='" + mibiao + '\'' +
                ", midaan='" + midaan + '\'' +
                ", eAdd=" + eAdd +
                ", eUpdate=" + eUpdate +
                ", login=" + login +
                ", xzlogin=" + xzlogin +
                ", remark='" + remark + '\'' +
                ", department=" + department +
                '}';
    }
}
