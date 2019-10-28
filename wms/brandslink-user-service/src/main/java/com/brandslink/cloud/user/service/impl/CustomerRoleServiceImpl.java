package com.brandslink.cloud.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.brandslink.cloud.common.entity.CustomerUserDetailInfo;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.brandslink.cloud.common.utils.GetUserDetailInfoUtil;
import com.brandslink.cloud.user.dto.request.CustomerRoleRequestDTO;
import com.brandslink.cloud.user.dto.response.CustomerRoleInfoResponseDTO;
import com.brandslink.cloud.user.entity.CustomerRoleInfo;
import com.brandslink.cloud.user.mapper.CustomerRoleInfoMapper;
import com.brandslink.cloud.user.mapper.MenuInfoMapper;
import com.brandslink.cloud.user.service.ICustomerRoleService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 用户管理业务接口实现
 * @date 2019/9/4 11:40
 */
@Service
public class CustomerRoleServiceImpl implements ICustomerRoleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerRoleServiceImpl.class);

    @Resource
    private CustomerRoleInfoMapper customerRoleInfoMapper;

    @Resource
    private MenuInfoMapper menuInfoMapper;

    @Resource
    private GetUserDetailInfoUtil getUserDetailInfoUtil;

    @Override
    public Page<CustomerRoleInfoResponseDTO> getRoleList(CustomerRoleRequestDTO request) {
        LOGGER.info("查询客户端角色列表信息request：{}", JSON.toJSONString(request));
        CustomerUserDetailInfo customerUserDetailInfo = getUserDetailInfoUtil.getCustomerDetails().getCustomerUserDetailInfo();
        request.setCustomerId(customerUserDetailInfo.getCustomerId());
        Page.builder(request.getPage(), request.getRow());
        return new Page<>(new PageInfo<>(customerRoleInfoMapper.selectRoleList(request)));
    }

    @Override
    public void addRole(String roleName, String roleDescription, List<Integer> menuIds) {
        LOGGER.info("新增角色request  roleName:{},roleDescription:{},menuIds:{}", roleName, roleDescription, menuIds.toString());
        CustomerUserDetailInfo userDetailInfo = getUserDetailInfoUtil.getCustomerDetails().getCustomerUserDetailInfo();
        Integer count = customerRoleInfoMapper.selectByRoleName(roleName, userDetailInfo.getCustomerId());
        if (null != count && count > 0) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100004);
        }
        CustomerRoleInfo roleInfo = new CustomerRoleInfo() {{
            setCreateBy(userDetailInfo.getName());
            setCreateTime(new Date());
            setRoleName(roleName);
            setCustomerId(userDetailInfo.getCustomerId());
            setRoleDescription(roleDescription);
        }};
        customerRoleInfoMapper.insertSelective(roleInfo);
//        List<Integer> homeMenuIds = menuInfoMapper.selectHomeMenuIds(2);
//        if (CollectionUtils.isNotEmpty(menuIds)) {
//            homeMenuIds.addAll(menuIds);
//        }
//        if (CollectionUtils.isNotEmpty(homeMenuIds)) {
//            customerRoleInfoMapper.insertMenusByRoleId(roleInfo.getId(), homeMenuIds, 0);
//        }
        customerRoleInfoMapper.insertMenusByRoleId(roleInfo.getId(), menuIds, 0);
    }

    @Override
    public void addPermission(Integer id, String roleDescription, List<Integer> menuIds) {
        LOGGER.info("设置权限request roleId:{},roleDescription:{},menuIds:{}", id, roleDescription, menuIds.toString());
        CustomerRoleInfo roleInfo = customerRoleInfoMapper.selectByPrimaryKey(id.longValue());
        if (!StringUtils.equals(roleInfo.getRoleDescription(), roleDescription)) {
            customerRoleInfoMapper.updateByPrimaryKeySelective(new CustomerRoleInfo() {{
                setId(id);
                setRoleDescription(roleDescription);
                setLastUpdateBy(getUserDetailInfoUtil.getCustomerDetails().getCustomerUserDetailInfo().getName());
                setLastUpdateTime(new Date());
            }});
        } else {
            customerRoleInfoMapper.updateByPrimaryKeySelective(new CustomerRoleInfo() {{
                setId(id);
                setLastUpdateBy(getUserDetailInfoUtil.getCustomerDetails().getCustomerUserDetailInfo().getName());
                setLastUpdateTime(new Date());
            }});
        }
        if (CollectionUtils.isNotEmpty(menuIds)) {
            customerRoleInfoMapper.deleteMenusByRoleId(id, 0);
            customerRoleInfoMapper.insertMenusByRoleId(id, menuIds, 0);
        }
    }

    @Override
    public Map<String, Object> getPermission(Integer id) {
        CustomerRoleInfo roleInfo = customerRoleInfoMapper.selectByPrimaryKey(id.longValue());
        Map<String, Object> map = new HashMap<>(3);
        map.put("roleName", roleInfo.getRoleName());
        map.put("roleDescription", roleInfo.getRoleDescription());
        map.put("menus", customerRoleInfoMapper.selectMenusByRoleId(id, 0));
        return map;
    }
}
