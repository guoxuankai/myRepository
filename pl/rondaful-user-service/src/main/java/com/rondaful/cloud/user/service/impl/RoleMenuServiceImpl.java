package com.rondaful.cloud.user.service.impl;

import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.user.entity.Menu;
import com.rondaful.cloud.user.entity.Role;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.mapper.MenuMapper;
import com.rondaful.cloud.user.mapper.RoleMapper;
import com.rondaful.cloud.user.mapper.RoleMenuMapper;
import com.rondaful.cloud.user.service.RoleMenuService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 角色权限实现类
 * @author Administrator
 *
 */
@Service("roleMenuService")
public class RoleMenuServiceImpl implements RoleMenuService{
	
	@Autowired
	private RoleMenuMapper roleMenuMapper;

	@Autowired
    private MenuMapper menuMapper;
	
	@Autowired
	private RoleMapper roleMapper;
	
	private Logger logger = LoggerFactory.getLogger(RoleMenuServiceImpl.class);

	/**
	 * 添加角色权限关系
	 * @param map
	 * @param menuIds
	 * @return
	 */
	@Override
	public Integer insertOrleMenu(Map<String, Object> map,List<Integer> menuIds) {
		// 创建用户id和角色id的关系表
        HashSet<Integer> ids = new HashSet<>();
        ids.addAll(menuIds);
        Menu menuByMenuId = null;
		for(Integer mId :menuIds){
            menuByMenuId = menuMapper.getMenuByMenuId(mId);
            if(menuByMenuId == null || menuByMenuId.getParentId() == 0) continue;
            Integer menuId = recursionMenuId(menuByMenuId.getParentId());
            ids.add(menuId);
        }
        map.put("ids",ids);
        Integer result = roleMenuMapper.insertOrleMenu(map);
		return result;
	}

    /**
     * 查詢menuid的父id
     * @param menuId
     */
	public Integer recursionMenuId(Integer menuId){
        Menu menuByMenuId = menuMapper.getMenuByMenuId(menuId);
        if ( menuByMenuId.getParentId() == 0 ) return menuByMenuId.getId();
        recursionMenuId(menuByMenuId.getParentId());
        return menuByMenuId.getId();
    }

	/**
	 * 删除角色权限关系
	 * @param rid
	 * @param platformType
	 * @return
	 */
	@Override
	public Integer roleMenuDelete(Integer rid,Integer platformType) {
		Integer result = roleMenuMapper.deleteRoleMenu(rid,platformType);
		if ( result == null ) {
			logger.error("删除角色权限关系失败");
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100413);
		}
		return result;
	}

	/**
	 * 显示角色详细信息
	 * @param map
	 * @return
	 */
	@Override
	public Page getRoleList(Map<String, Object> map) {
		String currPage = (String)map.get("currPage");
		String row = (String)map.get("row");
		Page.builder(currPage, row);
		//角色列表展示
		List<Role> roleList = roleMapper.getRoleByMap(map);
		List<Integer> mnuIds = null;
		List<String> menuNames = null;
		for(Role role : roleList) {
			//迭代每一位角色，根据角色id获取对应的权限名称
			List<Map<String,Object>> menuMap = roleMenuMapper.getMenuNameByRoleId(role.getRid());
			Integer menuid = null;
			String menuName = null;
			mnuIds = new ArrayList<Integer>();
			menuNames = new ArrayList<String>();
			for ( Map<String,Object> map01 : menuMap ) {
				menuid = (Integer)map01.get("id");
				menuName = (String)map01.get("name");
				mnuIds.add(menuid);
				String english = Utils.translation(menuName);
				menuNames.add(english);
			}
			//将权限名称添加到用户信息中
			role.setMenuId(mnuIds);
			role.setMenuName(menuNames);
		}
		PageInfo pi = new PageInfo<>(roleList);
		return new Page(pi);

	}
	
	

}
