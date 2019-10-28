package com.brandslink.cloud.finance.pojo.feature;

import com.brandslink.cloud.finance.pojo.base.BaseFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangzefei
 * @Classname RechargeFeature
 * @Description 充值特性
 * @Date 2019/8/29 9:34
 */
@Data
@ApiModel(value = "充值特性")
public class RechargeFeature extends BaseFeature {

    @ApiModelProperty(value = "凭证图片地址")
    private String certificateUrl;
}
