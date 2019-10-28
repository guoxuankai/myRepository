package com.rondaful.cloud.supplier.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/4
 * @Description:
 */
@ApiModel(value="分页返回对象")
public class PageDTO<T> {

    @ApiModelProperty(value = "总记录条数",name = "totalCount",dataType = "Long")
    private Integer totalCount;
    @ApiModelProperty(value = "当前页",name = "currentPage",dataType = "Long")
    private Integer currentPage;
    @ApiModelProperty(value = "数据集合",name = "list",dataType = "list")
    private List<T> list;

    public PageDTO() {}

    public PageDTO(Integer totalCount, Integer currentPage) {
        this.totalCount = totalCount;
        this.currentPage = currentPage;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
