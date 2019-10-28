package com.rondaful.cloud.order.entity.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统配置表
 * 实体类对应的数据表为：  tb_sys_config
 * @author lijt
 * @date 2019-09-06 10:20:20
 */
@ApiModel(value ="SysConfig")
@Data
public class SysConfig implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "键")
    private String key;

    @ApiModelProperty(value = "值")
    private String value;

    @ApiModelProperty(value = "描述")
    private String describe;
}