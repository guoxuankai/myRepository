package com.brandslink.cloud.finance.pojo.dto.CustomerConfig;

import com.brandslink.cloud.finance.pojo.base.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: zhangjinhua
 * @Date: 2019/8/21 15:22
 */
@Data
@ApiModel(value = "QueryCustomerConfigDto")
public class QueryCustomerConfigDto extends BaseVO {
    @ApiModelProperty(value = "主键标识")
    private Integer id;
    @ApiModelProperty(value = "版本号")
    private String version;
    @ApiModelProperty(value = "客户code")
    private String customerCode;
    @ApiModelProperty(value = "客户名称")
    private String customerName;
    @ApiModelProperty(value = "状态")
    Integer customerState;
    @ApiModelProperty(value = "账户余额阈值")
    private BigDecimal thresholdMoney;
    @ApiModelProperty(value = "存储费(百分比)")
    private Double storageFee;
    @ApiModelProperty(value = "入库费(百分比)")
    private Double instockFee;
    @ApiModelProperty(value = "出库费")
    private Double outstockFee;
    @ApiModelProperty(value = "更新时间/生效时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


}
