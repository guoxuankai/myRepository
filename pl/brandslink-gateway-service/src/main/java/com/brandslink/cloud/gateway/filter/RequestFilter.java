package com.brandslink.cloud.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.brandslink.cloud.gateway.enums.ResponseCodeEnum;
import com.brandslink.cloud.gateway.exception.GlobalException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;


/**
 * 请求过滤器
 * */
@Component
public class RequestFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        HttpSession session = request.getSession();
        String refererUrl = request.getHeader("Referer");
        if (StringUtils.isNotBlank(refererUrl) && refererUrl.endsWith("swagger-ui.html")) {
            try {
                ctx.addZuulRequestHeader("token", (String) session.getAttribute("token"));
            } catch (Exception e) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
            }
        }
        return null;
    }

}
