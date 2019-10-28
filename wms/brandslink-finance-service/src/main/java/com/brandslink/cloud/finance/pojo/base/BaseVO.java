package com.brandslink.cloud.finance.pojo.base;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author chenning
 * @Classname BaseVO
 * @Description 基础查询条件
 * @Date 2019/5/28 15:30
 */
public class BaseVO {
    @ApiModelProperty(value = "页数")
    private Integer pageNum=1;
    @ApiModelProperty(value = "页码")
    private Integer pageSize=10;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
