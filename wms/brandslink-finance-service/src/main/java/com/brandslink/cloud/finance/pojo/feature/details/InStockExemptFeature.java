package com.brandslink.cloud.finance.pojo.feature.details;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import com.brandslink.cloud.finance.pojo.base.BaseFeature;
import lombok.Data;

import java.math.BigDecimal;


/**
 * @author yangzefei
 * @Classname InStockExemptFeature
 * @Description 入库费免检特性
 * @Date 2019/8/29 10:25
 */
@Data
@ExcelTarget("inStockExempt")
public class InStockExemptFeature extends BaseFeature {
    @Excel(name = "件数")
    private Integer number;

    @Excel(name = "QC免检(元/件)")
    private BigDecimal qcPrice;
    @Excel(name = "QC费(元)")
    private BigDecimal qcCost;

    @Excel(name = "上架费(元/件)")
    private BigDecimal shelvePrice;
    @Excel(name = "上架费(元)")
    private BigDecimal shelveCost;
}
