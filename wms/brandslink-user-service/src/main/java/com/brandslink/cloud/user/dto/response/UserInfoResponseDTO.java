package com.brandslink.cloud.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户信息表dto
 *
 * @author tianye
 * @date 2019-06-10 10:00:40
 */
@ApiModel(value = "UserInfo")
public class UserInfoResponseDTO implements Serializable {

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "账号")
    private String account;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "性别 0：男 1：女 ")
    private Integer sex;

    @ApiModelProperty(value = "是否可用 0：可用 1：不可用")
    private Integer enabled;

    @ApiModelProperty(value = "联系方式")
    private String contactWay;

    @ApiModelProperty(value = "部门名称")
    private String departmentName;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "职位")
    private String position;

    @ApiModelProperty(value = "所属仓库")
    private List<RoleInfoResponseDTO.WarehouseDetail> warehouseList;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "角色列表")
    private List<RoleDetail> roleList;

    @ApiModelProperty(value = "仓库名称集合")
    private String warehouseNameList;

    public String getWarehouseNameList() {
        return warehouseNameList;
    }

    public void setWarehouseNameList(String warehouseNameList) {
        this.warehouseNameList = warehouseNameList;
    }

    public static class RoleDetail implements Serializable {

        @ApiModelProperty(value = "角色代码")
        private String roleCode;

        @ApiModelProperty(value = "角色名称")
        private String roleName;

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
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public String getContactWay() {
        return contactWay;
    }

    public void setContactWay(String contactWay) {
        this.contactWay = contactWay;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public List<RoleInfoResponseDTO.WarehouseDetail> getWarehouseList() {
        return warehouseList;
    }

    public void setWarehouseList(List<RoleInfoResponseDTO.WarehouseDetail> warehouseList) {
        this.warehouseList = warehouseList;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<RoleDetail> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<RoleDetail> roleList) {
        this.roleList = roleList;
    }
}