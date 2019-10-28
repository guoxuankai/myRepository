package com.brandslink.cloud.finance.pojo.vo.QuoteConfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: zhangjinhua
 * @Date: 2019/9/4 14:26
 */
@Data
@ApiModel(value = "EffectiveConfigVo")
public class EffectiveConfigVo {
    @ApiModelProperty(value = "id")
    Integer id;

    @ApiModelProperty(value = "生效日期")
    String effectiveDate;

    @ApiModelProperty("配置类型")
    private Integer configType;

    @ApiModelProperty(value = "生效人", hidden = true)
    private String updateBy;

}
