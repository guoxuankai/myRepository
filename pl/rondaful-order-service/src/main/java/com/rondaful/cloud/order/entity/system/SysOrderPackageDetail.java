package com.rondaful.cloud.order.entity.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rondaful.cloud.order.entity.JudgeAfterSaleDTO;
import com.rondaful.cloud.order.entity.PlatformOrderItemInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单包裹详情表
 * 实体类对应的数据表为：  tb_sys_order_package_detail
 * @author chenjiangxin
 * @date 2019-07-18 17:20:20
 */
@ApiModel(value ="SysOrderPackageDetail")
@Data
public class SysOrderPackageDetail implements Serializable {

    @ApiModelProperty(value = "平台SKU的绑定状态：已绑定:bind,未绑定:unbind,已移除:remove")
    private String bindStatus;

    @ApiModelProperty(value = "合并订单：判断售后信息")
    private List<JudgeAfterSaleDTO> judgeAfterSaleDTOList;

    @ApiModelProperty(value = "平台商品信息")
    private PlatformOrderItemInfo platformOrderItemInfo;

    @ApiModelProperty(value = "SKU总价（含运费）")
    private BigDecimal price;

    @ApiModelProperty(value = "SKU总运费（单个SKU的运费*数量）")
    private BigDecimal skuTotalShipFee;

    @ApiModelProperty(value = "可用库存")
    private Integer availableQty;

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "订单跟踪号")
    private String orderTrackId;

    @ApiModelProperty(value = "来源订单项ID串，多个订单项用#号分割")
    private String sourceOrderLineItemId;

    @ApiModelProperty(value = "订单项SKU")
    private String sku;

    @ApiModelProperty(value = "购买此SKU总数量")
    private Integer skuQuantity;

    @ApiModelProperty(value = "品连单个商品成本价")
    private BigDecimal skuCost;

    @ApiModelProperty(value = "商品URL")
    private String skuUrl;

    @ApiModelProperty(value = "商品名称")
    private String skuName;

    @ApiModelProperty(value = "商品英文名称")
    private String skuNameEn;

    @ApiModelProperty(value = "商品属性")
    private String skuAttr;

    @ApiModelProperty(value = "商品系统单价")
    private BigDecimal skuPrice;

    @ApiModelProperty(value = "平台sku")
    private String sourceSku;

    @ApiModelProperty(value = "单个商品体积，单位m³")
    private BigDecimal bulk;

    @ApiModelProperty(value = "单个商品重量，单位g")
    private BigDecimal weight;

    @ApiModelProperty(value = "所属供应商id")
    private Integer supplierId;

    @ApiModelProperty(value = "所属供应商名称")
    private String supplierName;

    @ApiModelProperty(value = "供应商所属供应链ID")
    private Integer supplyChainCompanyId;

    @ApiModelProperty(value = "供应商所属供应链名称")
    private String supplyChainCompanyName;

    @ApiModelProperty(value = "服务费扣取类型&数额[抽取类型(1为固定金额,2为百分比)#数额(例1#2.00,2#0.05)]")
    private String fareTypeAmount;

    @ApiModelProperty(value = "卖家费用")
    private BigDecimal sellerShipFee;

    @ApiModelProperty(value = "供应商费用")
    private BigDecimal supplierShipFee;

    @ApiModelProperty(value = "物流商费用")
    private BigDecimal logisticCompanyShipFee;

    @ApiModelProperty(value = "是否包邮： 0,不包邮 1,包邮  ")
    private Integer freeFreight;

    @ApiModelProperty(value = "是否进行过售后：0否 1是")
    private Integer isAfterSale;

    @ApiModelProperty(value = "创建人")
    private String creater;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "修改人")
    private String modifier;

    @ApiModelProperty(value = "修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifiedTime;

    @ApiModelProperty(value = "供应商SKU")
    private String supplierSku;
}