package com.brandslink.cloud.common.config;

import com.brandslink.cloud.common.constant.UserConstant;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

/**
 * feign拦截器，所有使用feign调用的接口，都会添加自定义请求头
 *
 * @ClassName FeignRequestInterceptor
 * @Author tianye
 * @Date 2019/6/22 15:08
 * @Version 1.0
 */
@Configuration
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(UserConstant.FEIGN_REQUEST_HEADER_NAME, UserConstant.FEIGN_REQUEST_HEADER_VALUE);
    }
}
