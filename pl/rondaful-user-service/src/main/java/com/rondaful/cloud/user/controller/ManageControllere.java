package com.rondaful.cloud.user.controller;

import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.MenuCommon;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.user.constants.UserConstants;
import com.rondaful.cloud.user.controller.model.manage.CreateSellerUserBean;
import com.rondaful.cloud.user.controller.model.manage.CreateSupplierUserBean;
import com.rondaful.cloud.user.controller.model.manage.SellerUserListBean;
import com.rondaful.cloud.user.controller.model.manage.SupplierUserListBean;
import com.rondaful.cloud.user.entity.GetSupplyChainByUserId;
import com.rondaful.cloud.user.entity.SellerUsername;
import com.rondaful.cloud.user.entity.SupplyChainCompanyListBean;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.service.ILoginService;
import com.rondaful.cloud.user.service.ManageService;
import com.rondaful.cloud.user.utils.ValidatorUtil;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理后台业务接口
 *
 * @author Administrator
 * 1.卖家管理->卖家列表
 */
@Api(description = "管理后台相关操作")
@RestController
public class ManageControllere {

    @Autowired
    private ManageService manageService;
    @Autowired
    private ILoginService loginService;

    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;

    private Logger logger = LoggerFactory.getLogger(ProviderController.class);


    @AspectContrLog(descrption = "新增卖家", actionType = SysLogActionType.ADD)
    @ApiOperation(value = "新增卖家",hidden = true)
    @RequestMapping(value = "/createSellerUser", method = RequestMethod.POST)
    public void createSellerUser(CreateSellerUserBean createSellerUserBean) {
        try {
            Integer result = manageService.insertParentUser(createSellerUserBean,UserConstants.SELLER_REGISTERED_TYPE);
        }catch (GlobalException e){
            logger.error("卖家注册失败",e);
            throw new GlobalException(e.getErrorCode(),e.getMessage());
        } catch (Exception e){
            logger.error("卖家注册失败",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @AspectContrLog(descrption = "新增供应商", actionType = SysLogActionType.ADD)
    @ApiOperation(value = "新增供应商",hidden = true)
    @RequestMapping(value = "/createSupplierUser", method = RequestMethod.POST)
    public void createSupplierUser(CreateSupplierUserBean createSupplierUserBean) {
        try {
            Integer result = manageService.insertParentUser(createSupplierUserBean,UserConstants.SUPPLIERPLATFORM);
        }catch (GlobalException e){
            logger.error("添加供应商失败",e);
            throw new GlobalException(e.getErrorCode(),e.getMessage());
        }catch (Exception e) {
            logger.error("添加供应商失败",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @AspectContrLog(descrption = "卖家管理->卖家列表", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "卖家管理->卖家列表",hidden = true)
    @RequestMapping(value = "/sellerUserList", method = RequestMethod.POST)
    public Page<SupplyChainCompanyListBean> sellerUserList(SellerUserListBean sellerUserListBean) {
        Date createStart = null;
        if (StringUtils.isNotBlank(sellerUserListBean.getCreateDateStart())) createStart = DateUtils.strToDate(sellerUserListBean.getCreateDateStart(),"yyyy-MM-dd HH:mm:ss");
        Date createClose = null;
        if (StringUtils.isNotBlank(sellerUserListBean.getCreateDateClose())) createClose = DateUtils.strToDate(sellerUserListBean.getCreateDateClose(),"yyyy-MM-dd HH:mm:ss");
        Date updateStart = null;
        if (StringUtils.isNotBlank(sellerUserListBean.getUpdateDateStart())) updateStart = DateUtils.strToDate(sellerUserListBean.getUpdateDateStart(),"yyyy-MM-dd HH:mm:ss");
        Date updateClose = null;
        if (StringUtils.isNotBlank(sellerUserListBean.getUpdateDateClose())) updateClose = DateUtils.strToDate(sellerUserListBean.getUpdateDateClose(),"yyyy-MM-dd HH:mm:ss");
        Page<SupplyChainCompanyListBean> userPage = null;
        try {
            Map<String,Object> map = new HashMap<>();
            map.put("status",sellerUserListBean.getStatus() != null ? Integer.parseInt(sellerUserListBean.getStatus()) : null);
            map.put("supplierUsername",sellerUserListBean.getSellerUsername());
            map.put("supplyChainCompanyId",sellerUserListBean.getSupplyChainCompanyId() != null ? Integer.parseInt(sellerUserListBean.getSupplyChainCompanyId().trim()) : null);
            map.put("createDateStart",createStart);
            map.put("createDateClose",createClose);
            map.put("updateDateStart",updateStart);
            map.put("updateDateClose",updateClose);
            map.put("platformType",UserConstants.SELLERPLATFORM);
            userPage = manageService.getSupplierUser(map, sellerUserListBean.getCurrPage(), sellerUserListBean.getRow());
        }catch (GlobalException e){
            logger.error("卖家列表展示失败",e);
            throw new GlobalException(e.getErrorCode(),e.getMessage());
        }catch (Exception e) {
            logger.error("卖家列表展示失败",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
        return userPage;
    }

    /************************供应商管理->供应商列表************************/
    @AspectContrLog(descrption = "供应商管理->供应商列表", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "供应商管理->供应商列表",hidden = true)
    @RequestMapping(value = "/supplierUserList", method = RequestMethod.POST)
    public Page<SupplyChainCompanyListBean> supplierUserList(SupplierUserListBean supplierUserListBean) {
        Date createStart = null;
        if (StringUtils.isNotBlank(supplierUserListBean.getCreateDateStart()))createStart = DateUtils.strToDate(supplierUserListBean.getCreateDateStart(),"yyyy-MM-dd HH:mm:ss");
        Date createClose = null;
        if (StringUtils.isNotBlank(supplierUserListBean.getCreateDateClose())) createClose = DateUtils.strToDate(supplierUserListBean.getCreateDateClose(),"yyyy-MM-dd HH:mm:ss");
        Date updateStart = null;
        if (StringUtils.isNotBlank(supplierUserListBean.getUpdateDateStart()))updateStart = DateUtils.strToDate(supplierUserListBean.getUpdateDateStart(),"yyyy-MM-dd HH:mm:ss");
        Date updateClose = null;
        if (StringUtils.isNotBlank(supplierUserListBean.getUpdateDateClose()))updateClose = DateUtils.strToDate(supplierUserListBean.getUpdateDateClose(),"yyyy-MM-dd HH:mm:ss");
        Page<SupplyChainCompanyListBean> userPage = null;
        try {
            Map<String,Object> map = new HashMap<>();
            map.put("delFlag",StringUtils.isNotBlank(supplierUserListBean.getDelFlag()) ? Integer.parseInt(supplierUserListBean.getDelFlag()) : null);
            map.put("supplierUsername",supplierUserListBean.getSupplierUsername());
            map.put("supplierCompany",supplierUserListBean.getSupplierCompany());
            map.put("supplyChainCompanyId",StringUtils.isNotBlank(supplierUserListBean.getSupplyChainCompanyId()) ? Integer.parseInt(supplierUserListBean.getSupplyChainCompanyId()) : null);
            map.put("createDateStart",createStart);
            map.put("createDateClose",createClose);
            map.put("updateDateStart",updateStart);
            map.put("updateDateClose",updateClose);
            map.put("platformType",UserConstants.SUPPLIERPLATFORM);
            userPage = manageService.getSupplierUser(map, supplierUserListBean.getCurrPage(), supplierUserListBean.getRow());
        }catch (GlobalException e){
            logger.error("供应商列表请求失败",e);
            throw new GlobalException(e.getErrorCode(),e.getMessage());
        }catch (Exception e) {
            logger.error("供应商列表请求失败",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
        return userPage;
    }

    @AspectContrLog(descrption = "管理员重置密码", actionType = SysLogActionType.UDPATE)
    @ApiOperation(value = "管理员重置密码",hidden = true)
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    @ApiImplicitParams({@ApiImplicitParam(name = "userId", value = "用户id", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "platformType", value = "平台类型：0供应商 1卖家 2管理后台", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "newPassword", value = "新密码", dataType = "string", paramType = "query", required = true)})
    public void resetPassword(String userId, String platformType, String newPassword) {
        try {
            ValidatorUtil.isPassword(newPassword);//验证密码格式
            Integer result = manageService.resetPassword(Integer.parseInt(userId), Integer.parseInt(platformType), newPassword);//根据id找到对应的卖家，然后将其密码更改
        } catch (GlobalException e) {
            logger.error("重置密码失败",e);
            throw new GlobalException(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error("重置密码失败",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @AspectContrLog(descrption = "管理员修改用户状态",actionType = SysLogActionType.UDPATE)
    @ApiOperation(value = "管理员修改用户状态",hidden = true)
    @RequestMapping(value = "/manageAccountDisabled",method = RequestMethod.POST)
    @ApiImplicitParams({@ApiImplicitParam(name = "status", value = "1 代表启动账户    0  代表关闭账号", dataType = "Integer",paramType = "query",required = true)})
    public void manageAccountDisabled(Integer status,
                                      @ApiParam(name = "userIds", value = "需要删除的账户id,可以传多个", required = true) @RequestParam("userIds") List<Integer> userIds) {
        try { //修改与SessionId对应的用户的账号状态
            manageService.manageAccountDisabled(userIds, status);
        }catch (GlobalException e){
            logger.error("添加供应商失败",e);
            throw new GlobalException(e.getErrorCode(),e.getMessage());
        }catch (Exception e) {
            logger.error("添加供应商失败",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @AspectContrLog(descrption = "设置供应链",actionType = SysLogActionType.UDPATE)
    @ApiOperation(value = "设置供应链")
    @RequestMapping(value = "/setSupplyChainCompany",method = RequestMethod.POST)
    @ApiImplicitParams({@ApiImplicitParam(name = "userId", value = "用户id", dataType = "string",paramType = "query",required = true),
            @ApiImplicitParam(name = "supplyChainId", value = "供应链公司id，一定要填数字！！！！", dataType = "string",paramType = "query",required = true),
            @ApiImplicitParam(name = "platformType", value = "用户平台 0 供应商  1 卖家", dataType = "string",paramType = "query",required = true)})
    public void setSupplyChainCompany(String userId, String supplyChainId, String platformType){
        try {
            UserAll userAll = getLoginUserInformationByToken.getUserInfo();
            Map<String,Object> map = new HashMap<>();
            map.put("userId",userId != null ? Integer.parseInt(userId) : null);
            map.put("supplyChainId",supplyChainId);
            map.put("platformType",platformType != null ? Integer.parseInt(platformType) : null);
            map.put("remarks",StringUtils.isNotBlank(userAll.getUser().getUsername()) ? userAll.getUser().getUsername() : "");
            map.put("updateDate",new Date());
            Integer result = manageService.setSupplyChainCompany(map);
        } catch (Exception e) {
            logger.error("设置供应链失败",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @AspectContrLog(descrption = "卖家用户名-下拉列表", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "卖家用户名-下拉列表",hidden = true)
    @RequestMapping(value = "/sellerUsernameList", method = RequestMethod.POST)
    public List<SellerUsername> getSellerUsernameList() {
        try {
            List<SellerUsername> sellerUsernameList = manageService.getSellerUsernameList();
            return sellerUsernameList;
        } catch (Exception e) {
            logger.error("卖家用户名-下拉列表显示失败",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @AspectContrLog(descrption = "供应商用户名-下拉列表", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "供应商用户名-下拉列表",hidden = true)
    @RequestMapping(value = "/getSupplierUsernameList", method = RequestMethod.POST)
    @ApiImplicitParams({@ApiImplicitParam(name = "delFlag", value = "供应商状态，传null则表示不筛选显示全部供应商，传1则只需要激活的供应商", dataType = "Integer",paramType = "query",required = false)})
    public List<GetSupplyChainByUserId> getSupplierUsernameList(Integer delFlag) {
        try {
            List<GetSupplyChainByUserId> supplierUsernameList = manageService.getSupplierUsernameList(delFlag);
            return supplierUsernameList;
        } catch (Exception e) {
            logger.error("供应商用户名-下拉列表",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @AspectContrLog(descrption = "供应链公司-下拉列表", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "供应链公司-下拉列表")
    @RequestMapping(value = "/getSupplyChainCompanyNameList", method = RequestMethod.POST)
    public List<Map<String,Object>> getSupplyChainCompanyNameList() {
        try {
            List<Map<String,Object>> supplyChainNameList = manageService.getSupplyChainCompanyNameList();
            return supplyChainNameList;
        } catch (Exception e) {
            logger.error("供应链公司-下拉列表",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @ApiOperation(value = "获取权限接口",hidden = true)
    @GetMapping("/getMenuByToken")
    public List<MenuCommon> getMenuByToken(){
        UserAll userAll = getLoginUserInformationByToken.getUserInfo();
        if (userAll !=null) {
            return userAll.getMenus();
        }else{
            return null;
        }
    }

}

