package com.brandslink.cloud.finance.pojo.feature.details;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import com.brandslink.cloud.finance.pojo.base.BaseFeature;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangzefei
 * @Classname StorageDetailFeature
 * @Description 存储费详情特性
 * @Date 2019/8/29 10:15
 */
@Data
@ExcelTarget("storageDetail")
public class StorageDetailFeature  extends BaseFeature {

    @Excel(name="入库日期")
    private String inStockDate;
    @Excel(name="入库数量")
    private Integer inStockNumber;
    @Excel(name="出库数量")
    private Integer outStockNumber;
    @Excel(name="在库数量")
    private Integer stockNumber;
    @Excel(name="体积(m³)")
    private Double volume;
    @Excel(name="价格(元/m³·天)")
    private BigDecimal price;
    @Excel(name="库龄")
    private Double stockAge;
}
