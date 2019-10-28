package com.brandslink.cloud.logistics.entity.centre;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(value = "LogisticsFreight")
public class LogisticsFreight implements Serializable {

    @NotBlank(message = "仓库编码不能为空")
    @ApiModelProperty(value = "仓库编码")
    private String warehouse;

    @NotBlank(message = "国家简码不能为空")
    @ApiModelProperty(value = "国家简码")
    private String country;

    @NotBlank(message = "城市不能为空")
    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "邮寄方式编码")
    private String method;

    @ApiModelProperty(value = "平台（所有字母小写）（ebay，amazon，wish，aliexpress）")
    private String platform;

    @Max(value = 2,message = "搜索类型最大值为2")
    @Min(value = 1,message = "搜索类型最小值为1")
    @NotNull(message = "搜索类型（1按重量计算，2按SKU计算）不能为空")
    @ApiModelProperty(value = "搜索类型：1按重量计算，2按SKU计算")
    private Byte searchType;

    @ApiModelProperty(value = "SKU数量映射集合")
    private List<SkuQuantity> skuQuantityList = new ArrayList<>();

    @ApiModelProperty(value = "长（mm）")
    private Integer length;

    @ApiModelProperty(value = "宽（mm）")
    private Integer wide;

    @ApiModelProperty(value = "高（mm）")
    private Integer height;

    @ApiModelProperty(value = "重量（g）")
    private Integer weight;
}
