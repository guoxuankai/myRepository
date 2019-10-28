package com.brandslink.cloud.user.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 用户所属仓库信息
 *
 * @ClassName UserWarehouseDetailResponseDTO
 * @Author tianye
 * @Date 2019/6/19 10:48
 * @Version 1.0
 */
@ApiModel(value = "用户所属仓库信息")
public class UserWarehouseDetailResponseDTO implements Serializable {

    @ApiModelProperty(value = "所属仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "所属仓库名称")
    private String warehouseName;

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }
}
