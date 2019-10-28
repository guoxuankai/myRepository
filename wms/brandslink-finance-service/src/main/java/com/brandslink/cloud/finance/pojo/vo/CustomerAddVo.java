package com.brandslink.cloud.finance.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangzefei
 * @Classname CustomerAddVo
 * @Description 新增客户模型
 * @Date 2019/9/7 13:44
 */
@Data
public class CustomerAddVo {

    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @ApiModelProperty(value = "客户名称")
    private String customerName;
}
