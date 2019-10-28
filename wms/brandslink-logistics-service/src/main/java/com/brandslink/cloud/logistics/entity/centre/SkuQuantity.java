package com.brandslink.cloud.logistics.entity.centre;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel(value = "SkuQuantity")
public class SkuQuantity implements Serializable {

    @NotBlank(message = "SKU不能为空")
    @ApiModelProperty(value = "SKU")
    private String sku;

    @Min(value = 1,message = "SKU数量最小值为1")
    @NotNull(message = "SKU数量不能为空")
    @ApiModelProperty(value = "SKU数量")
    private Integer quantity;
}