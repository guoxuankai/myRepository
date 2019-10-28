package com.rondaful.cloud.order.model.aliexpress.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/4
 * @Description:
 */
@ApiModel("速卖通订单查询实体类")
public class QueryPageDTO implements Serializable {
    private static final long serialVersionUID = -4124694808750686058L;

    @ApiModelProperty(name = "startTime",value = "下单开始时间",dataType = "string",example = "yyyy-mm-dd hh:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @ApiModelProperty(name = "endTime",value = "下单结束时间",dataType = "string",example = "yyyy-mm-dd hh:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    @ApiModelProperty(name = "plProcessStatus",value = "订单处理状态:0待处理，1转入成功，2转入失败，3部分转入成功",dataType = "Integer")
    private Integer plProcessStatus;
    @ApiModelProperty(name = "callBackStatus",value = "订单回传状态:0待标记，1标记成功，2标记失败，3部分标记成功",dataType = "Integer")
    private Integer callBackStatus;
    @ApiModelProperty(name = "pageSize",value = "每页展示条数",dataType = "Integer")
    private Integer pageSize;
    @ApiModelProperty(name = "currentPage",value = "当前页",dataType = "Integer")
    private Integer currentPage;
    @ApiModelProperty(name = "orderId",value = "平台订单号",dataType = "String")
    private String orderId;
    @ApiModelProperty(name = "orderStatus",value = "订单状态",dataType = "String")
    private String orderStatus;
    @ApiModelProperty(name = "queryType",value = "查询状态:1下单时间  2 系统发货时间",dataType = "String")
    private Integer queryType;
    @ApiModelProperty(name = "token",value = "导出时需传token",dataType = "String")
    private String token;
    private String plAccount;
    @ApiModelProperty(name = "i18n",value = "导出时需传环境标识",dataType = "String")
    private String i18n;

    private List<String> userNames;

    private List<Integer> empIds;

    public List<String> getUserNames() {
        return userNames;
    }

    public void setUserNames(List<String> userNames) {
        this.userNames = userNames;
    }

    public List<Integer> getEmpIds() {
        return empIds;
    }

    public void setEmpIds(List<Integer> empIds) {
        this.empIds = empIds;
    }

    public String getI18n() {
        return i18n;
    }

    public void setI18n(String i18n) {
        this.i18n = i18n;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public Integer getPlProcessStatus() {
        return plProcessStatus;
    }

    public void setPlProcessStatus(Integer plProcessStatus) {
        this.plProcessStatus = plProcessStatus;
    }

    public Integer getCallBackStatus() {
        return callBackStatus;
    }

    public void setCallBackStatus(Integer callBackStatus) {
        this.callBackStatus = callBackStatus;
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getQueryType() {
        return queryType==null?1:queryType;
    }

    public void setQueryType(Integer queryType) {
        this.queryType = queryType;
    }

    public String getPlAccount() {
        return plAccount;
    }

    public void setPlAccount(String plAccount) {
        this.plAccount = plAccount;
    }

    @Override
    public String toString() {
        return "QueryPageDTO{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", plProcessStatus=" + plProcessStatus +
                ", callBackStatus='" + callBackStatus + '\'' +
                ", pageSize=" + pageSize +
                ", currentPage=" + currentPage +
                '}';
    }
}
