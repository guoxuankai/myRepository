package com.brandslink.cloud.finance.pojo.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;

/**
 * @author yangzefei
 * @Classname CustomerFlowDetailDto
 * @Description 客户流水详情
 * @Date 2019/8/29 10:23
 */
@Data
@ExcelTarget("customerFlowDetail")
public class CustomerFlowDetailDto<T> {
    @ApiModelProperty(value = "主键标识")
    private Integer id;

    @Excel(name="客户")
    @ApiModelProperty(value = "客户")
    private String customerName;

    @Excel(name="单号")
    @ApiModelProperty(value = "单号")
    private String orderNo;

    @Excel(name="来源单号")
    @ApiModelProperty(value = "来源单号")
    private String sourceNo;

    @Excel(name="运单号")
    @ApiModelProperty(value = "运单号")
    private String waybillNo;

    @Excel(name="计费日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计费日期")
    private String billTime;

    @Excel(name="仓库")
    @ApiModelProperty(value = "仓库")
    private String warehouseName;

    @Excel(name="SKU")
    @ApiModelProperty(value = "SKU")
    private String sku;

    @Excel(name="商品名称")
    @ApiModelProperty(value = "商品名称")
    private String skuName;

    @Excel(name="货型")
    @ApiModelProperty(value = "货型")
    private String skuType;

    @Excel(name="应扣金额(元)")
    @ApiModelProperty(value = "应扣金额(元)")
    private BigDecimal originalCost;

    @Excel(name="折扣")
    @ApiModelProperty(value = "折扣")
    private Double discount;

    @Excel(name="实扣金额(元)")
    @ApiModelProperty(value = "实扣金额(元)")
    private BigDecimal discountCost;

    @ApiModelProperty(value = "详情类型 1:存储费,2:入库费(免检),3:销退费,4:出库操作费,5:入库费(抽检),6:入库费(全检)")
    private Integer detailType;

    @ApiModelProperty(value = "流水详情特性(json格式)")
    private String featureJson;

    @ApiModelProperty(value = "特性对象")
    @ExcelEntity
    private T feature;
}
