package com.rondaful.cloud.seller.controller;
  
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rondaful.cloud.common.annotation.RequestRequire;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.utils.HttpUtil;
import com.rondaful.cloud.seller.config.AliexpressConfig;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserAccountDTO;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.seller.dto.EmpAccountDTO;
import com.rondaful.cloud.seller.entity.Empower;
import com.rondaful.cloud.seller.entity.EmpowerLog;
import com.rondaful.cloud.seller.rabbitmq.EmpowerSender;
import com.rondaful.cloud.seller.remote.RemoteAfterSalesService;
import com.rondaful.cloud.seller.remote.RemoteOrderRuleService;
import com.rondaful.cloud.seller.remote.RemoteUserService;
import com.rondaful.cloud.seller.service.AuthorizationSellerService;
import com.rondaful.cloud.seller.vo.EmpowerVo;
import com.taobao.api.internal.util.WebUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jodd.util.CollectionUtil;
import jodd.util.StringUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api("授权Controller不再提供维护，如果需要增加新功能去EmpowerController增加，通用方法支持大部分的查询")
@RestController
@Deprecated
@RequestMapping("/Authorization")
public class AuthorizationSellerController extends BaseController{

	@Autowired
	private AuthorizationSellerService authorizationSellerService;

	@Autowired
	private RemoteOrderRuleService remoteOrderRuleService;

	@Autowired
	private GetLoginUserInformationByToken getUserInfo;

	@Autowired
	private EmpowerSender empowerSender;
	
	
	private final Logger logger = LoggerFactory.getLogger(AuthorizationSellerController.class);

	@Autowired
	private RedisUtils redisUtils;

	@Autowired
	private AliexpressConfig aliexpressConfig;
	@Autowired
	private RemoteAfterSalesService remoteAfterSalesService;
	@Autowired
	private RemoteUserService remoteUserService;
	
	/**
	 * 卖家数据控制
	 * @param empower
	 * @return
	 */
	public Empower sellerDataLimit(Empower empower){
		UserDTO userDTO=getUserInfo.getUserDTO();//取出数据
		//外部平台调用
		if (UserEnum.platformType.CMS.getPlatformType().equals(userDTO.getPlatformType())){
			if (!userDTO.getManage()) {//不是主账号做的业务操作
				//是否有数据权限 没有数据权限直接返回数据null
				if (userDTO.getBinds() == null) {
					return null;
				}
				List<String> cmsBindCode = Lists.newArrayList();
				for (UserAccountDTO dto:userDTO.getBinds()) {
					//取出数据权限卖家id
					if (UserEnum.platformType.SELLER.getPlatformType().equals(dto.getBindType())){
						cmsBindCode.addAll(dto.getBindCode());
					}
				}
				//赋值父id
				empower.setPinlianId(userDTO.getTopUserId());
				empower.setCmsBindCode(cmsBindCode);
				if(cmsBindCode==null || cmsBindCode.size()==0){
					return null;
				}
			} else {
				//是主账号则直接设置当前用户id查询
//				List<String> userId=new ArrayList<>();
//				userId.add(userDTO.getUserId().toString());
//				empower.setCmsBindCode(userId);
				empower.setCmsBindCode(null);
			}
		}else if (UserEnum.platformType.SELLER.getPlatformType().equals(userDTO.getPlatformType())) {//卖家平台账号进入
			if (!userDTO.getManage()) {//子账号
				empower.setPinlianId(userDTO.getTopUserId());
				empower.setBindCode(this.getEmpowerIds(userDTO.getBinds()));
				if(empower.getBindCode()==null || empower.getBindCode().size()==0){
					return null;
				}
			} else {//主账号
				List<String> userId=new ArrayList<>();
				userId.add(userDTO.getUserId().toString());
				empower.setCmsBindCode(userId);
			}
		}else{
			return null;
		}
		return empower;
	}
	
	
	/**
	 * 授权店铺用户账号设置
	 * @param empower
	 * @return
	 */
	public Empower empowerAccountSet(Empower empower){
		
		UserDTO userDTO = getUserInfo.getUserDTO();
		String pinlianAccount ="";
		String parentAccount ="";
		Integer userid = null;
		
		if (!userDTO.getManage()) {
			userid = userDTO.getTopUserId();
			pinlianAccount = userDTO.getTopUserLoginName();
			parentAccount = userDTO.getLoginName();
		} else {
			userid = userDTO.getUserId();
			pinlianAccount = userDTO.getLoginName();
			parentAccount = userDTO.getLoginName();
		}
		
		empower.setPinlianId(userid);
		empower.setPinlianAccount(pinlianAccount);
		empower.setParentAccount(parentAccount);
		
		return empower;
	}
	
	
	
	
	/**
	 * 查询授权列表信息
	 * @param empower
	 * @return
	 */
	@ApiOperation(value = "查询授权列表信息", notes = "查询授权列表信息")
	@PostMapping("/findAll")
	@RequestRequire(require = "page, row", parameter = String.class)
	public Page<Empower> findAll(Empower empower, String page, String row) {
		try {
			empower = sellerDataLimit(empower);

			Page<Empower> findAll = authorizationSellerService.findAll(empower,page,row);
			return findAll;
		} catch (Exception e) {
			logger.error("查询授权列表失败",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"查询授权列表失败");
		}
	}


	@GetMapping("/findAllNoPage")
	@ApiOperation(value = "不分页的查询授权列表，本方法不能再定任务和多线程中调用")
	@Deprecated
	public List<Empower> findAllNoPage(Empower empower) {
		try {
			empower = sellerDataLimit(empower);
			return authorizationSellerService.findAllNoPage(empower);
		} catch (Exception e) {
			logger.error("不分页的查询授权列表异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
		}
	}

	@GetMapping("/findAllNoPageNotLimit")
	@ApiOperation(value = "不分页的查询授权列表，本方法不能再定任务和多线程中调用")
	@Deprecated
	public List<Empower> findAllNoPageNotLimit(Empower empower) {
		try {
			//empower = dataLimit(empower);
			return authorizationSellerService.findAllNoPage(empower);
		} catch (Exception e) {
			logger.error("不分页的查询授权列表异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
		}
	}



	/**
	 * @param platform       平台 (1 ebay   2 amazon 3 aliexpress 4 other)
	 * @param account        第三方的账号或id
	 * @param webName        站点名称
	 * @return 授权对象
	 */
	@ApiOperation(value = "远程查询单个授权列表信息", notes = "")
	@GetMapping("/findOneByAccount")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "empowerId", value = "刊登账号id", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "webName", value = "站点", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "account", value = "账号", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "platform", value = "平台 (1 ebay   2 amazon 3 aliexpress 4 other)", required = true)
	})
	public Empower findOneEmpowByAccount(Integer platform, String account, String webName, String empowerId) {
		try {
			if (platform != null && StringUtils.isNotBlank(account)){
				if(platform == 2 && StringUtils.isNotBlank(webName)){
					return authorizationSellerService.selectOneByAcount(new Empower() {{
						setPlatform(platform);
						setThirdPartyName(account);
						setWebName(webName);
					}});
				}else if(platform == 1) {
					return authorizationSellerService.selectOneByAcount(new Empower() {{
						setPlatform(platform);
						setAccount(account);
					}});
				}else if (platform == 3){
					Empower empower=new Empower();
					empower.setThirdPartyName(account);
					empower.setPlatform(platform);
					empower.setEmpowerId(StringUtils.isEmpty(empowerId)?null:Integer.valueOf(empowerId));
					return this.authorizationSellerService.selectOneByAcount(empower);
				}
			}
			if (StringUtils.isNotBlank(empowerId)){
				return authorizationSellerService.selectByPrimaryKey(empowerId);
			}
		}catch (Exception e){
			logger.error("查询授权信息失败",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询授权信息失败");
		}
		throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "平台或者平台账号不能为空");
	}


	/**
	 * 远程调用查询授权全部信息
	 * @param
	 * @return
	 */
	@ApiOperation(value = "远程查询授权列表信息", notes = "查询授权列表信息")
	@GetMapping("/findAllRemote")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "status", value = "未授权  1 正常授权  2授权过期 3停用", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "platform", value = "平台 (1 ebay   2 amazon 3 aliexpress 4 other)", required = true)
	})
	public List<Empower> findAllRemote(Integer platform, Integer status) {
		try {
			return authorizationSellerService.findAll(platform, status);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
		}
	}


	/**
	 * 查询授权信息
	 * @param pinlianAccount 品连账号
	 * @param status 状态 0未授权  1 正常授权  2授权过期 3停用4迁移
	 * @param account 第三方账号
	 * @param platform 平台 (1 ebay   2 amazon 3 aliexpress 4 other)
	 * @return 授权数据列表
	 */
	@ApiOperation(value = "多条件查询授权信息", notes = "多条件查询授权信息")
	@GetMapping("/selectObjectByAccount")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "empowerId", value = "刊登账号id", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "pinlianAccount", value = "品连账号", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pinlianId", value = "品连父账号id", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "status", value = "未授权  1 正常授权  2授权过期 3停用", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "account", value = "账号", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "platform", value = "平台 (1 ebay   2 amazon 3 aliexpress 4 other)", required = false)
	})
	public List<Empower> selectObjectByAccount(Integer empowerId,String pinlianAccount, Integer status,String account,Integer platform,Integer pinlianId) {
		try {
			
			return authorizationSellerService.selectObjectByAccount(empowerId, pinlianAccount, status, account, platform,pinlianId);
		} catch (Exception e) {
			logger.error("多条件查询授权信息异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"多条件查询授权信息异常");
		}
	}

	
	/**
	 * 查询授权信息
	 * @param status 状态 0未授权  1 正常授权  2授权过期 3停用4迁移
	 * @param account 第三方账号
	 * @param platform 平台 (1 ebay   2 amazon 3 aliexpress 4 other)
	 * @return 授权数据列表
	 */
	@ApiOperation(value = "多条件查询授权信息(数据权限控制)", notes = "多条件查询授权信息")
	@GetMapping("/selectObjectByAccountDataLimit")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "empowerId", value = "刊登账号id", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "status", value = "未授权  1 正常授权  2授权过期 3停用", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "account", value = "账号", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "platform", value = "平台 (1 ebay   2 amazon 3 aliexpress 4 other)", required = true)
	})
	public List<Empower> selectObjectByAccountDataLimit(Integer empowerId, Integer status,String account,Integer platform) {
		try {
			Empower empower = new Empower();
			if(empowerId != null)	                empower.setEmpowerId(empowerId);
			if(status != null)	                    empower.setStatus(status);
			if(StringUtil.isNotBlank(account))	    empower.setAccount(account);
			if(platform != null)	                empower.setPlatform(platform);
			
			empower = sellerDataLimit(empower);
			
			return authorizationSellerService.selectObjectByAccountDataLimit(empower);
		} catch (Exception e) {
			logger.error("多条件查询授权信息数据权限控制异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"多条件查询授权信息数据权限控制异常");
		}
	}
	
	
	
	
	/**
	 * 通过品连用户id查询店铺信息
	 */
	@ApiOperation(value = "通过品连账号查询店铺信息", notes = "通过品连账号查询店铺信息")
	@PostMapping("/selectInfoByUserIds")
	public List<EmpowerVo> selectInfoByUserIds(@RequestBody  List<Integer>  pinlianIds) {
		try {
			return authorizationSellerService.selectInfoByUserIds(pinlianIds);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("通过品连账号查询店铺信息异常",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"通过品连账号查询店铺信息异常");
		}
	}
	

	
	/**
	 * 通过品连账号查询店铺信息
	 */
	@ApiOperation(value = "通过品连账号查询店铺信息", notes = "通过品连账号查询店铺信息")
	@ApiImplicitParam(name = "pinlianAccounts", value = "账号json数组.toSring", dataType = "String",paramType = "query")
	@PostMapping("/selectInfoByAccount")
	public List<EmpAccountDTO> selectInfoByAccount(String  pinlianAccounts) {
		if (StringUtils.isEmpty(pinlianAccounts)){
			UserAll userAll=getUserInfo.getUserInfo();
			if (UserEnum.platformType.SELLER.getPlatformType().equals(userAll.getUser().getPlatformType())){
				List<String> list=new ArrayList<>(1);
				if (userAll.getUser().getTopUserId()==0){
					list.add(userAll.getUser().getLoginName());
					pinlianAccounts=JSONObject.toJSONString(list);
				}else {
					list.add(userAll.getParentUser().getLoginName());
					pinlianAccounts=JSONObject.toJSONString(list);
				}
			}else {
				return new ArrayList<>(0);
			}
		}
		return authorizationSellerService.selectInfoByAccounts(JSONArray.parseArray(pinlianAccounts,String.class));
	}

	/**
	 * 通过品连账号查询店铺信息
	 */
	@ApiOperation(value = "通过品连账号查询店铺信息", notes = "通过品连账号查询店铺信息")
	@ApiImplicitParam(name = "empIds", value = "账号json数组.toSring", dataType = "String",paramType = "query",required = true)
	@PostMapping("/getEmpNameByIds")
	public List<EmpAccountDTO> getEmpNameByIds(String  empIds) {
		if (StringUtils.isEmpty(empIds)){
			return null;
		}
		return authorizationSellerService.getEmpNameByIds(JSONArray.parseArray(empIds,Integer.class));
	}
	
	
	

	/**
	 * 亚马逊店铺授权信息入库
	 * @param empower
	 */
	@ApiOperation(value = "亚马逊授权信息入库", notes = "授权信息入库")
	@PostMapping("/insertObjectAmazon")
	public void insertObjectAmazon(Empower empower) {
		
		if(authorizationSellerService.selectAllAccounts().contains(empower.getAccount())){
		    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "店铺名称已存在!");
	    }

		Integer checkAccount = authorizationSellerService.checkAccount(empower.getWebName(), empower.getThirdPartyName());
		if(checkAccount > 0){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"该授权信息已存在");
		}


		String checkAmazonTokenIsValid = remoteOrderRuleService.checkAmazonTokenIsValid(empower.getThirdPartyName(), empower.getWebName(), empower.getToken());
		String returnRemoteResultDataString = Utils.returnRemoteResultDataString(checkAmazonTokenIsValid, "转换失败");

		String parse = JSONObject.parse(returnRemoteResultDataString).toString();
		if ("1".equals(parse)) {
			
			empower = empowerAccountSet(empower);
			
			try {
				Integer empowerId = authorizationSellerService.insertAuthorizationSellerAmazon(empower);
				empower.setEmpowerId(empowerId);
			} catch (Exception e) {
				logger.error("亚马逊授权信息入库失败",e);
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"亚马逊授权信息入库失败");
			}

			if(1 == empower.getRentStatus()){
				try {
					//卖家供应链ID,名称
					String company ="";
					Object sell = remoteUserService.getSupplyChinByUserIdOrUsername(empower.getPinlianId(), null, 1);
                    if(sell!=null) {
                        JSONObject selljs = (JSONObject) JSONObject.toJSON(sell);
                        if ("true".equals(selljs.getString("success"))) {
                            JSONObject jsonObjectcompany = selljs.getJSONObject("data");
                            company = jsonObjectcompany.getString("supplyId");

                        }
                    }
					EmpowerVo vo = new EmpowerVo();
					vo.setPinlianAccount(empower.getPinlianAccount());
					vo.setCompany(company);
					vo.setAccount(empower.getAccount());
					vo.setPlatform(empower.getPlatform());
					empowerSender.send(vo);
				} catch (Exception e) {
					logger.error("授权消息发送失败",e);
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"授权消息发送失败");
				}
			}

//			//卖家供应链ID,名称
//			String company ="";
//			Object sell = remoteUserService.getSupplyChinByUserIdOrUsername(empower.getPinlianId(), null, 1);
//			JSONObject selljs = (JSONObject) JSONObject.toJSON(sell);
//			if ("true".equals(selljs.get("success"))) {
//				JSONObject jsonObjectcompany =JSONObject.parseObject(selljs.getJSONArray("data").toJSONString());
//				company = jsonObjectcompany.getString("supplyId");
//			}
//			String s = remoteAfterSalesService.updateEmpowerRent(empower.getPinlianAccount(),company,empower.getAccount(),empower.getPlatform()+"");
//			System.out.println(s);
		}else {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"亚马逊授权信息有误");
		}
	}



	/**
	 * 亚马逊店铺重新授权
	 * @param empower
	 */
	@ApiOperation(value = "亚马逊重新授权信息入库")
	@PostMapping("/updateObjectAmazon")
	public void updateObjectAmazon(Empower empower) {
			try {
				authorizationSellerService.updateSelectiveAmazon(empower);
			} catch (Exception e) {
				logger.error("亚马逊重新授权失败",e);
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"亚马逊重新授权失败");
			}
	}




	/**
	 * ebay信息入库
	 * @param empower
	 */
	@ApiOperation(value = "ebay信息入库", notes = "ebay信息入库")
	@PostMapping("/insertObjectEbay")
	public void insertObjectEbay(Empower empower) {
		if(authorizationSellerService.selectAllAccounts().contains(empower.getAccount())){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "店铺名称已存在!");
		}
		
		try {
			String sessionID = (String)redisUtils.get("sessionID"+getUserInfo.getUserInfo().getUser().getUserid().toString());
			String tokenAndTime = authorizationSellerService.getToken(sessionID);
			redisUtils.remove("sessionID"+getUserInfo.getUserInfo().getUser().getUserid().toString());

			String[] split = tokenAndTime.split("@@@");
			String token = split[0];
			String endTime = split[1];
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date time = sdf.parse(endTime);

			empower = empowerAccountSet(empower);
			
			empower.setPlatform(1);
			empower.setStatus(1);
			empower.setToken(token);
			empower.setCreateTime(new Date());
			empower.setAutoTime(new Date());
			empower.setEndTime(time);

			//将用户输入的基本信息写入数据库   平台  账号  授权时间  到期时间  状态值   paypal账号01
			Integer empowerId = authorizationSellerService.insertAuthorizationSellerEbay(empower);
			empower.setEmpowerId(empowerId);
			
			if(1 == empower.getRentStatus()){
				try {
					//卖家供应链ID,名称
					String company ="";
					Object sell = remoteUserService.getSupplyChinByUserIdOrUsername(empower.getPinlianId(), null, 1);
                    if(sell!=null) {
                        JSONObject selljs = (JSONObject) JSONObject.toJSON(sell);
                        if ("true".equals(selljs.getString("success"))) {
                            JSONObject jsonObjectcompany = selljs.getJSONObject("data");
                            company = jsonObjectcompany.getString("supplyId");

                        }
                    }
					EmpowerVo vo = new EmpowerVo();
					vo.setPinlianAccount(empower.getPinlianAccount());
					vo.setCompany(company);
					vo.setAccount(empower.getAccount());
					vo.setPlatform(empower.getPlatform());
					empowerSender.send(vo);
				} catch (Exception e) {
					logger.error("授权消息发送失败",e);
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"授权消息发送失败");
				}
			}
//			//卖家供应链ID,名称
//			String company ="";
//			Object sell = remoteUserService.getSupplyChinByUserIdOrUsername(empower.getPinlianId(), null, 1);
//			JSONObject selljs = (JSONObject) JSONObject.toJSON(sell);
//			if ("true".equals(selljs.get("success"))) {
//				JSONObject jsonObjectcompany =JSONObject.parseObject(selljs.getJSONArray("data").toJSONString());
//				company = jsonObjectcompany.getString("supplyId");
//			}
//			String s = remoteAfterSalesService.updateEmpowerRent(empower.getPinlianAccount(),company,empower.getAccount(),empower.getPlatform()+"");
		} catch (Exception e) {
			logger.error("ebay授权失败",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"ebay授权失败");
		}

	}



	/**
	 * ebay店铺重新授权
	 * @param empower
	 */
	@ApiOperation(value = "ebay店铺重新授权")
	@PostMapping("/updateObjectEbay")
	public void updateObjectEbay(Empower empower) {
		try {
			String sessionID = (String)redisUtils.get("sessionID"+getUserInfo.getUserInfo().getUser().getUserid().toString());

			String tokenAndTime = authorizationSellerService.getToken(sessionID);
			redisUtils.remove("sessionID"+getUserInfo.getUserInfo().getUser().getUserid().toString());


			String[] split = tokenAndTime.split("@@@");
			String token = split[0];
			String endTime = split[1];

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date time = sdf.parse(endTime);

			empower.setStatus(1);
			empower.setToken(token);
			empower.setEndTime(time);
			empower.setUpdateTime(new Date());
			
			//将用户输入的基本信息写入数据库   平台  账号  授权时间  到期时间  状态值   paypal账号01
			authorizationSellerService.updateSelectiveEbay(empower);

		} catch (Exception e) {
			logger.error("ebay重新授权失败",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"ebay重新授权失败");
		}

	}


	/**
	 * 获取ebay请求的url
	 */
	@ApiOperation(value = "获取ebay授权的url", notes = "获取ebay授权的url")
	@GetMapping("/getUrl")
	public String getUrl() {
		try {
			String url = authorizationSellerService.getUrl();
			String[] split = url.split("=");
			String sessionID = split[split.length -1];
			redisUtils.set("sessionID"+getUserInfo.getUserInfo().getUser().getUserid().toString(), sessionID);
			return url;
		} catch (Exception e) {
			logger.error("获取ebay授权的url失败",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"获取ebay授权的url失败");
		}

	}


	@ApiOperation(value = "ebay授权编辑", notes = "ebay授权编辑")
	@PostMapping("/editAuthorizationEbay")
	public void editAuthorizationEbay(Empower empower){
		if (StringUtils.isBlank(empower.getAccount()) || StringUtils.isBlank(empower.getPaypalAccount01()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "自定义名称或payPal账号不能为空！");
		
		if(authorizationSellerService.selectOtherAccounts(empower.getEmpowerId()).contains(empower.getAccount())){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "店铺名称已存在!");
		}
		
		try {
			authorizationSellerService.editAuthorization(empower);
		} catch (Exception e) {
			logger.error("ebay授权编辑失败",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"ebay授权编辑失败");
		}
	}


	@ApiOperation(value = "Amazon授权编辑", notes = "Amazon授权编辑")
	@PostMapping("/editAuthorizationAmazon")
	public void editAuthorizationAmazon(Empower empower){
		if (StringUtils.isBlank(empower.getAccount()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "自定义名称不能为空！");

		if(authorizationSellerService.selectOtherAccounts(empower.getEmpowerId()).contains(empower.getAccount())){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "店铺名称已存在!");
		}
		
		try {
			authorizationSellerService.editAuthorization(empower);
		} catch (Exception e) {
			logger.error("Amazon授权编辑失败",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"Amazon授权编辑失败");
		}
	}

	@ApiOperation(value = "Aliexpress授权编辑", notes = "Aliexpress授权编辑")
	@PostMapping("/editAuthorizationAliexpress")
	public void editAuthorizationAliexpress(Empower empower){
		if (StringUtils.isBlank(empower.getAccount()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "自定义名称不能为空！");

		if(authorizationSellerService.selectOtherAccounts(empower.getEmpowerId()).contains(empower.getAccount())){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "店铺名称已存在!");
		}
		
		try {
			authorizationSellerService.editAuthorization(empower);
		} catch (Exception e) {
			logger.error("Aliexpress授权编辑失败",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"Aliexpress授权编辑失败");
		}
	}

	
	@ApiOperation(value = "其他平台授权编辑", notes = "其他授权编辑")
	@PostMapping("/editAuthorizationOther")
	public void editAuthorizationOther(Empower empower){
		if (StringUtils.isBlank(empower.getAccount()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "自定义名称不能为空！");

		if(authorizationSellerService.selectOtherAccounts(empower.getEmpowerId()).contains(empower.getAccount())){
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "店铺名称已存在!");
		}
		
		try {
			authorizationSellerService.editAuthorization(empower);
		} catch (Exception e) {
			logger.error("其他平台授权编辑失败",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"其他平台授权编辑失败");
		}
	}
	
	

    
    
    


	/**
	 * 根据用户的id获取授权token
	 * @param sellerId
	 * @return
	 */
	@ApiOperation(value = "获取token", notes = "通过用户id获取token")
	@GetMapping("/getToken/{sellerId}")
	public String getToken(@PathVariable Integer sellerId) {
		try {
			return authorizationSellerService.getTokenBySellerId(sellerId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
		}
	}


	/**
	 * 通过账号名称停用
	 *
	 * @param account
	 * @return
	 */
	@ApiOperation(value = "通过账号名称停用", notes = "通过账号名称停用")
	@GetMapping("/blockUpByAccount")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "id", value = "刊登账号id", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "account", value = "账号", required = false)
	})
	public int blockUpByAccount(String account,Integer id) {
		try {
			return authorizationSellerService.blockUpByAccount(account,id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"通过账号名称停用异常");
		}

	}


	/**
	 * 通过账号名称停用
	 * @param account
	 * @return
	 */
	@ApiOperation(value = "通过账号名称启用", notes = "通过账号名称启用")
	@GetMapping("/startUsingByAccount")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "id", value = "刊登账号id", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "account", value = "账号", required = false)
	})
	public int startUsingByAccount(String account,Integer id) {
		try {
			return authorizationSellerService.startUsingByAccount(account,id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"通过账号名称启用异常");
		}

	}


	/**
	 * 查询当前账户下的账号
	 */
	@ApiOperation(value = "查询当前账户下的账号", notes = "查询当前账户下的账号")
	@PostMapping("/selectAccounts")
	public List<String> selectAccounts(){
		try {
			return authorizationSellerService.selectAccounts();
		} catch (Exception e) {
			e.printStackTrace();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"查询账户失败");
		}
	}


	/**
	 * 查询所有账户
	 */
	@ApiOperation(value = "查询所有账户", notes = "查询所有账户")
	@PostMapping("/selectAllAccounts")
	public List<String> selectAllAccounts(){
		try {
			return authorizationSellerService.selectAllAccounts();
		} catch (Exception e) {
			e.printStackTrace();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"查询所有账户失败");
		}
	}



	/**
	 * 查询所有账户
	 */
	@ApiOperation(value = "查询日志", notes = "查询日志")
	@GetMapping("/selectLogById")
	public List<EmpowerLog> selectLogById(Integer id){
		try {
			String handler = getUserInfo.getUserInfo().getUser().getLoginName();
			return authorizationSellerService.selectLogById(id,handler);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"查询日志失败");
		}
	}


	/**
	 * Aliexpress 授权地址
	 * @param
	 */
	@ApiOperation(value = "Aliexpress 授权地址", notes = "Aliexpress 授权地址")
	@PostMapping("/getAliexpressTokenUrl")
	public String getAliexpressTokenUrl()
	{
		StringBuffer str = new StringBuffer("https://oauth.aliexpress.com/authorize?response_type=code&" +
				"client_id="+aliexpressConfig.getAppKey()+"&redirect_uri="+aliexpressConfig.getUrl()+"&state=1212&view=web&sp=ae");
		return  str.toString();
	}

	/**
	 * Aliexpress信息授权入库
	 * @param code
	 */
	@ApiOperation(value = "Aliexpress信息授权入库", notes = "Aliexpress信息授权入库")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "code", value = "授权回调地址code参数", dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "account", value = "店铺账号", dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "rentStatus", value = "租赁标示", dataType = "Integer", paramType = "query")
	})
	@PostMapping("/insertAliexpress")
	public void insertAliexpress(String code,String account,Integer rentStatus) {
		try {

			String url="https://oauth.aliexpress.com/token";
			Map<String,String> props=new HashMap<String,String>();
			props.put("grant_type","authorization_code");
			/*测试时，需把test参数换成自己应用对应的值*/
			props.put("code",code);
			props.put("client_id",aliexpressConfig.getAppKey());
			props.put("client_secret",aliexpressConfig.getAppSecret());
			//前端地址
			props.put("redirect_uri",aliexpressConfig.getUrl());
			props.put("view","web");
			props.put("sp","ae");
			String josnStr = WebUtils.doPost(url, props, 30000, 30000);

//			{
//				"access_token": "5000***gC0",
//				"refresh_token": "5000***HHk1",
//				"w1_valid": 1559008461793,
//				"refresh_token_valid_time": 1527472460769,
//				"w2_valid": 1527474260769,
//				"user_id": "706388888",
//				"expire_time": 1559008461793,
//				"r2_valid": 1527731660769,
//				"locale": "zh_CN",
//				"r1_valid": 1559008461793,
//				"sp": "ae",
//				"user_nick": "cn10001234"
//			}
			logger.info("Aliexpress授权结果=",josnStr);
			JSONObject jsonObject = JSONObject.parseObject(josnStr);
			String accessToken = jsonObject.get("access_token")==null?null:jsonObject.get("access_token").toString();
			String refreshToken = jsonObject.get("refresh_token")==null?null:jsonObject.get("refresh_token").toString();
			String userNick = jsonObject.get("user_nick")==null?null:jsonObject.get("user_nick").toString();
			String userId = jsonObject.get("user_id")==null?null:jsonObject.get("user_id").toString();
			Long expireTime = jsonObject.get("expire_time")==null?null:Long.valueOf(jsonObject.get("expire_time").toString());
			if(accessToken==null || expireTime==null){
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,ResponseCodeEnum.RETURN_CODE_100400.getMsg());
			}else{
				String oauthUrl = aliexpressConfig.getAliexpressUrl()+"/aliexpress/oauth/permit";
				Map<String, String> paramsMap = Maps.newHashMap();
				paramsMap.put("token",accessToken);
				HttpUtil.post(oauthUrl, paramsMap);
			}
//			Integer checkAccount = authorizationSellerService.checkAccount(null, userNick);
//			if(checkAccount > 0){
//				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"该授权信息已存在");
//			}
			UserDTO userDTO = getUserInfo.getUserDTO();
			String pinlianAccount ="";
			String parentAccount ="";
			Integer userid = null;
			if (!userDTO.getManage()) {
				userid = userDTO.getTopUserId();
				pinlianAccount = userDTO.getTopUserLoginName();
			} else {
				userid = userDTO.getUserId();
				pinlianAccount = userDTO.getLoginName();
			}
			//查询是否存在
			Empower queryEmpower = new Empower();
			queryEmpower.setThirdPartyName(userId);
			queryEmpower.setPlatform(3);
			Empower oldEmpower = authorizationSellerService.selectAmazonAccount(queryEmpower);
			if(oldEmpower==null || oldEmpower.getEmpowerId()==null){
				Empower empower = new Empower();
				empower.setAccount(userNick);
				empower.setToken(accessToken);
				empower.setRefreshToken(refreshToken);
				empower.setThirdPartyName(userId);
				empower.setPlatform(3);
				//获取品连账号

				empower.setPinlianAccount(pinlianAccount);
				empower.setPinlianId(userid);
				empower.setNickName(pinlianAccount);
				empower.setStatus(1);
				//过期时间
				Date time = new Date(expireTime);
				empower.setCreateTime(new Date());
				empower.setAutoTime(new Date());
				empower.setEndTime(time);
				empower.setPaypalAccount01(account);
				empower.setRentStatus(rentStatus);
				
				//将用户输入的基本信息写入数据库   平台  账号  授权时间  到期时间  状态值   paypal账号01
				Integer empowerId = authorizationSellerService.insertAuthorizationSellerAliexpress(empower);
				empower.setEmpowerId(empowerId);

				if(1 == empower.getRentStatus()){
					try {
						//卖家供应链ID,名称
						String company ="";
						Object sell = remoteUserService.getSupplyChinByUserIdOrUsername(empower.getPinlianId(), null, 1);
                        if(sell!=null) {
                            JSONObject selljs = (JSONObject) JSONObject.toJSON(sell);
                            if ("true".equals(selljs.getString("success"))) {
                                JSONObject jsonObjectcompany = selljs.getJSONObject("data");
                                company = jsonObjectcompany.getString("supplyId");

                            }
                        }
						EmpowerVo vo = new EmpowerVo();
						vo.setPinlianAccount(empower.getPinlianAccount());
						vo.setCompany(company);
						vo.setAccount(empower.getAccount());
						vo.setPlatform(empower.getPlatform());
						empowerSender.send(vo);
					} catch (Exception e) {
						logger.error("授权消息发送失败",e);
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"授权消息发送失败");
					}
				}
//				//卖家供应链ID,名称
//				String company ="";
//				Object sell = remoteUserService.getSupplyChinByUserIdOrUsername(empower.getPinlianId(), null, 1);
//				JSONObject selljs = (JSONObject) JSONObject.toJSON(sell);
//				if ("true".equals(selljs.get("success"))) {
//					JSONObject jsonObjectcompany =JSONObject.parseObject(selljs.getJSONArray("data").toJSONString());
//					company = jsonObjectcompany.getString("supplyId");
//				}
//				String s = remoteAfterSalesService.updateEmpowerRent(empower.getPinlianAccount(),company,empower.getAccount(),empower.getPlatform()+"");
			}else {

				if(oldEmpower.getPinlianAccount()!=null && !oldEmpower.getPinlianAccount().equals(pinlianAccount)){
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"该授权信息已存在");
				}
				Empower empower = new Empower();
				empower.setEmpowerId(oldEmpower.getEmpowerId());
				empower.setToken(accessToken);
				empower.setRefreshToken(refreshToken);
				Date time = new Date(expireTime);
				empower.setAutoTime(new Date());
				empower.setEndTime(time);
				empower.setStatus(1);
				authorizationSellerService.updateSelectiveAliexpress(empower);
			}
		}catch(GlobalException e){
			throw e;
		} catch (Exception e) {
			logger.error("Aliexpress授权失败",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,ExceptionUtils.getMessage(e));
		}

	}

	/**
	 * Aliexpress 重新授权
	 * @param code
	 */
	@ApiOperation(value = "Aliexpress重新授权", notes = "Aliexpress重新授权")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "type", value = "重新授权类型1 code授权 2刷新授权", dataType = "int", paramType = "query")
	})
	@PostMapping("/updateAliexpress")
	public void updateAliexpress(Integer type,String code,String empowerId) {
		try {
			Empower queryEmpower = authorizationSellerService.selectByPrimaryKey(empowerId);

			if(type==1) {
				String url = "https://oauth.aliexpress.com/token";
				Map<String, String> props = new HashMap<String, String>();
				props.put("grant_type", "refresh_token");
				/*测试时，需把test参数换成自己应用对应的值*/
				props.put("refresh_token", queryEmpower.getRefreshToken());
				props.put("client_id", aliexpressConfig.getAppKey());
				props.put("client_secret", aliexpressConfig.getAppSecret());
				props.put("sp", "ae");
				String josnStr = WebUtils.doPost(url, props, 30000, 30000);


				logger.info("Aliexpress授权结果=", josnStr);
				JSONObject jsonObject = JSONObject.parseObject(josnStr);
				String accessToken = jsonObject.get("access_token") == null ? null : jsonObject.get("access_token").toString();
				String refreshToken = jsonObject.get("refresh_token") == null ? null : jsonObject.get("refresh_token").toString();
				String userNick = jsonObject.get("user_nick") == null ? null : jsonObject.get("user_nick").toString();
				String userId = jsonObject.get("user_id") == null ? null : jsonObject.get("user_id").toString();
				Long expireTime = jsonObject.get("expire_time") == null ? null : Long.valueOf(jsonObject.get("expire_time").toString());
				if (accessToken == null || expireTime == null) {
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, ResponseCodeEnum.RETURN_CODE_100500.getMsg());
				}
				Empower empower = new Empower();
				empower.setEmpowerId(queryEmpower.getEmpowerId());
				empower.setToken(accessToken);
				empower.setRefreshToken(refreshToken);
				Date time = new Date(expireTime);
				empower.setAutoTime(new Date());
				empower.setEndTime(time);
				authorizationSellerService.updateSelectiveAliexpress(empower);
			}else if(type==2){
				String url="https://oauth.aliexpress.com/token";
				Map<String,String> props=new HashMap<String,String>();
				props.put("grant_type","authorization_code");
				/*测试时，需把test参数换成自己应用对应的值*/
				props.put("code",code);
				props.put("client_id",aliexpressConfig.getAppKey());
				props.put("client_secret",aliexpressConfig.getAppSecret());
				//前端地址
				props.put("redirect_uri",aliexpressConfig.getUrl());
				props.put("view","web");
				props.put("sp","ae");
				String josnStr = WebUtils.doPost(url, props, 30000, 30000);

				logger.info("Aliexpress授权结果=",josnStr);
				JSONObject jsonObject = JSONObject.parseObject(josnStr);
				String accessToken = jsonObject.get("access_token")==null?null:jsonObject.get("access_token").toString();
				String refreshToken = jsonObject.get("refresh_token")==null?null:jsonObject.get("refresh_token").toString();
				String userNick = jsonObject.get("user_nick")==null?null:jsonObject.get("user_nick").toString();
				String userId = jsonObject.get("user_id")==null?null:jsonObject.get("user_id").toString();
				Long expireTime = jsonObject.get("expire_time")==null?null:Long.valueOf(jsonObject.get("expire_time").toString());
				if(accessToken==null || expireTime==null){
					throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,ResponseCodeEnum.RETURN_CODE_100500.getMsg());
				}
				Empower empower = new Empower();
				empower.setEmpowerId(queryEmpower.getEmpowerId());
				empower.setToken(accessToken);
				empower.setRefreshToken(refreshToken);
				Date time = new Date(expireTime);
				empower.setAutoTime(new Date());
				empower.setEndTime(time);
				authorizationSellerService.updateSelectiveAliexpress(empower);
			}

		} catch (Exception e) {
			logger.error("Aliexpress授权失败",e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,ExceptionUtils.getMessage(e));
		}

	}
	
	
	/**
	 * 其他类店铺授权信息入库
	 * @param empower
	 */
	@ApiOperation(value = "其他类店铺授权信息入库", notes = "其他授权信息入库")
	@GetMapping("/insertObjectOther/{account}")
	public void insertObjectOther(@PathVariable String account) {
		    if(authorizationSellerService.selectAllAccounts().contains(account)){
			    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "店铺名称已存在!");
		    }
		    
		    Empower empower = new Empower();
		    empower.setAccount(account);
		    empower.setCreateTime(new Date());
		    empower.setAutoTime(new Date());
		    empower.setPlatform(4);
		    empower.setStatus(1);
		    empower.setRentStatus(0);
			empower.setRentType(1);
		    //授权结束时间按一年时间
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(calendar.DAY_OF_YEAR, 5000);
			date = calendar.getTime();
			empower.setEndTime(date);
		    
		    empower = empowerAccountSet(empower);
		    
			try {
				Integer empowerId = authorizationSellerService.insertObjectSelective(empower);
			} catch (Exception e) {
				logger.error("其他类店铺授权信息入库失败",e);
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"其他类店铺授权信息入库失败");
			}
	}
	
	
	/**
	 * 其他类店铺授权信息入库
	 * @param empower
	 */
	@ApiOperation(value = "其他类店铺授权信息入库", notes = "其他授权信息入库")
	@GetMapping("/insertObjectOthers")
	public void insertObjectOthers(String account,Integer userId) {
		    if(authorizationSellerService.selectAllAccounts().contains(account)){
			    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "店铺名称已存在!");
		    }
		    
		    Empower empower = new Empower();
		    empower.setAccount(account);
		    empower.setCreateTime(new Date());
		    empower.setAutoTime(new Date());
		    empower.setPlatform(4);
		    empower.setStatus(1);
		    empower.setRentStatus(0);
		    empower.setPinlianAccount(account);
		    empower.setPinlianId(userId);
			empower.setRentType(1);
		    //授权结束时间按一年时间
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(calendar.DAY_OF_YEAR, 5000);
			date = calendar.getTime();
			empower.setEndTime(date);
		    
			try {
				Integer empowerId = authorizationSellerService.insertObjectSelective(empower);
			} catch (Exception e) {
				logger.error("其他类店铺授权信息入库失败",e);
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"其他类店铺授权信息入库失败");
			}
	}
	
	

	
	
	
	
	

}
