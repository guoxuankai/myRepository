package com.rondaful.cloud.supplier.dto;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/5/8
 * @Description:
 */
public class HouseNameDTO implements Serializable {
    private static final long serialVersionUID = -904418111067028844L;

    /**
     * 仓库code
     */
    private String code;

    /**
     * 仓库名称
     */
    private String name;

    public HouseNameDTO(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public HouseNameDTO(){}

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
