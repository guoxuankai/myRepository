package com.rondaful.cloud.user.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.user.UserAccountDTO;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.MD5;
import com.rondaful.cloud.user.entity.NewSupplierUser;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.model.PageDTO;
import com.rondaful.cloud.user.model.dto.SmsDTO;
import com.rondaful.cloud.user.model.dto.user.*;
import com.rondaful.cloud.user.model.request.user.SupplierUserDetailReq;
import com.rondaful.cloud.user.model.request.user.SupplierUserReq;
import com.rondaful.cloud.user.model.response.user.SupplierUserDetailResp;
import com.rondaful.cloud.user.remote.RemoteSupplierService;
import com.rondaful.cloud.user.service.IManageUserService;
import com.rondaful.cloud.user.service.IMessageService;
import com.rondaful.cloud.user.service.INewSupplierService;
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
 * @Date: 2019/5/4
 * @Description:
 */
@Api(description = "供应商管理")
@RestController
@RequestMapping("user/supplier/")
public class NewSupplierController extends BaseController{

    @Autowired
    private INewSupplierService supplierService;
    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;
    @Autowired
    private RemoteSupplierService remoteSupplierService;
    @Autowired
    private IMessageService messageService;
    @Autowired
    private IManageUserService manageUserService;


    @AspectContrLog(descrption = "新增供应商账号", actionType = SysLogActionType.ADD)
    @ApiOperation(value = "新增一级账号")
    @PostMapping("add")
    public Integer add(SupplierUserReq req){
        SupplierUserDTO dto=new SupplierUserDTO();
        BeanUtils.copyProperties(req,dto);

        UserCommon userCommon=getLoginUserInformationByToken.getUserInfo().getUser();
        if (UserEnum.platformType.CMS.getPlatformType().equals(userCommon.getPlatformType())){
            if (req.getTopUserId()==null){
                dto.setParentId(0);
                dto.setTopUserId(0);
                List<String> list=new ArrayList<>();
                if (StringUtils.isEmpty(req.getProportion())){
                    list.add(req.getClosedCircle());
                }else {
                    list=JSONArray.parseArray(req.getProportion(),String.class);
                }
                dto.setClosedCircle(JSONObject.toJSONString(list));
            }else {
                SupplierUserDetailDTO detailDTO=this.supplierService.getById(req.getTopUserId());
                dto.setParentId(detailDTO.getId());
                dto.setTopUserId(detailDTO.getTopUserId()==0?detailDTO.getId():detailDTO.getTopUserId());
            }

        }else {
            dto.setParentId(userCommon.getUserid());
            dto.setParentId(userCommon.getUserid());
            dto.setTopUserId(userCommon.getTopUserId()==0?userCommon.getUserid():userCommon.getTopUserId());
        }
        if (StringUtils.isNotEmpty(req.getJobNames())){
            List<String> job=new ArrayList<>();
            job.add(req.getJobNames());
            dto.setJobs(job);
        }
        if (StringUtils.isNotEmpty(req.getRoles())){
            dto.setRoleIds(JSONArray.parseArray(req.getRoles(),Integer.class));
        }
        if (StringUtils.isNotEmpty(req.getBinds())){
            dto.setBinds(JSONArray.parseArray(req.getBinds(), UserOrgDTO.class));
        }
        dto.setCreateBy(userCommon.getLoginName());
        Integer result=this.supplierService.addUser(dto);
        if (result>0&&dto.getTopUserId()==0){
            if (userCommon.getTopUserId()!=0){
                this.manageUserService.addBind(userCommon.getUserid(),UserEnum.platformType.SUPPLIER.getPlatformType(),result);
            }
            this.messageService.SendSms(req.getPhone(),IMessageService.CMS_ADD_SUPPLIER_ACOUNT,new SmsDTO(req.getLoginName(),req.getPassWord()));
        }
        return result;
    }

    @ApiOperation(value = "获取一级账号")
    @PostMapping("getBackPage")
    public PageDTO<BackSupplierDTO> getBackPage(QueryBackSupplierDTO dto){
        UserDTO userDTO=super.userToken.getUserDTO();
        List<Integer> sellerIds=new ArrayList<>();
        PageDTO<BackSupplierDTO> result=new PageDTO<>(0L,1L);
        result.setList(new ArrayList<>());
        if (dto.getSupplierId()!=null){
            sellerIds.add(dto.getSupplierId());
        }else if (!userDTO.getManage()){
            ManageUserDetailDTO userDetailDTO=this.manageUserService.getById(userDTO.getUserId(),null);
            List<UserOrgDTO> list=userDetailDTO.getBinds();
            if (CollectionUtils.isEmpty(list)){
                return result;
            }else {
                for (UserOrgDTO dto1:userDetailDTO.getBinds()) {
                    if (dto1.getBindType()==0){
                        sellerIds= JSONArray.parseArray(JSONObject.toJSONString(dto1.getBindCode()),Integer.class);
                    }
                }
            }
            if (CollectionUtils.isEmpty(sellerIds)){
                result.setList(new ArrayList<>());
                return result;
            }
        }
        dto.setUserIds(sellerIds);
        return this.supplierService.getPageBack(dto);
    }

    @ApiOperation(value = "根据id获取登录用户信息")
    @ApiImplicitParam(name = "userId", value = "userId", dataType = "Integer",paramType = "query")
    @GetMapping("getById")
    public SupplierUserDetailResp getById(Integer userId){
        if (userId==null){
            UserDTO userDTO=super.userToken.getUserDTO();
            userId=userDTO.getUserId();
        }
        SupplierUserDetailResp resp=new SupplierUserDetailResp();
        SupplierUserDetailDTO result=this.supplierService.getById(userId);
        if (result==null){
            return resp;
        }
        BeanUtils.copyProperties(result,resp);
        if (StringUtils.isNotEmpty(result.getClosedCircle())){
            List<String> list=JSONArray.parseArray(result.getClosedCircle(),String.class);
            if (list.size()==1){
                resp.setClosedCircle(list.get(0));
            }else {
                resp.setClosedCircle("5");
                resp.setProportion(list);
            }
        }
        return resp;
    }

    @ApiOperation(value = "根据id获取子用户信息")
    @ApiImplicitParam(name = "userId", value = "userId", dataType = "Integer",paramType = "query",required = true)
    @GetMapping("getChildById")
    public SupplierUserChiletDetail getChildById(Integer userId){
        return this.supplierService.getChildById(userId,super.request.getHeader("i18n"));
    }

    @AspectContrLog(descrption = "编辑供应商账号", actionType = SysLogActionType.UDPATE)
    @ApiOperation(value = "一级管理员的编辑")
    @PostMapping("update")
    public Integer update(SupplierUserDetailReq req){
        UserCommon userCommon=getLoginUserInformationByToken.getUserInfo().getUser();
        SupplierUserDTO dto=new SupplierUserDTO();
        BeanUtils.copyProperties(req,dto);
        dto.setUpdateBy(userCommon.getLoginName());
        SupplierCompanyDTO dto2=new SupplierCompanyDTO();
        BeanUtils.copyProperties(req,dto2);
        dto.setId(req.getUserId());
        List<String> list=new ArrayList<>();
        if (StringUtils.isNotEmpty(req.getClosedCircle())&&StringUtils.isEmpty(req.getProportion())){
            list.add(req.getClosedCircle());
        }else if (StringUtils.isNotEmpty(req.getClosedCircle())&&StringUtils.isNotEmpty(req.getProportion())){
            list=JSONArray.parseArray(req.getProportion(),String.class);
        }
        if (CollectionUtils.isNotEmpty(list)){
            dto.setClosedCircle(JSONObject.toJSONString(list));
        }
        if (StringUtils.isNotEmpty(req.getJobNames())){
            List<String> job=new ArrayList<>();
            job.add(req.getJobNames());
            dto.setJobs(job);
        }
        if (StringUtils.isNotEmpty(req.getRoles())){
            dto.setRoleIds(JSONArray.parseArray(req.getRoles(),Integer.class));
        }
        if (StringUtils.isNotEmpty(req.getBinds())){
            dto.setBinds(JSONArray.parseArray(req.getBinds(), UserOrgDTO.class));
        }
        dto.setUpdateBy(userCommon.getLoginName());
        return this.supplierService.update(dto,dto2);
    }

    @ApiOperation(value = "查询子账号列表")
    @PostMapping("getChildUser")
    public PageDTO<PithyUserDTO> getChildUser(QueryChildDTO dto){
        if (dto.getPlatformType()==null||dto.getTopUserId()==null){
            UserCommon userCommon=getLoginUserInformationByToken.getUserInfo().getUser();
            dto.setTopUserId(userCommon.getParentId()==0?userCommon.getUserid():userCommon.getTopUserId());
        }
        return this.supplierService.getChildPage(dto);
    }

    @ApiOperation(value = "修改密码及供应链公司")
    @PostMapping("updateDetail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "供应商id", dataType = "Integer",paramType = "query",required = true),
            @ApiImplicitParam(name = "passWord", value = "密码", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "supplyChainCompany", value = "供应链公司", dataType = "String",paramType = "query")
    })
    public Integer updateDetail(Integer userId,String passWord,String supplyChainCompany){
        UserCommon userCommon=getLoginUserInformationByToken.getUserInfo().getUser();
        return this.supplierService.updateDetail(userId,passWord,supplyChainCompany,userCommon.getLoginName());
    }


    @ApiOperation(value = "获取供应商一级账号")
    @PostMapping("getTopUser")
    public List<BindAccountDetailDTO> getTopUser(){
        UserDTO userDTO=super.userToken.getUserDTO();
        List<Integer> userIds=new ArrayList<>();
        if (!userDTO.getManage()){
            List<UserAccountDTO> list1=userDTO.getBinds();
            if (CollectionUtils.isEmpty(list1)){
                return new ArrayList<>();
            }
            for (UserAccountDTO accountDTO:list1) {
                if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(accountDTO.getBindType())){
                    userIds=JSONArray.parseArray(JSONObject.toJSONString(accountDTO.getBindCode()),Integer.class);
                }
            }
            if (CollectionUtils.isEmpty(userIds)){
                return new ArrayList<>(0);
            }
        }
        return this.supplierService.getTopUser(userIds).getList();
    }

    @ApiOperation(value = "根据账号id获取绑仓库名称")
    @ApiImplicitParam(name = "userId", value = "userId", dataType = "Integer",paramType = "query",required = true)
    @PostMapping("getBindAccount")
    public Object getBindAccount(Integer userId){
        SupplierUserDetailDTO dto=this.supplierService.getById(userId);
        if (dto==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"error.update.object.not.exist");
        }
        if (CollectionUtils.isEmpty(dto.getBinds())){
            return new ArrayList<>();
        }
        Object resp=this.remoteSupplierService.getBindName(JSONObject.toJSONString(dto.getBinds().get(0).getBindCode()));
        if (resp==null){
            return new ArrayList<>();
        }
        JSONObject object=JSONObject.parseObject(JSONObject.toJSONString(resp));
        return object.getJSONArray("data");
    }

    @ApiOperation(value = "绑定仓库code")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userId", dataType = "Integer",paramType = "query",required = true),
            @ApiImplicitParam(name = "bind", value = "绑定关系", dataType = "String",paramType = "query"),
    })
    @PostMapping("bindEmp")
    public Integer bindHouse(Integer userId,String bind){
        if (StringUtils.isEmpty(bind)||userId==null){
            return 0;
        }
        return this.supplierService.bindHouse(userId,JSONArray.parseArray(bind,UserOrgDTO.class));
    }

    @AspectContrLog(descrption = "删除供应商账号", actionType = SysLogActionType.UDPATE)
    @ApiOperation(value = "删除账号")
    @ApiImplicitParam(name = "userId", value = "userId", dataType = "Integer",paramType = "query",required = true)
    @PostMapping("delete")
    public Integer delete(Integer userId){
        if (userId==null){
            return 0;
        }
        return this.supplierService.delete(userId);
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
        SupplierUserDetailDTO dto=this.supplierService.getByName(userDTO.getLoginName());
        if (!dto.getPassWord().equals(oldPassWord)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"error.password.not.change");
        }
        return this.supplierService.updateDetail(userDTO.getUserId(),newPassWord,null,userDTO.getLoginName());
    }

    @ApiOperation(value = "修改头像")
    @ApiImplicitParam(name = "imgPath", value = "头像地址", dataType = "String",paramType = "query",required = true)
    @PostMapping("updateHeadImg")
    public Integer updateHeadImg(String imgPath){
        UserDTO userDTO=super.userToken.getUserDTO();
        return this.supplierService.updateHeadImg(imgPath,userDTO.getUserId(),userDTO.getLoginName());
    }

    @ApiOperation(value = "发送当前登录用户短信验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号(不传默认发送到当前账号)", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "email", value = "邮箱", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "type", value = "发送类型:5-手机号绑定，14-邮箱绑定", dataType = "Integer",paramType = "query",required = true)
    })
    @GetMapping("getSmsCode")
    public void getSmsCode(String phone,String email,Integer type){
        if (StringUtils.isEmpty(phone)&&StringUtils.isEmpty(email)){
            SupplierUserDetailDTO user=this.supplierService.getById(super.userToken.getUserDTO().getUserId());
            switch (type){
                case 4:
                    phone=user.getPhone();
                    break;
                case 13:
                    email=user.getEmail();
                    break;
                default:
                    return;
            }
        }
        if (StringUtils.isNotEmpty(phone)){
            this.messageService.SendSms(phone,type);
        }else if (StringUtils.isNotEmpty(email)){
            this.messageService.sendEmail(email,type);
        }else {
            return;
        }
    }

    @ApiOperation(value = "校验验证码和密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code1", value = "老方式获取的验证码", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "code2", value = "新方式获取的验证码", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "新手机号", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "email", value = "新邮箱", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "type", value = "发送类型:5-手机号绑定，14-邮箱绑定", dataType = "Integer",paramType = "query",required = true)
    })
    @PostMapping("checkCode")
    public Boolean updatePhone(String code1,String code2,String phone,Integer type,String email){
        SupplierUserDetailDTO detailDTO=this.supplierService.getById(super.userToken.getUserDTO().getUserId());
        if (StringUtils.isNotEmpty(code1)&&StringUtils.isNotEmpty(phone)&&!this.messageService.checkCode(detailDTO.getPhone(),type,code1)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"sms.error.code");
        }else if (StringUtils.isNotEmpty(code1)&&StringUtils.isNotEmpty(email)&&!this.messageService.checkEmailCode(detailDTO.getEmail(),type,code1)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"sms.error.code");
        }
        if (StringUtils.isNotEmpty(phone)&&!this.messageService.checkCode(phone,type,code2)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"sms.error.code");
        }else if (StringUtils.isNotEmpty(email)&&!this.messageService.checkEmailCode(email,type,code2)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"sms.error.code");
        }
        this.supplierService.updatePhone(detailDTO.getId(),phone,detailDTO.getLoginName(),email);
        return true;
    }

    @ApiOperation(value = "获取所有有效的供应商公司")
    @GetMapping("getsActiveSupp")
    public List<BindAccountDetailDTO> getsActiveSupp(){
        UserDTO userDTO=super.userToken.getUserDTO();
        if (!UserEnum.platformType.CMS.getPlatformType().equals(userDTO.getPlatformType())){
            return null;
        }
        List<Integer> userIds=new ArrayList<>();
        if (!userDTO.getManage()){
            if (CollectionUtils.isEmpty(userDTO.getBinds())){
                return new ArrayList<>(0);
            }
            for (UserAccountDTO dto:userDTO.getBinds()) {
                if (UserEnum.platformType.SUPPLIER.getPlatformType().equals(dto.getBindType())){
                    userIds=JSONArray.parseArray(JSONObject.toJSONString(dto.getBindCode()),Integer.class);
                }
            }
            if (CollectionUtils.isEmpty(userIds)){
                return new ArrayList<>(0);
            }
        }
        return this.supplierService.getsActiveSupp(userIds);
    }


    @ApiOperation("忘记密码发送验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "email", value = "邮箱", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "type", value = "5:供应商注册 3:忘记验证码", dataType = "String",paramType = "query")
    })
    @GetMapping("forget/sendMsg")
    public Boolean sendMsg(String phone,String email,Integer type){
        if (StringUtils.isNotEmpty(phone)){
            if (this.supplierService.getByPhone(phone)==null){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"error.update.object.not.exist");
            }
            this.messageService.SendSms(phone,type);
        }else if(StringUtils.isNotEmpty(email)){
            this.messageService.sendEmail(email,type);
        }else {
            return false;
        }
        return true;
    }

    @ApiOperation(value = "忘记密码修改提交")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "email", value = "邮箱", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "code", value = "code", dataType = "String",paramType = "query",required = true),
            @ApiImplicitParam(name = "passWord", value = "密码", dataType = "String",paramType = "query",required = true)
    })
    @PostMapping("forget/checkMsg")
    public Boolean checkMsg(String phone,String email,String code,String passWord){
        NewSupplierUser user=null;
        if (StringUtils.isNotEmpty(phone)){
            if (!this.messageService.checkCode(phone,IMessageService.SUPPLIER_FORGET_PASSWORD_TYPE,code)){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"sms.error.code");
            }
            user= this.supplierService.getByPhone(phone);
        }else if (StringUtils.isNotEmpty(email)){
            if (!this.messageService.checkEmailCode(email,IMessageService.SUPPLIER_FORGET_PASSWORD_TYPE,code)){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"sms.error.code");
            }
            user= this.supplierService.getByEmail(email);
        }else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"error.update.object.not.exist");
        }
        if (user==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"error.update.object.not.exist");
        }
        return this.supplierService.updateDetail(user.getId(),passWord,null,user.getLoginName())>0?true:false;
    }


    @ApiOperation(value = "单纯校验验证码和获取验证方式的关联")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "通过老联系路径获取的验证码", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "新手机号", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "email", value = "新邮箱", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "type", value = "发送类型:5-手机号绑定，14-邮箱绑定", dataType = "Integer",paramType = "query",required = true)
    })
    @PostMapping("verify")
    public Boolean verify(String code,String phone,String email,Integer type){
        if (StringUtils.isNotEmpty(phone)&&this.messageService.checkCode(phone,type,code)){
            return true;
        }else if (StringUtils.isNotEmpty(email)&&this.messageService.checkEmailCode(email,type,code)){
            return true;
        }
        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"sms.error.code");
    }

}
