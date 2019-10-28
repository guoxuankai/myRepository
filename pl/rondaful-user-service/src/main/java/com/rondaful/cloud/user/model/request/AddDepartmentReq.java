package com.rondaful.cloud.user.model.request;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/4/24
 * @Description:
 */
public class AddDepartmentReq implements Serializable {
    private static final long serialVersionUID = -246901519369110280L;
    @ApiModelProperty(value = "所属平台",name = "platform",dataType = "Byte")
    private Byte platform;
    @ApiModelProperty(value = "等级",name = "level",dataType = "Byte")
    private Byte level;
    @ApiModelProperty(value = "上级id",name = "parentId",dataType = "Integer")
    private Integer parentId;
    @ApiModelProperty(value = "部门名称",name = "departmentName",dataType = "String")
    private String departmentName;
    @ApiModelProperty(value = "职位列表",name = "positionNames",dataType = "String")
    private String positionNames;

    public Byte getPlatform() {
        return platform;
    }

    public void setPlatform(Byte platform) {
        this.platform = platform;
    }

    public Byte getLevel() {
        return level;
    }

    public void setLevel(Byte level) {
        this.level = level;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getPositionNames() {
        return positionNames;
    }

    public void setPositionNames(String positionNames) {
        this.positionNames = positionNames;
    }
}
