package com.rondaful.cloud.user.model.request.user;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: xqq
 * @Date: 2019/6/22
 * @Description:
 */
public class SupplyChainQueryPageReq implements Serializable {
    private static final long serialVersionUID = -9027860484279098436L;

    @ApiModelProperty(value = "公司名称",name = "companyName",dataType = "String")
    private String companyName;

    @ApiModelProperty(value = "开始时间：yyyy-MM-dd HH:mm:ss",name = "startTime",dataType = "String")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty(value = "结束时间：yyyy-MM-dd HH:mm:ss",name = "endTime",dataType = "String")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @ApiModelProperty(value = "查询时间类型:1-新增时间,2-修改时间",name = "dateType",dataType = "Integer")
    private Integer dateType;

    @ApiModelProperty(value = "当前页",name = "currentPage",dataType = "Integer",required = true)
    private Integer currentPage;

    @ApiModelProperty(value = "每页条数",name = "pageSize",dataType = "Integer",required = true)
    private Integer pageSize;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getDateType() {
        return dateType;
    }

    public void setDateType(Integer dateType) {
        this.dateType = dateType;
    }

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
}
