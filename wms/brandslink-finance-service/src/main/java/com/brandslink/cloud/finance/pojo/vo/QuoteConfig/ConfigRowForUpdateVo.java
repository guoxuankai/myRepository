package com.brandslink.cloud.finance.pojo.vo.QuoteConfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: zhangjinhua
 * @Date: 2019/9/3 14:52
 */
@Data
@ApiModel("ConfigRowForUpdateVo")
public class ConfigRowForUpdateVo {
    @ApiModelProperty(value = "主键标识")
    private Integer id;

    @ApiModelProperty(value = "财务配置ID")
    private Integer configId;

    @ApiModelProperty("排序位置")
    private Integer rowIndex;
    @ApiModelProperty(value = "货型分类(分组名称)")
    private String rowName;
    @ApiModelProperty(value = "创建人", hidden = true)
    private String createBy;
    @ApiModelProperty(value = "创建时间", hidden = true)
    private String createTime;
}
