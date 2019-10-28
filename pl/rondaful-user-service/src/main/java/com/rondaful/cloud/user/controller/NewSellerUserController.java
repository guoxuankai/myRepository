package com.rondaful.cloud.user.controller;

import cn.afterturn.easypoi.entity.vo.BigExcelConstants;
import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.handler.inter.IExcelExportServer;
import cn.afterturn.easypoi.view.PoiBaseView;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.user.UserAccountDTO;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.enums.UserStatusEnum;
import com.rondaful.cloud.user.model.PageDTO;
import com.rondaful.cloud.user.model.dto.SmsDTO;
import com.rondaful.cloud.user.model.dto.user.*;
import com.rondaful.cloud.user.model.request.user.SellerUpdateStatusReq;
import com.rondaful.cloud.user.model.request.user.SellerUserCenterReq;
import com.rondaful.cloud.user.model.request.user.SellerUserDetailReq;
import com.rondaful.cloud.user.model.request.user.SellerUserReq;
import com.rondaful.cloud.user.model.response.user.CheckAccount;
import com.rondaful.cloud.user.remote.EmpowerService;
import com.rondaful.cloud.user.service.IManageUserService;
import com.rondaful.cloud.user.service.IMessageService;
import com.rondaful.cloud.user.service.ISellerUserService;
import com.rondaful.cloud.user.service.impl.ExcelExportServerImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * @Author: xqq
 * @Date: 2019/4/28
 * @Description:
 */
@Api(description = "商家用户管理")
@RestController
@RequestMapping("seller/user/")
public class NewSellerUserController extends BaseController{
    private Logger logger = LoggerFactory.getLogger(NewSellerUserController.class);

    @Autowired
    private ISellerUserService userService;
    @Autowired
    private IManageUserService manageUserService;
    @Autowired
    private IMessageService messageService;
    @Autowired
    private EmpowerService empowerService;
    @Autowired
    private IExcelExportServer excelExportServerImpl;

    @AspectContrLog(descrption = "新增卖家", actionType = SysLogActionType.ADD)
    @ApiOperation(value = "卖家账号添加")
    @PostMapping("add")
    public Integer add(SellerUserReq req){
        UserCommon userCommon=super.userToken.getUserInfo().getUser();
        SellerUserDTO dto=new SellerUserDTO();
        BeanUtils.copyProperties(req,dto);
        if (UserEnum.platformType.CMS.getPlatformType().equals(userCommon.getPlatformType())&&req.getTopUserId()==null){
            dto.setParentId(0);
            dto.setLevel(1);
            dto.setParentId(0);
            dto.setTopUserId(0);
            dto.setStatus(UserStatusEnum.NO_ACTIVATE.getStatus());
        }else {
            dto.setStatus(UserStatusEnum.ACTIVATE.getStatus());
            if (req.getTopUserId()!=null){
                SellerUserDetailDTO detailDTO=this.userService.getById(req.getTopUserId());
                dto.setParentId(detailDTO.getId());
                dto.setLevel(detailDTO.getLevel()+1);
                dto.setTopUserId(detailDTO.getTopUserId()==0?detailDTO.getId():detailDTO.getTopUserId());
            }else {
                dto.setParentId(userCommon.getUserid());
                dto.setLevel(userCommon.getLevel()+1);
                dto.setParentId(userCommon.getUserid());
                dto.setTopUserId(userCommon.getTopUserId()==0?userCommon.getUserid():userCommon.getTopUserId());
            }

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
        Integer result=this.userService.add(dto);
        if (dto.getTopUserId()==0&&result>0){
            this.messageService.SendSms(req.getPhone(),IMessageService.CMS_ADD_SELLER_ACOUNT,new SmsDTO(req.getLoginName(),req.getPassWord()));
        }
        return result;
    }

    @ApiOperation(value = "一级总后台分页查询接口")
    @PostMapping("getBackPage")
    public PageDTO<BackSellerPageDTO> getBackPage(QueryBackSellerDTO dto){
        if (StringUtils.isNotEmpty(dto.getApplyStatus())){
            return this.userService.getsCreditPage(dto.getApplyStatus(),dto.getCurrentPage(),dto.getPageSize());
        }
        UserDTO userDTO=super.userToken.getUserDTO();
        List<Integer> sellerIds=new ArrayList<>();
        if (dto.getId()!=null){
            sellerIds.add(dto.getId());
        } else if (!userDTO.getManage()&&dto.getQueryType()==null){
            if (CollectionUtils.isEmpty(userDTO.getBinds())){
                return null;
            }
            List<UserOrgDTO> binds=new ArrayList<>();
            if (UserEnum.platformType.CMS.getPlatformType().equals(userDTO.getPlatformType())){
                ManageUserDetailDTO manageUser=this.manageUserService.getById(userDTO.getUserId(),null);
                binds=manageUser.getBinds();
            }else if (UserEnum.platformType.SELLER.getPlatformType().equals(userDTO.getPlatformType())){
                SellerUserDetailDTO detailDTO=this.userService.getById(userDTO.getUserId());
                binds=detailDTO.getBinds();
            }
            if (CollectionUtils.isEmpty(binds)){
                return new PageDTO<>(0L,1L);
            }
            for (UserOrgDTO dto1:binds) {
                if (UserEnum.platformType.SELLER.getPlatformType().equals(dto1.getBindType())){
                    sellerIds=JSONArray.parseArray(JSONObject.toJSONString(dto1.getBindCode()),Integer.class);
                }
            }
        }
        dto.setUserIds(sellerIds);
        return this.userService.getBackPage(dto);
    }

    @ApiOperation(value = "根据id获取一级用户信息")
    @ApiImplicitParam(name = "userId", value = "userId", dataType = "Integer",paramType = "query")
    @GetMapping("getById")
    public SellerUserDetailDTO getById(Integer userId){
        if (userId==null){
            userId=super.userToken.getUserDTO().getUserId();
        }
        return this.userService.getById(userId);
    }


    @ApiOperation(value = "根据id获取子用户信息")
    @ApiImplicitParam(name = "userId", value = "userId", dataType = "Integer",paramType = "query",required = true)
    @GetMapping("getChildById")
    public SellerUserChildDetail getChildById(Integer userId){
        return this.userService.getChildById(userId, super.request.getHeader("i18n"));
    }

    @AspectContrLog(descrption = "修改密码及供应链公司", actionType = SysLogActionType.UDPATE)
    @ApiOperation(value = "修改密码及供应链公司")
    @PostMapping("updateDetail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sellerId", value = "商家id", dataType = "Integer",paramType = "query",required = true),
            @ApiImplicitParam(name = "passWord", value = "密码", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "supplyChainCompany", value = "供应链公司", dataType = "String",paramType = "query")
    })
    public Integer updateDetail(Integer sellerId,String passWord,String supplyChainCompany){
        UserCommon userCommon=super.userToken.getUserInfo().getUser();
        return this.userService.updateDetail(sellerId,passWord,supplyChainCompany,userCommon.getLoginName());
    }

    @AspectContrLog(descrption = "一级管理员的编辑", actionType = SysLogActionType.UDPATE)
    @ApiOperation(value = "一级管理员的编辑")
    @PostMapping("update")
    public Integer update(SellerUserDetailReq req){
        UserCommon userCommon=super.userToken.getUserInfo().getUser();
        SellerUserDTO dto=new SellerUserDTO();
        BeanUtils.copyProperties(req,dto);
        dto.setUpdateDate(new Date());
        dto.setUpdateBy(userCommon.getLoginName());
        SellerCompanyDTO dto2=new SellerCompanyDTO();
        BeanUtils.copyProperties(req,dto2);
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
        return this.userService.update(dto,dto2);
    }

    @ApiOperation(value = "查询子账号列表")
    @PostMapping("getChildUser")
    public PageDTO<PithyUserDTO> getChildUser(QueryChildDTO dto){
        UserDTO userDTO=super.userToken.getUserDTO();
        if (!UserEnum.platformType.CMS.getPlatformType().equals(userDTO.getPlatformType())){
            dto.setTopUserId(userDTO.getTopUserId());
        }
        return this.userService.getChildPage(dto);
    }

    @ApiOperation(value = "获取卖家一级账号")
    @ApiImplicitParam(name = "type", value = "1:有效用户 2:审核中 3:审核失败 4:禁用 0:待激活", dataType = "Integer",paramType = "query")
    @PostMapping("getTopUser")
    public List<BindAccountDetailDTO> getTopUser(Integer type){
        if (type==null){
            type=1;
        }
        UserDTO userDTO=super.userToken.getUserDTO();
        List<Integer> userIds=new ArrayList<>();
        if (type!=null&&type==3){
            type=null;
        }else if (!userDTO.getManage()){
            List<UserAccountDTO> list1=userDTO.getBinds();
            if (CollectionUtils.isEmpty(list1)){
                return new ArrayList<>();
            }
            for (UserAccountDTO accountDTO:list1) {
                if (UserEnum.platformType.SELLER.getPlatformType().equals(accountDTO.getBindType())){
                    userIds=JSONArray.parseArray(JSONObject.toJSONString(accountDTO.getBindCode()),Integer.class);
                }
            }
            if (CollectionUtils.isEmpty(userIds)){
                return new ArrayList<>(0);
            }
        }
        return this.userService.getsParent(userIds,type).getList();
    }

    @ApiOperation(value = "获取卖家一级账号总数")
    @ApiImplicitParam(name = "type", value = "为空查询成功，2-审核列表相关", dataType = "Integer",paramType = "query")
    @PostMapping("getTopUserSize")
    public Integer getTopUserSize(Integer type){
        return this.userService.getSizeByStatus(type);
    }



    @ApiOperation(value = "绑定店铺id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userId", dataType = "Integer",paramType = "query",required = true),
            @ApiImplicitParam(name = "bind", value = "绑定关系", dataType = "String",paramType = "query"),
    })
    @PostMapping("bindEmp")
    public Integer bindEmp(Integer userId,String bind){
        if (StringUtils.isEmpty(bind)||userId==null){
            return 0;
        }
        return this.userService.bindEmp(JSONArray.parseArray(bind,UserOrgDTO.class),userId,super.userToken.getUserDTO().getLoginName());
    }

    @AspectContrLog(descrption = "删除账号", actionType = SysLogActionType.DELETE)
    @ApiOperation(value = "删除账号")
    @ApiImplicitParam(name = "userId", value = "userId", dataType = "Integer",paramType = "query",required = true)
    @PostMapping("delete")
    public Integer delete(Integer userId){
        if (userId==null){
            return 0;
        }
        return this.userService.delete(userId);
    }

    @ApiOperation(value = "根据账号id获取绑定门店名称")
    @ApiImplicitParam(name = "userId", value = "userId", dataType = "Integer",paramType = "query",required = true)
    @PostMapping("getBindAccount")
    public Object getBindAccount(Integer userId){
        SellerUserDetailDTO detailDTO=this.userService.getById(userId);
        if (detailDTO==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"error.update.object.not.exist");
        }
        if (CollectionUtils.isEmpty(detailDTO.getBinds())){
            return new ArrayList<>();
        }
        Object resp=this.empowerService.getsEmpName(JSONObject.toJSONString(detailDTO.getBinds().get(0).getBindCode()));
        if (resp==null){
            return new ArrayList<>();
        }
        JSONObject object=JSONObject.parseObject(JSONObject.toJSONString(resp));
        return object.getJSONArray("data");
    }

    @ApiOperation(value = "验证账号是否激活")
    @GetMapping("checkAccount")
    public CheckAccount checkBindPhone(){
        UserDTO userDTO=super.userToken.getUserDTO();
        if (userDTO.getManage()){
            SellerUserDetailDTO user=this.userService.getById(userDTO.getUserId());
            if (UserStatusEnum.ACTIVATE.getStatus().equals(user.getStatus())&&user.getUpdateDate().compareTo(user.getAuthDate()==null?new Date():user.getAuthDate())!=0){
                return new CheckAccount(UserStatusEnum.CHANGE.getStatus(),null);
            }else if (UserStatusEnum.ACTIVATE.getStatus().equals(user.getStatus())){
                this.userService.updateHeadImg(user.getId(),user.getHeadImg());
            }
            return new CheckAccount(user.getStatus(),user.getRemark());
        }
        return new CheckAccount(UserStatusEnum.CHANGE.getStatus(),null);
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
        SellerUserDetailDTO dto=this.userService.getByName(userDTO.getLoginName());
        if (!dto.getPassWord().equals(oldPassWord)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"error.password.not.change");
        }
        Integer result=this.userService.updateDetail(userDTO.getUserId(),newPassWord,null,userDTO.getLoginName());
        return result;
    }

    @ApiOperation(value = "修改头像")
    @ApiImplicitParam(name = "imgPath", value = "头像地址", dataType = "String",paramType = "query",required = true)
    @PostMapping("updateHeadImg")
    public Integer updateHeadImg(String imgPath){
        return this.userService.updateHeadImg(super.userToken.getUserDTO().getUserId(),imgPath);
    }

    @ApiOperation(value = "证码发送当前登录用户短信验")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号(不传默认发送到当前账号)", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "email", value = "邮箱", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "type", value = "发送类型:4-手机号绑定，13-邮箱绑定", dataType = "Integer",paramType = "query",required = true)
    })
    @GetMapping("getSmsCode")
    public void getSmsCode(String phone,String email,Integer type){
        if (StringUtils.isEmpty(phone)&&StringUtils.isEmpty(email)){
            SellerUserDetailDTO user=this.userService.getById(super.userToken.getUserDTO().getUserId());
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
            @ApiImplicitParam(name = "code1", value = "通过老联系路径获取的验证码", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "code2", value = "通过新联系路径获取的验证码", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "新手机号", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "email", value = "新邮箱", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "type", value = "发送类型:4-手机号绑定，13-邮箱绑定", dataType = "Integer",paramType = "query",required = true)
    })
    @PostMapping("checkCode")
    public Boolean updatePhone(String code1,String code2,String phone,Integer type,String email){
        SellerUserDetailDTO detailDTO=this.userService.getById(super.userToken.getUserDTO().getUserId());
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
        this.userService.updatePhone(phone,detailDTO.getId(),email);
        return true;
    }

    @ApiOperation(value = "卖家账号申请")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "msgCode", value = "验证码", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "passWord", value = "密码", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "email", value = "邮箱", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "registerSource", value = "app;pc;wechat", dataType = "String",paramType = "query")
    })
    @PostMapping("apply")
    public Integer apply(String phone,String msgCode,String passWord,String email,String registerSource){
        SellerUserDTO userDTO=new SellerUserDTO();
        if (StringUtils.isNotEmpty(phone)){
            if (!this.messageService.checkCode(phone,IMessageService.SELLER_REGISTERED_TYPE,msgCode)){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"sms.error.code");
            }
            userDTO.setLoginName(phone);
        }else if (StringUtils.isNotEmpty(email)){
            if (!this.messageService.checkEmailCode(email,IMessageService.SELLER_REGISTERED_TYPE,msgCode)){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"sms.error.code");
            }
            userDTO.setLoginName(email);
        }else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.account.not.exist");
        }

        StringBuilder userId=new StringBuilder (this.userService.getMaxId().toString());
        while (userId.length()<6){
            userId.insert(0,"0");
        }
        userId.insert(0,"UU_PL");
        userDTO.setLoginName(userId.toString());
        userDTO.setPhone(phone);
        userDTO.setEmail(email);
        userDTO.setCreateBy(phone);
        userDTO.setParentId(0);
        userDTO.setLevel(1);
        userDTO.setPassWord(passWord);
        userDTO.setParentId(0);
        userDTO.setTopUserId(0);
        userDTO.setStatus(UserStatusEnum.NO_ACTIVATE.getStatus());
        userDTO.setRegisterSource(registerSource);
        Integer result=this.userService.add(userDTO);
        return result;
    }

    @ApiOperation(value = "修改卖家状态")
    @PostMapping("updateStatus")
    public Integer updateStatus(SellerUpdateStatusReq req){
        UserDTO userDTO=super.userToken.getUserDTO();
        SellerUserDTO dto=new SellerUserDTO();
        BeanUtils.copyProperties(req,dto);
        dto.setId(req.getUserId());
        dto.setUpdateBy(userDTO.getLoginName());
        Integer result=this.userService.updateStatus(dto,req.getMainCategory());
        if (result>0){
            SellerUserDetailDTO detailDTO=this.getById(req.getUserId());
            if (UserStatusEnum.ACTIVATE.getStatus().equals(req.getStatus())){
                this.manageUserService.addBind(userDTO.getUserId(),UserEnum.platformType.SELLER.getPlatformType(),req.getUserId());
                this.messageService.SendSms(detailDTO.getPhone(),IMessageService.SELLER_AUDIT_SUCCED);
            }else if(UserStatusEnum.AUDIT_FILE.getStatus().equals(req.getStatus())){
                this.messageService.SendSms(detailDTO.getPhone(),IMessageService.SELLER_AUDIT_FILE);
            }
        }
        return result;

    }
    @ApiOperation("忘记密码发送验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "email", value = "邮箱", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "type", value = "1:卖家注册 2:忘记验证码 ", dataType = "String",paramType = "query")
    })
    @GetMapping("forget/sendMsg")
    public Boolean sendMsg(String phone,String email,Integer type){
        if (StringUtils.isNotEmpty(phone)){
            SellerUserDetailDTO user=this.userService.getByPhone(phone);
            if (type==1&&user!=null){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.phone.exist");
            }
            this.messageService.SendSms(phone,type);
        }else if(StringUtils.isNotEmpty(email)){
            SellerUserDetailDTO user=this.userService.getByEmail(email);
            if (type==1&&user!=null){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.email.exist");
            }
            this.messageService.sendEmail(email,type);
        }else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"error.account.not.exist");
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
        SellerUserDetailDTO user=null;
        if (StringUtils.isNotEmpty(phone)){
            if (!this.messageService.checkCode(phone,IMessageService.SELLER_FORGET_PASSWORD_TYPE,code)){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"sms.error.code");
            }
            user= this.userService.getByPhone(phone);
        }else if (StringUtils.isNotEmpty(email)){
            if (!this.messageService.checkEmailCode(email,IMessageService.SELLER_FORGET_PASSWORD_TYPE,code)){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"sms.error.code");
            }
            user= this.userService.getByEmail(email);
        }else {
            return false;
        }
        if (user==null||user.getId()==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"error.account.not.exist");
        }
        return this.userService.updateDetail(user.getId(),passWord,null,user.getLoginName())>0?true:false;
    }

    @ApiOperation(value = "可以自由编辑属性")
    @PostMapping("updateCenter")
    public Integer updateCenter(SellerUserCenterReq req){
        UserDTO userDTO=super.userToken.getUserDTO();
        SellerUserDTO dto=new SellerUserDTO();
        BeanUtils.copyProperties(req,dto);
        dto.setUpdateBy(userDTO.getLoginName());
        dto.setId(userDTO.getUserId());
        if (StringUtils.isNotEmpty(req.getJobNames())){
            List<String> jobs=new ArrayList<>();
            jobs.add(req.getJobNames());
            dto.setJobs(jobs);
        }
        return this.userService.updateCenter(dto);
    }

    @ApiOperation(value = "子账号绑定店铺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "子账号id", dataType = "Integer",paramType = "query"),
            @ApiImplicitParam(name = "empowerId", value = "店铺id", dataType = "Integer",paramType = "query",required = true)
    })
    @GetMapping("bindStore")
    public Integer bindStore(Integer userId,Integer empowerId){
        if (userId==null){
            userId=super.userToken.getUserDTO().getUserId();
        }
        if (empowerId==null||empowerId==0){
            return 0;
        }
        UserDTO userDTO=super.userToken.getUserDTO();
        Integer result=this.userService.bindStore(userId,empowerId);
        if (result>0&&UserEnum.platformType.SELLER.getPlatformType().equals(userDTO.getPlatformType())&&!userDTO.getManage()){
            super.updateToken(userId,empowerId);
        }
        return result;
    }

    @ApiOperation(value = "单纯校验验证码和获取验证方式的关联")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "通过老联系路径获取的验证码", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "新手机号", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "email", value = "新邮箱", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "type", value = "发送类型:4-手机号绑定，13-邮箱绑定", dataType = "Integer",paramType = "query",required = true)
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

    @ApiOperation(value = "导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "卖家主账号id", dataType = "Integer",paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "创建时间:yyyy-MM-dd HH:mm:ss", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间:yyyy-MM-dd HH:mm:ss", dataType = "String",paramType = "query")
    })
    @GetMapping("export")
    public void export(ModelMap map, Integer userId, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date createTime,@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime){

        SellerUserExportDTO dto=new SellerUserExportDTO();
        try {
            Field[] fields=dto.getClass().getDeclaredFields();

            for (int i = 1; i < fields.length; i++) {
                Field field=fields[i];
                String fileName=fields[i].getName();
                Excel excel=field.getAnnotation(Excel.class);
                System.out.println(excel.name());
                InvocationHandler invocationHandler=Proxy.getInvocationHandler(excel);
                Field declaredField =invocationHandler.getClass().getDeclaredField("memberValues");
                declaredField.setAccessible(true);
                Map memberValues = (Map) declaredField.get(invocationHandler);
                memberValues.put("name", Utils.i18n("export.execl.seller."+fileName));
            }
        } catch (Exception e) {
            logger.error("导出时反射设置国家化异常");
        }
        Map<String,Object> dataParams=new HashMap<>();
        dataParams.put("type", ExcelExportServerImpl.EXPORT_SELLER_USER);
        JSONObject jsParams=new JSONObject();
        jsParams.put("userId",userId==null?super.userToken.getUserDTO().getTopUserId():userId);
        jsParams.put("startTime",createTime);
        jsParams.put("endTime",endTime);
        dataParams.put("params",jsParams.toJSONString());
        
        ExportParams params=new ExportParams(null,"seller_user", ExcelType.XSSF);
        map.put(BigExcelConstants.CLASS,dto.getClass());
        map.put(BigExcelConstants.PARAMS,params);
        map.put(BigExcelConstants.DATA_PARAMS,dataParams);
        map.put(BigExcelConstants.DATA_INTER, excelExportServerImpl);
        map.put("fileName","seller_user");
        PoiBaseView.render(map,super.request,super.response,BigExcelConstants.EASYPOI_BIG_EXCEL_VIEW);
    }

}
