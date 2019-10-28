package com.brandslink.cloud.logistics.thirdLogistics.bean.MiaoXin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("MiaoXinPrintVO")
public class MiaoXinPrintVO implements Serializable {

    @ApiModelProperty(value = "标签格式:A4_EMS_BGD.frx")
    private String format;

//    @NotBlank(message = "打印类型不能为空")
    @ApiModelProperty(value = "打印类型:lab10_10/A4/1/invoice")
    private String printType;

    @ApiModelProperty(value = "打印语种")
    private String print;

    @NotEmpty(message = "请求打印的订单集合不能为空")
    @ApiModelProperty(value = "订单ID集合")
    private List<String> orderIds;

}
