package com.brandslink.cloud.logistics.thirdLogistics.bean.SunYou;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("SunYouCommonBean")
public class SunYouCommonBean {

    @ApiModelProperty("顺友流水号集合")
    private List<String> syOrderNoList;

    @ApiModelProperty("客户订单号集合")
    private List<String> customerNoList;

    @ApiModelProperty("0：返回包含多个面单的单个 PDF 文件 1：返回包含多个 PDF 文件的 ZIP 包，每个 PDF 文件中仅包含一个包裹的面单信息，仅有一个 包裹时也将被打包为 ZIP 默认值：0")
    private Integer packMethod;

    @ApiModelProperty("0：标签返回数据类型为 byte 数组 1：标签返回数据类型为路径 默认值：0")
    private Integer dataFormat;

    @ApiModelProperty("顺友流水号")
    private String syOrderNo;

    @ApiModelProperty("客户订单号")
    private String customerNo;

    @DecimalMin(value = "0.001", message = "客户订单号限制为3位小数，最小值为0.001")
    @Digits(integer = 18, fraction = 3, message = "客户订单号限制为3位小数，最小值为0.001")
    @ApiModelProperty("客户订单号，单位kg")
    private BigDecimal predictionWeight;
}
