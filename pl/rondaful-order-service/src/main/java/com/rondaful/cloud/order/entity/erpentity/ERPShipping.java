package com.rondaful.cloud.order.entity.erpentity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value ="ERPShipping")//发货时调用ERP订单接收接口orderReceive使用
public class ERPShipping implements Serializable {
    @ApiModelProperty(value = "发货仓库CODE")
    private String warehouse_code;//
    @ApiModelProperty(value = "发货物流商CODE")
    private String shipping_code;//

    public String getWarehouse_code() {
        return warehouse_code;
    }

    public void setWarehouse_code(String warehouse_code) {
        this.warehouse_code = warehouse_code;
    }

    public String getShipping_code() {
        return shipping_code;
    }

    public void setShipping_code(String shipping_code) {
        this.shipping_code = shipping_code;
    }
}
