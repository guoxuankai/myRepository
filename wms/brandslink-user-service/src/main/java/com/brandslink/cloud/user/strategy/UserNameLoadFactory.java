package com.brandslink.cloud.user.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录策略工厂
 *
 * @ClassName UserNameLoadFactory
 * @Author tianye
 * @Date 2019/8/29 10:56
 * @Version 1.0
 */
@Service
public class UserNameLoadFactory {

    private final Map<String, IUserNameLoadStrategy> strategyMap = new ConcurrentHashMap<>();

    @Autowired
    public UserNameLoadFactory(Map<String, IUserNameLoadStrategy> strategyMap) {
        this.strategyMap.clear();
        strategyMap.forEach(this.strategyMap::put);
    }

    public UserDetails loadUserByUsername(String platformType, String username) throws UsernameNotFoundException {
        return strategyMap.get(platformType).loadUserByUsername(username);
    }

    public UserDetails loadUserByMobile(String platformType, String mobile) throws UsernameNotFoundException {
        return strategyMap.get(platformType).loadUserByMobile(mobile);
    }
}
