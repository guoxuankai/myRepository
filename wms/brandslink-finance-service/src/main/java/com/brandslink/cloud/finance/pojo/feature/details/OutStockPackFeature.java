package com.brandslink.cloud.finance.pojo.feature.details;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import com.brandslink.cloud.finance.pojo.base.BaseFeature;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangzefei
 * @Classname OutStockPackFeature
 * @Description 出库打包费特性
 * @Date 2019/8/29 10:32
 */
@Data
@ExcelTarget("outStockPack")
public class OutStockPackFeature extends OutStockOperateFeature {

    @Excel(name = "打包费(元/件)")
    private BigDecimal packPrice;
    @Excel(name = "打包费(元)")
    private BigDecimal packCost;

    public OutStockPackFeature(Integer quoteId,Integer number,BigDecimal operatePrice,BigDecimal packPrice) {
        super(quoteId,number, operatePrice);
        this.packPrice = packPrice;
        this.packCost = packPrice.multiply(new BigDecimal(number));
    }
}
