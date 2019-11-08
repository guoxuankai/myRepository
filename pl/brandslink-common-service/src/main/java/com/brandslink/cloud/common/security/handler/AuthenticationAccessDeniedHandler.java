package com.brandslink.cloud.common.security.handler;

import com.brandslink.cloud.common.entity.Result;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.utils.Utils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 权限不足Handler
 *
 * @ClassName AuthenticationAccessDeniedHandler
 * @Author tianye
 * @Date 2019/5/27 18:13
 * @Version 1.0
 */
@Component
public class AuthenticationAccessDeniedHandler implements AccessDeniedHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse resp, AccessDeniedException e) throws IOException {
        String requestURI = httpServletRequest.getRequestURI();
        LOGGER.info("访问：{} 该url权限不足!", requestURI);
        resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        resp.setContentType("application/json;charset=utf-8");
        Utils.print(JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100401.getCode(), Utils.translation("权限不足，请联系管理员!"))));
    }
}
