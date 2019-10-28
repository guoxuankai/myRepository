package com.rondaful.cloud.supplier.model.request.third;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/7/29
 * @Description:
 */
public class ThirdBaseReq implements Serializable {
    private static final long serialVersionUID = -2246697513076751803L;

    @ApiModelProperty(value="系统分发的appkey",name = "appKey",dataType = "String",required = true)
    private String appKey;

    @ApiModelProperty(value="系统分发的apptoken",name = "appToken",dataType = "String",required = true)
    private String appToken;

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
}
