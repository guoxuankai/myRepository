package com.brandslink.cloud.finance.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 财务报价
 * 实体类对应的数据表为：  tf_sys_quote
 *
 * @author guoxuankai
 * @date 2019-08-19 10:45:56
 */
@Data
@ApiModel(value = "财务报价")
public class StandardQuote implements Serializable {
    @ApiModelProperty(value = "主键标识")
    private Integer id;

    @ApiModelProperty(value = "配置ID")
    private Integer configId;

    @ApiModelProperty(value = "版本号")
    private String version;

    @ApiModelProperty(value = "报价类型 1:仓储费,2:入库作业费,3:出库作业费,4:库内作业费")
    private Integer quoteType;

    @ApiModelProperty(value = "配置状态 1:待提交,2:待生效,3:已生效,4:已失效")
    private Integer quoteStatus;

    @ApiModelProperty(value = "提交时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    @ApiModelProperty(value = "更新/生效 人")
    private String updateBy;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "更新时间/生效时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private static final long serialVersionUID = 1L;


}