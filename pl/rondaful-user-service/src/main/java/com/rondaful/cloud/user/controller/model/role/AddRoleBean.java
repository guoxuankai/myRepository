package com.rondaful.cloud.user.controller.model.role;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.util.List;

@ApiModel(value = "添加角色")
public class AddRoleBean implements Serializable {
    private static final long serialVersionUID = -1381460799868148756L;

    @ApiModelProperty(name = "roleCode", value = "角色代码", dataType = "string", required = true)
    private String roleCode;
    @ApiModelProperty(name = "roleName", value = "角色名称", dataType = "string", required = true)
    private String roleName;
    @ApiModelProperty(name = "roleExplain", value = "角色描述", dataType = "string",required = false)
    private String roleExplain;
    @ApiModelProperty(name = "status", value = "角色状态   1:启动  0暂停", dataType = "Integer", required = false)
    private Integer status;
    @ApiModelProperty(name = "createId", value = "创建角色者id", dataType = "Integer", required = true)
    private Integer createId;

    @ApiModelProperty(name = "menuIds",value="当前用户创建的权限id",required = false)
    List<Integer> menuIds;

    public List<Integer> getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(List<Integer> menuIds) {
        this.menuIds = menuIds;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleExplain() {
        return roleExplain;
    }

    public void setRoleExplain(String roleExplain) {
        this.roleExplain = roleExplain;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCreateId() {
        return createId;
    }

    public void setCreateId(Integer createId) {
        this.createId = createId;
    }
}
