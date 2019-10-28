package com.brandslink.cloud.finance.pojo.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 财务配置
 * 实体类对应的数据表为：  tf_sys_config
 *
 * @author yangzefei
 * @date 2019-08-29 17:24:02
 */
@Data
@ApiModel(value = "QuoteConfig")
public class QuoteConfig implements Serializable {
    @ApiModelProperty(value = "主键标识")
    private Integer id;

    @ApiModelProperty(value = "版本号")
    private String version;

    @ApiModelProperty(value = "配置类型 1:商品货型,2:仓储费库龄配置,3:卸货费配置,4:打包费配置")
    private Byte configType;

    @ApiModelProperty(value = "配置状态 1:待提交,2:待生效,3:已生效,4:已失效")
    private Byte configStatus;

    @ApiModelProperty(value = "提交时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    @ApiModelProperty(value = "更新/生效 人")
    private String updateBy;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "更新时间/生效时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


}