package com.brandslink.cloud.finance.pojo.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangzefei
 * @Classname BaseFeature
 * @Description 费用特性
 * @Date 2019/8/28 14:48
 */
@Data
@ApiModel(value = "费用特性")
public class BaseFeature {

    @ApiModelProperty(value = "报价ID")
    private Integer quoteId;

    public BaseFeature(){}
    public BaseFeature(Integer quoteId){
        this.quoteId=quoteId;
    }

}
