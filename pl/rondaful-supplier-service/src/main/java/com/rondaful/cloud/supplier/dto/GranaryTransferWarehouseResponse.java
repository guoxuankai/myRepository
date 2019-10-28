package com.rondaful.cloud.supplier.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 谷仓查询中转仓库API
 *
 * @ClassName GranaryTransferWarehouseResponse
 * @Author tianye
 * @Date 2019/4/28 17:44
 * @Version 1.0
 */
public class GranaryTransferWarehouseResponse extends GranaryResponseBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "数据")
    private List<TransferWarehouseDeatil> data;


    public List<TransferWarehouseDeatil> getData() {
        return data;
    }

    public void setData(List<TransferWarehouseDeatil> data) {
        this.data = data;
    }

    public static class TransferWarehouseDeatil {

        @ApiModelProperty(value = "中转仓库编号")
        private String transitWarehouseCode;

        @ApiModelProperty(value = "中转仓库名称")
        private String transitWarehouseName;

        public String getTransitWarehouseCode() {
            return transitWarehouseCode;
        }

        public void setTransitWarehouseCode(String transitWarehouseCode) {
            this.transitWarehouseCode = transitWarehouseCode;
        }

        public String getTransitWarehouseName() {
            return transitWarehouseName;
        }

        public void setTransitWarehouseName(String transitWarehouseName) {
            this.transitWarehouseName = transitWarehouseName;
        }
    }

}
