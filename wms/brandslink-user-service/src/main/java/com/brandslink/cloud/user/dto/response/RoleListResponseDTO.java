package com.brandslink.cloud.user.dto.response;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 仓库角色列表DTO
 *
 * @ClassName RoleListResponseDTO
 * @Author tianye
 * @Date 2019/6/18 16:01
 * @Version 1.0
 */
public class RoleListResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "角色列表")
    private List<RoleListResponse> roleList;

    public List<RoleListResponse> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<RoleListResponse> roleList) {
        this.roleList = roleList;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public static class RoleListResponse implements Serializable {

        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "主键")
        private Integer id;

        @ApiModelProperty(value = "角色名称")
        private String roleName;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }
    }
}
