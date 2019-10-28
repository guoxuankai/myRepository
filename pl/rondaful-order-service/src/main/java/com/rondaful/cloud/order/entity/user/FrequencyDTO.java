package com.rondaful.cloud.order.entity.user;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 第三方APP请求次数限制
 *
 * @author Blade
 * @date 2019-07-09 18:19:13
 **/
public class FrequencyDTO implements Serializable {
    private static final long serialVersionUID = -6777270627845770687L;

    @ApiModelProperty(value = "请求次数")
    private String frequencyAstrict;

    @ApiModelProperty(value = "请求的地址")
    private String routeURL;
}
