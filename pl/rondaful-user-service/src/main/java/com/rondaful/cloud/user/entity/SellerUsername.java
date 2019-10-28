package com.rondaful.cloud.user.entity;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 管理后台-卖家下拉列表
 */
public class SellerUsername implements Serializable {

    @ApiModelProperty(value = "id", required = false)
    private Integer id;

    @ApiModelProperty(value = "用户账户名称", required = false)
    private String username;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
