package com.rondaful.cloud.transorder.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

@ApiModel(description = "平台账户信息分装对象")
public class PlatformAccount implements Serializable {

    @ApiModelProperty(value = "所属平台名称[Amazon, eBay, wish, aliexpress]")
    private String platform;

    @ApiModelProperty(value = "平台下的账户列表和站点的拼接[ 账号#split#站点 ]")
    private List<String> accounts;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public List<String> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<String> accounts) {
        this.accounts = accounts;
    }
}
