package com.brandslink.cloud.logistics.entity.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "YuanPerGram")
public class YuanPerGram implements Serializable {
    @NotNull(message = "每%克不能为空")
    @ApiModelProperty(value = "每%克")
    @Min(value = 1, message = "每%克最小值为1")
    private Integer perGram;

    @NotNull(message = "%元不能为空")
    @Digits(integer = 18, fraction = 3, message = "%元限制为18位整数3位小数")
    @ApiModelProperty(value = "%元")
    private BigDecimal yuan;
}