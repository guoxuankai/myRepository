package com.brandslink.cloud.finance.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 财务报价详情
 * 实体类对应的数据表为：  tf_sys_quote_cells
 *
 * @author guoxuankai
 * @date 2019-08-19 10:45:56
 */
@ApiModel(value = "StandardQuoteDetail")
@Data
public class StandardQuoteDetail implements Serializable {
    @ApiModelProperty(value = "主键标识")
    private Integer id;

    @ApiModelProperty(value = "列iD,对应tf_sys_config_row的id")
    private Integer rowId;

    @ApiModelProperty(value = "列ID 对应tf_sys_config_celles id")
    private Integer cellsId;

    @ApiModelProperty(value = "财务报价ID")
    private Integer quoteId;

    @ApiModelProperty(value = "财务报价项类型 1:存储费,2:入库卸货费,3:入库操作费,4:B2C出库费,5:非B2C出库费,6:盘点费报价,7:增值费报价")
    private Integer quoteType;

    @ApiModelProperty(value = "报价")
    private BigDecimal quoteValue;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    
    private static final long serialVersionUID = 1L;

}