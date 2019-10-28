package com.rondaful.cloud.user.model.dto.menu;

import com.rondaful.cloud.common.entity.user.MenuCommon;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/28
 * @Description:
 */
public class MenuDTO implements Serializable {
    private static final long serialVersionUID = -903323000274672385L;

    @ApiModelProperty(value = "id,主键自增")
    private Integer id;

    @ApiModelProperty(value = "平台类型   0供应商平台  1卖家平台  2管理平台")
    private Integer platformType;

    @ApiModelProperty(value = "父id")
    private Integer parentId;

    @ApiModelProperty(value = "层级父id")
    private String parentIds;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "授权的url列表[多个以 , 分隔]")
    private String href;

    @ApiModelProperty(value = "菜单图片")
    private String icon;

    @ApiModelProperty(value = "菜单级别 1:一级 2:二级 3:三级(按钮级别)")
    private Integer level;

    @ApiModelProperty(value = "是否可见 [0:可见 1:不可见]")
    private String vshow;

    @ApiModelProperty(value = "权限(暂时预留)")
    private String permission;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "是否删除 [0:不删除  1:删除]")
    private Integer delFlag;

    private List<MenuDTO> children;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Integer platformType) {
        this.platformType = platformType;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getVshow() {
        return vshow;
    }

    public void setVshow(String vshow) {
        this.vshow = vshow;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public List<MenuDTO> getChildren() {
        return children;
    }

    public void setChildren(List<MenuDTO> children) {
        this.children = children;
    }
}
