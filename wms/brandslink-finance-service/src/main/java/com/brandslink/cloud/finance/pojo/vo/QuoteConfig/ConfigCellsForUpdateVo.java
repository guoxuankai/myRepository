package com.brandslink.cloud.finance.pojo.vo.QuoteConfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: zhangjinhua
 * @Date: 2019/9/3 9:50
 */
@Data
@ApiModel("ConfigCellsForUpdateVo")
public class ConfigCellsForUpdateVo {


    @ApiModelProperty(value = "主键标识")
    private Integer id;

    @ApiModelProperty(value = "财务配置行ID")
    private Integer rowId;

    @ApiModelProperty(value = "财务配置ID")
    private Integer configId;
    @ApiModelProperty(value = "单元格名称")
    private String cellsName;
    @ApiModelProperty(value = "配置项类型1:重量(kg),2:长(cm),3:宽(cm),4:高(cm), 10:存储费库龄(天),15:卸货费配置(kg),20:操作费,21:打包费,30-35:入库操作费,40:盘点费,50-56:增值费")
    private String cellsType;

    @ApiModelProperty(value = "计费单位 1:元/包裹，2:元/件(该字段只针对打包费)")
    private String cellsUnit;


    @ApiModelProperty(value = "区间开始值")
    private String startValue;

    @ApiModelProperty(value = "区间结束值")
    private String endValue;
    @ApiModelProperty(value = "创建人", hidden = true)
    private String createBy;
    @ApiModelProperty(value = "创建时间", hidden = true)
    private String createTime;

}
