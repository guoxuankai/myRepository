package com.rondaful.cloud.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.MenuCommon;
import com.rondaful.cloud.common.entity.user.RoleCommon;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.common.utils.RemoteUtil;
import com.rondaful.cloud.common.utils.UserUtils;
import com.rondaful.cloud.user.constants.UserConstants;
import com.rondaful.cloud.user.controller.utils.ControllerUtil;
import com.rondaful.cloud.user.entity.*;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.mapper.*;
import com.rondaful.cloud.user.remote.UserFinanceInitialization;
import com.rondaful.cloud.user.service.ManageService;
import com.rondaful.cloud.user.utils.MD5;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Service("manageService")
public class ManageServiceImpl extends ControllerUtil implements ManageService{
	
	@Autowired
	private ManageMapper manageMapper;
	
	@Autowired
	private PublicCommomMapper userMapper;

	@Autowired
	private UserRoleMapper userRoleMapper;

	@Autowired
	private MenuMapper menuMapper;

	@Autowired
	private RoleMapper roleMapper;

	@Autowired
	private SupplierMapper supplierMapper;

	@Autowired
	private RedisUtils redisUtils;

	@Autowired
	private UserFinanceInitialization userFinanceInitialization;

	@Autowired
	private SellerMapper sellerMapper;

	@Autowired
	private SupplyChainCompanyMapper supplyChainCompanyMapper;

	private final Logger logger = LoggerFactory.getLogger(ManageServiceImpl.class);
	
	@Autowired
	private GetLoginUserInformationByToken getLoginUserInformationByToken;

	/**
	 * 管理后台登录
	 * @param username
	 * @param password
	 * @return
	 */
	@Override
	public Map<String,Object> manageUserLogin(String username, String password,Integer platformType,HttpServletResponse response) throws InvocationTargetException, IllegalAccessException {
			Map<String, Object> map = new HashMap<>();
			//封装登录用户的基础信息，角色信息，权限信息
			UserAll userAll = new UserAll();
			User user = null;//当前登录用户的基础信息
			List<RoleCommon> roleBeans = new ArrayList<>();//当前登录用户的角色信息
			HashSet<MenuCommon> menuBeans = new HashSet<>();//当前登录用户的权限信息
			if ( StringUtils.isNotBlank(username) && platformType != null ) user = getUserByPhoneOrEmail(username, null, null, platformType);//根据此用户名查询用户是否存在
			if (user == null) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100433.getCode(), "当前用户不存在，请注册或者重新登录");
			}else if (!user.getUsername().equals(username)) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100001);
			}else if (user.getStatus().intValue() == 0) {
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100425.getCode(),"当前用户已停用或者已注销，请联系管理员重新分配");
			} else if (platformType.intValue() == 1) {
				if (user.getPlatformType().intValue() != 1) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100425.getCode(), "您不是本平台卖家用户，请重新登录");
			}

			//用户已经存在，进行密码加密
			String handlerPassword = password;
			if ( user.getParentId() == null ) handlerPassword = MD5.md5Password(password);
			if (!handlerPassword.equals(user.getPassword())) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100001); //判断密码是否匹配

			//根据userId查询到对应的角色id
			List<Integer> roleIds = null;
			if (user.getUserid() != null) roleIds = userRoleMapper.getRoleIdsByUserId(user.getUserid());
			Role role1 = null;//根据角色id获取角色信息
			for (Integer roleId : roleIds) {
				if (roleId != null) role1 = roleMapper.selectRoleFindById(roleId);
				RoleCommon roleCommon = new RoleCommon();
				BeanUtils.copyProperties(roleCommon,role1);
				roleBeans.add(roleCommon);
			}

			//获取权限信息
			User finalUser = user;
			List<Menu> all;
			if (user.getParentId() == null) {  //当账号是主账号德时候，不经过角色，直接拥有所属平台的所有菜单权限
				all = menuMapper.findAll(new Menu() {{
					setPlatformType(finalUser.getPlatformType());
					setDelFlag(0);
				}});
			} else {   //当账号不是主账号的时候，经过角色
				all = menuMapper.findAll(new Menu() {{
					setPlatformType(finalUser.getPlatformType());
					setDelFlag(0);
					setUserId(finalUser.getUserid());
					// setRoleIds(roleIds);
				}});
			}
			if (all == null || all.size() == 0) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100436);
			all.forEach(menu -> menuBeans.add(new MenuCommon(menu.getId(), menu.getPlatformType(), menu.getParentId(),
					menu.getParentIds(), menu.getName(), menu.getSort(), menu.getHref(), menu.getIcon(),menu.getLevel(),
					menu.getVshow(), menu.getPermission(), menu.getRemarks(),menu.getCreateDate(), menu.getUpdateDate(),
					menu.getDelFlag())));
			this.setMenuAndUrls(userAll, menuBeans);    //设置权限和菜单
			UserCommon userCommon = new UserCommon();//封装用户信息
			BeanUtils.copyProperties(userCommon,user);
			userAll.setUser(userCommon);
			userAll.setRoles(roleBeans);

			//设置CMS查询管理员,前期产品没提出来,只要在CMS登陆成功就给权限,后期版本是否考虑放在数据库
			userAll.getRoles().add(new RoleCommon(UserUtils.CMS_ADMIN_PERMISSIONS));

			//判断当前用户是什么级别的账号，如果是子账号则找到他的父级账户
			userAll.setParentUser(new UserCommon());
			if ( userAll.getUser().getParentId() != null){
				//说明是子账户,则获取他的父账户信息
				User parentUser = manageMapper.getParentUserBySubUserParentIdManage(userAll.getUser().getParentId());
				UserCommon parentUserCommon = new UserCommon();
				BeanUtils.copyProperties(parentUserCommon, parentUser);
				userAll.setParentUser(userCommon);
				map.put("parentUsers",parentUser);
			}

			//生成会话信息
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			String requestType = request.getHeader("user-agent");
			String token = null;
			String str = getUserMD5(userAll);
			if ( requestType.indexOf("Android") != -1 ){//安卓App情况下
				token = getPcUserToken(userAll);
				redisUtils.removePattern(str+"APP"+"*");
			}else{//在pc端情况下
				token = getPcUserToken(userAll);
				redisUtils.removePattern(str+"PC"+"*");
			}
			//储存当前用户的信息
			redisUtils.set(UserConstants.REDIS_USER_KEY_fix + token, userAll, UserConstants.REDIS_USER_TOKEN_TIMEOUT);
			//返回token
			response.setHeader("Access-Control-Expose-Headers", "token");
			response.setHeader(UserConstants.REQUEST_HEADER_NAME, token);
			userAll.setUrls(null);
			map.put("token", token);
			map.put("user", userAll);
			return map;//返回结果
	}

	/**
	 * 创建主用户
	 * @param object
	 * @return
	 */
	@Override
	public Integer insertParentUser(Object object,Integer platformType) {
		UserAll userAll = getLoginUserInformationByToken.getUserInfo();//当前登录者信息
		User user = JSONObject.parseObject(JSONObject.toJSONString(object),User.class);//将用户传递的参数转成User对象封装
		user.setPlatformType(platformType);//写入平台
		user.setCreateDate(new Date());
		user.setEnabled(StringUtils.isNotBlank(userAll.getUser().getUsername()) ? userAll.getUser().getUsername() : "");
		if (user.getPlatformType().intValue() == 0 && user.getClosedCircle() == null) user.setClosedCircle(3);
		dataMatch(user);//验证数据

		//判断当前用户名是否重复
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("phone", StringUtils.isNotBlank(user.getPhone()) ? user.getPhone():null);
		map.put("username", StringUtils.isNotBlank(user.getUsername()) ? user.getUsername():null);
		map.put("platformType",user.getPlatformType());
		map.put("email",StringUtils.isNotBlank(user.getEmail()) ? user.getEmail() : null);
		User isUser = null;
		//根据创建的用户名查询数据库中是否存在该用户
		if (user.getPlatformType().intValue() == 0) isUser = supplierMapper.getSupplierUserByPhoneOrEmail(map);
		else if ( user.getPlatformType().intValue() == 1 )isUser = sellerMapper.getSellerUserByPhoneOrEmail(map);
		else isUser = manageMapper.getManageUserByPhoneOrEmail(map);
		if ( isUser != null ) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100407);

		//用户注册
		String oldPassword = user.getPassword();
		String passwordMd5 = MD5.md5Password(user.getPassword());
		user.setPassword(passwordMd5);
		Integer result = manageMapper.insertParentUser(user);

		//与供应链公司进行绑定关系
		Integer userAndSupplyChain = null;
		if ( user.getSupplyChainCompany() != null ){
			Integer selectSupplyChainCompanyStatus = manageMapper.selectSupplyChainCompanyStatus(user.getSupplyChainCompany() != null ? Integer.parseInt(user.getSupplyChainCompany().trim()):null);
			if ( selectSupplyChainCompanyStatus == null || selectSupplyChainCompanyStatus.intValue() == 0  ) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"该供应链公司已停用，请重新绑定");
		map.clear();
		map.put("userId",user.getUserid());
		map.put("supplyChainId",Integer.parseInt(user.getSupplyChainCompany() != null ? user.getSupplyChainCompany().trim():null));
		map.put("platformType",user.getPlatformType());
		userAndSupplyChain = manageMapper.userAndSupplyChainBinding(map);
		}

		//供应商添加成功，进行手机短信通知
		if (result != null && userAndSupplyChain != null && platformType.intValue() == 0){
			if (StringUtils.isNotBlank(user.getPhone())){
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("user",user.getUsername());
				jsonObject.put("password",oldPassword);
				sendSmsJson(6,user.getPhone(),jsonObject);
			}
		}

		//卖家创建成功后进行财务初始化
		Object obj = null;
		if (platformType.intValue() == 1){
			String isInitSeller = sellerMapper.isInitSeller(user.getUserid());
			if ( StringUtils.isNotBlank(user.getPhone()) && (isInitSeller != null && isInitSeller.equals("0"))){
				Companyinfo companyinfo = new Companyinfo();
				User name = new User();
				if( user.getSupplyChainCompany() != null ) name = supplyChainCompanyMapper.getSupplyChainCompanyUser(Integer.parseInt(user.getSupplyChainCompany()));//查询供应链名称
				//远程调用卖家财务初始化接口
				RemoteUtil.invoke(userFinanceInitialization.sellerInit(
						user.getUserid(),
						StringUtils.isNotBlank(user.getUsername()) ? user.getUsername() : "",
						user.getSupplyChainCompany() != null ? Integer.parseInt(user.getSupplyChainCompany()) : null,
						StringUtils.isNotBlank(name.getCompanyNameUser()) ? name.getCompanyNameUser() : "",
						StringUtils.isNotBlank(user.getLinkman()) ? user.getLinkman() : "",
						StringUtils.isNotBlank(user.getPhone()) ? user.getPhone() : "",
						StringUtils.isNotBlank(companyinfo.getRegArea()) ? companyinfo.getRegArea() : "",
						StringUtils.isNotBlank(companyinfo.getRegAddress()) ? companyinfo.getRegAddress() : ""));
				companyinfo = null;name = null;//释放资源
				obj = RemoteUtil.getObject();
				Integer initResultOk = null;
				if (obj == null) logger.error("卖家的财务初始化失败");
				else {
					map.clear();
					map.put("userId",user.getUserid());
					map.put("insertResult",UserConstants.INITRESULTOK);
					initResultOk = sellerMapper.sellerInitResultOk(map);//将账户标记为已经初始化
					logger.info("卖家的财务初始化成功");
				}
			}
			//卖家创建成功后进行短信通知
			if ( (result != null && result.intValue() != 0) && (obj != null && (boolean)obj == true ) && StringUtils.isNotBlank(user.getPhone()) ){
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("user",user.getUsername());
				jsonObject.put("password",oldPassword);
				sendSmsJson(7,user.getPhone(),jsonObject);
			}
		}
		return result;
	}

	/**
	 * 根据手机和邮箱找到用户
	 * @param phone
	 * @param email
	 * @param username
	 * @param platformType
	 * @return
	 */
	public User getUserByPhoneOrEmail(String username, String phone, String email,Integer platformType) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("phone", phone);
		map.put("email", email);
		map.put("username", username);
		map.put("platformType",platformType);
		User user = manageMapper.getManageUserByPhoneOrEmail(map);
		return user;
	}

	/**
	 * 卖家用户名-下拉列表
	 * @param user
	 * @param currPage
	 * @param row
	 * @return
	 */
	@Override
	public Page getSellerUser(User user,String currPage,String row) {
		Page.builder(currPage, row);
		List<User> sellerUsers = manageMapper.getSellerUsers(user);
		PageInfo pi = new PageInfo<>(sellerUsers);
		return new Page(pi);
	}

	/**
	 * 管理员重置密码
	 * @param userId
	 * @param platformType
	 * @param newPassword
	 * @return
	 */
	@Override
	public Integer resetPassword(Integer userId, Integer platformType, String newPassword) {
		UserAll userAll = getLoginUserInformationByToken.getUserInfo();
		Map<String,Object> map = new HashMap<String,Object>();
		String password = MD5.md5Password(newPassword);//将密码进行加密，替换原来的密码
		map.put("userId", userId);
		map.put("password", password);
		User user = manageMapper.selectUserByUserId(userId,platformType);//根据id 找到用户
		Integer result = null;
		if ( user != null ) {
			map.put("platformType",platformType);
//			map.put("updateDate",new Date());
//			map.put("remarks",userAll.getUser().getUsername());
			result = manageMapper.passwordUpdate(map);
			if (result == null) {
				logger.error("重置密码失败，该用户不存在");
				throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"重置密码失败，该用户不存在");
			}
		}
		return result;
	}


	/**
	 * 后台管理->用户列表
	 * @param map
	 * @param currPage
	 * @param row
	 * @return
	 */
	@Override
	public Page<SupplyChainCompanyListBean> getSupplierUser(Map<String,Object> map, String currPage, String row) {
		Page.builder(currPage, row);
		List<SupplyChainCompanyListBean> supplierUsers = manageMapper.getSupplierUser(map);
		if (supplierUsers != null){
			for ( SupplyChainCompanyListBean s : supplierUsers ){
				if (s.getSupplyChainCompany() != null && s.getSupplyChainCompany().equals("") == false){
					String name = manageMapper.getSupplyChainCompanyName(Integer.parseInt(s.getSupplyChainCompany().trim()));
					s.setSupplyChainCompany(name);
				}
			}
		}
		PageInfo pi = new PageInfo<>(supplierUsers);
		return new Page(pi);
	}

	/**
	 * 管理员修改用户状态
	 * @param userIds
	 * @param status
	 * @return
	 */
	@Override
	public Integer manageAccountDisabled(List<Integer> userIds, Integer status) {
		Map map = new HashMap();
		UserAll userAll = getLoginUserInformationByToken.getUserInfo();
		map.put("platformType",userAll.getUser().getPlatformType());//根据uid查询用户信息
		map.put("userIds",userIds);
    	List<User> user = userMapper.selectByPrimaryKeys(map);
    	if(user == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100410.getCode(),"没有找到用户信息,请重试");
    	map.put("date", new Date());//获取日志
		map.put("updatePerson",StringUtils.isNotBlank(userAll.getUser().getUsername()) ? userAll.getUser().getUsername() : "");
    	map.put("status", status);
    	Integer result = userMapper.updataStatus(map);//根据用户id修改账号状态
    	if(result == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100408);
    	return result;//返回结果
	}

	/**
	 * 卖家用户名-下拉列表
	 * @return
	 */
	@Override
	public List<SellerUsername> getSellerUsernameList() {
		return manageMapper.getSellerUsernameList();
	}

	/**
	 * 供应商用户名-下拉列表
	 * @return
	 */
	@Override
	public List<GetSupplyChainByUserId> getSupplierUsernameList(Integer delFlag) {
		List<GetSupplyChainByUserId> supplierName = manageMapper.getSupplierUsernameList(delFlag);
		return supplierName;
	}

	/**
	 * 供应链公司-下拉列表
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getSupplyChainCompanyNameList() {
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		List<HashMap<String, Object>> supplyChainCompanyName= manageMapper.getSupplyChainCompanyNameList();
		if (supplyChainCompanyName != null && !supplyChainCompanyName.isEmpty()){
			for (Map map1 : supplyChainCompanyName){
				Map<String,Object> map = new HashMap<>();
				String supplyId = null;
				String supplyChainName = null;
				if ( map1.get("supplyId") != null) supplyId = map1.get("supplyId").toString();
				if (map1.get("supplyChainName")!= null) supplyChainName = (String)map1.get("supplyChainName");
				map.put("supplyId",Integer.parseInt(supplyId));
				map.put("supplyChainName",supplyChainName);
				mapList.add(map);
			}
		}
		return mapList;
	}

	/**
	 * 设置供应链
	 * @param map
	 * @return
	 */
	@Override
	public Integer setSupplyChainCompany(Map<String, Object> map) {
		Integer result = manageMapper.updateUserSupplyChainCompany(map);//更新user表中的供应链公司
		Integer isUserAndSupplyData = manageMapper.isUserAndSupplyData(map);//判断当前用户是否有关联供应链数据
		Integer userOrSupply = null;
 		if (isUserAndSupplyData.intValue() == 0) userOrSupply = manageMapper.userAndSupplyChainBinding(map);//注册关联数据
		else userOrSupply = manageMapper.updateuserOrSupply(map);//修改关联数据

		//用户已修改供应链公司===》远程调用接口财务接口修改用户绑定供应链公司
		Integer platformType = (Integer)map.get("platformType") != null ? (Integer)map.get("platformType") : null;//平台
		Integer userId = (Integer) map.get("userId") != null ? (Integer) map.get("userId") : null;//用户id
		Integer supplyChainId = StringUtils.isNotBlank((String)map.get("supplyChainId")) ? Integer.parseInt((String)map.get("supplyChainId")) : null;//供应链id
		if ( result != null && result.intValue() != 0 ) {
			if ( 1 == platformType.intValue()){
				//远程调用卖家绑定供应链公司接口
				boolean sellerRebindResult = false;
				RemoteUtil.invoke(userFinanceInitialization.sellerRebind(userId, supplyChainId));
				Object sellerRebindObj = RemoteUtil.getObject();
				if ( sellerRebindObj instanceof Boolean ) sellerRebindResult = (boolean)sellerRebindObj;
				if ( sellerRebindResult == false ) logger.error("远程调用卖家绑定供应链公司接口失败");
				else logger.info("远程调用卖家绑定供应链公司接口成功");
			}else {
				//远程调用供应商绑定供应链公司接口
				boolean sellerRebindResult = false;
				RemoteUtil.invoke(userFinanceInitialization.supplierRebind(userId, supplyChainId));
				Object sellerRebindObj = RemoteUtil.getObject();
				if ( sellerRebindObj instanceof Boolean ) sellerRebindResult = (boolean)sellerRebindObj;
				if ( sellerRebindResult == false ) logger.error("远程调用供应商绑定供应链公司接口失败");
				else logger.info("远程调用供应商绑定供应链公司接口成功");
			}
		}
		return result + userOrSupply;
	}

	/**
	 * 忘记密码
	 * @param phone
	 * @param email
	 * @param platformType
	 * @param newPassword
	 * @return
	 */
	@Override
	public Integer getManageUserByPhoneAndPlatformType(String phone, String email, Integer platformType, String newPassword) {
		Map<String, Object> map = new HashMap<String, Object>();//封装条件数据
		map.put("phone", phone);
		map.put("platformType", platformType);
		map.put("email", email);
		User user = manageMapper.getManageUserByPhoneAndPlatformType(map);//根据手机号码或者邮箱及平台找到对应的用户数据
		if (user == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406.getCode(), "找不到要修改密码的用户");
		if (platformType.intValue() == 2) {
			if (user.getPlatformType().intValue() != 2) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100425.getCode(), "您不是本平台后台管理用户");
		}
		String newPasswordMd5 = MD5.md5Password(newPassword);//将新密码加密
		if ( user.getPassword().equals(newPasswordMd5) ) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100431.getCode(),"新密码与旧密码一致");
		map.put("newPasswordMd5", newPasswordMd5);
		map.put("update", new Date());
		Integer result = manageMapper.manageUserpasswordUpadate(map);//替换对应用户中的密码
		if (result == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100431);
		return result;
	}

	/**
	 * 修改密码
	 * @param userId
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	@Override
	public Integer managePasswordUpdate(Integer userId, String oldPassword, String newPassword) {
		UserAll userAll = getLoginUserInformationByToken.getUserInfo();
		User user = manageMapper.selectManageByPrimaryKey(userId);
		Map<String, Object> map = new HashMap<String, Object>();
		Integer result = null;
		if (user != null) {
			if (user.getPassword().equals(MD5.md5Password(oldPassword))) {//对比密码
				newPassword = MD5.md5Password(newPassword);
				if ( MD5.md5Password(oldPassword).equals(newPassword) ) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(),"新密码与旧密码一致");//对比新密码和旧密码是否一致
				map.put("userId", userId);
				map.put("newPassword", newPassword);
				map.put("update", new Date());
				map.put("remarks",StringUtils.isNotBlank(userAll.getUser().getUsername()) ? userAll.getUser().getUsername() : "");
				result = manageMapper.mamageUserpasswordUpadate(map);
			} else throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "原密码错误，请重新输入");
		} else throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), "未找到该用户，请重试");
		return result;
	}

	/**
	 * 设置用户对象的权限列表 和 菜单结构
	 *
	 * @param userAll   用户对象
	 * @param menuBeans 用户所有的菜单的hash列表
	 */
	private void setMenuAndUrls(UserAll userAll, HashSet<MenuCommon> menuBeans) {
		userAll.setMenus(new ArrayList<>());
		userAll.setUrls(new HashSet<>());
		menuBeans.forEach(m -> {
			if (StringUtils.isNotBlank(m.getHref())) {
				for (String s : m.getHref().split(",")) {
					userAll.getUrls().add(s.trim());
				}
			}
			if (m.getParentId() == 0)
				userAll.getMenus().add(m);
			menuBeans.forEach(mm -> {
				if (mm.getParentId().equals(m.getId())) {
					if (m.getChildren() == null) {
						m.setChildren(new ArrayList<>());
					}
					m.getChildren().add(mm);
				}
			});
		});
	}
	
	
}
