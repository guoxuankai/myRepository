package com.brandslink.cloud.finance.pojo.vo.CustomerConfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: zhangjinhua
 * @Date: 2019/8/29 11:12
 */
@Data
@ApiModel(value = "EditorCustomerVo")
public class EffectiveCstomerVo {
    @ApiModelProperty(value = "id")
    Integer id;

    @ApiModelProperty(value = "客户Code，关联tf_customer")
    private String customerCode;

    @ApiModelProperty(value = "生效日期")
    String effectiveDate;

    @ApiModelProperty(value = "更新人", hidden = true)
    private String updateBy;


}
