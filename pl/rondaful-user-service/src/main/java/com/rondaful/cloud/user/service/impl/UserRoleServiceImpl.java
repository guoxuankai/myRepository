package com.rondaful.cloud.user.service.impl;

import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.user.entity.RoleInfo;
import com.rondaful.cloud.user.entity.User;
import com.rondaful.cloud.user.mapper.PublicCommomMapper;
import com.rondaful.cloud.user.mapper.UserRoleMapper;
import com.rondaful.cloud.user.service.UserRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("userRoleService")
public class UserRoleServiceImpl implements UserRoleService{
	
	@Autowired
	UserRoleMapper userRoleMapper;
	
	@Autowired
    PublicCommomMapper userMapper;
	
	private Logger logger = LoggerFactory.getLogger(UserRoleServiceImpl.class);

	/**
	 * 创建用户和角色的关系
	 * @param map
	 * @return
	 */
	@Override
	public Integer insertUser_Orle(Map<String,Object> map) {
		// 创建用户id和角色id的关系表
		Integer result = userRoleMapper.insertUserRole(map);
		return result;
	}

	/**
	 * 用户列表
	 * @param map  查询条件
	 * @return
	 */
	@Override
	public Page userListQuery(Map<String, Object> map) {
		//用户列表展示
		Page.builder((String)map.get("currPage"), (String)map.get("row"));
		List<Map<String,Object>> userMap = new ArrayList<>();
		List<User> userList = userMapper.getUserByMap(map);
		if (userList != null){
			for(User user : userList) {
				Map<String,Object> map1 = new HashMap<>();
				//迭代每一位用户，根据用户id获取对应的角色名称
				List<RoleInfo> roleName = userRoleMapper.getRoleName(user.getUserid());
				map1.put("user",user);
				map1.put("RoleInfo",roleName);
				userMap.add(map1);
			}
		}
		PageInfo pi = new PageInfo<>(userMap);
		return new Page(pi);
	}

}
