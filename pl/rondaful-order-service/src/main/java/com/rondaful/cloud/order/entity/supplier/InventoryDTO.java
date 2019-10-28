package com.rondaful.cloud.order.entity.supplier;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: wujiachuang
 * @Date: 2019/6/17
 * @Description:
 */
@Data
public class InventoryDTO implements Serializable {
    private static final long serialVersionUID = -2804400861579285800L;

    @ApiModelProperty(value = "")
    private String pictureUrl;

    @ApiModelProperty(value = "品连sku")
    private String pinlianSku;

    @ApiModelProperty(value = "供应商sku")
    private String supplierSku;

    @ApiModelProperty(value = "商品名称")
    private String commodityName;

    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "仓库id")
    private Integer warehouseId;

    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "在途数量")
    private Integer instransitQty;

    @ApiModelProperty(value = "待上架数量")
    private Integer pendingQty;

//    @ApiModelProperty(value = "可用数量")
//    private Integer availableQty;

    @ApiModelProperty(value = "本地可用库存数量")
    private Integer localAvailableQty;

    @ApiModelProperty(value = "故障品数量")
    private Integer defectsQty;

    @ApiModelProperty(value = "待出库")
    private Integer waitingShippingQty;

    @ApiModelProperty(value = "待调入")
    private Integer tuneInQty;

    @ApiModelProperty(value = "待调出")
    private Integer tuneOutQty;

    @ApiModelProperty(value = "预警值")
    private Integer warnVal;

    @ApiModelProperty(value = "备货数量")
    private Integer stockingQty;

    @ApiModelProperty(value = "缺货数量")
    private Integer piNoStockQty;

    @ApiModelProperty(value = "最后更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;
}
