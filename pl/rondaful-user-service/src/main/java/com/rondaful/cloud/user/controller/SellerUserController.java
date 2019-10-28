package com.rondaful.cloud.user.controller;

import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.common.utils.RemoteUtil;
import com.rondaful.cloud.user.constants.UserConstants;
import com.rondaful.cloud.user.controller.utils.ControllerUtil;
import com.rondaful.cloud.user.entity.Companyinfo;
import com.rondaful.cloud.user.entity.Salesreturn;
import com.rondaful.cloud.user.entity.User;
import com.rondaful.cloud.user.entity.UserAndCompanyAndSalesReturnBean;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.remote.UserFinanceInitialization;
import com.rondaful.cloud.user.service.CompanyinfoService;
import com.rondaful.cloud.user.service.PublicCommomService;
import com.rondaful.cloud.user.service.SellerService;
import com.rondaful.cloud.user.service.SupplyChainCompanyService;
import com.rondaful.cloud.user.utils.ValidatorUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 卖家接口
 * @author Administrator
 *
 */
@RestController
@Api(description = "卖家相关操作")
public class SellerUserController extends ControllerUtil {
	
	@Autowired
	private RedisUtils redisUtils;
	
	@Autowired
	private PublicCommomService publicCommomService;

	@Autowired
	private SellerService sellerService;
	
	@Autowired
	private CompanyinfoService companyinfoService;

	@Autowired
	private SupplyChainCompanyService supplyChainCompanyService;

	@Autowired
	private GetLoginUserInformationByToken getLoginUserInformationByToken;

	@Autowired
	private UserFinanceInitialization userFinanceInitialization;
	
	private Logger logger = LoggerFactory.getLogger(PublicCommomController.class);


	@AspectContrLog(descrption = "卖家个人中心",actionType = SysLogActionType.QUERY)
	@ApiOperation(value ="卖家个人中心")
	@RequestMapping(value = "/login/getSellerPersonalCenter", method=RequestMethod.POST)
	@ApiImplicitParams({@ApiImplicitParam(name = "userId", value = "卖家当前登录用户id", dataType = "string",paramType = "query",required = true)})
	public UserAndCompanyAndSalesReturnBean getSellerPersonalCenter(String userId) {
		try {
			UserAndCompanyAndSalesReturnBean user = sellerService.getSellerPersonalCenter(Integer.parseInt(userId));//获取基本信息
			return user;
		} catch (GlobalException e) {
			logger.error("卖家个人中心展示失败",e.getMessage());
			throw new GlobalException(e.getErrorCode(),e.getMessage());
		} catch (Exception e){
			logger.error("卖家个人中心展示失败",e.getMessage(),e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
		}
	}

	@AspectContrLog(descrption = "卖家个人中心资料修改 ",actionType = SysLogActionType.UDPATE)
	@ApiOperation(value ="卖家个人中心资料修改")
	@RequestMapping(value="/login/sellerUpdateInfo",method=RequestMethod.POST)
	public void sellerUpdateInfo(@RequestBody UserAndCompanyAndSalesReturnBean userAndCompanyAndSalesReturnBean) {
		try {
			UserAll userAll = getLoginUserInformationByToken.getUserInfo();
			if (userAll == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406,"未登录或登录超时请重新登录");

			User user = new User();
			Companyinfo companyinfo= new Companyinfo();
			if (userAndCompanyAndSalesReturnBean != null){
			 	user = userAndCompanyAndSalesReturnBean.getUser();
				companyinfo = userAndCompanyAndSalesReturnBean.getCompanyinfo();
				if (companyinfo != null && StringUtils.isNotBlank(companyinfo.getCompanyName()))
					user.setCompanyNameUser(companyinfo.getCompanyName());
			}
			dataMatch(user);// 验证数据的有效性，即基本格式
			Integer userResult = publicCommomService.updateInfo(user,null);//进行修改用户基本资料
			//新增或修改企业信息
			if (companyinfo != null) isCompanyInfoAndSalesReturn(companyinfo,user.getPlatformType(),user.getUserid().toString());
			//卖家财务信息修改
			if (userResult != null || userResult.intValue() != 0) sellerFinanceUpdate(user,companyinfo);
		} catch (GlobalException e) {
			logger.error("用户资料修改失败",e);
			throw new GlobalException(e.getErrorCode(),e.getMessage());
		} catch (Exception e){
			logger.error("用户资料修改失败",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
		}
		logger.info("基本数据修改完毕");
	}
	/**
	 *卖家财务信息修改
	 * @param user
	 * @param companyinfo
	 * @return
	 */
	public void sellerFinanceUpdate(User user,Companyinfo companyinfo){
		if (user == null) user = new User();
		if (companyinfo == null) companyinfo = new Companyinfo();
		User supplyChainCompany = new User();
		if( user.getSupplyChainCompany() != null ) supplyChainCompany = supplyChainCompanyService.getSupplyChainCompanyUser(Integer.parseInt(user.getSupplyChainCompany()));//查询供应链名
		RemoteUtil.invoke(userFinanceInitialization.sellerUpdate(user.getUserid(),
				StringUtils.isNotBlank(user.getUsername()) ? user.getUsername() : "",
				user.getSupplyChainCompany() != null ? Integer.parseInt(user.getSupplyChainCompany()) : null,
				StringUtils.isNotBlank(supplyChainCompany.getCompanyNameUser()) ? supplyChainCompany.getCompanyNameUser() : "",
				StringUtils.isNotBlank(user.getLinkman()) ? user.getLinkman() : "",
				StringUtils.isNotBlank(user.getPhone()) ? user.getPhone() : "",
				StringUtils.isNotBlank(companyinfo.getRegArea()) ? companyinfo.getRegArea() : "",
				StringUtils.isNotBlank(companyinfo.getRegAddress()) ? companyinfo.getRegAddress() : ""));
		supplyChainCompany = null;//释放资源
		Object obj = RemoteUtil.getObject();
		boolean sellerFinanceUpdateResult = false;
		if (obj instanceof Boolean ) sellerFinanceUpdateResult = (boolean) obj;
		if ( sellerFinanceUpdateResult == false) logger.error("卖家财务信息修改失败！");
		else logger.info("卖家财务信息修改成功！");
	}
	/**
	 * 判断当前用户的的退货信息是否注册
	 * @param userId
	 * @param platformTypes
	 * @param companyinfo
	 * @return
	 */
	public void isCompanyInfoAndSalesReturn(Companyinfo companyinfo, Integer platformTypes, String userId){
		Companyinfo isCompanyinfo = null;//公司信息新增或修改
		Integer companyinfoResult = null;//公司信息注册结果判断
		if ( companyinfo != null ){
			if (platformTypes != null && StringUtils.isNotBlank(userId)) isCompanyinfo = companyinfoService.isCompanyinfo(Integer.parseInt(userId),platformTypes);
			if (isCompanyinfo == null){//isCompanyinfo为null的则代表没有注册企业信息，为新增
				companyinfo.setUserId(Integer.parseInt(userId));
				companyinfo.setPlatformType(platformTypes);
				if ( companyinfo != null ) companyinfoResult = companyinfoService.insertCompanyInfo(companyinfo);
			}else{//isCompanyinfo不为null的则代表没有注册企业信息 为修改
				companyinfo.setUserId(Integer.parseInt(userId));
				companyinfo.setPlatformType(platformTypes);
				if (companyinfo != null) companyinfoResult = companyinfoService.updateCompanyInfo(companyinfo);
			}
		}
	}

	@AspectContrLog(descrption = "卖家手机号码绑定 ",actionType = SysLogActionType.UDPATE)
	@ApiOperation(value ="卖家手机号码绑定")
	@RequestMapping(value="/login/sellerPhoneBinding",method=RequestMethod.POST)
	@ApiImplicitParams({@ApiImplicitParam(name = "userId",value = "用户id",dataType = "string",paramType = "query",required = true ),
			@ApiImplicitParam(name = "phone",value = "手机",dataType = "string",paramType = "query",required = true ),
			@ApiImplicitParam(name = "code",value = "验证码",dataType = "string",paramType = "query",required = true )})
	public void sellerPhoneBinding(String userId,String phone,String code){
		try {
			if (!ValidatorUtil.isMobile(phone)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"手机格式错误");
			if (!redisUtils.exists(phone)) {//手机验证模式下
				logger.debug("验证码失效");//没有这个验证码，说明验证码已经失效了
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100423);
			}
			String userRandomCode = (String)redisUtils.get(phone);
			if (!userRandomCode.equals(code)) {//验证码不匹配
				logger.debug("验证码不匹配");
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100424);
			}
			Integer isPhone = sellerService.isPhoneSellerUser(phone,Integer.parseInt(userId));
			if (isPhone.intValue() != 0 ) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"该手机已经绑定，请重新绑定");
			Integer result = sellerService.sellerPhoneBinding(Integer.parseInt(userId),phone);
		} catch (GlobalException e) {
			logger.error("卖家手机号码绑定失败",e);
			throw new GlobalException(e.getErrorCode(),e.getMessage());
		} catch (NumberFormatException e) {
			logger.error("卖家手机号码绑定失败",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
		}
	}

	@ApiOperation(value ="获取是否需要绑定手机信息")
	@RequestMapping(value="/login/getIsPhoneBinding",method=RequestMethod.POST)
	@ApiImplicitParams({@ApiImplicitParam(name = "userId",value = "用户id",dataType = "string",paramType = "query",required = true )})
	public boolean getIsPhoneBinding(Integer userId){
		try {
			Boolean result = sellerService.getIsPhoneBinding(userId);
			return Boolean.valueOf(result);
		} catch (Exception e) {
			logger.error("获取是否需要绑定手机信息失败",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
		}
	}
	
	
}
