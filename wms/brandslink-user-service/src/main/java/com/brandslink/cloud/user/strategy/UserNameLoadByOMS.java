package com.brandslink.cloud.user.strategy;

import com.brandslink.cloud.common.constant.UserConstant;
import com.brandslink.cloud.common.entity.CustomerDetails;
import com.brandslink.cloud.common.entity.CustomerInfoEntity;
import com.brandslink.cloud.common.entity.CustomerUserDetailInfo;
import com.brandslink.cloud.common.entity.UserEntity;
import com.brandslink.cloud.common.utils.RedisUtils;
import com.brandslink.cloud.user.entity.CustomerInfo;
import com.brandslink.cloud.user.entity.CustomerUserInfo;
import com.brandslink.cloud.user.mapper.CustomerInfoMapper;
import com.brandslink.cloud.user.mapper.CustomerUserInfoMapper;
import com.brandslink.cloud.user.utils.CustomerUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * oms系统登录策略
 *
 * @ClassName UserNameLoadByOMS
 * @Author tianye
 * @Date 2019/8/29 11:06
 * @Version 1.0
 */
@Component(UserConstant.PLATFORM_TYPE_FLAG_OMS)
public class UserNameLoadByOMS implements IUserNameLoadStrategy {

    @Resource
    private CustomerUserInfoMapper customerUserInfoMapper;

    @Resource
    private CustomerInfoMapper customerInfoMapper;

    @Resource
    private RedisUtils redisUtils;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        List<CustomerUserInfo> customerUserInfos = customerUserInfoMapper.selectByAccountResult(username);
        return createUserEntity(customerUserInfos, "账号不存在，请重新登录!");
    }

    @Override
    public UserDetails loadUserByMobile(String mobile) throws UsernameNotFoundException {

        List<CustomerUserInfo> customerUserInfos = customerUserInfoMapper.selectByMobileResult(mobile);
        return createUserEntity(customerUserInfos, "手机号不存在，请重新登录!");
    }

    /**
     * 构建UserEntity
     *
     * @param customerUserInfos
     * @param message
     * @return
     */
    private UserEntity createUserEntity(List<CustomerUserInfo> customerUserInfos, String message) {
        if (CollectionUtils.isEmpty(customerUserInfos)) {
            throw new InternalAuthenticationServiceException(message);
        }

        CustomerUserInfo customerUserInfo = customerUserInfos.get(0);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (StringUtils.isNotBlank(customerUserInfo.getRoleCode())) {
            authorities = customerUserInfos.stream().map(CustomerUserInfo::getRoleCode).distinct().map(String::valueOf).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        }

        UserEntity userEntity = CustomerUtil.commonCreateUserEntity(customerUserInfo.getAccount(), customerUserInfo.getPassword(), customerUserInfo.getEnabled(), authorities);

        // 客户账户信息
        CustomerUserDetailInfo customerUserDetailInfo = new CustomerUserDetailInfo();

        BeanUtils.copyProperties(customerUserInfo, customerUserDetailInfo);
        customerUserDetailInfo.setRoleCode(customerUserInfos.stream().filter(c -> StringUtils.isNotBlank(c.getRoleCode())).map(CustomerUserInfo::getRoleCode).distinct().collect(Collectors.joining(",")));
        customerUserDetailInfo.setRoleName(customerUserInfos.stream().filter(c -> StringUtils.isNotBlank(c.getRoleName())).map(CustomerUserInfo::getRoleName).distinct().collect(Collectors.joining(",")));
        customerUserDetailInfo.setPassword(null);

        // 客户信息
        CustomerInfoEntity customerInfoEntity = new CustomerInfoEntity();

        List<CustomerInfo> customerInfo = customerInfoMapper.selectCustomerDetailAndWarehouseInfoByPrimaryKey(customerUserDetailInfo.getCustomerId());
        BeanUtils.copyProperties(customerInfo.get(0), customerInfoEntity);
        customerInfoEntity.setWarehouseName(customerInfo.stream().filter(c -> StringUtils.isNotBlank(c.getWarehouseName())).map(CustomerInfo::getWarehouseName).distinct().collect(Collectors.joining(",")));
        customerInfoEntity.setWarehouseCode(customerInfo.stream().filter(c -> StringUtils.isNotBlank(c.getWarehouseCode())).map(CustomerInfo::getWarehouseCode).distinct().collect(Collectors.joining(",")));

        CustomerDetails customerDetails = new CustomerDetails();

        customerDetails.setCustomerInfoEntity(customerInfoEntity);
        customerDetails.setCustomerUserDetailInfo(customerUserDetailInfo);

        redisUtils.set(UserConstant.PLATFORM_TYPE_ACCOUNT_REDIS_USER_TOKEN_KEY_FIX_OMS + userEntity.getUsername(), customerDetails, UserConstant.REDIS_TIMEOUT);

        return userEntity;
    }
}
