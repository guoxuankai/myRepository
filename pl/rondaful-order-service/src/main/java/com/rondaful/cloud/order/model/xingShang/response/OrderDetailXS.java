package com.rondaful.cloud.order.model.xingShang.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: 闵功伟
 * @description:
 * @date: 2019/06/03
 */
@Data
@ApiModel(value ="OrderDetailXS")
public class OrderDetailXS implements Serializable {

    private static final long serialVersionUID = -2738259635479851408L;
    @ApiModelProperty(value = "品连订单ID")
    private String sysOrderId;

    @ApiModelProperty(value = "来源订单ID")
    private String sourceOrderId;

//    @ApiModelProperty(value = "商品名称")
//    private String itemName;

//    @ApiModelProperty(value = "商品英文名称")
//    private String itemNameEn;

//    @ApiModelProperty(value = "订单项SKU")
//    private String sku;

    @ApiModelProperty(value = "购买此SKU总数量")
    private Integer skuQuantity;

//    @ApiModelProperty(value = "创建时间")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
//    private Date createDate;

//    @ApiModelProperty(value = "更新时间")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
//    private Date updateDate;

//    @ApiModelProperty(value = "备注")
//    private String remark;

    @ApiModelProperty(value = "原始sku")
    private String sourceSku;
}
