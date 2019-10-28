package com.rondaful.cloud.supplier.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 谷仓创建入库单API
 *
 * @ClassName GranaryTransferWarehouseResponse
 * @Author tianye
 * @Date 2019/4/28 17:44
 * @Version 1.0
 */
public class GranaryCreateWarehouseWarrantResponse extends GranaryResponseBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "数据")
    private CreateWarrantDeatil data;

    public CreateWarrantDeatil getData() {
        return data;
    }

    public void setData(CreateWarrantDeatil data) {
        this.data = data;
    }

    public static class CreateWarrantDeatil {

        @ApiModelProperty(value = "入库单单号")
        private String receivingCode;

        public String getReceivingCode() {
            return receivingCode;
        }

        public void setReceivingCode(String receivingCode) {
            this.receivingCode = receivingCode;
        }
    }

}
