package com.brandslink.cloud.common.utils;

import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.brandslink.cloud.common.constant.UserConstant.IS_EMAIL;
import static com.brandslink.cloud.common.constant.UserConstant.IS_MOBILE_NUM;

/**
 * 手机号工具类
 *
 * @ClassName PhoneUtils
 * @Author tianye
 * @Date 2019/7/23 9:45
 * @Version 1.0
 */
public class PhoneUtils {

    /**
     * 校验手机号是否合法
     *
     * @param contactWay
     */
    public static void judgeContactWay(String contactWay) {
        if (StringUtils.isNotBlank(contactWay)) {
            Pattern p = Pattern.compile(IS_MOBILE_NUM);
            Matcher m = p.matcher(contactWay);
            if (!m.matches()) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "联系方式不合法，请输入正确的手机号!");
            }
        }
    }

    /**
     * 校验邮箱是否合法
     *
     * @param email
     */
    public static void judgeEmail(String email) {
        if (StringUtils.isNotBlank(email)) {
            Pattern p = Pattern.compile(IS_EMAIL);
            Matcher m = p.matcher(email);
            if (!m.matches()) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "邮箱不合法，请输入正确的邮箱!");
            }
        }
    }
}
