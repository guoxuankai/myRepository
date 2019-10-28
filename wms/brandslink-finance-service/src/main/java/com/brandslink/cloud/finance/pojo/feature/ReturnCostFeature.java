package com.brandslink.cloud.finance.pojo.feature;

import com.brandslink.cloud.finance.pojo.base.BaseFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangzefei
 * @Classname ReturnCostFeature
 * @Description 销退费特性
 * @Date 2019/8/29 9:18
 */
@Data
@ApiModel(value = "销退费特性")
public class ReturnCostFeature extends BaseFeature {
    @ApiModelProperty(value = "上架费用")
    private BigDecimal shelveCost=BigDecimal.ZERO;

    public void add(BigDecimal shelveCost){
        this.shelveCost=this.shelveCost.add(shelveCost);
    }
}
