package com.brandslink.cloud.finance.pojo.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 物流商收费导入失败
 * 实体类对应的数据表为：  import_failure
 *
 * @author guoxuankai
 * @date 2019-09-03 09:40:52
 */
@Data
@ApiModel(value = "物流商收费导入失败")
public class ImportFailure implements Serializable {

    @ApiModelProperty(value = "主键id")
    private Integer id;

    @Excel(name = "包裹号", orderNum = "0")
    @ApiModelProperty(value = "")
    private String packageNo;

    @Excel(name = "物流运单号", orderNum = "1")
    @ApiModelProperty(value = "物流运单号")
    private String waybill;

    @Excel(name = "失败原因", orderNum = "2")
    @ApiModelProperty(value = "失败原因")
    private String reason;

    @Excel(name = "导入人", orderNum = "3")
    @ApiModelProperty(value = "导入人")
    private String importPeople;

    @Excel(name = "导入时间", format = "yyyy-MM-dd HH:mm:ss", orderNum = "4")
    @ApiModelProperty(value = "导入时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date importTime;

    private static final long serialVersionUID = 1L;

    public ImportFailure() {
    }

    public ImportFailure(String packageNo, String waybill, String reason, String importPeople) {
        this.packageNo = packageNo;
        this.waybill = waybill;
        this.reason = reason;
        this.importPeople = importPeople;
    }
}