package com.brandslink.cloud.user.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 新增菜单请求model
 *
 * @author tianye
 * @date 2019-06-10 10:00:40
 */
@ApiModel(value = "新增菜单请求model")
public class AddTopMenuInfoRequestDTO implements Serializable {

    @ApiModelProperty(value = "菜单名称")
    private String name;

    @ApiModelProperty(value = "菜单类型 0：菜单 1：功能")
    private Integer type;

    @ApiModelProperty(value = "所属平台 0：wmsPC端 1：wmsPDA端 2：oms端 3：ocms端")
    private Integer belong;

    @ApiModelProperty(value = "菜单图标")
    private String icon;

    @ApiModelProperty(value = "排序序号")
    private Integer seq;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "菜单url，多个以逗号分隔")
    private String url;

    private static final long serialVersionUID = 1L;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getBelong() {
        return belong;
    }

    public void setBelong(Integer belong) {
        this.belong = belong;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}