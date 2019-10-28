package com.rondaful.cloud.order.entity.user;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 第三方APP验证信息
 *
 * @author Blade
 * @date 2019-07-09 18:18:43
 **/
public class ThirdAppDTO implements Serializable {

    private static final long serialVersionUID = 3556202266815039794L;
    private Integer id;

    @ApiModelProperty(value = "第三方应用名, 目前是主账号的登录账号")
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

    @ApiModelProperty(value = "版本号")
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    @Override
    public String toString() {
        return FastJsonUtils.toJsonString(this);
    }
}
