package com.rondaful.cloud.user.model.dto.dep;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/24
 * @Description:
 */
public class DepartmentTreeDTO implements Serializable {
    private static final long serialVersionUID = 8605183976549932115L;

    private Integer id;

    @ApiModelProperty(value = "等级")
    private Integer level;
    @ApiModelProperty(value = "上级id")
    private Integer parentId;
    @ApiModelProperty(value = "部门名称")
    private String departmentName;
    @ApiModelProperty(value = "职位列表")
    private List<String> positionNames;
    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date creatTime;
    @ApiModelProperty(value = "修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    @ApiModelProperty(value = "下级列表")
    List<DepartmentTreeDTO> childList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
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

    public List<String> getPositionNames() {
        return positionNames;
    }

    public void setPositionNames(List<String> positionNames) {
        this.positionNames = positionNames;
    }

    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<DepartmentTreeDTO> getChildList() {
        return childList;
    }

    public void setChildList(List<DepartmentTreeDTO> childList) {
        this.childList = childList;
    }


}
