package com.brandslink.cloud.user.utils;

import com.brandslink.cloud.common.entity.UserEntity;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 生成随机数工具类
 *
 * @ClassName CustomerUtil
 * @Author tianye
 * @Date 2019/7/17 10:03
 * @Version 1.0
 */
public class CustomerUtil {

    /**
     * 生成12位客户id
     *
     * @return
     */
    public static String getCustomerIdByUUId() {
        //最大支持1-9个集群机器部署
        int machineId = 1;
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        //有可能是负数
        if (hashCodeV < 0) {
            hashCodeV = -hashCodeV;
        }
        return machineId + String.format("%011d", hashCodeV);
    }

    /**
     * 公共构建UserEntity
     *
     * @param account
     * @param password
     * @param enabled
     * @param authorities
     * @return
     */
    public static UserEntity commonCreateUserEntity(String account, String password, Integer enabled, List<SimpleGrantedAuthority> authorities) {
        UserEntity result = new UserEntity();
        result.setUsername(account);
        result.setPassword(password);
        result.setEnabled(enabled);
        result.setAuthorities(authorities);
        return result;
    }

    /**
     * 生成客户主账号 -> prefix+6位年月日+4位随机数
     *
     * @return
     */
    public static String createAccount(String prefix) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(prefix)) {
            builder.append(prefix);
        }
        String date = new SimpleDateFormat("yyMMdd").format(new Date());
        String randomNumeric = RandomStringUtils.randomNumeric(4);
        return builder.append(date).append(randomNumeric).toString();
    }

    /**
     * 生成6位随机数字字符串
     *
     * @return
     */
    public static String createRandomNumeric() {
        return RandomStringUtils.randomNumeric(6);
    }

}
