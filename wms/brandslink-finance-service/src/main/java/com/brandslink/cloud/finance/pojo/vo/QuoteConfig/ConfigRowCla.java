package com.brandslink.cloud.finance.pojo.vo.QuoteConfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: zhangjinhua
 * @Date: 2019/9/3 9:48
 */
@Data

@ApiModel(value = "ConfigRowCla")
public class ConfigRowCla {


    @ApiModelProperty(value = "主键标识", hidden = true)
    private Integer id;

    @ApiModelProperty(value = "财务配置ID", hidden = true)
    private Integer configId;

    @ApiModelProperty(value = "货型分类(分组名称)")
    private String rowName;
    @ApiModelProperty("排序位置")
    private Integer rowIndex;
    @ApiModelProperty(value = "创建人", hidden = true)
    private String createBy;
    @ApiModelProperty(value = "创建时间", hidden = true)
    private String createTime;
    @ApiModelProperty(value = "配置详情信息(配置类型为非商品类型时传)")
    List<ConfigCellsCla> cellsList;
}
