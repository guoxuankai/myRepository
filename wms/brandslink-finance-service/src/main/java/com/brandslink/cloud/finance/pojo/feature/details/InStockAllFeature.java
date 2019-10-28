package com.brandslink.cloud.finance.pojo.feature.details;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import com.brandslink.cloud.finance.pojo.base.BaseFeature;
import lombok.Data;

import java.math.BigDecimal;


/**
 * @author yangzefei
 * @Classname InStockAllFeature
 * @Description 入库费全检特性
 * @Date 2019/8/29 10:25
 */
@Data
@ExcelTarget("inStockAll")
public class InStockAllFeature extends BaseFeature {
    @Excel(name = "件数")
    private Integer number;

    @Excel(name = "QC全检(元/件)")
    private BigDecimal qcPrice;
    @Excel(name = "QC费(元)")
    private BigDecimal qcCost;

    @Excel(name = "上架费(元/件)")
    private BigDecimal shelvePrice;
    @Excel(name = "上架费(元)")
    private BigDecimal shelveCost;


    public InStockAllFeature(Integer quoteId,Integer number,BigDecimal qcPrice,BigDecimal shelvePrice){
        super(quoteId);
        this.number=number;
        this.qcPrice=qcPrice;
        this.qcCost=qcPrice.multiply(new BigDecimal(number));

        this.shelvePrice=shelvePrice;
        this.shelveCost=shelvePrice.multiply(new BigDecimal(number));;
    }
}
