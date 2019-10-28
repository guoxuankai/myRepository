package com.rondaful.cloud.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.user.controller.utils.ControllerUtil;
import com.rondaful.cloud.user.entity.User;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.mapper.*;
import com.rondaful.cloud.user.service.PublicCommomService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;

@Service("publicCommomService")
public class PublicCommomServiceImpl extends ControllerUtil implements PublicCommomService {

	@Autowired
	private ManageMapper manageMapper;

	@Autowired
	private SellerMapper sellerMapper;

	@Autowired
	private SupplierMapper supplierMapper;

	@Autowired
	private PublicCommomMapper userMapper;
	
	@Autowired
	private UserRoleMapper userRoleMapper;

	private Logger logger = LoggerFactory.getLogger(PublicCommomServiceImpl.class);

	@Autowired
	private GetLoginUserInformationByToken getLoginUserInformationByToken;

	/************ 子用户注册 ************/
	@Override
	public Integer insertUser(User user) throws SQLException {
		// 判断该用户是否被注册
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("phone", user.getPhone());
		map.put("username", user.getUsername());
		map.put("platformType",user.getPlatformType());
		map.put("email",StringUtils.isNotBlank(user.getEmail()) ? user.getEmail() : null);
		User data = null;
		Integer uid = null;
		//根据创建的用户名查询数据库中是否存在该用户
		if (user.getPlatformType().intValue() == 0) data = supplierMapper.getSupplierUserByPhoneOrEmail(map);
		else if ( user.getPlatformType().intValue() == 1 )data = sellerMapper.getSellerUserByPhoneOrEmail(map);
		else data = manageMapper.getManageUserByPhoneOrEmail(map);
		//该用户没有被注册
		if (data == null) {
			Date date = new Date();
			user.setCreateDate(date);
			uid = userMapper.insert(user);
		}else{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100407);
		}
		return uid;
	}

	/**
	 * 根据用户id修改用户账号状态
	 * @param userIds
	 * @param status
	 * @return
	 */
    @Override
	public Integer updataStatus(List<Integer> userIds, Integer status) {
    	Map map = new HashMap();
		//根据uid查询用户信息
		UserAll userAll = getLoginUserInformationByToken.getUserInfo();
    	List<User> user = getUsersByIds(userIds,userAll.getUser().getPlatformType());
		if(user == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100410.getCode(),"没有找到用户信息,请重试");
    	for ( User us : user) {
    		if ( us.getParentId() == null )throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401.getCode(),"您无权限操作管理员账户，请重试");
    	}
    	//获取日志
    	Date date = new Date();
    	map.put("userIds", userIds);
    	map.put("date", date);
    	map.put("status", status);
    	map.put("platformType",userAll.getUser().getPlatformType());
    	map.put("updatePerson",StringUtils.isNotBlank(userAll.getUser().getUsername()) ? userAll.getUser().getUsername() : "");
		//根据用户id修改账号状态
    	Integer result = userMapper.updataStatus(map);
    	if(result == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100408);
    	//返回结果
    	return result;
	}

	/**
	 * 修改账号资料
	 * @param user
	 * @param roleIds
	 * @return
	 */
	public Integer updateInfo(User user,List<Integer> roleIds) {
		Map<String,Object> map = new HashMap<String, Object>();
		UserAll userAll = getLoginUserInformationByToken.getUserInfo();
		User userResult = null;
		//找到该用户
		if ( user.getPlatformType().intValue() == 0 ){
			if (user.getUserid() != null) userResult = supplierMapper.getSupplierUserById(user.getUserid());
		}else if ( user.getPlatformType().intValue() == 1 ){
			if (user.getUserid() != null) userResult = sellerMapper.selectSellerByPrimaryKey(user.getUserid());
		}else{
			if (user.getUserid() != null) userResult = manageMapper.selectManageByPrimaryKey(user.getUserid());
		}

		//子账户修改需要判断
		if (userResult.getParentId() != null){
			//判断修改的用户名和手机号是否和现有的用户资料冲突
			map.put("phone", user.getPhone());
			map.put("username", user.getUsername());
			map.put("platformType",user.getPlatformType());
			map.put("userId",user.getUserid());
			User data = null;
			//根据创建的用户名查询数据库中是否存在该用户
			if (user.getPlatformType().intValue() == 0 && (StringUtils.isNotBlank(user.getPhone()) || StringUtils.isNotBlank(user.getUsername()))) data = supplierMapper.getSupplierUserByPhoneOrEmail(map);
			else if ( user.getPlatformType().intValue() == 1 && (StringUtils.isNotBlank(user.getPhone()) || StringUtils.isNotBlank(user.getUsername())))data = sellerMapper.getSellerUserByPhoneOrEmail(map);
 			else if (StringUtils.isNotBlank(user.getPhone()) || StringUtils.isNotBlank(user.getUsername())) data = manageMapper.getManageUserByPhoneOrEmail(map);
			if (data != null)throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"用户名或手机号码已经注册");
		}

		//查询修改后的手机号码是否在该平台注册
		if (user.getPhone() != null){
			map.clear();
			map.put("userId",user.getUserid());
			map.put("phone",user.getPhone());
			map.put("platformType",user.getPlatformType());
			Integer isPhoneReg = userMapper.isPhoneReg(map);
			if (isPhoneReg.intValue() != 0) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"该手机号码已经在本平台注册");
		}

		//无论主账户还是子账户都需要修改信息
		Integer result = null;
		if (userResult != null) {
			user.setUpdateDate(new Date());
			user.setRemarks(StringUtils.isNotBlank(userAll.getUser().getUsername()) ? userAll.getUser().getUsername() : "");
			//账户信息修改
			if (user.getPlatformType().intValue() == 0) result = supplierMapper.supplierUpdateInfo(user);
			else if ( user.getPlatformType().intValue() == 1 ) result = sellerMapper.sellerUpdateInfo(user);
			else result = manageMapper.manageUpdateInfo(user);

			//子级账户的资料修改,需要修改角色信息
			if ( userResult.getParentId() != null ) {
				if ( roleIds != null ) {
					Integer roleDeleteResult = userRoleMapper.deleteUser_Role(user.getUserid());//1.先删除当前用户的角色信息    2.增加用户的角色信息
					if ( roleDeleteResult != null ) {
						map.clear();// 增加用户角色id的关系表
						map.put("userId",user.getUserid());
						map.put("role",roleIds);
						Integer inserRoleResult = userRoleMapper.insertUserRole(map);
					}else{
						throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100411.getCode(),"修改个人资料失败，请稍后重试");
					}
				}
			}
			if (result == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100411.getCode(),"修改个人资料失败，请稍后重试");
		}else {
			logger.error("未找到该用户请重新输入");
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"未找到该用户请重新输入");
		}
		return result;
	}

	/**
	 *绑定手机号码===》判断该账户的手机是否存在
	 * @param id
	 * @param platformType
	 * @return
	 */
    @Override
    public String getBingdingUserPhone(Integer id,Integer platformType) {
        return userMapper.getBingdingUserPhone(id,platformType);
    }

	@Override
	public Page<User> findAllByPage(Map<String,Object> map) {
		List<User> all = null;
		if (!map.isEmpty()) all = userMapper.findAll(map);
		PageInfo<User> pageInfo = new PageInfo(all);
		return new Page(pageInfo);
	}

	/************************ 批量删除账号 ************************/
	@Override
	public Integer delectAccount(List<Integer> userIds) {
		Integer delFlag = 0;//删除的状态码
		Map<String,Object> map = new HashMap<String,Object>();
		UserAll userAll = getLoginUserInformationByToken.getUserInfo();
		//根据用户id查询用户信息
		List<User> user = getUsersByIds(userIds,userAll.getUser().getPlatformType());
		for ( User us : user) {
			if ( us.getParentId() == null ) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401.getCode(),"您无权限操作管理员账户，请重试");
		}
		if ( user == null ) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100410);
		// 获取日志
		Date date = new Date();
		map.put("userIds", userIds);
		map.put("date", date);
		map.put("delFlag", delFlag);
		map.put("platformType",userAll.getUser().getPlatformType());
		Integer result = userMapper.delectAccount(map);
		if (result == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100409);
		//返回结果
		return result;
	}
	public List<User> getUsersByIds(List<Integer> userIds,Integer platformType) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("userIds",userIds);
		map.put("platformType",platformType);
		return userMapper.selectByPrimaryKeys(map);
	}

	@Override
	public JSONObject findUserIdOrUserName(String[] param, String paramType, Integer type) {
		Set<String> list = new HashSet<>(Arrays.asList(param));
		List<Map<String, String>> data = userMapper.findUserIdOrUserName(list.toArray(new String[list.size()]), paramType, type);
		JSONObject json = new JSONObject();
		data.forEach(u -> {
			String userid = String.valueOf(u.get("userId"));
			if (paramType.equalsIgnoreCase("userId")) {
				json.put(userid, u.get("username"));
			} else {
				json.put(u.get("username"), userid);
			}
		});
		return json;
	}


}
