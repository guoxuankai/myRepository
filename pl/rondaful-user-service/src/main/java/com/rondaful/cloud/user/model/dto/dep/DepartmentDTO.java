package com.rondaful.cloud.user.model.dto.dep;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/24
 * @Description:
 */
public class DepartmentDTO implements Serializable {
    private static final long serialVersionUID = -6307609416854969474L;

    @ApiModelProperty(value = "所属平台")
    private Byte platform;
    @ApiModelProperty(value = "上级id")
    private Integer parentId;
    @ApiModelProperty(value = "部门名称")
    private String departmentName;
    @ApiModelProperty(value = "职位列表")
    private List<String> positionNames;
    @ApiModelProperty(value = "归属账号")
    private Integer attribution;
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "等级")
    private Integer level;

    public Byte getPlatform() {
        return platform;
    }

    public void setPlatform(Byte platform) {
        this.platform = platform;
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

    public Integer getAttribution() {
        return attribution;
    }

    public void setAttribution(Integer attribution) {
        this.attribution = attribution;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "DepartmentDTO{" +
                "platform=" + platform +
                ", parentId=" + parentId +
                ", departmentName='" + departmentName + '\'' +
                ", positionNames=" + positionNames +
                ", attribution=" + attribution +
                ", createBy='" + createBy + '\'' +
                '}';
    }
}
