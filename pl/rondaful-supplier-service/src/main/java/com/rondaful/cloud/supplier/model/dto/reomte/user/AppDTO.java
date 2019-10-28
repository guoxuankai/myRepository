package com.rondaful.cloud.supplier.model.dto.reomte.user;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/7/2
 * @Description:
 */
public class AppDTO implements Serializable {
    private static final long serialVersionUID = -2355172539056831039L;

    private Integer id;

    @ApiModelProperty(value = "第三方应用名")
    private String appName;

    @ApiModelProperty(value = "appkey")
    private String appKey;

    @ApiModelProperty(value = "apptoken")
    private String appToken;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "单位:  /s")
    private List<FrequencyDTO> frequencyAstricts;

    @ApiModelProperty(value = "ip白名单")
    private List<String> ips;
    
    @ApiModelProperty(value = "回调地址")
    private String roleBack;


	public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<FrequencyDTO> getFrequencyAstricts() {
        return frequencyAstricts;
    }

    public void setFrequencyAstricts(List<FrequencyDTO> frequencyAstricts) {
        this.frequencyAstricts = frequencyAstricts;
    }

    public List<String> getIps() {
        return ips;
    }

    public void setIps(List<String> ips) {
        this.ips = ips;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getRoleBack() {
        return roleBack;
    }

    public void setRoleBack(String roleBack) {
        this.roleBack = roleBack;
    }
}
