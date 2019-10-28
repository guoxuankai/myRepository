package com.brandslink.cloud.finance.pojo.feature.details;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import com.brandslink.cloud.finance.pojo.base.BaseFeature;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangzefei
 * @Classname ReturnDetailFeature
 * @Description 销退费详情特性
 * @Date 2019/8/29 10:30
 */
@Data
@ExcelTarget("returnDetail")
public class ReturnDetailFeature extends BaseFeature {
    @Excel(name = "件数")
    private Integer number;
    @Excel(name = "上架费(元/件)")
    private BigDecimal shelvePrice;
    @Excel(name = "上架费(元)")
    private BigDecimal shelveCost;

    public ReturnDetailFeature(Integer quoteId,Integer number,BigDecimal shelvePrice){
        super(quoteId);
        this.number=number;
        this.shelvePrice=shelvePrice;
        this.shelveCost=shelvePrice.multiply(new BigDecimal(number));
    }
}
