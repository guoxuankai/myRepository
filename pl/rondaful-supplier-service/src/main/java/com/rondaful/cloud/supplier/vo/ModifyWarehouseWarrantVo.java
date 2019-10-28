package com.rondaful.cloud.supplier.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 编辑入库单 Vo
 *
 * @ClassName ModifyWarehouseWarrantVo
 * @Author tianye
 * @Date 2019/4/29 14:43
 * @Version 1.0
 */
public class ModifyWarehouseWarrantVo extends WarehouseWarrantVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id", required = true)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
