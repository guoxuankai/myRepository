package com.brandslink.cloud.finance.pojo.feature;

import com.brandslink.cloud.finance.pojo.base.BaseFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangzefei
 * @Classname OutStockCostFeature
 * @Description 出库费特性
 * @Date 2019/8/29 9:22
 */
@Data
@ApiModel(value = "出库费特性")
public class OutStockCostFeature extends BaseFeature {
    @ApiModelProperty(value = "平台订单号")
    private String platformOrderNo;
    @ApiModelProperty(value = "订单类型 4:B2C出库,5:非B2C出库")
    private Integer orderType;
    @ApiModelProperty(value = "操作费")
    private BigDecimal operateCost;
    @ApiModelProperty(value = "打包费")
    private BigDecimal packCost;

    public OutStockCostFeature(Integer orderType,String platformOrderNo){
        this.platformOrderNo=platformOrderNo;
        this.orderType=orderType;
        this.operateCost=BigDecimal.ZERO;
        this.packCost=BigDecimal.ZERO;
    }

    public void add(BigDecimal operateCost,BigDecimal packCost){
        this.operateCost=this.operateCost.add(operateCost);
        this.packCost=this.packCost.add(packCost);
    }

}
