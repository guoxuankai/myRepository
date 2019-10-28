package com.rondaful.cloud.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.rondaful.cloud.gateway.enums.ResponseCodeEnum;
import com.rondaful.cloud.gateway.exception.GlobalException;
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
        String remoteAddr=request.getRemoteAddr();
        ctx.getZuulRequestHeaders().put("HTTP_X_FORWARDED_FOR",remoteAddr);
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
