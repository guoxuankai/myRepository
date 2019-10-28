package com.brandslink.cloud.logistics.thirdLogistics.bean.YunTu;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@ApiModel(value = "YunTuChildDetail")
public class YunTuChildDetail implements Serializable {

    @Size(max = 50, message = "商品SKU，必须小于50")
    @JsonProperty(value = "sKU")
    @ApiModelProperty(value = "用于填写商品SKU，FBA订单必填")
    private String SKU;

    @JsonProperty(value = "quantity")
    @ApiModelProperty(value = "申报数量，FBA订单必填")
    private Integer Quantity;
}
