package com.brandslink.cloud.user.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.user.dto.request.CustomerRoleRequestDTO;
import com.brandslink.cloud.user.dto.response.CustomerRoleInfoResponseDTO;
import com.brandslink.cloud.user.entity.CustomerRoleInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface CustomerRoleInfoMapper extends BaseMapper<CustomerRoleInfo> {

    /**
     * 通过请求url查询所需要的角色列表
     *
     * @param requestUrl
     * @return
     */
    List<Integer> getRoleListByMenuUrl(@Param("requestUrl") String requestUrl);

    /**
     * 根据角色id查询所有url列表
     *
     * @param authorityList
     * @return
     */
    List<String> selectMenuUrlByRoleList(List<Integer> authorityList);

    /**
     * @param userId
     * @param roleIds
     * @description: 新增用户对应的角色
     */
    void insertCustomerAccountRole(@Param("userId") Integer userId, @Param("roleIds") Set<Integer> roleIds);

    /**
     * @param userId
     * @description: 根据账号删除角色
     */
    void deleteCustomerRoleByAccountId(@Param("userId") Integer userId);

    /**
     * 根据用户id查询对应角色信息
     *
     * @param idList
     * @return
     */
    List<CustomerRoleInfo> selectByUserIdList(List<Integer> idList);

    /**
     * 查询角色列表
     *
     * @param request
     * @return
     */
    List<CustomerRoleInfoResponseDTO> selectRoleList(CustomerRoleRequestDTO request);

    /**
     * 根据角色名称以及客户id查询角色名称是否存在
     *
     * @param roleName
     * @param customerId
     * @return
     */
    Integer selectByRoleName(@Param("roleName") String roleName, @Param("customerId") Integer customerId);

    /**
     * 查询初始化角色id  0：初始化角色  -1：主账号角色
     *
     * @param flag
     * @return
     */
    Integer selectInitializeRoleId(@Param("flag") Integer flag);

    /**
     * 根据角色id设置角色权限
     *
     * @param roleId
     * @param menuIds
     */
    void insertMenusByRoleId(@Param("roleId") Integer roleId, @Param("list") List<Integer> menuIds, @Param("flag") Integer flag);

    /**
     * 根据角色id删除对应权限
     *
     * @param id
     * @param flag
     */
    void deleteMenusByRoleId(@Param("roleId") Integer id, @Param("flag") Integer flag);

    /**
     * 根据角色id获取当前角色对应的菜单id （过滤菜单类型，只保留功能类型）
     *
     * @param id
     * @return
     */
    List<Integer> selectMenusByRoleId(@Param("id") Integer id, @Param("flag") Integer flag);

    /**
     * 根据角色id获取当前角色对应的菜单id (包含菜单类型)
     *
     * @param id
     * @return
     */
    List<Integer> selectMenusByRoleIdAll(@Param("id") Integer id, @Param("flag") Integer flag);

}