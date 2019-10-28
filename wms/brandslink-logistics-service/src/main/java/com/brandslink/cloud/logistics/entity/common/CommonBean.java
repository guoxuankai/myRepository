package com.brandslink.cloud.logistics.entity.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("CommonBean")
public class CommonBean implements Serializable {

    @ApiModelProperty(value = "对象名称")
    private String name;

    @ApiModelProperty(value = "对象编码")
    private String code;

    @ApiModelProperty(value = "对象类型")
    private String type;
}
