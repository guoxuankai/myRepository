package com.rondaful.cloud.user.model.dto.user;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/29
 * @Description:
 */
public class QueryBackSellerDTO implements Serializable {
    private static final long serialVersionUID = -5038563820842301263L;

    @ApiModelProperty(value = "状态:1-审核通过,4-禁用,2-审核中,3-审核失败,0-待激活",name = "status",dataType = "string")
    private Integer status;

    @ApiModelProperty(value = "卖家id",name = "id",dataType = "string")
    private Integer id;

    @ApiModelProperty(value = "供应链公司",name = "supplyChainCompany",dataType = "string")
    private String supplyChainCompany;

    @ApiModelProperty(value = "开始时间(yyyy-MM-dd HH:mm:ss)",name = "startTime",dataType = "string")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty(value = "结束时间(yyyy-MM-dd HH:mm:ss)",name = "endTime",dataType = "string")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @ApiModelProperty(value = "展示条数",name = "totalCount",dataType = "Integer")
    private Integer pageSize;
    @ApiModelProperty(value = "当前页",name = "currentPage",dataType = "Integer")
    private Integer currentPage;
    @ApiModelProperty(value = "查询时间类型:1新增  2  修改",name = "Integer",dataType = "Integer")
    private Integer dateType;
    @ApiModelProperty(value = "2查询申请，审核失败账户",name = "Integer",dataType = "Integer")
    private Integer queryType;

    @ApiModelProperty(value = "手机号",name = "phone",dataType = "String")
    private String phone;

    @ApiModelProperty(value = "邮箱",name = "email",dataType = "String")
    private String email;

    @ApiModelProperty(value = "登录账号",name = "loginName",dataType = "String")
    private String loginName;

    private List<Integer> userIds;

    @ApiModelProperty(value = "授信的审核状态",name = "applyStatus",dataType = "Integer")
    private String applyStatus;

    @ApiModelProperty(value = "用户名",name = "userName",dataType = "String")
    private String userName;

    public List<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Integer> userIds) {
        this.userIds = userIds;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSupplyChainCompany() {
        return supplyChainCompany;
    }

    public void setSupplyChainCompany(String supplyChainCompany) {
        this.supplyChainCompany = supplyChainCompany;
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

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getDateType() {
        return dateType;
    }

    public void setDateType(Integer dateType) {
        this.dateType = dateType;
    }

    public Integer getQueryType() {
        return queryType;
    }

    public void setQueryType(Integer queryType) {
        this.queryType = queryType;
    }

    public String getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(String applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
