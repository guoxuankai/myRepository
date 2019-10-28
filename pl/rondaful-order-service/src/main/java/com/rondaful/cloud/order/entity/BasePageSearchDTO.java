package com.rondaful.cloud.order.entity;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author Blade
 * @date 2019-06-19 14:57:06
 **/
public class BasePageSearchDTO {

    @ApiModelProperty(value = "当前页")
    private Integer pageNumber;

    @ApiModelProperty(value = "条数")
    private Integer pageSize;

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
