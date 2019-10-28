package com.brandslink.cloud.user.dto.request;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 添加，更新客户端账号传输对象
 * @date 2019/9/4 14:16
 */
public class AddOrUpdateCustomerAccountRequestDTO {

    @ApiModelProperty(value = "主键id")
    private Integer id;

    @ApiModelProperty(value = "账号")
    @NotBlank(message = "账号不能为空")
    @Pattern(regexp= "^[a-zA-Z_0-9]{4,25}+$", message = "账号请输入4-25位字母,数字")
    private String account;

    @ApiModelProperty(value = "账号名称")
    @NotBlank(message = "账号名称不能为空")
    @Pattern(regexp = "[\u4e00-\u9fa5_a-zA-Z0-9]{1,25}", message = "请输入1-25位汉字，英文，数字")
    private String name;

    @ApiModelProperty(value = "账号密码")
    private String password;

    @ApiModelProperty(value = "手机号码")
    private String contactPhone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "联系电话")
    private String contactWay;

    @ApiModelProperty(value = "角色id")
    private List<Integer> roleIds;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactWay() {
        return contactWay;
    }

    public void setContactWay(String contactWay) {
        this.contactWay = contactWay;
    }

    public List<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
    }
}
