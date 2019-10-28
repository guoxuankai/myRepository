package com.rondaful.cloud.user.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import io.swagger.annotations.ApiModelProperty;

public class Role implements Serializable {
	
	@ApiModelProperty(value="角色id",required = false)
    private Integer rid;
    
	@ApiModelProperty(value="角色类型",required = true)
    private Integer roleType;

	@ApiModelProperty(value="角色名称",required = true)
    private String roleName;

	@ApiModelProperty(value="角色代码",required = true)
    private String roleCode;

	@ApiModelProperty(value="角色状态",required = true)
    private Integer status = 1;

	@ApiModelProperty(value="角色说明",required = true)
    private String roleExplain;

	@ApiModelProperty(value="角色是否删除",required = false)
    private Integer delFlag = 1;

	@ApiModelProperty(value="备注",required = false)
    private String remarks;

	@ApiModelProperty(value="创建时间",required = false)
    private Date createDate;

	@ApiModelProperty(value="修改时间",required = false)
    private Date updateDate;

	@ApiModelProperty(value="地址--备用",required = false)
    private String url;

	@ApiModelProperty(value="授权--备用",required = false)
    private String enabled;
    
	@ApiModelProperty(value="对应的权限名称",required = false)
    private List<String> menuName;
	
	@ApiModelProperty(value="对应的权限id",required = false)
    private List<Integer> menuId;
    
	@ApiModelProperty(value="创建角色者id",required = false)
	private Integer createId; 
   // private List<Integer> menuIds;

    // private List<menu>
    
    private static final long serialVersionUID = 1L;

    /*public List<Integer> getMenuIds() {
		return menuIds;
	}

	public void setMenuIds(List<Integer> menuIds) {
		this.menuIds = menuIds;
	}*/

    
    
	public Integer getRid() {
        return rid;
    }

	public List<Integer> getMenuId() {
		return menuId;
	}

	public void setMenuId(List<Integer> menuId) {
		this.menuId = menuId;
	}

    public Role(String roleCode) {
        this.roleCode = roleCode;
    }

	public Role(Integer roleType, String roleName, String roleCode, String roleExplain, String remarks,
			Integer createId) {
		super();
		this.roleType = roleType;
		this.roleName = roleName;
		this.roleCode = roleCode;
		this.roleExplain = roleExplain;
		this.remarks = remarks;
		this.createId = createId;
	}

    public Role(Integer rid, String roleName, Integer status, String roleExplain) {
        this.rid = rid;
        this.roleName = roleName;
        this.status = status;
        this.roleExplain = roleExplain;
    }

    public Role() {
		super();
	}

	public Integer getCreateId() {
		return createId;
	}

	public void setCreateId(Integer createId) {
		this.createId = createId;
	}

	public List<String> getMenuName() {
		return menuName;
	}

	public void setMenuName(List<String> menuName) {
		this.menuName = menuName;
	}

	public void setRid(Integer rid) {
        this.rid = rid;
    }

    public Integer getRoleType() {
        return roleType;
    }

    public void setRoleType(Integer roleType) {
        this.roleType = roleType == null ? null : roleType;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName == null ? null : roleName.trim();
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode == null ? null : roleCode.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRoleExplain() {
        return roleExplain;
    }

    public void setRoleExplain(String roleExplain) {
        this.roleExplain = roleExplain == null ? null : roleExplain.trim();
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag == null ? null : delFlag;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled == null ? null : enabled.trim();
    }
}