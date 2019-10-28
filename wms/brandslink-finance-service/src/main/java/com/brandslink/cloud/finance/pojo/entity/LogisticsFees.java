package com.brandslink.cloud.finance.pojo.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 实体类对应的数据表为：  tf_logistics_fees
 *
 * @author guoxuankai
 * @date 2019-08-22 14:45:47
 */
@Data
@ApiModel(value = "LogisticsFees")
public class LogisticsFees implements Serializable {
    @ApiModelProperty(value = "主键id")
    private Integer id;

    @Excel(name = "客户名称", orderNum = "0")
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "包裹号")
    private String packageNo;

    @ApiModelProperty(value = "发货仓库")
    private String warehouse;

    @Excel(name = "物流商", orderNum = "2")
    @ApiModelProperty(value = "物流商")
    private String logisticsProvider;

    @Excel(name = "物流运单", orderNum = "3")
    @ApiModelProperty(value = "物流运单")
    private String waybill;

    @ApiModelProperty(value = "邮寄方式")
    private String mailingMethod;

    @ApiModelProperty(value = "发货时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deliveryTime;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @Excel(name = "物流跟踪号", orderNum = "1")
    @ApiModelProperty(value = "跟踪号")
    private String trackingNumber;

    @ApiModelProperty(value = "目的国家")
    private String country;

    @ApiModelProperty(value = "城市")
    private String city;

    //    @Excel(name = "状态",replace = {"待导入_1", "待确认_2", "已确认_3"},orderNum = "1")
    @ApiModelProperty(value = "状态：1.待导入2.待确认3.已确认")
    private Integer status;

    @Excel(name = "物流计重(kg)", orderNum = "5")
    @ApiModelProperty(value = "计抛重量")
    private BigDecimal calculativeWeight;

    @Excel(name = "物流实重(kg)", orderNum = "4")
    @ApiModelProperty(value = "实际重量")
    private BigDecimal actualWeight;

    @ApiModelProperty(value = "仓库运费")
    private BigDecimal warehouseFreight;

    @ApiModelProperty(value = "物流计重")
    private BigDecimal logisticCalculativeWeight;

    @ApiModelProperty(value = "物流实重")
    private BigDecimal logisticActualWeight;

    @Excel(name = "物流运费", orderNum = "6")
    @ApiModelProperty(value = "物流运费")
    private BigDecimal logisticFreight;

    @ApiModelProperty(value = "导入人")
    private String importPeople;

    //    @Excel(name = "导入时间", format = "yyyy-MM-dd HH:mm:ss", orderNum = "2")
    @ApiModelProperty(value = "导入时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date importTime;

    @ApiModelProperty(value = "确认人")
    private String affirmPeople;

    @ApiModelProperty(value = "确认时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date affirmTime;

    @ApiModelProperty(value = "发货平台")
    private String deliveryPlatform;

    private static final long serialVersionUID = 1L;



}