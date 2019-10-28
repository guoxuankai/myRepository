package com.rondaful.cloud.order.entity.goodcang;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GCInventoryVO {

    @ApiModelProperty(value = "在途数量")
    private Integer onway;//": "0",
    @ApiModelProperty(value = "待上架数量")
    private Integer pending;//": "0",
    @ApiModelProperty(value = "缺货数量")
    private Integer pi_no_stock;//": "0",
    @ApiModelProperty(value = "商品编码（客户代码-商品编码）")
    private String product_barcode;//": "G1149-B-1-8A7D85B8-896310",
    @ApiModelProperty(value = "商品销售价值")
    private BigDecimal product_sales_value;//": "0.00",
    @ApiModelProperty(value = "SKU")
    private String product_sku;//": "B-1-8A7D85B8-896310",
    @ApiModelProperty(value = "待出库数量")
    private Integer reserved;//": "18",
    @ApiModelProperty(value = "可售数量")
    private Integer sellable;//": "504",
    @ApiModelProperty(value = "历史出库数量")
    private Integer shipped;//": "0",
    @ApiModelProperty(value = "已销售的共享数量")
    private Integer sold_shared;//": "0",
    @ApiModelProperty(value = "备货数量")
    private Integer stocking;//": "0",
    @ApiModelProperty(value = "待调入数量")
    private Integer tune_in;//": "0",
    @ApiModelProperty(value = "待调出数量")
    private Integer tune_out;//": "0",
    @ApiModelProperty(value = "不合格数量")
    private Integer unsellable;//": "0",
    @ApiModelProperty(value = "仓库代码")
    private String warehouse_code;//": "USEA",
    @ApiModelProperty(value = "仓库描述")
    private String warehouse_desc;//": "美东仓库"
}
