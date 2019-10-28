package com.rondaful.cloud.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.user.entity.NewRole;
import com.rondaful.cloud.user.entity.NewRoleMenu;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.mapper.NewRoleMapper;
import com.rondaful.cloud.user.mapper.NewRoleMenuMapper;
import com.rondaful.cloud.user.mapper.NewUserRoleMapper;
import com.rondaful.cloud.user.model.PageDTO;
import com.rondaful.cloud.user.model.dto.menu.TreeDTO;
import com.rondaful.cloud.user.model.dto.role.BindRoleDTO;
import com.rondaful.cloud.user.model.dto.role.QueryRolePageDTO;
import com.rondaful.cloud.user.model.dto.role.RoleDTO;
import com.rondaful.cloud.user.service.INewRoleService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/25
 * @Description:
 */
@Service("roleServiceImpl")
public class NewRoleServiceImpl implements INewRoleService {
    private Logger logger = LoggerFactory.getLogger(NewRoleServiceImpl.class);
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private NewRoleMapper roleMapper;
    @Autowired
    private NewRoleMenuMapper roleMenuMapper;
    @Autowired
    private NewUserRoleMapper userRoleMapper;


    /**
     * 新增角色
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer add(RoleDTO dto) {
        logger.info("新增角色:dto={}",dto.toString());
        NewRole role=new NewRole();
        BeanUtils.copyProperties(dto,role);
        role.setCreateDate(new Date());
        role.setVsersion(1);
        role.setUpdateDate(role.getCreateDate());
        role.setUpdateBy(role.getCreateBy());
        role.setStatus(1);
        this.roleMapper.insertId(role);
        if (CollectionUtils.isEmpty(dto.getList())){
            return role.getId();
        }
        List<NewRoleMenu> menus=new ArrayList<>();
        for (Integer menuId:dto.getList()) {
            NewRoleMenu roleMenu=new NewRoleMenu();
            roleMenu.setRoleId(role.getId() );
            roleMenu.setMenuId(menuId);
            menus.add(roleMenu);
        }
        this.roleMenuMapper.insertBatch(menus);
        return role.getId();
    }

    /**
     * 删除角色
     *
     * @param roleId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer delete(Integer roleId) {
        logger.info("删除角色:roleId={}",roleId);
        if (CollectionUtils.isNotEmpty(this.userRoleMapper.getByRoleId(roleId))){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"role.error.bind.user");
        }
        this.roleMapper.deleteByPrimaryKey(roleId.longValue());
        this.roleMenuMapper.deleteByRoleId(roleId);
        this.clearCache(roleId);
        return 1;
    }

    /**
     * 修改角色名
     *
     * @param roleId
     * @param roleName
     * @param remark
     * @return
     */
    @Override
    public Integer updateRoleName(Integer roleId, String roleName, String remark,String updateBy) {
        logger.info("修改角色:roleId={},roleName={},remark={}",roleId,roleName,remark);
        NewRole role=new NewRole();
        role.setUpdateBy(updateBy);
        role.setUpdateDate(new Date());
        role.setRemark(remark);
        role.setId(roleId);
        role.setRoleName(roleName);
        return this.roleMapper.updateByPrimaryKeySelective(role);
    }

    /**
     * 修改菜单授权
     *
     * @param list
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateRoleMenu(List<Integer> list,Integer roleId) {
        logger.info("修改菜单授权:list={}", CollectionUtils.isEmpty(list));
        NewRole roleDO=this.roleMapper.selectByPrimaryKey(roleId.longValue());
        if (roleDO==null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.update.object.not.exist");
        }
        List<NewRoleMenu> menus=new ArrayList<>(list.size());
        for (Integer menuId:list) {
            NewRoleMenu menu=new NewRoleMenu();
            menu.setMenuId(menuId);
            menu.setRoleId(roleId);
            menus.add(menu);
        }
        this.roleMenuMapper.deleteByRoleId(roleId);
        if (CollectionUtils.isNotEmpty(list)){
            this.roleMenuMapper.insertBatch(menus);
        }
        this.clearCache(roleId);
        return 1;
    }

    /**
     * 分页查询角色
     *
     * @param dto
     * @return
     */
    @Override
    public PageDTO<RoleDTO> getPage(QueryRolePageDTO dto) {
        logger.info("分页查询角色:dto={}",dto.toString());
        PageHelper.startPage(dto.getCurrentPage(),dto.getPageSize());
        List<NewRole> list=this.roleMapper.getPage(dto.getRoleName(),dto.getStartTime(),dto.getEndTime(),dto.getPlatform(),dto.getAttribution());
        PageInfo<NewRole> pageInfo=new PageInfo<>(list);
        PageDTO<RoleDTO> result=new PageDTO<>(pageInfo.getTotal(),dto.getCurrentPage().longValue());
        if (pageInfo.getTotal()<1){
            return result;
        }
        List<RoleDTO> data=new ArrayList<>(pageInfo.getList().size());
        for (NewRole newRole:pageInfo.getList()) {
            RoleDTO dto1=new RoleDTO();
            BeanUtils.copyProperties(newRole,dto1);
            dto1.setList(this.roleMenuMapper.getMenu(newRole.getId()));
            data.add(dto1);
        }
        result.setList(data);
        return result;
    }

    /**
     * 根据角色id获取路径权限
     *
     * @param roleId
     * @return
     */
    @Override
    public List<Integer> getMenu(Integer roleId) {
        logger.info("根据角色id获取路径权限:roleId={}",roleId);
        return this.roleMenuMapper.getMenu(roleId);
    }

    /**
     * 根据角色id批量获取路径权限
     *
     * @param roleIds
     * @return
     */
    @Override
    public List<Integer> getsMenu(List<Integer> roleIds) {
        return this.roleMenuMapper.getsMenu(roleIds);
    }

    /**
     * 根据角色批量获取角色名
     *
     * @param roleIds
     * @return
     */
    @Override
    public List<BindRoleDTO> getsName(List<Integer> roleIds) {
        List<NewRole> list=this.roleMapper.getsName(roleIds);
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        List<BindRoleDTO> result=new ArrayList<>(list.size());
        for (NewRole role:list) {
            result.add(new BindRoleDTO(role.getId(),role.getRoleName()));
        }
        return result;
    }

    /**
     * 查询所有角色名
     *
     * @param platformType
     * @param attributionId
     * @return
     */
    @Override
    public List<TreeDTO> getTree(Integer platformType, Integer attributionId) {
        List<NewRole> list= this.roleMapper.selectByAttr(platformType,attributionId);
        List<TreeDTO> result=new ArrayList<>();
        for (NewRole newRole:list) {
            result.add(new TreeDTO(newRole.getId(),newRole.getRoleName()));
        }
        return result;
    }

    /**
     * 根据路由获取用户id
     *
     * @param href
     * @return
     */
    @Override
    public List<Integer> getsByHref(String href,Integer platformType) {
        List<Integer> roleIds=this.roleMenuMapper.getsByHref(href,platformType);
        if (CollectionUtils.isEmpty(roleIds)){
            return null;
        }
        List<Integer> userIds=this.userRoleMapper.getsByRoleId(roleIds);
        return userIds;
    }

    /**
     * 清除缓存
     * @param roleId
     */
    private void clearCache(Integer roleId){
        String key=ROLE_MENU+roleId;
        this.redisTemplate.delete(key);
    }

}
