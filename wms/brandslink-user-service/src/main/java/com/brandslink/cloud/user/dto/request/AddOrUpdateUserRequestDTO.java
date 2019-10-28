package com.brandslink.cloud.user.dto.request;

import com.brandslink.cloud.user.dto.response.RoleInfoResponseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 新增or修改用户请求model
 *
 * @ClassName AddOrUpdateUserRequestDTO
 * @Author tianye
 * @Date 2019/6/12 14:18
 * @Version 1.0
 */
@ApiModel(value = "新增or修改用户请求model")
public class AddOrUpdateUserRequestDTO implements Serializable {

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "账号", required = true)
    private String account;

    @ApiModelProperty(value = "姓名", required = true)
    private String name;

    @ApiModelProperty(value = "性别 0：男 1：女", required = true)
    private Integer sex;

    @ApiModelProperty(value = "是否可用 0：可用 1：不可用 默认为0")
    private Integer enabled = 0;

    @ApiModelProperty(value = "联系方式")
    private String contactWay;

    private List<DepartmentDetail> departmentDetailList;

    @ApiModelProperty(value = "职位", required = true)
    private String position;

    @ApiModelProperty(value = "所属仓库信息", required = true)
    private List<RoleInfoResponseDTO.WarehouseDetail> warehouseList;

    @ApiModelProperty(value = "所属公司名称", required = true)
    private String companyName;

    @ApiModelProperty(value = "所属公司id", required = true)
    private Integer companyId;

    @ApiModelProperty(value = "角色id集合")
    private List<Integer> roleIds;

    public static class DepartmentDetail implements Serializable {

        @ApiModelProperty(value = "部门名称", required = true)
        private String name;

        @ApiModelProperty(value = "部门id", required = true)
        private Integer id;

        @ApiModelProperty(value = "部门顺序", required = true)
        private Integer seq;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getSeq() {
            return seq;
        }

        public void setSeq(Integer seq) {
            this.seq = seq;
        }
    }

    public List<DepartmentDetail> getDepartmentDetailList() {
        return departmentDetailList;
    }

    public void setDepartmentDetailList(List<DepartmentDetail> departmentDetailList) {
        this.departmentDetailList = departmentDetailList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        if (null != enabled) {
            this.enabled = enabled;
        }
    }

    public String getContactWay() {
        return contactWay;
    }

    public void setContactWay(String contactWay) {
        this.contactWay = contactWay;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public List<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
    }
}
