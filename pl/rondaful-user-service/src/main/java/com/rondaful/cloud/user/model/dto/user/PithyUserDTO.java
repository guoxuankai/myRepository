package com.rondaful.cloud.user.model.dto.user;

import com.rondaful.cloud.user.model.dto.role.BindRoleDTO;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/28
 * @Description:
 */
public class PithyUserDTO implements Serializable {
    private static final long serialVersionUID = -4688939148672071927L;

    @ApiModelProperty(value = "")
    private Integer id;

    @ApiModelProperty(value = "登录名",name = "loginName",dataType = "String")
    private String loginName;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "职位")
    private List<String> jobNames;

    @ApiModelProperty(value = "角色列表")
    private List<BindRoleDTO> roles;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    @ApiModelProperty(value = "组织名")
    private String orgs;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "绑定类型")
    private List<UserOrgDTO> binds;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getJobNames() {
        return jobNames;
    }

    public void setJobNames(List<String> jobNames) {
        this.jobNames = jobNames;
    }

    public List<BindRoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<BindRoleDTO> roles) {
        this.roles = roles;
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

    public String getOrgs() {
        return orgs;
    }

    public void setOrgs(String orgs) {
        this.orgs = orgs;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
