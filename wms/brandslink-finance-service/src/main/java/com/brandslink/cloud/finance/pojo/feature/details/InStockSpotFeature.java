package com.brandslink.cloud.finance.pojo.feature.details;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import com.brandslink.cloud.finance.pojo.base.BaseFeature;
import lombok.Data;

import java.math.BigDecimal;


/**
 * @author yangzefei
 * @Classname InStockSpotFeature
 * @Description 入库费抽检特性
 * @Date 2019/8/29 10:25
 */
@Data
@ExcelTarget("inStockSpot")
public class InStockSpotFeature extends BaseFeature {
    @Excel(name = "件数")
    private Integer number;
    @Excel(name = "QC抽检(元/件)")
    private BigDecimal qcPrice;
    @Excel(name = "QC费(元)")
    private BigDecimal qcCost;

    @Excel(name = "上架费(元/件)")
    private BigDecimal shelvePrice;
    @Excel(name = "上架费(元)")
    private BigDecimal shelveCost;
}
