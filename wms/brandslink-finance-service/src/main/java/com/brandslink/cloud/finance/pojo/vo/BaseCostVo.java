package com.brandslink.cloud.finance.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangzefei
 * @Classname BaseCostVo
 * @Description 费用计算基础模型
 * @Date 2019/9/6 10:19
 */
@Data
public class BaseCostVo {
    @ApiModelProperty(value = "来源单号")
    private String sourceNo;
    @ApiModelProperty(value = "运单号")
    private String waybillNo;
    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;
    @ApiModelProperty(value = "仓库名称",hidden = true)
    private String warehouseName;
}
