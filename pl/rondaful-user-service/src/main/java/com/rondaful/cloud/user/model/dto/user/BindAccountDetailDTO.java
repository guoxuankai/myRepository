package com.rondaful.cloud.user.model.dto.user;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/29
 * @Description:
 */
public class BindAccountDetailDTO implements Serializable {
    private static final long serialVersionUID = -4423375352248623555L;

    @ApiModelProperty(value = "账号id",name = "id", dataType = "String",required = false)
    private String id;
    @ApiModelProperty(value = "name",name = "name", dataType = "String",required = false)
    private String name;

    private List<BindAccountDetailDTO> childs;

    public BindAccountDetailDTO(){}

    public BindAccountDetailDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BindAccountDetailDTO> getChilds() {
        return childs;
    }

    public void setChilds(List<BindAccountDetailDTO> childs) {
        this.childs = childs;
    }
}
