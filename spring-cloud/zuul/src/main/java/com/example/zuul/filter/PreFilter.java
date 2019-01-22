package com.example.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class PreFilter extends ZuulFilter {


    /**
     * 前置过滤器。
     * 但是在 zuul 中定义了四种不同生命周期的过滤器类型：
     * 1、pre：请求在路由之前被调用,如:身份验证；
     * 2、route：请求在路由时被调用；
     * 3、post：路由到微服务之后执行(在route和error过滤器之后被调用)；
     * 4、error：处理请求发生错误时被调用；
     *
     * @return
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 过滤的优先级，数字越大，优先级越低。
     *
     * @return
     */
    @Override
    public int filterOrder() {
        return 1;
    }

    /**
     * 是否执行该过滤器。
     * true：说明需要过滤；
     * false：说明不要过滤；
     *
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 过滤器的具体逻辑。
     *
     * @return
     */
    @Override
    public Object run() {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        HttpServletResponse response = RequestContext.getCurrentContext().getResponse();
        String requestURI = request.getRequestURI();
        if (!(requestURI.equals("/web/login.html") || requestURI.equals("/web/register.html") || requestURI.equals("/user/user/login")||requestURI.equals("/user/user/login")||requestURI.equals("/user/user/register")||requestURI.equals("/office/dept/getAll")|| requestURI.endsWith(".css") || requestURI.endsWith(".js"))) {

            Object userId = request.getSession().getAttribute("userId");
            if (userId == null) {
                try {
                    response.sendRedirect("/web/login.html");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }


        return null;
    }
}

