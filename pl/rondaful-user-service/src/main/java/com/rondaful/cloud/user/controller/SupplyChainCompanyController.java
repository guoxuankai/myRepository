package com.rondaful.cloud.user.controller;

import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.common.utils.RemoteUtil;
import com.rondaful.cloud.user.constants.UserConstants;
import com.rondaful.cloud.user.controller.utils.ControllerUtil;
import com.rondaful.cloud.user.entity.Companyinfo;
import com.rondaful.cloud.user.entity.SupplyChainCompanyListBean;
import com.rondaful.cloud.user.entity.User;
import com.rondaful.cloud.user.entity.UserAndCompanyAndSalesReturnBean;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.remote.UserFinanceInitialization;
import com.rondaful.cloud.user.service.CompanyinfoService;
import com.rondaful.cloud.user.service.SupplyChainCompanyService;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("supplyChainCompany")
@Api(description = "供应链公司管理层")
public class SupplyChainCompanyController extends ControllerUtil {

    @Autowired
    private SupplyChainCompanyService supplyChainCompanyService;

    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;

    @Autowired
    private UserFinanceInitialization userFinanceInitialization;
    
    @Autowired
    private CompanyinfoService companyinfoService;

    private Logger logger = LoggerFactory.getLogger(SupplyChainCompanyController.class);

    @AspectContrLog(descrption = "新增供应链公司",actionType = SysLogActionType.ADD)
    @ApiOperation(value ="新增供应链公司")
    @RequestMapping(value="/insertSupplyChainCompany",method= RequestMethod.POST)
    public void insertSupplyChainCompany(@RequestBody UserAndCompanyAndSalesReturnBean userAndCompanyAndSalesReturnBean){
        try {
            User user = new User();
            Companyinfo companyinfo = new Companyinfo();
            if (userAndCompanyAndSalesReturnBean != null){
                user = userAndCompanyAndSalesReturnBean.getUser();
                companyinfo = userAndCompanyAndSalesReturnBean.getCompanyinfo();
            }
            dataMatch(user);// 验证数据的有效性，即基本格式
            user.setCompanyNameUser(companyinfo.getCompanyName() != null ? companyinfo.getCompanyName() : null);
            Integer userId = supplyChainCompanyService.insertSupplyChainCompany(user);//进行增加用户操作
            logger.info("供应链公司用户信息添加完毕！");
            companyinfo.setUserId(user.getUserid());//添加用户相关联的企业信息
            companyinfo.setPlatformType(UserConstants.SUPPLYCHAINCOMPANY);
            Integer companyinfoResult = companyinfoService.insertCompanyInfo(companyinfo);
            logger.info("供应链公司企业信息添加完毕！");
            
            //用户已添加供应商信息，进行远程调用财务接口进行供应链添加
            if ( companyinfoResult != null && companyinfoResult.intValue() != 0 ) {
            	boolean addAccountResult = false;
            	RemoteUtil.invoke(userFinanceInitialization.addAccount(
            			user.getUserid(), 
            			StringUtils.isNotBlank(companyinfo.getCompanyName()) ? companyinfo.getCompanyName() : "" ,
    					StringUtils.isNotBlank(user.getLinkman()) ? user.getLinkman() : "",
    					StringUtils.isNotBlank(user.getPhone()) ? user.getPhone() : "",
            			StringUtils.isNotBlank(companyinfo.getRegArea()) ? companyinfo.getRegArea() : "",
            			StringUtils.isNotBlank(companyinfo.getRegAddress()) ? companyinfo.getRegAddress() : ""));
            	Object obj = RemoteUtil.getObject();
            	if ( obj instanceof Boolean ) addAccountResult = (boolean)obj;
            	if (!addAccountResult) logger.error("财务供应链信息添加失败！");
            	else logger.info("财务供应链信息添加成功！");
            }
        }catch (GlobalException e) {
            logger.error("供应链公司添加失败！",e);
            throw new GlobalException(e.getErrorCode(),e.getMessage());
        }catch (Exception e) {
            logger.error("供应链公司添加失败！",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }

    }

    @AspectContrLog(descrption = "供应链公司列表",actionType = SysLogActionType.QUERY)
    @ApiOperation(value ="供应链公司列表")
    @RequestMapping(value="/supplyChainCompanyList",method= RequestMethod.POST)
    @ApiImplicitParams({ @ApiImplicitParam(name = "status", value = "供应链公司状态", dataType = "string", paramType = "query",required = false),
            @ApiImplicitParam(name = "supplyChainCompany", value = "供应链公司名称", dataType = "string", paramType = "query",required = false),
            @ApiImplicitParam(name = "createDateStart", value = "供应链公司新增开始时间", dataType = "string", paramType = "query",required = false),
            @ApiImplicitParam(name = "createDateClose", value = "供应链公司新增结束时间", dataType = "string", paramType = "query",required = false),
            @ApiImplicitParam(name = "updateDateStart", value = "供应链公司修改开始时间", dataType = "string", paramType = "query",required = false),
            @ApiImplicitParam(name = "updateDateClose", value = "供应链公司修改结束时间", dataType = "string", paramType = "query",required = false),
            @ApiImplicitParam(name = "currPage", value = "数据页数", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "row", value = "数据数量", dataType = "string", paramType = "query", required = true)})
    public Page<SupplyChainCompanyListBean> supplyChainCompanyList(String status, String supplyChainCompany, String createDateStart, String createDateClose,
                                                                   String updateDateStart, String updateDateClose, String currPage, String row ){
        Date createStart = null;
        if (StringUtils.isNotBlank(createDateStart)) createStart = DateUtils.strToDate(createDateStart,"yyyy-MM-dd HH:mm:ss");
        Date createClose = null;
        if (StringUtils.isNotBlank(createDateClose)) createClose = DateUtils.strToDate(createDateClose,"yyyy-MM-dd HH:mm:ss");
        Date updateStart = null;
        if (StringUtils.isNotBlank(updateDateStart)) updateStart = DateUtils.strToDate(updateDateStart,"yyyy-MM-dd HH:mm:ss");
        Date updateClose = null;
        if (StringUtils.isNotBlank(updateDateClose)) updateClose = DateUtils.strToDate(updateDateClose,"yyyy-MM-dd HH:mm:ss");
        List<SupplyChainCompanyListBean> supplyChainCompanyListBean = new ArrayList<SupplyChainCompanyListBean>();
        Page<SupplyChainCompanyListBean> supplyChainList = null;
        try {
            UserAll userAll = getLoginUserInformationByToken.getUserInfo();
            supplyChainList = supplyChainCompanyService.getSupplierChainCompanyUser(status,supplyChainCompany,createStart,createClose,updateStart,updateClose,currPage, row);//查询公司基础信息
        } catch (GlobalException e) {
            logger.error("供应链公司列表显示失败---》supplyChainCompanyList",e.getMessage());
            throw new GlobalException(e.getErrorCode(),e.getMessage());
        } catch (Exception e){
            logger.error("供应链公司列表显示失败---》supplyChainCompanyList",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
        return supplyChainList;//响应结果
    }

    @AspectContrLog(descrption = "供应链公司信息修改",actionType = SysLogActionType.UDPATE)
    @ApiOperation(value ="供应链公司信息修改")
    @RequestMapping(value="/supplyChainUpdateInfo",method=RequestMethod.POST)
    public void supplyChainUpdateInfo( @RequestBody UserAndCompanyAndSalesReturnBean userAndCompanyAndSalesReturnBean) {
        try {
            User user = new User();
            Companyinfo companyinfo = new Companyinfo();
            if (userAndCompanyAndSalesReturnBean != null){
                user = userAndCompanyAndSalesReturnBean.getUser();
                dataMatch(user);// 验证数据的有效性，即基本格式
                companyinfo = userAndCompanyAndSalesReturnBean.getCompanyinfo();
            }
            user.setPlatformType(UserConstants.SUPPLYCHAINCOMPANY);
            if (companyinfo.getUserId() == null) companyinfo.setUserId(user.getUserid());//进行修改用户基本资料
            if (companyinfo.getPlatformType() == null) companyinfo.setPlatformType(UserConstants.SUPPLYCHAINCOMPANY);
            Integer userResult = supplyChainCompanyService.supplyChainUpdateInfo(user,companyinfo);
            
            //进行远程调用财务接口进行供应链公司修改
            if (userResult != null && userResult.intValue() != 0) {
            	boolean updateAccountInfoResult = false;
            	RemoteUtil.invoke(userFinanceInitialization.updateAccountInfo(
            			user.getUserid(), 
            			StringUtils.isNotBlank(companyinfo.getCompanyName()) ? companyinfo.getCompanyName() : "" ,
    					StringUtils.isNotBlank(user.getLinkman()) ? user.getLinkman() : "",
    					StringUtils.isNotBlank(user.getPhone()) ? user.getPhone() : "",
            			StringUtils.isNotBlank(companyinfo.getRegArea()) ? companyinfo.getRegArea() : "",
            			StringUtils.isNotBlank(companyinfo.getRegAddress()) ? companyinfo.getRegAddress() : ""));
            	Object obj = RemoteUtil.getObject();
            	if ( obj instanceof Boolean ) updateAccountInfoResult = (boolean)obj;
            	if (!updateAccountInfoResult) logger.error("供应链公司财务编辑失败");
            	else logger.info("供应链公司财务编辑成功");
            }
        } catch (GlobalException e) {
            logger.error("用户资料修改失败",e);
            throw new GlobalException(e.getErrorCode(),e.getMessage());
        } catch (Exception e){
            logger.error("用户资料修改失败",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @AspectContrLog(descrption = "供应链公司个人中心",actionType = SysLogActionType.QUERY)
    @ApiOperation(value ="供应链公司个人中心")
    @RequestMapping(value="/getSupplyChainCompanyInfo",method=RequestMethod.POST)
    @ApiImplicitParams({@ApiImplicitParam( name = "supplyChainCompanyId", value = "供应链公司id", dataType = "string",paramType = "query",required = true) })
    public UserAndCompanyAndSalesReturnBean getSupplyChainCompanyInfo(String supplyChainCompanyId){
        UserAndCompanyAndSalesReturnBean userAndCompanyAndSalesReturnBean = new UserAndCompanyAndSalesReturnBean();//封装供应链公司信息
        try {
            User user = supplyChainCompanyService.getSupplyChainCompanyUser(Integer.parseInt(supplyChainCompanyId));//获取基本信息
            if ( user != null ){//获取企业信息
                userAndCompanyAndSalesReturnBean.setUser(user);
                Companyinfo companyinfo = companyinfoService.getSupplyChainCompanyUserCompanyInfo(user.getUserid());
                if (companyinfo != null) userAndCompanyAndSalesReturnBean.setCompanyinfo(companyinfo);
            }
            return userAndCompanyAndSalesReturnBean;
        } catch (GlobalException e) {
            logger.error("供应链公司个人中心展示失败---》getSupplyChainCompanyInfo",e.getMessage());
            throw new GlobalException(e.getErrorCode(),e.getMessage());
        } catch (Exception e){
            logger.error("供应链公司个人中心展示失败---》getSupplyChainCompanyInfo");
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    @AspectContrLog(descrption = "供应链公司的停用和启用",actionType = SysLogActionType.UDPATE)
    @ApiOperation(value = "供应链公司的停用和启用")
    @RequestMapping(value = "/supplyChainCompanyStatusUpdate",method = RequestMethod.POST)
    @ApiImplicitParams({@ApiImplicitParam(name = "status", value = "1 代表启动账户    0  代表关闭账号", dataType = "Integer",paramType = "query",required = true)})
    public void supplyChainCompanyStatusUpdate(Integer status, @ApiParam(name = "userIds", value = "需要删除的公司id,可以传多个", required = true) @RequestParam("userIds") List<Integer> userIds){
        try {
        	//用户停用供应链公司
        	Integer result = supplyChainCompanyService.supplyChainCompanyStatusUpdate(status,userIds);
        } catch (GlobalException e){
            logger.error("供应链公司的停用和启用--->supplyChainCompanyStatusUpdate",e);
            throw new GlobalException(e.getErrorCode(),e.getMessage());
        }catch (Exception e) {
            logger.error("供应链公司的停用和启用--->supplyChainCompanyStatusUpdate",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }



}
