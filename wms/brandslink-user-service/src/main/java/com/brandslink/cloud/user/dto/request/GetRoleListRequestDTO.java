package com.brandslink.cloud.user.dto.request;

import com.brandslink.cloud.common.utils.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;


/**
 * 模糊查询角色列表请求model
 *
 * @ClassName GetUserListRequestDTO
 * @Author tianye
 * @Date 2019/6/12 10:42
 * @Version 1.0
 */
@ApiModel(value ="模糊查询角色列表请求model")
public class GetRoleListRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "页码", required = true)
    private String page;

    @ApiModelProperty(value = "每页显示行数", required = true)
    private String row;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "所属仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建开始时间 yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "创建结束时间 yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateTime;

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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = DateUtils.strToDate(createTime, DateUtils.FORMAT_2);
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = DateUtils.strToDate(lastUpdateTime, DateUtils.FORMAT_2);
    }
}
