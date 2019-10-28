package com.rondaful.cloud.order.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
@ApiModel(value = "卖家规则接口入参承载体")
public class SellerRuleModel {
    @ApiModelProperty(name = "ruleType",value = "规则类型: 1为公共规则,2为卖家规则",dataType = "Integer")
    private Integer ruleType;
    @ApiModelProperty(name = "ruleEventType",value = "处理事件类型",dataType = "Integer")
    private Integer  ruleEventType;
    @ApiModelProperty(name = "beginDate",value = "创建时间之开始",dataType = "Date")
    private Date beginDate;
    @ApiModelProperty(name = "endDate",value = "创建时间之结束",dataType = "Date")
    private Date endDate;
    @ApiModelProperty(name = "status",value = "规则所处状态: 0为停用,1启用,2为所有",dataType = "Integer")
    private Integer status;
    @ApiModelProperty(name = "searchType",value = "以何种方式进行查询：1为规则名称，2为卖家账号",dataType = "Integer")
    private Integer searchType;
    @ApiModelProperty(name = "searchStr",value = "查询的字串信息",dataType = "String")
    private String searchStr;

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }

    public Integer getRuleEventType() {
        return ruleEventType;
    }

    public void setRuleEventType(Integer ruleEventType) {
        this.ruleEventType = ruleEventType;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSearchType() {
        return searchType;
    }

    public void setSearchType(Integer searchType) {
        this.searchType = searchType;
    }

    public String getSearchStr() {
        return searchStr;
    }

    public void setSearchStr(String searchStr) {
        this.searchStr = searchStr;
    }

    @Override
    public String toString() {
        return "SellerRuleModel{" +
                "ruleType=" + ruleType +
                ", ruleEventType=" + ruleEventType +
                ", beginDate=" + beginDate +
                ", endDate=" + endDate +
                ", status=" + status +
                ", searchType=" + searchType +
                ", searchStr='" + searchStr + '\'' +
                '}';
    }
}
