package com.brandslink.cloud.finance.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangzefei
 * @Classname OutStockCostVo
 * @Description 出库参数模型
 * @Date 2019/9/5 13:42
 */
@Data
public class OutStockCostVo extends StockCostVo {
    @ApiModelProperty(value = "订单类型 4:B2C出库,5:非B2C出库")
    private Integer orderType;

    @ApiModelProperty(value = "平台订单号")
    private String platformOrderNo;
}
