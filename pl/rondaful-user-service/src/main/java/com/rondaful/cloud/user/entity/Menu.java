package com.rondaful.cloud.user.entity;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Menu implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5809397043605379884L;

	@ApiModelProperty(value="权限信息待确认")
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

	@ApiModelProperty(value = "创建时间")
	private Date createDate;

	@ApiModelProperty(value = "更新时间")
	private Date updateDate;

	@ApiModelProperty(value = "是否删除 [0:不删除  1:删除]")
	private Integer delFlag;

	/**
	 * 用来封装树形结构的子列表
	 */
	private List<Menu> children;

	/**
	 * 角色id列表,一个用来查询的参数
	 */
	private List<Integer> roleIds;

	/**
	 * 用户id ，一个用来查询的参数
	 */
	private Integer userId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public Integer getPlatformType() {
		return platformType;
	}

	public void setPlatformType(Integer platformType) {
		this.platformType = platformType;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public List<Integer> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<Integer> roleIds) {
		this.roleIds = roleIds;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

    public List<Menu> getChildren() {
        return children;
    }

    public void setChildren(List<Menu> children) {
        this.children = children;
    }
}
