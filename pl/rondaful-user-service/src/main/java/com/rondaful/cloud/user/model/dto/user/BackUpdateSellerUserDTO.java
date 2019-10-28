package com.rondaful.cloud.user.model.dto.user;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/29
 * @Description:
 */
public class BackUpdateSellerUserDTO implements Serializable {
    private static final long serialVersionUID = 1823780112501396045L;

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "联系地址")
    private String address;

    @ApiModelProperty(value = "供应链公司")
    private String supplyChainCompany;

    @ApiModelProperty(value = "登录名")
    private String loginName;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "密码")
    private String passWord;

    @ApiModelProperty(value = "手机")
    private String phone;

    @ApiModelProperty(value = "固定电话")
    private String mobile;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "国家")
    private Integer country;

    @ApiModelProperty(value = "省/州")
    private Integer province;

    @ApiModelProperty(value = "城市")
    private Integer city;

    @ApiModelProperty(value = "部门id")
    private Integer departmentId;

    @ApiModelProperty(value = "职位")
    private List<String> jobNames;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "角色")
    private List<Integer> roleIds;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "绑定类型")
    private List<UserOrgDTO> binds;
}
