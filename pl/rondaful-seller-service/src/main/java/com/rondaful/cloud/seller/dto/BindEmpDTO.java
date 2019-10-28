package com.rondaful.cloud.seller.dto;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/5/4
 * @Description:
 */
public class BindEmpDTO implements Serializable {
    private static final long serialVersionUID = -5362980980993060439L;
    private Integer id;

    private String name;

    public BindEmpDTO(){}

    public BindEmpDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

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
}
