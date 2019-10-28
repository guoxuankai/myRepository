package com.brandslink.cloud.user.strategy;

import com.brandslink.cloud.common.constant.UserConstant;
import com.brandslink.cloud.common.entity.UserDetailInfo;
import com.brandslink.cloud.common.entity.UserEntity;
import com.brandslink.cloud.common.utils.RedisUtils;
import com.brandslink.cloud.user.entity.UserInfo;
import com.brandslink.cloud.user.mapper.UserInfoMapper;
import com.brandslink.cloud.user.utils.CustomerUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * wms系统登录策略
 *
 * @ClassName UserNameLoadByWMS
 * @Author tianye
 * @Date 2019/8/29 10:50
 * @Version 1.0
 */
@Component(UserConstant.PLATFORM_TYPE_FLAG_WMS)
public class UserNameLoadByWMS implements IUserNameLoadStrategy {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private RedisUtils redisUtils;

    @Autowired
    private HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 实现通过账号查询用户信息逻辑
        List<UserInfo> userInfo = userInfoMapper.selectByAccountResult(username);

        if (CollectionUtils.isEmpty(userInfo)) {
            throw new BadCredentialsException("账号不存在，请重新登录!");
        }

        UserInfo info = userInfo.get(0);

        // 登录仓库代码
        String warehouseCode = request.getParameter("warehouseCode");
        // 登录仓库名称
        String warehouseName = request.getParameter("warehouseName");

        if (StringUtils.isNotBlank(warehouseCode) && StringUtils.isNotBlank(warehouseName)) {
            List<String> warehouseCodeList = userInfo.stream().map(UserInfo::getWarehouseCode).distinct().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(warehouseCodeList) || !warehouseCodeList.contains(warehouseCode)) {
                throw new BadCredentialsException("没有【" + warehouseName + "】登录权限，请选择其他仓库!");
            }
            info.setWarehouseCode(warehouseCode);
            info.setWarehouseName(warehouseName);
        }
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (StringUtils.isNotBlank(info.getRoleCode())) {
            authorities = userInfo.stream().map(UserInfo::getRoleCode).distinct().map(String::valueOf).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        }

        UserEntity userEntity = CustomerUtil.commonCreateUserEntity(info.getAccount(), info.getPassword(), info.getEnabled(), authorities);

        UserDetailInfo detailInfo = new UserDetailInfo();
        BeanUtils.copyProperties(info, detailInfo);
        detailInfo.setRoleCode(userInfo.stream().filter(c -> StringUtils.isNotBlank(c.getRoleCode())).map(UserInfo::getRoleCode).distinct().collect(Collectors.joining(",")));
        detailInfo.setRoleName(userInfo.stream().filter(c -> StringUtils.isNotBlank(c.getRoleName())).map(UserInfo::getRoleName).distinct().collect(Collectors.joining(",")));
        if (StringUtils.isBlank(warehouseCode) && StringUtils.isBlank(warehouseName)) {
            detailInfo.setWarehouseCode(userInfo.stream().filter(c -> StringUtils.isNotBlank(c.getWarehouseCode())).map(UserInfo::getWarehouseCode).distinct().collect(Collectors.joining(",")));
            detailInfo.setWarehouseName(userInfo.stream().map(m -> {
                if (StringUtils.isNotBlank(m.getWarehouseName())) {
                    return m.getWarehouseName().split("-")[0];
                }
                return null;
            }).distinct().collect(Collectors.joining(",")));
        }

        detailInfo.setPassword(null);
        redisUtils.set(UserConstant.PLATFORM_TYPE_ACCOUNT_REDIS_USER_TOKEN_KEY_FIX_WMS + detailInfo.getAccount(), detailInfo, UserConstant.REDIS_TIMEOUT);

        return userEntity;
    }

    @Override
    public UserDetails loadUserByMobile(String mobile) throws UsernameNotFoundException {
        throw new InternalAuthenticationServiceException("wms系统暂不支持手机号短信验证码登录!");
    }

}
