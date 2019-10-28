package com.rondaful.cloud.supplier.model.dto;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/6/22
 * @Description:
 */
public class KeyValueDTO implements Serializable {
    private static final long serialVersionUID = 2834477517336353127L;

    private String key;

    private String name;

    private String desc;

    public KeyValueDTO(){}

    public KeyValueDTO(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public KeyValueDTO(String key, String name, String desc) {
        this.key = key;
        this.name = name;
        this.desc = desc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {

        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
