package com.rondaful.cloud.user.model.dto.menu;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/4/30
 * @Description:
 */
public class TreeDTO implements Serializable {
    private static final long serialVersionUID = 8340480385095267932L;

    @ApiModelProperty(value = "id",name = "id")
    private Integer id;

    @ApiModelProperty(value = "name",name = "id")
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TreeDTO(){}

    public TreeDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
