package com.rondaful.cloud.order.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(description = "国家和地区列表对象")
public class CountryCode implements Serializable {


    @ApiModelProperty(value = "id")
    private Integer id;           //'id',

    @ApiModelProperty(value = "国家双字母缩写")
    private String iso;           //'国家双字母缩写',

    @ApiModelProperty(value = "国家三字母缩写")
    private String iso3;          //'国家三字母缩写',

    @ApiModelProperty(value = "国家名称（大写）")
    private String name;          //'国家名称（大写）',

    @ApiModelProperty(value = "国家中文名称")
    private String nameZh;        //'国家中文名称',

    @ApiModelProperty(value = "国家昵称")
    private String nicename;      //'国家昵称',

    @ApiModelProperty(value = "国家数字编号")
    private Short numcode;       //'国家数字编号',

    @ApiModelProperty(value = "国际长途区号")
    private Integer phonecode;     //'国际长途区号'

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso == null ? null : iso.trim();
    }

    public String getIso3() {
        return iso3;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3 == null ? null : iso3.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh == null ? null : nameZh.trim();
    }

    public String getNicename() {
        return nicename;
    }

    public void setNicename(String nicename) {
        this.nicename = nicename == null ? null : nicename.trim();
    }

    public Short getNumcode() {
        return numcode;
    }

    public void setNumcode(Short numcode) {
        this.numcode = numcode;
    }

    public Integer getPhonecode() {
        return phonecode;
    }

    public void setPhonecode(Integer phonecode) {
        this.phonecode = phonecode;
    }
}