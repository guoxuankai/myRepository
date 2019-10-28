package com.brandslink.cloud.user.dto.request;

import com.brandslink.cloud.common.utils.DateUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 模糊查询用户列表请求model
 *
 * @ClassName GetUserListRequestDTO
 * @Author tianye
 * @Date 2019/6/12 10:42
 * @Version 1.0
 */
@ApiModel(value ="模糊查询用户列表请求model")
public class GetUserListRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "页码", required = true)
    private String page;

    @ApiModelProperty(value = "每页显示行数", required = true)
    private String row;

    @ApiModelProperty(value = "账号")
    private String account;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "职位")
    private String position;

    @ApiModelProperty(value = "所属仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "角色")
    private String role;

    @ApiModelProperty(value = "创建开始时间 yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty(value = "创建结束时间 yyyy-MM-dd HH:mm:ss")
    private Date endTime;

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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = DateUtils.strToDate(startTime, DateUtils.FORMAT_2);
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = DateUtils.strToDate(endTime, DateUtils.FORMAT_2);
    }
}

