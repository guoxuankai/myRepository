package com.brandslink.cloud.finance.pojo.feature.details;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import com.brandslink.cloud.finance.pojo.base.BaseFeature;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangzefei
 * @Classname OutStockOperateFeature
 * @Description 出库操作费
 * @Date 2019/8/29 10:32
 */
@Data
@ExcelTarget("outStockOperate")
public class OutStockOperateFeature extends BaseFeature {
    @Excel(name = "件数")
    private Integer number;
    @Excel(name = "操作费(元/件)")
    private BigDecimal operatePrice;
    @Excel(name = "操作费(元)")
    private BigDecimal operateCost;

    public OutStockOperateFeature(Integer quoteId,Integer number,BigDecimal operatePrice){
        super(quoteId);
        this.number=number;
        this.operatePrice=operatePrice;
        this.operateCost=operatePrice.multiply(new BigDecimal(number));
    }
}
