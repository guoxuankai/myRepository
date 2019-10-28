package com.rondaful.cloud.transorder.entity.commodity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "CodeAndValueVo")
public class CodeAndValueVo {

    @ApiModelProperty(value = "code")
    private String code;

    @ApiModelProperty(value = "value")
    private String value;
}