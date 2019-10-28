package com.rondaful.cloud.supplier.model.request.logistics;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/10/17
 * @Description:
 */
public class QueryPageReq implements Serializable {
    private static final long serialVersionUID = 8560145042897193058L;

    @ApiModelProperty(value = "当前页",name = "currentPage",dataType = "Integer",required = true)
    private Integer currentPage;

    @ApiModelProperty(value = "展示条数",name = "pageSize",dataType = "Integer",required = true)
    private Integer pageSize;

    @ApiModelProperty(value = "仓库id",name = "warehouseId",dataType = "Integer",required = true)
    private Integer warehouseId;

    @ApiModelProperty(value = "1:物流商 2:物流商code 3:物流方式 4:物流方式code",name = "queryType",dataType = "Integer")
    private Integer queryType;

    @ApiModelProperty(value = "查询内容",name = "queryText",dataType = "String")
    private String queryText;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Integer getQueryType() {
        return queryType;
    }

    public void setQueryType(Integer queryType) {
        this.queryType = queryType;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }
}
