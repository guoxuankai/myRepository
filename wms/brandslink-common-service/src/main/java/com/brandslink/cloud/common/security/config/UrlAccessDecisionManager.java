package com.brandslink.cloud.common.security.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.constant.UserConstant;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 实现自定义 决策管理器 判断是否含有权限
 *
 * @ClassName UrlAccessDecisionManager
 * @Author tianye
 * @Date 2019/5/27 18:02
 * @Version 1.0
 */
@Component
public class UrlAccessDecisionManager implements AccessDecisionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlAccessDecisionManager.class);

    @Value("${system.gateway.address}")
    private String address;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) throws AccessDeniedException {
        HttpServletRequest request = ((FilterInvocation) o).getHttpRequest();
        String requestURI = request.getRequestURI();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        // 如果当前用户没有任何角色，则不能访问任何需要权限的接口
        if (CollectionUtils.isEmpty(authorities)) {
            LOGGER.debug("当前用户没有任何角色，不能访问任何需要权限的接口!");
            throw new AccessDeniedException("没有访问权限");
        }
        for (ConfigAttribute configAttribute : collection) {
            String needRole = configAttribute.getAttribute();
            for (GrantedAuthority ga : authorities) {
                if (needRole.trim().equals(ga.getAuthority().trim())) {
                    return;
                }
            }
        }
        // 平台登录类型
        String platformType = request.getHeader(UserConstant.PLATFORM_TYPE_REQUEST_HEADER_NAME);
        // 支持requestUrl模糊匹配
        String authority = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        // 通过用户角色id查询所能访问的所有url
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("authority", authority);
        map.add("platformType", platformType);
        ResponseEntity<String> roles = restTemplate.postForEntity(address + "/user/role/getMenusByRoleList", map, String.class);
//        ResponseEntity<String> roles = restTemplate.postForEntity("http://localhost:8192/role/getMenusByRoleList", map, String.class);
        JSONObject result = JSON.parseObject(roles.getBody());
        // 调用接口错误
        if (null == result || !(boolean) result.get("success")) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100407);
        }
        String data = (String) result.get("data");
        if (StringUtils.isBlank(data)) {
            LOGGER.debug("当前用户所拥有的角色，没有配置模糊匹配的url!");
            throw new AccessDeniedException("没有访问权限");
        }
        String[] split = data.split(",");
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (String str : split) {
            if (antPathMatcher.match(str, requestURI)) {
                return;
            }
        }
        LOGGER.debug("当前用户没有请求url的权限!");
        throw new AccessDeniedException("没有访问权限");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
