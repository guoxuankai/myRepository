package com.brandslink.cloud.common.utils;

import com.alibaba.fastjson.JSON;
import com.brandslink.cloud.common.constant.CustomerConstant;
import com.brandslink.cloud.common.entity.CustomerInfoEntity;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * 客户信息工具类
 *
 * @ClassName AttestationSignUtil
 * @Author tianye
 * @Date 2019/7/17 11:24
 * @Version 1.0
 */
@Component
public class AttestationSignUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttestationSignUtil.class);

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 获取客户信息
     *
     * @return
     */
    public CustomerInfoEntity verifySign() {
        // 请求对象
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        verifyException(requestAttributes, ResponseCodeEnum.RETURN_CODE_100700);
        HttpServletRequest request = requestAttributes.getRequest();
        // 验证是否存在
        String customerAppId = request.getParameter(CustomerConstant.REQUEST_PARAMETER_ACCESS_TOKEN);
        verifyException(customerAppId, ResponseCodeEnum.RETURN_CODE_100703);
        customerAppId = customerAppId.substring(32);
        LOGGER.info("customerAppId is :{}", customerAppId);
        // 客户秘钥
        CustomerInfoEntity entity = (CustomerInfoEntity) redisUtils.get(CustomerConstant.REDIS_PREFIX + customerAppId);
        verifyException(entity, ResponseCodeEnum.RETURN_CODE_100703);
        entity.setCustomerSecretKey(null);
        return entity;
    }

//    /**
//     * 根据request验证签名
//     *
//     * @return
//     */
//    public CustomerInfoEntity verifySign() {
//        // 请求对象
//        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        verifyException(requestAttributes, ResponseCodeEnum.RETURN_CODE_100700);
//        HttpServletRequest request = requestAttributes.getRequest();
//        JSONObject jsonObject = null;
//        String requestMethod = request.getMethod();
//        // 客户id
//        String customerAppId;
//        // 请求签名
//        String sign;
//        if (CustomerConstant.REQUEST_METHOD_POST.equals(requestMethod)) {
//            // 获取请求体
//            BufferedReader reader = null;
//            String str;
//            StringBuilder wholeStr = new StringBuilder();
//            try {
//                reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
//                while ((str = reader.readLine()) != null) {
//                    wholeStr.append(str);
//                }
//            } catch (IOException e) {
//                LOGGER.error("get InputStream has IOException：{}", e.getMessage());
//                e.printStackTrace();
//            } finally {
//                if (null != reader) {
//                    try {
//                        reader.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            jsonObject = JSON.parseObject(wholeStr.toString());
//            customerAppId = jsonObject.getString(CustomerConstant.REQUEST_PARAMETER_CUSTOMER_ID);
//            sign = jsonObject.getString(CustomerConstant.REQUEST_PARAMETER_SIGN);
//        } else if (CustomerConstant.REQUEST_METHOD_GET.equals(requestMethod)) {
//            customerAppId = request.getParameter(CustomerConstant.REQUEST_PARAMETER_CUSTOMER_ID);
//            sign = request.getParameter(CustomerConstant.REQUEST_PARAMETER_SIGN);
//        } else {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100705.getCode(), "暂不支持 " + requestMethod + " 这种请求方式");
//        }
//        // 验证是否存在
//        verifyException(customerAppId, ResponseCodeEnum.RETURN_CODE_100701);
//        verifyException(sign, ResponseCodeEnum.RETURN_CODE_100702);
//        // 客户秘钥
//        CustomerInfoEntity entity = (CustomerInfoEntity) redisUtils.get(CustomerConstant.REDIS_PREFIX + customerAppId);
//        verifyException(entity, ResponseCodeEnum.RETURN_CODE_100703);
//        if (!StringUtils.equals("1", entity.getStatus())) {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100706);
//        }
//        String secretKey = entity.getCustomerSecretKey();
//        verifyException(secretKey, ResponseCodeEnum.RETURN_CODE_100703);
//        Map<String, Object> params = new HashMap<>();
//        if (CustomerConstant.REQUEST_METHOD_POST.equals(requestMethod)) {
//            Set<String> keySet = jsonObject.keySet();
//            LOGGER.debug("获取url参数名称列表：{}", JSON.toJSONString(keySet));
//            for (String next : keySet) {
//                Object value = jsonObject.get(next);
//                if (!CustomerConstant.REQUEST_PARAMETER_SIGN.equals(next) && Objects.nonNull(value)) {
//                    params.put(next, value);
//                }
//            }
//        } else {
//            // url参数
//            Enumeration<String> enu = request.getParameterNames();
//            LOGGER.debug("获取url参数名称列表：{}", JSON.toJSONString(request.getParameterNames()));
//            while (enu.hasMoreElements()) {
//                String paramName = enu.nextElement().trim();
//                if (!CustomerConstant.REQUEST_PARAMETER_SIGN.equals(paramName)) {
//                    String value = request.getParameter(paramName);
//                    // 参数value解码
//                    String decode = null;
//                    try {
//                        decode = URLDecoder.decode(value, CustomerConstant.INPUT_CHARSET);
//                    } catch (UnsupportedEncodingException e) {
//                        LOGGER.error("参数value解码异常：{}", e.getMessage());
//                        e.printStackTrace();
//                    }
//                    if (StringUtils.isNotBlank(decode)) {
//                        params.put(paramName, decode);
//                    }
//                }
//            }
//        }
//        // 排序
//        Map<String, Object> sortParams = new TreeMap<>(params);
//        if (!sign.equals(DigestUtils.md5Hex(JSON.toJSONString(sortParams) + secretKey))) {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100704);
//        }
//        entity.setCustomerSecretKey(null);
//        return entity;
//    }

    /**
     * 生成签名
     *
     * @param params
     * @param secretKey
     */
    private String createSign(Map<String, Object> params, String secretKey) {
        verifyException(secretKey, ResponseCodeEnum.RETURN_CODE_100707);
        Map<String, Object> myParams = new HashMap<>();
        params.forEach((k, v) -> {
            if (Objects.nonNull(v) && !CustomerConstant.REQUEST_PARAMETER_SIGN.equals(k)) {
                myParams.put(k, v);
            }
        });
        Map<String, Object> sortParams = new TreeMap<>(myParams);
        return DigestUtils.md5Hex(JSON.toJSONString(sortParams) + secretKey);
    }

    /**
     * 根据请求体生成签名
     *
     * @param params
     * @return
     */
    public String createSign(Map<String, Object> params) {
        String customerAppId = (String) params.get(CustomerConstant.REQUEST_PARAMETER_CUSTOMER_ID);
        LOGGER.info("customerAppId：{}", customerAppId);
        verifyException(customerAppId, ResponseCodeEnum.RETURN_CODE_100703);
        CustomerInfoEntity entity = (CustomerInfoEntity) redisUtils.get(CustomerConstant.REDIS_PREFIX + customerAppId);
        verifyException(entity, ResponseCodeEnum.RETURN_CODE_100703);
        if (!StringUtils.equals("1", entity.getStatus())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100706);
        }
        String secretKey = entity.getCustomerSecretKey();
        return createSign(params, secretKey);
    }

    /**
     * 公共校验
     *
     * @param source
     * @param codeEnum
     */
    private void verifyException(Object source, ResponseCodeEnum codeEnum) {
        if (source instanceof String) {
            if (StringUtils.isBlank((String) source)) {
                throw new GlobalException(codeEnum);
            }
        } else {
            if (null == source) {
                throw new GlobalException(codeEnum);
            }
        }
    }
}
