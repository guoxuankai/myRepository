package com.brandslink.cloud.user.strategy;

import com.brandslink.cloud.common.constant.UserConstant;
import com.brandslink.cloud.user.entity.UserInfo;
import com.brandslink.cloud.user.mapper.UserInfoMapper;
import com.brandslink.cloud.user.utils.CustomerUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * wms系统登录策略
 *
 * @ClassName UserNameLoadByWMS
 * @Author tianye
 * @Date 2019/8/29 10:50
 * @Version 1.0
 */
@Component(UserConstant.PLATFORM_TYPE_FLAG_OCMS)
public class UserNameLoadByOCMS implements IUserNameLoadStrategy {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (!StringUtils.equals(username, UserConstant.ADMIN)) {
            throw new BadCredentialsException("账号不存在，请重新登录!");
        }

        // 实现通过账号查询用户信息逻辑
        UserInfo info = userInfoMapper.selectByAccount(username);

        return CustomerUtil.commonCreateUserEntity(info.getAccount(), info.getPassword(), info.getEnabled(), null);
    }

    @Override
    public UserDetails loadUserByMobile(String mobile) throws UsernameNotFoundException {
        throw new InternalAuthenticationServiceException("ocms系统暂不支持手机号短信验证码登录!");
    }

}
