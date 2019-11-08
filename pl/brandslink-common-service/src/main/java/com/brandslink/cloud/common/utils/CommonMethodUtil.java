package com.brandslink.cloud.common.utils;

import com.brandslink.cloud.common.constant.UserConstant;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.stereotype.Component;

/**
 * 公共方法
 *
 * @ClassName CommonMethodUtil
 * @Author tianye
 * @Date 2019/6/19 11:16
 * @Version 1.0
 */
@Component
public class CommonMethodUtil {

    @Value("${system.ignore.login.url}")
    private String ignore;

    @Value("${swagger.enable}")
    public boolean isDev;

    /**
     * 公共配置springsecurity忽略请求
     *
     * @param web
     */
    public void CommonSettingWebSecurity(WebSecurity web) throws Exception {
        // 忽略预请求url
        web.ignoring().antMatchers("/favicon**", "/status", "/actuator/service-registry");
        if (StringUtils.isNotBlank(ignore)) {
            ignore = ignore.replaceAll(" ", "");
            String[] split = ignore.split(",");
            web.ignoring().antMatchers(split);
        }
        if (isDev) {
            web.ignoring().antMatchers("/swagger**", "/rest/api/doc");
        }
    }


    /**
     * 获取对应平台标识
     *
     * @param platformType
     * @return
     */
    public String getPlatformType(String platformType) {
        String result = StringUtils.EMPTY;
        if (StringUtils.isBlank(platformType)) {
            return result;
        }
        switch (platformType) {
            case "0":
                result = UserConstant.PLATFORM_TYPE_FLAG_WMS;
                break;
            case "1":
                result = UserConstant.PLATFORM_TYPE_FLAG_OMS;
                break;
            case "2":
                result = UserConstant.PLATFORM_TYPE_FLAG_OCMS;
                break;
            default:
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100713);
        }
        return result;
    }
}
