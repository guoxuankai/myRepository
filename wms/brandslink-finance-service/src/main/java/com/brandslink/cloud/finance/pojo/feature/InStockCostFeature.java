package com.brandslink.cloud.finance.pojo.feature;

import com.brandslink.cloud.finance.pojo.base.BaseFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangzefei
 * @Classname InStockCostFeature
 * @Description 入库费特性
 * @Date 2019/8/28 15:10
 */
@Data
@ApiModel(value = "入库费特性")
public class InStockCostFeature extends BaseFeature {
    @ApiModelProperty(value = "质检方式 2:免检,6:抽检,7:全检")
    private Integer qcType;
    @ApiModelProperty(value = "质检费用")
    private BigDecimal qcCost;
    @ApiModelProperty(value = "上架费用")
    private BigDecimal shelveCost;

    public InStockCostFeature(Integer qcType){
        this.qcType=qcType;
        this.qcCost=BigDecimal.ZERO;
        this.shelveCost=BigDecimal.ZERO;
    }

    /**
     * 增加
     * @param qcCost
     * @param shelveCost
     */
    public void add(BigDecimal qcCost,BigDecimal shelveCost){
        this.qcCost=this.qcCost.add(qcCost);
        this.shelveCost=this.shelveCost.add(shelveCost);
    }
}
