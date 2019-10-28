package com.rondaful.cloud.user.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.entity.user.UserAccountDTO;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.MD5;
import com.rondaful.cloud.user.entity.NewSellerUser;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.enums.UserStatusEnum;
import com.rondaful.cloud.user.model.PageDTO;
import com.rondaful.cloud.user.model.dto.user.*;
import com.rondaful.cloud.user.model.request.user.ManageUserReq;
import com.rondaful.cloud.user.model.response.user.BindOrgResp;
import com.rondaful.cloud.user.model.response.user.BindOrgaAllResp;
import com.rondaful.cloud.user.model.response.user.ManageUserResp;
import com.rondaful.cloud.user.service.IManageUserService;
import com.rondaful.cloud.user.service.INewSupplierService;
import com.rondaful.cloud.user.service.ISellerUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/26
 * @Description:
 */
@Api(description = "总后台用户相关")
@RestController()
@RequestMapping("user/manage/")
public class ManageUserController extends BaseController{

    @Autowired
    private IManageUserService userService;
    @Autowired
    private ISellerUserService sellerUserService;
    @Autowired
    private INewSupplierService supplierService;

    @ApiOperation(value = "新增用户")
    @PostMapping("add")
    public Integer add(ManageUserReq req){
        ManageUserDTO dto=new ManageUserDTO();
        BeanUtils.copyProperties(req,dto);
        dto.setLevel(1);

        if (StringUtils.isNotEmpty(req.getJobNames())){
            List<String> job=new ArrayList<>(1);
            job.add(req.getJobNames());
            dto.setJobNames(job);
        }
        if (StringUtils.isNotEmpty(req.getBinds())){
            dto.setBinds(JSONArray.parseArray(req.getBinds(), UserOrgDTO.class));
        }
        if (StringUtils.isNotEmpty(req.getRoles())){
            dto.setRoles(JSONArray.parseArray(req.getRoles(),Integer.class));
        }
        dto.setParentId(super.userToken.getUserDTO().getUserId());
        dto.setPlatformType(super.userToken.getUserDTO().getPlatformType());
        dto.setCreateBy(super.userToken.getUserDTO().getLoginName());
        return this.userService.add(dto);
    }

    @ApiOperation(value = "编辑用户")
    @PostMapping("update")
    public Integer update(ManageUserReq req){
        ManageUserDTO dto=new ManageUserDTO();
        BeanUtils.copyProperties(req,dto);
        if (StringUtils.isNotEmpty(req.getJobNames())){
            List<String> list=new ArrayList<>();
            list.add(req.getJobNames());
            dto.setJobNames(list);
        }
        if (StringUtils.isNotEmpty(req.getBinds())){
            dto.setBinds(JSONArray.parseArray(req.getBinds(), UserOrgDTO.class));
        }
        if (StringUtils.isNotEmpty(req.getRoles())){
            dto.setRoles(JSONArray.parseArray(req.getRoles(),Integer.class));
        }
        dto.setCreateBy(super.userToken.getUserDTO().getLoginName());
        return this.userService.update(dto);
    }

    @ApiOperation(value = "分页查询用户")
    @PostMapping("getPage")
    public PageDTO<PithyUserDTO> getPage(QuerManagePageDTO dto){

        return this.userService.getPage(dto);
    }

    @ApiOperation(value = "获取用户绑定的角色")
    @ApiImplicitParam(name = "userId", value = "用户id", dataType = "Integer", paramType = "query", required = true)
    @PostMapping("getBindAccount")
    public List<BindOrgaAllResp> getBindAccount(Integer userId){
        ManageUserDetailDTO dto=this.userService.getById(userId,null);
        if (dto==null){
            return null;
        }
        return this.getAcc(dto.getBinds());
    }


    @ApiOperation(value = "获取平台的一级账号")
    @GetMapping("getAccount")
    public List<BindAccountDTO> getAccount(){
        List<BindAccountDTO> list=new ArrayList<>();
        List<Integer> sellerIds=new ArrayList<>();
        List<Integer> supplierIds=new ArrayList<>();
        list.add(this.sellerUserService.getsParent(sellerIds, UserStatusEnum.ACTIVATE.getStatus()));
        list.add(this.supplierService.getTopUser(supplierIds));
        return list;
    }

    @ApiOperation(value = "根据id获取详情")
    @ApiImplicitParam(name = "id", value = "用户id", dataType = "Integer", paramType = "query", required = true)
    @GetMapping("getDetail")
    public ManageUserResp getDetail(Integer id){
        ManageUserResp result=new ManageUserResp();
        ManageUserDetailDTO dto=this.userService.getById(id,super.request.getHeader("i18n"));
        if (dto==null){
            return null;
        }
        BeanUtils.copyProperties(dto,result);
        result.setAccounts(this.getAcc(dto.getBinds()));
        return result;
    }

    @ApiOperation(value = "删除用户")
    @ApiImplicitParam(name = "userId", value = "用户id", dataType = "Integer", paramType = "query", required = true)
    @PostMapping("delete")
    public Integer delete(Integer userId){
        return this.userService.delete(userId);
    }

    @ApiOperation(value = "绑定账号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bind", value = "绑定关系", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "Integer", paramType = "query", required = true)
    })
    @PostMapping("bind")
    public Integer bind(String bind,Integer userId){
        return this.userService.bindAccount(JSONArray.parseArray(bind, UserOrgDTO.class),userId);
    }


    /**
     * 修改密码（需旧密码确认）
     * @param newPassWord
     * @param oldPassWord
     * @return
     */
    @ApiOperation(value = "更改当前商家密码（需旧密码确认）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "newPassWord", value = "新密码", dataType = "String",paramType = "query",required = true),
            @ApiImplicitParam(name = "oldPassWord", value = "旧密码", dataType = "String",paramType = "query",required = true)
    })
    @PostMapping("updatePassWord")
    public Integer updatePassWord(String newPassWord,String oldPassWord){
        UserDTO userDTO=super.userToken.getUserDTO();
        ManageUserDTO dto=this.userService.getByName(userDTO.getLoginName());
        if (!dto.getPassWord().equals(oldPassWord)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"error.password.not.change");
        }
        return this.userService.updatePassWord(userDTO.getUserId(),newPassWord);
    }


    /**
     * 按照前端要求返回指定结构的绑定账号树
     * @param orgs
     * @return
     */
    private List<BindOrgaAllResp> getAcc(List<UserOrgDTO> orgs){
        List<BindOrgaAllResp> account=new ArrayList<>();
        if (CollectionUtils.isEmpty(orgs)){
            return account;
        }
        for (UserOrgDTO orgDTO:orgs) {
            BindOrgaAllResp resp=new BindOrgaAllResp();
            resp.setType(orgDTO.getBindType());
            List<BindOrgResp> orgResps=new ArrayList<>();
            switch (orgDTO.getBindType()){
                case 0 :
                    for (String code:orgDTO.getBindCode()) {
                        SupplierUserDetailDTO detailDTO=this.supplierService.getById(Integer.valueOf(code));
                        orgResps.add(new BindOrgResp(code,detailDTO.getLoginName()));
                    }
                    break;
                case 1:
                    List<NewSellerUser> list=this.sellerUserService.getsName(orgDTO.getBindCode());
                    for (NewSellerUser sellerUser:list) {
                        orgResps.add(new BindOrgResp(sellerUser.getId().toString(),sellerUser.getLoginName()));
                    }
                    break;
                default:

            }
            resp.setList(orgResps);
            account.add(resp);
        }
        return account;
    }

}
