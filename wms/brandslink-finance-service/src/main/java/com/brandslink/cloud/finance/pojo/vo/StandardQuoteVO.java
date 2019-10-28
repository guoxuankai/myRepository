package com.brandslink.cloud.finance.pojo.vo;

import com.brandslink.cloud.finance.pojo.base.BaseVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 财务报价
 * 实体类对应的数据表为：  tf_sys_quote
 *
 * @author guoxuankai
 * @date 2019-08-19 10:45:56
 */
@ApiModel(value = "财务报价查询实体")
@Data
public class StandardQuoteVO extends BaseVO {

    @ApiModelProperty(value = "版本号")
    private String version;

    @ApiModelProperty(value = "报价类型 1:仓储费,2:入库作业费,3:出库作业费,4:库内作业费")
    private Integer quoteType;

    @ApiModelProperty(value = "配置状态 1:待提交,2:待生效,3:已生效,4:已失效")
    private Integer quoteStatus;

    @ApiModelProperty(value = "生效起始时间,yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startEffectTime;

    @ApiModelProperty(value = "生效结束时间,yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endEffectTime;

    @ApiModelProperty(value = "创建起始时间,yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startCreateTime;

    @ApiModelProperty(value = "创建结束时间,yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endCreateTime;

    @ApiModelProperty(value = "提交起始时间,yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startSubmitTime;

    @ApiModelProperty(value = "提交结束时间,yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endSubmitTime;



}