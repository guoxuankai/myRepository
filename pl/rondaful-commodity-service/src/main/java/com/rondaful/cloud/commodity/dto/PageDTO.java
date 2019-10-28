package com.rondaful.cloud.commodity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;


@ApiModel(value="分页返回对象")
public class PageDTO<T> {

    @ApiModelProperty(value = "总记录条数",name = "totalCount",dataType = "Long")
    private Long totalCount;
    @ApiModelProperty(value = "当前页",name = "currentPage",dataType = "Long")
    private Long currentPage;
    @ApiModelProperty(value = "数据集合",name = "list",dataType = "list")
    private List<T> list;

    public PageDTO() {}

    public PageDTO(Long totalCount, Long currentPage) {
        this.totalCount = totalCount;
        this.currentPage = currentPage;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Long currentPage) {
        this.currentPage = currentPage;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
