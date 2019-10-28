package com.brandslink.cloud.logistics.thirdLogistics.bean.YunTu;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(value = "YunTuChildOrder")
public class YunTuChildOrder implements Serializable {

    @Size(max = 200, message = "箱子编号，字符个数必须小于200")
    @JsonProperty(value = "boxNumber")
    @ApiModelProperty(value = "箱子编号，FBA订单必填")
    private String BoxNumber;

    @JsonProperty(value = "length")
    @ApiModelProperty(value = "预估包裹单边长，单位cm，默认1，FBA订单必填")
    private Integer Length;

    @JsonProperty(value = "width")
    @ApiModelProperty(value = "预估包裹单边宽，单位cm，默认1，FBA订单必填")
    private Integer Width;

    @JsonProperty(value = "height")
    @ApiModelProperty(value = "预估包裹单边高，单位cm，默认1，FBA订单必填")
    private Integer Height;

    @JsonProperty(value = "boxWeight")
    @ApiModelProperty(value = "预估包裹总重量，单位kg,最多3位小数，FBA订单必填")
    private BigDecimal BoxWeight;

    @Valid
    @JsonProperty(value = "childDetails")
    @ApiModelProperty(value = "单箱SKU信息，FBA订单必填")
    private List<YunTuChildDetail> ChildDetails = new ArrayList<>();


}
