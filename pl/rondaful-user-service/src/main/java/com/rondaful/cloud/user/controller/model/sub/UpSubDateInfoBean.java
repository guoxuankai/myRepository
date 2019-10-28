package com.rondaful.cloud.user.controller.model.sub;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

@ApiModel(value = "子级账号资料修改")
public class UpSubDateInfoBean implements Serializable {
    private static final long serialVersionUID = -7875483843948136171L;

            @ApiModelProperty(name = "userid", value = "注：需要根据子账号的id进行修改", dataType = "Integer",required = true)
            private Integer userid;
            @ApiModelProperty(name = "username", value = "账号名称", dataType = "string",required = false)
            private String username;
            @ApiModelProperty(name = "password", value = "登录密码", dataType = "string",required = false)
            private String password;
            @ApiModelProperty(name = "mobile", value = "座机", dataType = "string", required = false)
            private String mobile;
            @ApiModelProperty(name = "phone", value = "手机", dataType = "string", required = false)
            private String phone;
            @ApiModelProperty(name = "email", value = "邮箱", dataType = "string", required = false)
            private String email;
            @ApiModelProperty(name = "status", value = "账号状态:1启动  0停用", dataType = "string",required = false)
            private String status;
            @ApiModelProperty(name = "roleIds",value = "角色id",required = false)
            private List<Integer> roleIds;

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
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
        if (StringUtils.isNotBlank(status)){
            return Integer.parseInt(status);
        }else{
            return null;
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
    }
}
