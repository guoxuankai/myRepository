package com.brandslink.cloud.finance.pojo.dto.CustomerConfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: zhangjinhua
 * @Date: 2019/8/22 9:16
 */
@Data
@ApiModel(value = "SelectCustomerDto")
public class SelectCustomerDto {
    @ApiModelProperty(value = "客户Id")
    private Integer customerId;
    @ApiModelProperty(value = "客户code")
    private Integer customerCode;

    @ApiModelProperty(value = "客户名称")
    private String customerName;


}
