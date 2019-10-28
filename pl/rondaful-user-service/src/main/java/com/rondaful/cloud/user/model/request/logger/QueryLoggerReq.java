package com.rondaful.cloud.user.model.request.logger;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/6/12
 * @Description:
 */
@ApiModel(value = "日志查询实体类")
public class QueryLoggerReq implements Serializable {
    private static final long serialVersionUID = 3488220514368350624L;

    @ApiModelProperty(value = "登录名", name = "loginName", dataType = "string",required = false)
    private String loginName;

    @ApiModelProperty(value = "起始创建时间:yyyy-MM-dd HH:mm:ss",name = "startTime",dataType = "string",required = false)
    private String startTime;

    @ApiModelProperty(value = "结束创建时间:yyyy-MM-dd HH:mm:ss",name = "endTime",dataType = "string",required = false)
    private String endTime;

    @ApiModelProperty(value = "每页最多条数",name = "currPage",dataType = "string",required = true)
    private Integer pageSize;

    @ApiModelProperty(value = "当前页",name = "row",dataType = "string",required = true)
    private Integer currentPage;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getPageSize() {
        return pageSize==null?20:pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrentPage() {
        return currentPage==null?1:currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }
}
