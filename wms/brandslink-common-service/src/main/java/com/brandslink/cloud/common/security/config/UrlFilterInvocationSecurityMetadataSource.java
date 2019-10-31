package com.brandslink.cloud.common.security.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.constant.UserConstant;
import com.brandslink.cloud.common.entity.UserEntity;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 通过当前的请求地址，获取该地址需要的用户角色
 *
 * @ClassName UrlFilterInvocationSecurityMetadataSource
 * @Author tianye
 * @Date 2019/5/27 17:52
 * @Version 1.0
 */
@Component
public class UrlFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlFilterInvocationSecurityMetadataSource.class);

    /**
     * 首页免鉴权接口集合
     */
    private static final List<String> urls = new ArrayList<>();

    @Value("${system.gateway.address}")
    private String address;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${system.default.request.url}")
    private String defaultRequestUrl;

    @Override
    public void afterPropertiesSet() {
        if (StringUtils.isNotBlank(defaultRequestUrl)) {
            String[] split = defaultRequestUrl.replaceAll(" ", "").split(",");
            urls.addAll(Arrays.asList(split));
        }
    }

    /**
     * 主要责任就是当访问一个url时返回这个url所需要的访问权限
     *
     * @param o
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws AuthenticationException, AccessDeniedException {
        HttpServletRequest request = ((FilterInvocation) o).getHttpRequest();
        // 平台登录类型
        String platformType = request.getHeader(UserConstant.PLATFORM_TYPE_REQUEST_HEADER_NAME);
        // 运营平台暂时没有登录，如果是运营平台请求，全部放行
        if (StringUtils.equals(platformType, "2")) {
            return null;
        }
        // 内部调用接口放行
        String header = request.getHeader(UserConstant.FEIGN_REQUEST_HEADER_NAME);
        if (StringUtils.equals(header, UserConstant.FEIGN_REQUEST_HEADER_VALUE)) {
            return null;
        }

        // admin用户直接放行，用于测试
        UserEntity userEntity = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (StringUtils.equals(userEntity.getUsername(), UserConstant.ADMIN)) {
            return null;
        }
        String requestURI = request.getRequestURI();
        // 修改密码接口，只需要登录就可以访问，不需要鉴权
        if (StringUtils.equals(requestURI, UserConstant.CHANGE_PASSWORD_URL)) {
            return null;
        }
        // 首页接口，登录即可访问，不需要鉴权
        if (CollectionUtils.isNotEmpty(urls)) {
            for (String url : urls) {
                if (StringUtils.equals(url, requestURI)) {
                    return null;
                }
            }
        }

        // 通过请求url查询所需要的角色列表
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("requestUrl", requestURI);
        map.add("platformType", platformType);
        ResponseEntity<String> roles = restTemplate.postForEntity(address + "/user/role/getMenusByRequestUrl", map, String.class);
//        ResponseEntity<String> roles = restTemplate.postForEntity("http://localhost:8192/role/getMenusByRequestUrl", map, String.class);
        JSONObject result = JSON.parseObject(roles.getBody());
        // 调用接口错误
        if (null == result || !(boolean) result.get("success")) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100407);
        }
        String data = (String) result.get("data");
        LOGGER.info("根据url：{},查询所需要的访问权限，角色列表：{}", requestURI, data);
        // 获取角色列表
        List<Integer> list = JSON.parseArray(data, Integer.class);
        // 如果返回角色列表为空，返回-1，后续会进行模糊匹配
        if (CollectionUtils.isEmpty(list)) {
            return SecurityConfig.createList("-1");
        }
        String[] strings = list.stream().distinct().map(String::valueOf).toArray(String[]::new);
        return SecurityConfig.createList(strings);
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

}
