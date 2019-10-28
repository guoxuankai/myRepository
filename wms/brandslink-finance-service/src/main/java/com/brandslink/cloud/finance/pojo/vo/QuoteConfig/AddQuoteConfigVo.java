package com.brandslink.cloud.finance.pojo.vo.QuoteConfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: zhangjinhua
 * @Date: 2019/9/2 13:53
 */
@Data
@ApiModel(value = "AddQuoteConfigVo")
public class AddQuoteConfigVo {
    @ApiModelProperty(value = "主键id",hidden = true)
    private Integer id;
    @ApiModelProperty(value = "版本号",hidden = true)
    private String version;

    @ApiModelProperty(value = "配置类型 1:商品货型,2:仓储费库龄配置,3:卸货费配置,4:打包费配置")
    private Byte configType;

    @ApiModelProperty(value = "配置状态 1:待提交,2:待生效,3:已生效,4:已失效")
    private Byte configStatus;

    @ApiModelProperty(value = "创建人",hidden = true)
    private String createBy;

    @ApiModelProperty(value = "行信息(配置类型为商品类型时传)")
    List<ConfigRowCla> rowList;

    @ApiModelProperty(value = "配置详情信息(配置类型为非商品类型时传)")
    List<ConfigCellsCla> cellsList;



}
