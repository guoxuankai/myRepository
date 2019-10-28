package com.brandslink.cloud.finance.pojo.vo;

import com.brandslink.cloud.finance.pojo.feature.LogisticsCostFeature;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangzefei
 * @Classname LogisticsCostVo
 * @Description 物流商运费计算模型
 * @Date 2019/9/7 9:51
 */
@Data
public class LogisticsCostVo extends BaseCostVo {

    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @ApiModelProperty(value = "客户名称",hidden = true)
    private String customerName;

    @ApiModelProperty(value = "物流商运费特性")
    private LogisticsCostFeature feature;
}
