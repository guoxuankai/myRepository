package com.brandslink.cloud.finance.pojo.entity;

import java.math.BigDecimal;

import com.brandslink.cloud.finance.pojo.base.BaseObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 客户资金流水详情
 * 实体类对应的数据表为：  tf_customer_flow_detail
 * @author yangzefei
 * @date 2019-08-26 09:51:15
 */
@Data
@ApiModel(value ="customerFlowDetail")
public class CustomerFlowDetail extends BaseObject {

    @ApiModelProperty(value = "客户流水id")
    private Integer customerFlowId;

    @ApiModelProperty(value = "商品sku")
    private String sku;

    @ApiModelProperty(value = "sku名称")
    private String skuName;

    @ApiModelProperty(value = "sku类型")
    private String skuType;

    @ApiModelProperty(value = "sku数量")
    private Integer skuNumber;

    @ApiModelProperty(value = "折前变动金额")
    private BigDecimal originalCost;

    @ApiModelProperty(value = "折扣")
    private Double discount;

    @ApiModelProperty(value = "折后变动金额")
    private BigDecimal discountCost;

    @ApiModelProperty(value = "详情类型 1:存储费,2:入库费(免检),3:销退费,4:出库操作费,5:出库打包费,6:入库费(抽检),7:入库费(全检)")
    private Integer detailType;

    @ApiModelProperty(value = "存储不同费用类型的特性字段(json格式)")
    private String featureJson;
}