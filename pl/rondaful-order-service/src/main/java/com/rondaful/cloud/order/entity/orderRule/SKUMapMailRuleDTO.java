package com.rondaful.cloud.order.entity.orderRule;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@ApiModel(value ="SKUMapMailRuleDTO")
public class SKUMapMailRuleDTO implements Serializable {

    @ApiModelProperty(value = "ebay\"/\"amazon\"/\"aliexpress")
    private String platform;

    @ApiModelProperty(value = "{\"platformSKU0\":\"pinlianSKU0\",\"platformSKU1\":\"pinlianSKU1\"}")
    private Map<String, String> skuRelationMap;

    @ApiModelProperty(value = "卖家店铺ID（授权ID）")
    private Integer empowerID;

    private static final long serialVersionUID = 1L;
}
