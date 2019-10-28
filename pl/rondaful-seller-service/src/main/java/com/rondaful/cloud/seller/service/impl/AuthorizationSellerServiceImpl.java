package com.rondaful.cloud.seller.service.impl;

import com.ebay.sdk.ApiAccount;
import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.sdk.ApiException;
import com.ebay.sdk.SdkException;
import com.ebay.sdk.call.FetchTokenCall;
import com.ebay.sdk.call.GetSessionIDCall;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.seller.dto.BindEmpDTO;
import com.rondaful.cloud.seller.dto.EmpAccountDTO;
import com.rondaful.cloud.seller.entity.Empower;
import com.rondaful.cloud.seller.entity.EmpowerLog;
import com.rondaful.cloud.seller.mapper.EmpowerLogMapper;
import com.rondaful.cloud.seller.mapper.EmpowerMapper;
import com.rondaful.cloud.seller.service.AuthorizationSellerService;
import com.rondaful.cloud.seller.vo.EmpowerVo;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthorizationSellerServiceImpl implements AuthorizationSellerService {

	
	
	@Value("${ebay.appid}")
	private String appid;
	
	@Value("${ebay.developer}")
	private String developer;
	
	@Value("${ebay.cert}")
	private String cert;
	
	@Value("${ebay.ruName}")
	private String ruName;

//	public static String appid = "Qingdaow-rondaful-PRD-760c321e5-1c3e761b";
//	public static String developer = "41de149d-3a20-4b8f-8f16-941f7a14ceac";
//	public static String cert = "PRD-60c321e5bc1d-daed-487d-9e96-f903";
//	public static String ruName = "Qingdaowangushi-Qingdaow-rondaf-eunonvhdg";

	
	@Autowired
	private EmpowerMapper empowerMapper;

	@Autowired
	private EmpowerLogMapper empowerLogMapper;

	@Autowired
	private GetLoginUserInformationByToken getUserInfo;


	private final Logger logger = LoggerFactory.getLogger(AuthorizationSellerServiceImpl.class);


	/**
	 * 亚马逊店铺授权信息入库
	 */
	public Integer insertAuthorizationSellerAmazon(Empower empower) {
		if (StringUtils.isBlank(empower.getAccount()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "自定义账号不能为空");
		if (empower.getStatus() == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "授权状态值不能为空");
		if (empower.getPlatform() == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "所属平台信息不能为空");
		if (StringUtils.isBlank(empower.getToken()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "授权token不能为空");
		if (StringUtils.isBlank(empower.getThirdPartyName()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "亚马逊sellerID信息不能为空");
		if (StringUtils.isBlank(empower.getWebName()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "亚马逊站点信息不能为空");

		try { 
			
			empower.setAutoTime(new Date());
			empower.setCreateTime(new Date());
			empower.setUpdateTime(new Date());

			//授权结束时间按一年时间
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(calendar.DAY_OF_YEAR, 365);
			date = calendar.getTime();
			empower.setEndTime(date);

			int insertSelective = empowerMapper.insertSelective(empower);
			Integer empowerId = empower.getEmpowerId();

			//授权成功操作日志方能入库   失败则日志信息不入库
			if (insertSelective > 0) {
				UserAll userInfo = getUserInfo.getUserInfo();

				EmpowerLog empowerLog = new EmpowerLog();
				empowerLog.setCreatetime(new Date());
				empowerLog.setEmpowerid(empowerId);
				empowerLog.setHandler(userInfo.getUser().getLoginName());
				empowerLog.setOperation("添加授权");
				empowerLogMapper.insertSelective(empowerLog);
				logger.info("日志写入成功");
			}
			return empowerId;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("亚马逊授权信息入库失败", e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "亚马逊授权信息入库失败");
		}
	}

	/**
	 * ebay店铺授权信息入库
	 * 先将用户输入的基本信息写入数据库   平台  账号  卖家id 授权时间  到期时间  状态值   paypal账号01
	 */
	public Integer insertAuthorizationSellerEbay(Empower empower) {

		try {

			int insertSelective = empowerMapper.insertSelective(empower);
			Integer empowerId = empower.getEmpowerId();

			//授权成功操作日志方能入库   失败则日志信息不入库
			if (insertSelective > 0) {
				UserAll userInfo = getUserInfo.getUserInfo();

				EmpowerLog empowerLog = new EmpowerLog();
				empowerLog.setCreatetime(new Date());
				empowerLog.setEmpowerid(empowerId);
				empowerLog.setHandler(userInfo.getUser().getLoginName());
				empowerLog.setOperation("添加授权");
				empowerLogMapper.insertSelective(empowerLog);
				logger.info("日志写入成功");
			}

			return empowerId;
		} catch (Exception e) {
			logger.error("ebay店铺授权信息入库失败", e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "ebay店铺授权信息入库失败");
		}

	}



	/**
	 * 根据用户的id获取亚马逊的授权token
	 */
	public String getTokenBySellerId(Integer sellerId) {
		if (sellerId == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403);
		return empowerMapper.getToken(sellerId);
	}


	/**
	 * 通过账号(店铺名称)停用
	 *
	 * @param account
	 * @return
	 */
	public int blockUpByAccount(String account, Integer id) {
		if (StringUtils.isBlank(account))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403);
		int status = 3;
		int updateStatusByAccount = empowerMapper.updateStatusByAccount(account, status);

		if (updateStatusByAccount > 0) {
			UserAll userInfo = getUserInfo.getUserInfo();

			EmpowerLog empowerLog = new EmpowerLog();
			empowerLog.setCreatetime(new Date());
			empowerLog.setEmpowerid(id);
			empowerLog.setHandler(userInfo.getUser().getLoginName());
			empowerLog.setOperation("停用授权");
			empowerLogMapper.insertSelective(empowerLog);
			logger.info("日志写入成功");
		}
		return updateStatusByAccount;
	}


	/**
	 * 过期停用
	 */
	public int pastDueBlockUp(String account, Integer status) {
		if (status == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403);
		return empowerMapper.updateStatusByAccount(account, status);
	}


	/**
	 * 通过账号(店铺名称)启用
	 *
	 * @param account
	 * @return
	 */
	public int startUsingByAccount(String account,Integer id) {
		if (StringUtils.isBlank(account))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403);
		int status = 1;
		int updateStatusByAccount = empowerMapper.updateStatusByAccount(account, status);

		if (updateStatusByAccount > 0) {
			UserAll userInfo = getUserInfo.getUserInfo();

			EmpowerLog empowerLog = new EmpowerLog();
			empowerLog.setCreatetime(new Date());
			empowerLog.setEmpowerid(id);
			empowerLog.setHandler(userInfo.getUser().getLoginName());
			empowerLog.setOperation("启用授权");
			empowerLogMapper.insertSelective(empowerLog);
			logger.info("日志写入成功");
		}
		return updateStatusByAccount;
	}


	/**
	 * 获取账号的sessionId
	 */
	public String getSessionID() {

		ApiContext apiContext = new ApiContext();
		ApiAccount apiAccount = new ApiAccount();
		apiAccount.setApplication(appid);
		apiAccount.setDeveloper(developer);
		apiAccount.setCertificate(cert);
		apiContext.getApiCredential().setApiAccount(apiAccount);
		apiContext.setApiServerUrl("https://api.ebay.com/wsapi");
		apiContext.setTimeout(500000);

		GetSessionIDCall getSessionIDCall = new GetSessionIDCall(apiContext);
		getSessionIDCall.setRuName(ruName);

		try {
			String sessionID = getSessionIDCall.getSessionID();
			return sessionID;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取账号的sessionId
	 */
	public String getUrl() {
		ApiContext apiContext = new ApiContext();
		ApiAccount apiAccount = new ApiAccount();
		apiAccount.setApplication(appid);
		apiAccount.setDeveloper(developer);
		apiAccount.setCertificate(cert);
		apiContext.getApiCredential().setApiAccount(apiAccount);
		apiContext.setApiServerUrl("https://api.ebay.com/wsapi");
		apiContext.setTimeout(500000);

		GetSessionIDCall getSessionIDCall = new GetSessionIDCall(apiContext);
		getSessionIDCall.setRuName(ruName);

		try {
			String sessionID = getSessionIDCall.getSessionID();
			String url = "https://signin.ebay.com/ws/eBayISAPI.dll?SignIn&runame=%s&SessID=%s";
			url = String.format(url, ruName, sessionID);
			return url;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 获取账号的token
	 * @throws Exception
	 * @throws SdkException
	 * @throws ApiException
	 */
	public String getToken(String sessionID) throws ApiException, SdkException, Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		FetchTokenCall call = new FetchTokenCall();
		ApiContext apiContext = new ApiContext();
		apiContext.setApiServerUrl("https://api.ebay.com/wsapi");
		ApiCredential credential = new ApiCredential();
		ApiAccount account = new ApiAccount();
		account.setApplication(appid);
		account.setDeveloper(developer);
		account.setCertificate(cert);
		credential.setApiAccount(account);
		apiContext.setApiCredential(credential);
		call.setApiContext(apiContext);
		call.setSessionID(sessionID);
		call.fetchToken();
		Calendar ExpirationTime = call.getHardExpirationTime();
		String format = sdf.format(ExpirationTime.getTime());

		return call.fetchToken() + "@@@" + format;
	}


	/**
	 * 查询列表信息
	 */
	public Page<Empower> findAll(Empower empower,String page, String row) {
		try {

			this.checkeEffective(empower);

			Page.builder(page, row);
			List<Empower> findAll = empowerMapper.findAll(empower);
			
			PageInfo<Empower> pageInfo = new PageInfo(findAll);
			return new Page(pageInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public List<Empower> findAllNoPage(Empower empower) {
		try {

			if (empower.getBeginTime() != null || empower.getAfterTime() != null) {
				this.checkeEffective(empower);
				return empowerMapper.findAll(empower);
			} else {
				this.checkeEffective(empower);
				return empowerMapper.findAll(empower);
			}
		} catch (Exception e) {
			logger.error("非分页查询授权列表异常", e);
			return null;
		}
	}

	/**
	 * 检查相关授权是否有效
	 * @param empower 授权信息
	 * @throws ParseException 异常
	 */
	private void checkeEffective(Empower empower) throws ParseException {
//		List<Empower> findAll = empowerMapper.findAll(empower);
//
//		//遍历查询出来的结果值
//		for (int i = 0; i < findAll.size(); i++) {
//			//当前时间与过期时间比较
//			if (findAll.get(i).getEndTime() != null && findAll.get(i).getEndTime().compareTo(new Date()) < 1) {
//				//修改状态为授权过期
//				Integer status = 2;
//				empowerMapper.updateStatusByAccount(findAll.get(i).getAccount(), status);
//			}
//
//		}
		Integer count = empowerMapper.checkEndTime();
		if(count>0){
			empowerMapper.updateEndTime();
		}
	}



	public Empower selectOneByAcount(Empower empower) {
		return empowerMapper.selectOneByAcount(empower);
	}


	public Empower selectByPrimaryKey(String empowerId) {
		return empowerMapper.selectByPrimaryKey(empowerId);
	}

	/**
	 * 多条件查询授权信息
	 */
	public List<Empower> selectObjectByAccountDataLimit(Empower empower) {
		return empowerMapper.selectObjectByAccountDataLimit(empower);
	}


	/**
	 * 远程调用查询授权全部信息
	 */
	public List<Empower> findAll(Integer platform, Integer status) {
		return empowerMapper.findAllRemote(platform, status);
	}


	/**
	 * 查询所有账户
	 */
	public List<String> selectAccounts() {
		//只查询当前账户的授权账号
		String username = getUserInfo.getUserInfo().getUser().getLoginName();
		return empowerMapper.selectAccounts(username);
	}


	/**
	 * 查询日志
	 */
	public List<EmpowerLog> selectLogById(Integer id,String handler) {
		if (id == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "id值不能为空");

		List<EmpowerLog> selectByPrimaryKey = empowerLogMapper.selectByPrimaryKey(id,handler);

		return selectByPrimaryKey;
	}


	/**
	 * 
	 * @param empower
	 * @return
	 */
	public Integer insertObjectSelective(Empower empower) {
		Integer insertObjectSelective = empowerMapper.insertObjectSelective(empower);
		Integer empowerId = empower.getEmpowerId();

		
		UserAll userInfo = getUserInfo.getUserInfo();
		EmpowerLog empowerLog = new EmpowerLog();
		empowerLog.setCreatetime(new Date());
		empowerLog.setEmpowerid(empowerId);
		empowerLog.setHandler(userInfo.getUser().getLoginName());
		empowerLog.setOperation("添加授权");
		empowerLogMapper.insertSelective(empowerLog);
		logger.info("日志写入成功");
		
		return insertObjectSelective;
	}



	public List<String> selectAllAccounts() {
		return empowerMapper.selectAllAccounts();
	}


	@Override
	public Empower selectAmazonAccount(Empower t) {
		return empowerMapper.selectAmazonAccount(t);
	}


	public Integer checkAccount(String webName, String thirdPartyName) {
		return empowerMapper.checkAccount(webName, thirdPartyName);
	}



	public void updateSelectiveAmazon(Empower empower) {
		try {
			//授权结束时间按一年时间
			empower.setStatus(1);
			empower.setUpdateTime(new Date());
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(calendar.DAY_OF_YEAR, 365);
			date = calendar.getTime();
			empower.setEndTime(date);

			int insertSelective = empowerMapper.updateByPrimaryKeySelective(empower);
			Integer empowerId = empower.getEmpowerId();

			//授权成功操作日志方能入库   失败则日志信息不入库
			if (insertSelective > 0) {
				UserAll userInfo = getUserInfo.getUserInfo();

				EmpowerLog empowerLog = new EmpowerLog();
				empowerLog.setCreatetime(new Date());
				empowerLog.setEmpowerid(empowerId);
				empowerLog.setHandler(userInfo.getUser().getLoginName());
				empowerLog.setOperation("重新授权");
				empowerLogMapper.insertSelective(empowerLog);
				logger.info("日志写入成功");
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("亚马逊重新授权失败", e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "亚马逊重新授权失败");
		}
	}


	/**
	 * ebay店铺重新授权
	 */
	public void updateSelectiveEbay(Empower empower) {
		try {
			int insertSelective = empowerMapper.updateByPrimaryKeySelective(empower);
			Integer empowerId = empower.getEmpowerId();

			
			UserAll userInfo = getUserInfo.getUserInfo();
			EmpowerLog empowerLog = new EmpowerLog();
			empowerLog.setCreatetime(new Date());
			empowerLog.setEmpowerid(empowerId);
			empowerLog.setHandler(userInfo.getUser().getLoginName());
			empowerLog.setOperation("重新授权");
			empowerLogMapper.insertSelective(empowerLog);
			logger.info("日志写入成功");

		} catch (Exception e) {
			logger.error("ebay店铺重新授权失败", e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "ebay店铺重新授权失败");
		}
	}
	/**
	 * Aliexpress店铺授权信息入库
	 * 先将用户输入的基本信息写入数据库   平台  账号  卖家id 授权时间  到期时间  状态值   paypal账号01
	 */
	public Integer insertAuthorizationSellerAliexpress(Empower empower) {
		try {
			int insertSelective = empowerMapper.insertSelective(empower);
			Integer empowerId = empower.getEmpowerId();

			//授权成功操作日志方能入库   失败则日志信息不入库
			if (insertSelective > 0) {
				UserAll userInfo = getUserInfo.getUserInfo();

				EmpowerLog empowerLog = new EmpowerLog();
				empowerLog.setCreatetime(new Date());
				empowerLog.setEmpowerid(empowerId);
				empowerLog.setHandler(userInfo.getUser().getLoginName());
				empowerLog.setOperation("添加授权");
				empowerLogMapper.insertSelective(empowerLog);
				logger.info("日志写入成功");
			}
			return empowerId;
		} catch (Exception e) {
			logger.error("Aliexpress店铺授权信息入库失败", e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "Aliexpress店铺授权信息入库失败");
		}

	}

	/**
	 * Aliexpress店铺重新授权
	 */
	public void updateSelectiveAliexpress(Empower empower) {
		try {
			int insertSelective = empowerMapper.updateByPrimaryKeySelective(empower);
			Integer empowerId = empower.getEmpowerId();

			//授权成功操作日志方能入库   失败则日志信息不入库
			if (insertSelective > 0) {
				UserAll userInfo = getUserInfo.getUserInfo();

				EmpowerLog empowerLog = new EmpowerLog();
				empowerLog.setCreatetime(new Date());
				empowerLog.setEmpowerid(empowerId);
				empowerLog.setHandler(userInfo.getUser().getLoginName());
				empowerLog.setOperation("重新授权");
				empowerLogMapper.insertSelective(empowerLog);
				logger.info("日志写入成功");
			}

		} catch (Exception e) {
			logger.error("Aliexpress店铺重新授权失败", e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "Aliexpress店铺重新授权失败");
		}
	}


	/**
	 * 授权编辑
	 */
	public void editAuthorization(Empower empower) {
		try {
			int insertSelective = empowerMapper.updateByPrimaryKeySelective(empower);
			Integer empowerId = empower.getEmpowerId();
			
			EmpowerLog empowerLog = new EmpowerLog();
			empowerLog.setCreatetime(new Date());
			empowerLog.setEmpowerid(empowerId);
			empowerLog.setHandler(getUserInfo.getUserInfo().getUser().getLoginName());
			empowerLog.setOperation("编辑授权");
			empowerLogMapper.insertSelective(empowerLog);
			logger.info("日志写入成功");
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("店铺授权编辑失败", e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "店铺授权编辑失败");
		}
	}


	
	/**
	 * 查询当前账户外的店铺账号
	 */
	public List<String> selectOtherAccounts(Integer empowerId) {
		return empowerMapper.selectOtherAccounts(empowerId);
	}

	
	/**
	 * 通过品连账号查询店铺信息
	 * @param pinlianAccounts
	 * @return
	 */
	public List<EmpowerVo> selectInfoByUserIds(List<Integer> pinlianIds) {
		return empowerMapper.selectInfoByUserIds(pinlianIds);
	}

	
	/**
	 * 通过品连ID查询店铺信息
	 * @param pinlianAccounts
	 * @return
	 */
	@Override
	public List<EmpAccountDTO> selectInfoByAccounts(List<String> pinlianAccounts) {
		
		Map<Integer,List<BindEmpDTO>> map=new HashMap<>();
	    List<EmpowerVo> selectInfoByAccounts = empowerMapper.selectInfoByAccounts(pinlianAccounts);
	    if (CollectionUtils.isEmpty(selectInfoByAccounts)){
	    	return new ArrayList<>();
		}
		List<EmpAccountDTO> result=new ArrayList<>(3);
		for (EmpowerVo empowerVo: selectInfoByAccounts) {
			if(!map.containsKey(empowerVo.getPlatform())){
				map.put(empowerVo.getPlatform(), new ArrayList<>());
			}
			map.get(empowerVo.getPlatform()).add(new BindEmpDTO(empowerVo.getEmpowerId(),empowerVo.getAccount()));
		}

		for (Map.Entry<Integer,List<BindEmpDTO>> entry:map.entrySet()) {
			result.add(new EmpAccountDTO(entry.getKey(),entry.getValue()));
		}
		return result;
	}


	/**
	 * @param empowerIds
	 * @return
	 */
	@Override
	public List<EmpAccountDTO> getEmpNameByIds(List<Integer> empowerIds) {
		List<Empower> list=this.empowerMapper.getEmpowerByIds(empowerIds);
		if (CollectionUtils.isEmpty(list)){
			return null;
		}
		Map<Integer,List<BindEmpDTO>> map=new HashMap<>();
		List<EmpAccountDTO> result=new ArrayList<>(3);
		for (Empower emp:list) {
			if(!map.containsKey(emp.getPlatform())){
				map.put(emp.getPlatform(), new ArrayList<>());
			}
			map.get(emp.getPlatform()).add(new BindEmpDTO(emp.getEmpowerId(),emp.getAccount()));
		}
		for (Map.Entry<Integer,List<BindEmpDTO>> entry:map.entrySet()) {
			result.add(new EmpAccountDTO(entry.getKey(),entry.getValue()));
		}
		return result;
	}
	
	
	
	public List<Empower> getEmpowerByIds(List<Integer> empowerIds){
		List<Empower> list=this.empowerMapper.getEmpowerByIds(empowerIds);
		return list;
	}

	
	public List<Empower> selectObjectByAccount(Integer empowerId,String pinlianAccount, Integer status, String account, Integer platform,Integer pinlianId) {
		return empowerMapper.selectObjectByAccount(empowerId, pinlianAccount, status, account, platform,pinlianId);
	}
}
