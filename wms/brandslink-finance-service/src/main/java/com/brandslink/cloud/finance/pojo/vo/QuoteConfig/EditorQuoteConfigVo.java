package com.brandslink.cloud.finance.pojo.vo.QuoteConfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: zhangjinhua
 * @Date: 2019/9/3 14:49
 */
@Data
@ApiModel(value = "EditorQuoteConfigVo")
public class EditorQuoteConfigVo {
    @ApiModelProperty(value = "版本号")
    private String version;
    @ApiModelProperty(value = "行信息")
    List<ConfigRowForUpdateVo> rowList;
    @ApiModelProperty(value = "配置详情信息")
    List<ConfigCellsForUpdateVo> cellsList;
}
