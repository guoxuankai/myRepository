
package com.rondaful.cloud.user.service.impl;

import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.user.controller.utils.ControllerUtil;
import com.rondaful.cloud.user.entity.Role;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.mapper.PublicCommomMapper;
import com.rondaful.cloud.user.mapper.RoleMapper;
import com.rondaful.cloud.user.mapper.UserRoleMapper;
import com.rondaful.cloud.user.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色业务实现类
 * @author Administrator
 *
 */
@Service("roleService")
public class RoleServiceImpl extends ControllerUtil implements RoleService {
	
	@Autowired
	private RoleMapper roleMapper;
	
	@Autowired
	private UserRoleMapper userRoleMapper;
	
	@Autowired
	private PublicCommomMapper userMapper;
	
	private Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);
	
	@Autowired
	private GetLoginUserInformationByToken getLoginUserInformationByToken;
	
	@Autowired
	private RedisUtils redisUtils;

	/**
	 * 获取角色的名称和id
	 * @param userId
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getroleInfo(Integer userId) {
		List<Map<String,Object>> getRolemap = roleMapper.getroleInfo(userId);
		return getRolemap;
	}

	/**
	 * 获取角色数量
	 * @param userId
	 * @return
	 */
	@Override
	public Integer getRoleCode(Integer userId) {
		Integer result = roleMapper.getRoleCode(userId);
		//如创建数量为0，则1，如不为0，则在原数量上+1
		if (result != null && result.intValue() == 0 ) return 1;
		if (result == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(),"获取角色代码失败");
		return result+1;
	}
	
	/***********创建角色***********/
	@Override
	public Integer insertRole(Role role,Integer platformType) {
		//判断当前角色是否存在
		Role isRoleResult = roleMapper.isRoleByName(role.getRoleName(),role.getCreateId());
		Integer result = null;
		if ( isRoleResult == null ) {
			role.setCreateDate(new Date());
			role.setUpdateDate(new Date());
			result = roleMapper.insertRole(role);
		}else {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100413.getCode(),"您已经创建当前角色，请重新创建");
		}
		return result;
	}

	/***********修改角色状态***********/
	@Override
	public Integer updataStatus(Integer status, List<Integer> roleIds) {
		Map<String,Object> map = new HashMap<String,Object>();//数据传递
		List<Role> roles = rolesFindByIds(roleIds);//查看根据此id能否找到对应的角色
		if ( roles == null ) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100415);
		Date date = new Date();//封装信息
		map.put("roleIds", roleIds);
		map.put("status", status);
		map.put("date", date);
		//根据信息修改角色状态
		Integer result = roleMapper.updataStatus(map);
		logger.info("角色状态修改成功");
		return result;
	}

	/***********删除角色***********/
	@Override
	public Integer deleteRole(List<Integer> roleIds) {
		//删除的状态码
		Integer delFlag = 0;
		Map<String,Object> map = new HashMap<String,Object>();
		//根据角色id查询角色信息
		List<Role> roles = rolesFindByIds(roleIds);
		Date date = new Date();
		Integer result = roleMapper.delectRole(roleIds);
		logger.info("修改角色成功");
		return result;
	}
	
	/***********修改角色资料***********/
	@Override
	public Integer updateInfo(Role role) {
		role.setUpdateDate(new Date());
		Integer result = roleMapper.updateInfo(role);
		logger.info("角色修改成功");
		return result;
	}
	
	/***********根据多角色id找到对应的角色信息***********/
	@Override
	public List<Role> rolesFindByIds(List<Integer> roleIds) {
		return roleMapper.selectRolesFindByIds(roleIds);
	}

}
