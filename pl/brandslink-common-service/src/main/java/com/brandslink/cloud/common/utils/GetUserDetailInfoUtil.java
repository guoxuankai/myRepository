package com.brandslink.cloud.common.utils;

import com.brandslink.cloud.common.constant.UserConstant;
import com.brandslink.cloud.common.entity.CustomerDetails;
import com.brandslink.cloud.common.entity.UserDetailInfo;
import com.brandslink.cloud.common.entity.UserEntity;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取当前登录信息
 *
 * @ClassName GetUserDetails
 * @Author tianye
 * @Date 2019/6/17 18:33
 * @Version 1.0
 */
@Component
public class GetUserDetailInfoUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetUserDetailInfoUtil.class);

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 获取登录用户信息  wms系统
     *
     * @return
     */
    public UserDetailInfo getUserDetailInfo() {
        HttpServletRequest request = getRequest();
        String platformType = getPlatformType();
        if (!StringUtils.equals(platformType, "0")) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100718);
        }
        return (UserDetailInfo) commonGetUserDetails(request);
    }

    /**
     * 获取登录客户详细信息  oms系统
     *
     * @return
     */
    public CustomerDetails getCustomerDetails() {
        HttpServletRequest request = getRequest();
        String platformType = getPlatformType();
        if (!StringUtils.equals(platformType, "1")) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100717);
        }
        return (CustomerDetails) commonGetUserDetails(request);
    }

    /**
     * 获取当前登录系统 0：wms 1：oms 2：ocms
     *
     * @return
     */
    public String getPlatformType() {
        HttpServletRequest request = getRequest();
        return request.getHeader(UserConstant.PLATFORM_TYPE_REQUEST_HEADER_NAME);
    }

    /**
     * 获取请求对象
     *
     * @return
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getRequest();
    }

    /**
     * 获取登录用户信息
     *
     * @param request
     * @return
     */
    private Object commonGetUserDetails(HttpServletRequest request) {
        String token = request.getHeader(UserConstant.TOKEN_REQUEST_HEADER_NAME);
        LOGGER.info("根据token获取当前登录用户信息:token={}", token);
        if (StringUtils.isBlank(token)) {
            token = request.getParameter(UserConstant.TOKEN_REQUEST_HEADER_NAME);
            if (StringUtils.isBlank(token)) {
                LOGGER.error("根据token获取当前登录用户信息，没有携带token，请先登录");
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406.getCode(), "未登录,请先登录!");
            }
        }
        UserEntity entity = (UserEntity) redisUtils.get(token);
        if (null == entity) {
            LOGGER.error("根据token获取当前登录用户信息，token失效，请重新登录");
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406.getCode(), "未登录或登录超时,请重新登录!");
        }
        Object obj = redisUtils.get(entity.getUsername() + token);
        if (null == obj) {
            LOGGER.error("根据token获取当前登录用户信息，token失效，请重新登录");
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406.getCode(), "未登录或登录超时,请重新登录!");
        }
        return obj;
    }

}
