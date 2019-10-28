package com.brandslink.cloud.finance.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

/**
 * @author yangzefei
 * @Classname CustomerRechargeVo
 * @Description 客户充值模型
 * @Date 2019/9/3 9:32
 */
@Data
public class CustomerRechargeVo {

    /**
     * 客户编码
     */
    @ApiModelProperty(name = "客户编码")
    @NotEmpty(message = "客户编码不能为空")
    private String customerCode;

    /**
     * 充值金额
     */
    @ApiModelProperty(name = "充值金额")
    @Digits(integer=10,fraction=4)
    private BigDecimal money;

    /**
     * 充值凭证
     */
    @ApiModelProperty(name = "充值凭证")
    @NotEmpty(message = "充值凭证不能为空")
    private String certificateUrl;
}
