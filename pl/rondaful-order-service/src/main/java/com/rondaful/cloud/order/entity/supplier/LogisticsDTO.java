package com.rondaful.cloud.order.entity.supplier;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@ApiModel(value ="LogisticsDTO")
public class LogisticsDTO implements Serializable {
    @ApiModelProperty(value = "")
    private Long id;

    @ApiModelProperty(value = "物流方式简称")
    private String shortName;

    @ApiModelProperty(value = "物流方式类型 默认0 0自营仓库物流")
    private Byte type;

    @ApiModelProperty(value = "物流方式代码")
    private String code;

    @ApiModelProperty(value = "物流商代码")//对应系统订单：shipping_carrier_used_code邮寄方式(货物承运公司)CODE
    private String carrierCode;
    
    @ApiModelProperty(value = "供应商")
    private String supplier;

    @ApiModelProperty(value = "物流商名称")
    private String carrierName;

    @ApiModelProperty(value = "状态 默认0 0停用 1启用")
    private Integer status;

    @ApiModelProperty(value = "所属平台 A供应商平台 B管理后台")
    private Integer sysCode;

    @ApiModelProperty(value = "最后更新人id")
    private Long lastUpdateBy;
    
    @ApiModelProperty(value = "ebay物流商代码")
    private String ebayCarrier;
    
    @ApiModelProperty(value = "amazon物流商代码")
    private String amazonCarrier;

    @ApiModelProperty(value = "amazon物流方式")
    private String amazonCode;

    @ApiModelProperty(value = "速卖通物流方式code")
    private String aliexpressCode;
    
    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;
    
//    @ApiModelProperty(value = "仓库代码")
//    private String warehouseCode;

    @ApiModelProperty(value = "仓库Id")
    private String warehouseId;

    @ApiModelProperty(value = "线上物流类型")
    private String onlineLogistics;

    private static final long serialVersionUID = 1L;

}