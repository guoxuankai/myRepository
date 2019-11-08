package com.brandslink.cloud.common.security.config;

import com.alibaba.fastjson.JSON;
import com.brandslink.cloud.common.constant.UserConstant;
import com.brandslink.cloud.common.entity.Result;
import com.brandslink.cloud.common.entity.UserEntity;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.utils.Base64Util;
import com.brandslink.cloud.common.utils.CommonMethodUtil;
import com.brandslink.cloud.common.utils.RedisUtils;
import com.brandslink.cloud.common.utils.Utils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 鉴权过滤器
 *
 * @ClassName TokenAuthenticationFilter
 * @Author tianye
 * @Date 2019/5/27 18:02
 * @Version 1.0
 */
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /**
     * 免登录接口集合
     */
    private static final List<String> urls = new ArrayList<>();

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private CommonMethodUtil commonMethodUtil;

    /**
     * 免登录接口
     */
    @Value("${system.ignore.login.url}")
    private String ignore;

    @Value("${swagger.enable}")
    public boolean isDev;

    /**
     * 程序启动时初始化免登录接口
     *
     * @throws ServletException
     */
    @Override
    public void afterPropertiesSet() {
        urls.add("/favicon**");
        urls.add("/status");
        urls.add("/actuator/service-registry");
        if (StringUtils.isNotBlank(ignore)) {
            ignore = ignore.replaceAll(" ", "");
            String[] split = ignore.split(",");
            urls.addAll(Arrays.asList(split));
        }
        if (isDev) {
            urls.addAll(Arrays.asList("/swagger**", "/rest/api/doc"));
        }
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        LOGGER.debug("配置的免登录接口：{}", JSON.toJSONString(urls));

        String uri = request.getRequestURI();

        LOGGER.debug("TokenAuthenticationFilter requestURI ：{}", uri);

        // 平台类型 0：wms 1：oms 2：ocms
        String platformType = request.getHeader(UserConstant.PLATFORM_TYPE_REQUEST_HEADER_NAME);
        // 运营平台暂时没有登录，如果是运营平台请求，全部放行
        if (StringUtils.equals(platformType, "2")) {
            chain.doFilter(request, response);
            return;
        }
        // 账号密码登录接口放行
        if (new AntPathRequestMatcher(UserConstant.LOGIN_URL).matches(request)) {
            platformType = request.getParameter(UserConstant.PLATFORM_TYPE_REQUEST_HEADER_NAME);
            if (StringUtils.isBlank(platformType)) {
                Utils.print(JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100714)));
                return;
            }
            request.setAttribute(UserConstant.PLATFORM_TYPE_REQUEST_HEADER_NAME, commonMethodUtil.getPlatformType(platformType));
            // 解析前端的密文密码
            HttpServletRequestWrapper wrapperLogin = new HttpServletRequestWrapper(request) {
                @Override
                public String getParameter(String name) {
                    if (UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY.equals(name)) {
                        return Base64Util.decodeBase64(super.getParameter(name)).split("-")[0];
                    }
                    return super.getParameter(name);
                }
            };
            chain.doFilter(wrapperLogin, response);
            return;
        }

        // 手机号短信登录接口放行
        if (new AntPathRequestMatcher(UserConstant.SMS_LOGIN_URL).matches(request)) {
            platformType = request.getParameter(UserConstant.PLATFORM_TYPE_REQUEST_HEADER_NAME);
            if (StringUtils.isBlank(platformType)) {
                Utils.print(JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100714)));
                return;
            }
            request.setAttribute(UserConstant.PLATFORM_TYPE_REQUEST_HEADER_NAME, commonMethodUtil.getPlatformType(platformType));
            chain.doFilter(request, response);
            return;
        }

        // 免登录接口放行
        for (String s : urls) {
            if (new AntPathRequestMatcher(s.trim()).matches(request)) {
                chain.doFilter(request, response);
                return;
            }
        }

        // 内部调用接口放行
        String header = request.getHeader(UserConstant.FEIGN_REQUEST_HEADER_NAME);
        if (StringUtils.equals(header, UserConstant.FEIGN_REQUEST_HEADER_VALUE)) {
            LOGGER.debug("内部服务调用接口 requestURL：{}", uri);
            chain.doFilter(request, response);
            return;
        }

        if (StringUtils.isBlank(platformType)) {
            Utils.print(JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100714)));
            return;
        }

        String token = request.getHeader(UserConstant.TOKEN_REQUEST_HEADER_NAME);
        LOGGER.debug("请求接口：{}，携带token：{}，进入TokenAuthenticationFilter", uri, token);
        if (StringUtils.isBlank(token)) {
            Utils.print(JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100406.getCode(), Utils.translation("未登录,请先登录!"))));
            return;
        }

        Object obj = redisUtils.get(token);
        if (null == obj) {
            Utils.print(JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100406.getCode(), Utils.translation("未登录或登录超时,请重新登录!"))));
            return;
        }

        if (obj instanceof UserEntity) {
            UserEntity userEntity = (UserEntity) obj;
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userEntity, null, userEntity.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            redisUtils.set(token, obj, UserConstant.REDIS_USER_TOKEN_KEY_TIMEOUT);
            redisUtils.expireTimeSet(userEntity.getUsername() + token, UserConstant.REDIS_USER_TOKEN_KEY_TIMEOUT);
        }

        chain.doFilter(request, response);
    }

}
