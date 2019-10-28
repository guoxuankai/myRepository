package com.rondaful.cloud.user.model.dto.area;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/25
 * @Description:
 */
public class AreaCodeDTO implements Serializable {
    private static final long serialVersionUID = -381376252172591257L;

    @ApiModelProperty(value = "id",name = "id",dataType = "Integer")
    private Integer id;

    @ApiModelProperty(value = "级别",name = "level",dataType = "Integer")
    private Integer level;

    @ApiModelProperty(value = "名称",name = "name",dataType = "String")
    private String name;

    @ApiModelProperty(value = "编码",name = "code",dataType = "String")
    private String code;

    @ApiModelProperty(value = "父级id",name = "parentId",dataType = "Integer")
    private Integer parentId;

    @ApiModelProperty(value = "子集列表",name = "childList",dataType = "List")
    private List<AreaCodeDTO> childList;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public List<AreaCodeDTO> getChildList() {
        return childList;
    }

    public void setChildList(List<AreaCodeDTO> childList) {
        this.childList = childList;
    }
}
