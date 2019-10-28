package com.brandslink.cloud.user.dto.response;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 开发者中心信息dto
 *
 * @ClassName DeveloperCentreInfoResponseDTO
 * @Author tianye
 * @Date 2019/9/9 15:33
 * @Version 1.0
 */
public class DeveloperCentreInfoResponseDTO implements Serializable {

    @ApiModelProperty(value = "客户全称")
    private String name;

    @ApiModelProperty(value = "客户id")
    private String customerId;

    @ApiModelProperty(value = "token")
    private String token;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
