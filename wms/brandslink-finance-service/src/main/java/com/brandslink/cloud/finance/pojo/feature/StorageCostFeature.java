package com.brandslink.cloud.finance.pojo.feature;

import com.brandslink.cloud.finance.pojo.base.BaseFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangzefei
 * @Classname StorageCostFeature
 * @Description 存储费特性
 * @Date 2019/8/28 14:33
 */
@Data
@ApiModel(value = "存储费特性")
public class StorageCostFeature extends BaseFeature {
    @ApiModelProperty(value = "上期结余")
    private Integer oldBalance;
    @ApiModelProperty(value = "今日入库")
    private Integer inStock;
    @ApiModelProperty(value = "今日出库")
    private Integer outStock;
}
