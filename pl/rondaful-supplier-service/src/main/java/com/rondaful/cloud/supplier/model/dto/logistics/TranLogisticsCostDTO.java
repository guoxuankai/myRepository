package com.rondaful.cloud.supplier.model.dto.logistics;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: xqq
 * @Date: 2019/10/18
 * @Description:
 */
public class TranLogisticsCostDTO implements Serializable {
    private static final long serialVersionUID = 3330886604577633335L;

    @ApiModelProperty(value = "运输方式代码")
    private String smCode;

    @ApiModelProperty(value = "运输方式中文")
    private String smName;

    @ApiModelProperty(value = "运输方式英文")
    private String smNameEn;

    @ApiModelProperty(value = "最快时效")
    private Integer smDeliveryTimeMin;

    @ApiModelProperty(value = "最快时效")
    private Integer smDeliveryTimeMax;

    @ApiModelProperty(value = "总费用")
    private BigDecimal total;

    public String getSmCode() {
        return smCode;
    }

    public void setSmCode(String smCode) {
        this.smCode = smCode;
    }

    public String getSmName() {
        return smName;
    }

    public void setSmName(String smName) {
        this.smName = smName;
    }

    public String getSmNameEn() {
        return smNameEn;
    }

    public void setSmNameEn(String smNameEn) {
        this.smNameEn = smNameEn;
    }

    public Integer getSmDeliveryTimeMin() {
        return smDeliveryTimeMin;
    }

    public void setSmDeliveryTimeMin(Integer smDeliveryTimeMin) {
        this.smDeliveryTimeMin = smDeliveryTimeMin;
    }

    public Integer getSmDeliveryTimeMax() {
        return smDeliveryTimeMax;
    }

    public void setSmDeliveryTimeMax(Integer smDeliveryTimeMax) {
        this.smDeliveryTimeMax = smDeliveryTimeMax;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
