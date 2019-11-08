package com.brandslink.cloud.common.entity.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 登录成功返回DTO
 *
 * @ClassName LoginSuccessResponseDTO
 * @Author tianye
 * @Date 2019/6/20 10:55
 * @Version 1.0
 */
@ApiModel(value = "登录成功返回DTO")
public class LoginSuccessResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "账号")
    private String account;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "token")
    private String token;

    @ApiModelProperty(value = "platformType")
    private String platformType;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }
}
