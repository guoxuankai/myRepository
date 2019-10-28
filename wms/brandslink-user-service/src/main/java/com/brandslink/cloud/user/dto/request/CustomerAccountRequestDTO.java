package com.brandslink.cloud.user.dto.request;

import com.brandslink.cloud.common.utils.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description:
 * @date 2019/9/4 10:55
 */
@ApiModel(value = "账号查询对象")
public class CustomerAccountRequestDTO implements Serializable {

    @ApiModelProperty(value = "页码")
    private String page;

    @ApiModelProperty(value = "每页显示行数")
    private String row;

    @ApiModelProperty(value = "排序：升序：asc，倒序：desc（默认）")
    private String order = "desc";

    @ApiModelProperty(value = "可排序字段")
    private String orderField = "create_time";

    @ApiModelProperty(value = "创建时间开始 格式yyyy-MM-dd HH:mm:ss")
    private Date createTimeStart;

    @ApiModelProperty(value = "创建时间结束 格式yyyy-MM-dd HH:mm:ss")
    private Date createTimeEnd;

    @ApiModelProperty(value = "修改时间开始 格式yyyy-MM-dd HH:mm:ss")
    private Date updateTimeStart;

    @ApiModelProperty(value = "修改时间结束 格式yyyy-MM-dd HH:mm:ss")
    private Date updateTimeEnd;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "更新人")
    private String lastUpdateBy;

    @ApiModelProperty(value = "账号")
    private String account;


    @ApiModelProperty(value = "账号名称")
    private String name;
    @ApiModelProperty(value = "账号状态")
    private Integer enabled;

    @ApiModelProperty(value = "所属客户id -> 内部获取，前端不传")
    private Integer customerId;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getOrderField() {
        return orderField;
    }

    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    public Date getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeStart(String createTimeStart) {
        this.createTimeStart = DateUtils.strToDate(createTimeStart, DateUtils.FORMAT_2);
    }

    public Date getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(String createTimeEnd) {
        this.createTimeEnd = DateUtils.strToDate(createTimeEnd, DateUtils.FORMAT_2);
    }

    public Date getUpdateTimeStart() {
        return updateTimeStart;
    }

    public void setUpdateTimeStart(String updateTimeStart) {
        this.updateTimeStart = DateUtils.strToDate(updateTimeStart, DateUtils.FORMAT_2);
    }

    public Date getUpdateTimeEnd() {
        return updateTimeEnd;
    }

    public void setUpdateTimeEnd(String updateTimeEnd) {
        this.updateTimeEnd = DateUtils.strToDate(updateTimeEnd, DateUtils.FORMAT_2);
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

}

