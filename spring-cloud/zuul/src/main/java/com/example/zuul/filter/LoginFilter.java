package com.example.zuul.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import io.micrometer.core.instrument.util.IOUtils;
import org.springframework.cloud.netflix.ribbon.RibbonHttpResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;


public class LoginFilter extends ZuulFilter {


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
        return "post";
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
        String requestURI = request.getRequestURI();
        //获得登陆后的响应数据，将其存入session当中(这里暂时用session代替redis)
        if (requestURI.equals("/user/user/login")) {

            try {
                Object zuulResponse = RequestContext.getCurrentContext().get("zuulResponse");
                if (zuulResponse != null) {
                    RibbonHttpResponse resp = (RibbonHttpResponse) zuulResponse;
                    String body = IOUtils.toString(resp.getBody());

                    Map maps = (Map) JSON.parse(body);

                    request.getSession().setAttribute("userId", maps.get("id"));

                    resp.close();
                    RequestContext.getCurrentContext().setResponseBody(body);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        System.out.println(requestURI);

        return null;
    }
}

