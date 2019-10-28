package com.rondaful.cloud.order.entity;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class AreaCode implements Serializable {
    private static final long serialVersionUID = -6241147629834035445L;
    @ApiModelProperty(value = "")
    private Integer id;

    @ApiModelProperty(value = "级别")
    private Integer level;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "父级id")
    private Integer parentId;

    @ApiModelProperty(value = "父级id")
    private String nameEn;

    @ApiModelProperty(value = "邮编")
    private String postCode;

    public AreaCode(){}

    public AreaCode(String code, String postCode) {
        this.code = code;
        this.postCode = postCode;
    }

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
        this.name = name == null ? null : name.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    @Override
    public String toString() {
        return "AreaCode{" +
                "id=" + id +
                ", level=" + level +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", parentId=" + parentId +
                ", nameEn='" + nameEn + '\'' +
                ", postCode='" + postCode + '\'' +
                '}';
    }
}