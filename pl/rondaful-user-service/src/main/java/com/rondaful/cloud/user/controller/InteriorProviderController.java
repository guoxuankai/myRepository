package com.rondaful.cloud.user.controller;

import com.rondaful.cloud.common.entity.third.AppDTO;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.user.entity.*;
import com.rondaful.cloud.user.enums.UserStatusEnum;
import com.rondaful.cloud.user.model.PageDTO;
import com.rondaful.cloud.user.model.dto.download.DownloadDTO;
import com.rondaful.cloud.user.model.dto.user.*;
import com.rondaful.cloud.user.model.response.provider.ProviderUserDTO;
import com.rondaful.cloud.user.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: xqq
 * @Date: 2019/8/9
 * @Description:
 */
@Api(description = "内部服务调用接口(新版)")
@RestController
@RequestMapping("/provider/")
public class InteriorProviderController {

    @Autowired
    private INewSupplierService supplierService;
    @Autowired
    private ISellerUserService sellerUserService;
    @Autowired
    private IManageUserService manageUserService;
    @Autowired
    private IThirdAppService thirdAppService;
    @Autowired
    private INewRoleService roleService;
    @Autowired
    private IDownloadService downloadService;


    @ApiOperation(value = "根据用户名或者id获取对应的供应链信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userid",value = "用户id", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "username",value = "用户名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "platformType",value = "用户平台：1卖家 0供应商", dataType = "Integer", paramType = "query",required = true )
    })
    @GetMapping(value = "getSupplyChinByUserIdOrUsername")
    public GetSupplyChainByUserId getSupplyChinByUserIdOrUsername(Integer userid, String username,Integer platformType){
        GetSupplyChainByUserId result=new GetSupplyChainByUserId();
        switch (platformType){
            case 1:
                if (userid==null&&StringUtils.isNotEmpty(username)){
                    SellerUserDetailDTO detailDTO=this.sellerUserService.getByName(username);
                    userid=(detailDTO==null)?null:detailDTO.getId();
                }
                SellerUserDetailDTO detailDTO=this.sellerUserService.getById(userid);
                result.setUserId(detailDTO.getId());
                result.setLoginName(detailDTO.getLoginName());
                result.setTopUserId(detailDTO.getTopUserId()==0?detailDTO.getId():detailDTO.getTopUserId());
                result.setSupplyId(StringUtils.isNotEmpty(detailDTO.getSupplyChainCompany())?Integer.valueOf(detailDTO.getSupplyChainCompany()):null);
                result.setSupplyChainCompanyName(detailDTO.getSupplyChainCompanyName());
                result.setUserName(detailDTO.getUserName());
                break;
            case 0:
                if (userid==null&&StringUtils.isNotEmpty(username)){
                    SupplierUserDetailDTO supDTO1=this.supplierService.getByName(username);
                    userid=(supDTO1==null)?null:supDTO1.getId();
                }
                SupplierUserDetailDTO supDTO2=this.supplierService.getById(userid);
                result.setUserId(supDTO2.getId());
                result.setLoginName(supDTO2.getLoginName());
                result.setTopUserId(supDTO2.getTopUserId()==0?supDTO2.getId():supDTO2.getTopUserId());
                result.setSupplyId(StringUtils.isNotEmpty(supDTO2.getSupplyChainCompany())?Integer.valueOf(supDTO2.getSupplyChainCompany()):null);
                result.setSupplyChainCompanyName(supDTO2.getSupplyChainCompanyName());
                result.setSupplierCompanyName(supDTO2.getCompanyNameUser());
                result.setUserName(supDTO2.getUserName());
                break;
            default:
                return result;
        }
        return result;
    }

    @ApiOperation(value ="根据用户id数组获取基本信息")
    @GetMapping(value = "getSupplierList")
    public List<ProviderUserDTO> getSupplierList(@RequestParam("userIds") List<Integer> userIds,String platformType){
        List<ProviderUserDTO> result=new ArrayList<>();
        for (Integer userId:userIds) {
            ProviderUserDTO userDTO=new ProviderUserDTO();
            switch (platformType){
                case "0":
                    SupplierUserDetailDTO supplierUser=this.supplierService.getById(userId);
                    if (supplierUser==null){
                        continue;
                    }
                    userDTO=new ProviderUserDTO(supplierUser.getId(),supplierUser.getLoginName(),supplierUser.getCompanyName(),supplierUser.getSupplyChainCompany(),supplierUser.getSupplyChainCompanyName());
                    userDTO.setTopUserId(supplierUser.getTopUserId()==null||supplierUser.getTopUserId()==0?userDTO.getUserId():userDTO.getTopUserId());
                    userDTO.setMaxCommodity(supplierUser.getMaxCommodity());
                    userDTO.setUserName(supplierUser.getUserName());
                    break;
                case "1":
                    SellerUserDetailDTO sellerUser = this.sellerUserService.getById(userId);
                    if (sellerUser==null){
                        continue;
                    }
                    userDTO=new ProviderUserDTO(sellerUser.getId(),sellerUser.getLoginName(),sellerUser.getCompanyName(),sellerUser.getSupplyChainCompany(),sellerUser.getSupplyChainCompanyName());
                    userDTO.setTopUserId(sellerUser.getTopUserId()==null||sellerUser.getTopUserId()==0?sellerUser.getId():sellerUser.getTopUserId());
                    userDTO.setUserName(sellerUser.getUserName());
                    break;
                case "2":
                    ManageUserDetailDTO manageUser = this.manageUserService.getById(userId,null);
                    if (manageUser==null){
                        continue;
                    }
                    userDTO=new ProviderUserDTO(manageUser.getId(),manageUser.getLoginName(),null,null,null);
                    userDTO.setTopUserId(manageUser.getTopUserId()==null||manageUser.getTopUserId()==0?manageUser.getId():manageUser.getTopUserId());
                    userDTO.setUserName(manageUser.getUserName());
                    break;
                default:
                    return new ArrayList<>();
            }
            userDTO.setPlatformType(Integer.valueOf(platformType));
            result.add(userDTO);
        }
        return result;
    }

    @ApiOperation(value = "根据用户id获取对应平台下子账号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "platformType", value = "平台类型：0  供应商，1 卖家 , 3全部", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "登录名", dataType = "String", paramType = "query")
    })
    @PostMapping("getChildAccount")
    public List<ChileUserListRequest> getChildAccount(Integer platformType, Integer userId, String userName){
        List<ChileUserListRequest> result=new ArrayList<>();
        if (userId==null&&StringUtils.isEmpty(userName)){
            return result;
        }
        switch (platformType){
            case 0:
                if (StringUtils.isNotEmpty(userName)){
                    SupplierUserDetailDTO detailDTO=this.supplierService.getByName(userName);
                    if (detailDTO==null|| detailDTO.getTopUserId()>0){
                        return new ArrayList<>();
                    }
                    userId=detailDTO.getId();
                }
                List<NewSupplierUser> list=this.supplierService.getChildName(userId);
                for (NewSupplierUser user:list) {
                    if (UserStatusEnum.ACTIVATE.getStatus().equals(user.getStatus())){
                        result.add(new ChileUserListRequest(user.getId(),user.getUserName(),platformType,user.getLoginName()));
                    }
                }
                break;
            case 1:
                if (StringUtils.isNotEmpty(userName)){
                    SellerUserDetailDTO detailDTO=this.sellerUserService.getByName(userName);
                    if (detailDTO==null||detailDTO.getTopUserId()>0){
                        return new ArrayList<>();
                    }
                    userId=detailDTO.getId();
                }
                List<NewSellerUser> list1=this.sellerUserService.getsChildName(userId);
                for (NewSellerUser user:list1) {
                    if (UserStatusEnum.ACTIVATE.getStatus().equals(user.getStatus())){
                        result.add(new ChileUserListRequest(user.getId(),user.getUserName(),platformType,user.getLoginName()));
                    }
                }
                break;
            case 2:
                if (StringUtils.isNotEmpty(userName)){
                    ManageUserDTO detailDTO=this.manageUserService.getByName(userName);
                    if (detailDTO==null||detailDTO.getTopUserId()>0){
                        return new ArrayList<>();
                    }
                    userId=detailDTO.getId();
                }
                List<ManageUser> list2=this.manageUserService.getsChildName(userId);
                if (CollectionUtils.isEmpty(list2)){
                    return result;
                }
                list2.forEach(user->{
                    result.add(new ChileUserListRequest(user.getId(),user.getUserName(), UserEnum.platformType.CMS.getPlatformType(),user.getLoginName()));
                });
                break;
            default:
                return null;
        }
        return result;
    }

    @ApiOperation(value = "根据key取得第三方应信息")
    @PostMapping("getByAppKey")
    @ApiImplicitParam(name = "appKey", value = "仓库编码", required = true, dataType = "string", paramType = "query")
    public AppDTO getByAppKey(String appKey) {
        return thirdAppService.getByAppKey(appKey);
    }

    @ApiOperation(value = "获取所有有效的应用列表")
    @GetMapping("getAppAll")
    public List<AppDTO> getAppAll() {
        return thirdAppService.getsAll();
    }

    @ApiOperation(value = "应用授权 根据绑定类型记账号获取主绑定账号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bindType", value = "0:供应商账号  1:卖家账号", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "bindCode", value = "账号id", dataType = "String", paramType = "query")
    })
    @GetMapping("getsByBindCode")
    public List<AppDTO> getsByBindCode(Integer bindType,String bindCode){
        return this.thirdAppService.getsByBindCode(bindType,bindCode);
    }

    @ApiOperation(value = "根据路由获取有对应权限的用户id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "href", value = "路由", dataType = "String", paramType = "query",required = true),
            @ApiImplicitParam(name = "platformType", value = "0:供应商 1:卖家 2:后台", dataType = "Integer", paramType = "query",required = true),
            @ApiImplicitParam(name = "loginName", value = "登录名", dataType = "String", paramType = "query")
    })
    @GetMapping("getsUserIdByHref")
    public Set<String> getsUserIdByHref(String href,Integer platformType,String loginName){
        List<String> result=new ArrayList<>();
        List<Integer> userIds=this.roleService.getsByHref(href,platformType);
        if (CollectionUtils.isNotEmpty(userIds)){
            userIds.forEach(id->{
                switch (platformType){
                    case 0:
                        SupplierUserDetailDTO detailDTO=this.supplierService.getById(id);
                        if (detailDTO!=null){
                            result.add(detailDTO.getLoginName());
                        }
                        break;
                    case 1:
                        SellerUserDetailDTO detailDTO1=this.sellerUserService.getById(id);
                        if (detailDTO1!=null){
                            result.add(detailDTO1.getLoginName());
                        }
                        break;
                    case 2:
                        ManageUserDetailDTO detailDTO2=this.manageUserService.getById(id,null);
                        if (detailDTO2!=null){
                            result.add(detailDTO2.getLoginName());
                        }
                        break;
                        default:
                }
            });

        }
        BindAccountDTO accountDTO=new BindAccountDTO();
        List<ChileUserListRequest> listRequests=this.getChildAccount(platformType,null,loginName);
        if (StringUtils.isNotEmpty(loginName)&&CollectionUtils.isEmpty(listRequests)){
            return null;
        }
        List<String> childs=new ArrayList<>();
        listRequests.forEach(listRequest->{
            childs.add(listRequest.getLoginName());
        });
        switch (platformType){
            case 1:
                accountDTO=this.sellerUserService.getsParent(null,UserStatusEnum.ACTIVATE.getStatus());
                break;
            case 2:
                break;
            case 0:
                accountDTO=this.supplierService.getTopUser(null);
                break;
            default:
                return null;
        }
        if (accountDTO!=null&&CollectionUtils.isNotEmpty(accountDTO.getList())){
            for (BindAccountDetailDTO dto:accountDTO.getList()) {
                result.add(dto.getName());
            }
        }
        if (CollectionUtils.isNotEmpty(childs)){
            childs.retainAll(result);
        }
        return new HashSet<>(StringUtils.isEmpty(loginName)?result:childs);

    }

    @ApiOperation(value = "分页获取平台有效的账号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currentPage", value = "当前页", dataType = "Integer", paramType = "query",required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", dataType = "Integer", paramType = "query",required = true),
            @ApiImplicitParam(name = "platformType", value = "1:卖家(后期其他平台想要开放)", dataType = "Integer", paramType = "query",required = true)
    })
    @GetMapping("getPageUser")
    public PageDTO getPageUser(Integer currentPage,Integer pageSize,Integer platformType){
        if (pageSize==null||currentPage==null){
            currentPage=1;
            pageSize=100;
        }
        return this.sellerUserService.getPageUser(currentPage,pageSize);
    }

    @ApiOperation(value = "根据店铺id获取子账号信息")
    @ApiImplicitParam(name = "storeId", value = "店铺授权的品连id", dataType = "Integer", paramType = "query",required = true)
    @GetMapping("getsUserByStore")
    public List<ProviderUserDTO> getsUserByStore(Integer storeId){
        if (storeId==null){
            return null;
        }
        return this.sellerUserService.getsUserByStore(storeId);
    }

    @ApiOperation(value = "导出任务创建")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "job", value = "任务名", dataType = "Integer", paramType = "query",required = true),
            @ApiImplicitParam(name = "userId", value = "当前用户id", dataType = "Integer", paramType = "query",required = true),
            @ApiImplicitParam(name = "topUserId", value = "主账号id", dataType = "Integer", paramType = "query",required = true),
            @ApiImplicitParam(name = "platformType", value = "用户平台类型", dataType = "Integer", paramType = "query",required = true)
    })
    @PostMapping("insertDown")
    public Integer insertDown(String job,Integer userId,Integer topUserId,Integer platformType){
        DownloadDTO dto =new DownloadDTO();
        dto.setJob(job);
        dto.setUserId(userId);
        dto.setTopUserId(topUserId);
        dto.setPlatformType(platformType);
        return this.downloadService.insert(dto);
    }

    @ApiOperation(value = "修改下载任务的状态")
    @PostMapping("updateDownStatus")
    public Integer updateDownStatus(Integer id,String url,Integer status){
        return this.downloadService.updateStatus(id,url,status);
    }

}
