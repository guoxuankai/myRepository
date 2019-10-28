package com.rondaful.cloud.user.controller.model.sub;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.util.List;

@ApiModel(value = "子账号注册")
public class HandleRegBean implements Serializable {
    private static final long serialVersionUID = -1404836326207947986L;

            @ApiModelProperty(name = "parentId", value = "创建账户者id", dataType = "Integer",required = true)
            private Integer parentId;
            @ApiModelProperty(name = "username", value = "用户名", dataType = "string",required = true)
            private String username;
            @ApiModelProperty(name = "password", value = "密码", dataType = "string",required = true)
            private String password;
            @ApiModelProperty(name = "linkman", value = "联系人", dataType = "string", required = false)
            private String linkman;
            @ApiModelProperty(name = "mobile", value = "座机", dataType = "string", required = false)
            private String mobile;
            @ApiModelProperty(name = "phone", value = "手机", dataType = "string",  required = false)
            private String phone;
            @ApiModelProperty(name = "email", value = "邮箱", dataType = "string",  required = false)
            private String email;
            @ApiModelProperty(name = "status", value = "账户状态", dataType = "Integer",  required = false)
            private Integer status;
            @ApiModelProperty(name = "platformType", value = "平台类型", dataType = "Integer",  required = true)
            private Integer platformType;
            @ApiModelProperty(name = "role",value="子账号关联id",required = false)
            private List<Integer> role;

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Integer platformType) {
        this.platformType = platformType;
    }

    public List<Integer> getRole() {
        return role;
    }

    public void setRole(List<Integer> role) {
        this.role = role;
    }
}
