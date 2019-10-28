package com.rondaful.cloud.order.filter;

import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.HttpUtil;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.entity.user.ThirdAppDTO;
import com.rondaful.cloud.order.remote.RemoteUserService;
import com.rondaful.cloud.order.service.ISystemOrderCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * URL映射过滤器
 *
 * @author zhangjinglei
 * @date 2019年05月06日上午11:31:52
 */
//@ServletComponentScan
//@Component
//@WebFilter(urlPatterns = {"/order/distributionOrder"}, filterName = "urlMappingFilter")
public class MapsFilter implements Filter {
    private String gcFilterURL = MapsUtils.getProperty("gcFilterURL");
    private String gcAppToken = MapsUtils.getProperty("gcAppToken");
    private String gcAppKey = MapsUtils.getProperty("gcAppKey");
    private String xsFilterURL = MapsUtils.getProperty("xsFilterURL");
    private String xsAppToken = MapsUtils.getProperty("xsAppToken");
    private String xsAppKey = MapsUtils.getProperty("xsAppKey");
    private String version = MapsUtils.getProperty("version");

    private static final Logger _log = LoggerFactory.getLogger(MapsFilter.class);

    @Autowired
    private ISystemOrderCommonService systemOrderCommonService;

    @Override
    public void destroy() {
        _log.debug("销毁过滤器");
    }

    public void doFilterDuplicate(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        List<String> filterList = this.getFilterList();
        if (!filterList.contains(request.getRequestURI())) {
            chain.doFilter(req, response);
            return;
        } else {
//            String serverName = request.getServerName();
//            _log.error("____________请求的__________serverName:{}___________", serverName);

            StringBuffer requestURL = request.getRequestURL();
            StringBuffer url = request.getRequestURL();
            String tempContextUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/").toString();
            _log.error("___________第一种方式，获取请求域名为_________{}_________", tempContextUrl);

            StringBuffer url0 = request.getRequestURL();
            String tempContextUrl0 = url0.delete(url0.length() - request.getRequestURI().length(), url0.length()).append(request.getServletContext().getContextPath()).append("/").toString();
            _log.error("___________第二种方式，获取请求域名为_________{}_________", tempContextUrl0);

            Map<String, String[]> params = request.getParameterMap();
            String[] appTokens = params.get("AppToken");
            String[] appKeys = params.get("AppKey");
            String[] versions = params.get("version");
            //谷仓API订阅请求参数
            String[] signs = params.get("Sign");
            String[] messageTypes = params.get("MessageType");
            String[] messages = params.get("Message");
            String[] messageIds = params.get("MessageId");
            String[] sendTimes = params.get("SendTime");
            if (appTokens[0].equals(gcAppToken)) {
                request.setAttribute("distribution", Constants.DistributionType.DistributionType_GC);
                if (messageTypes == null || messageTypes.length == 0) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "公共请求参数错误。。。");
                }
                if (messageTypes[0].equals("SendOrder")) {
                    req.getRequestDispatcher("/order/goodCang/order/acceptOrderList").forward(request, response);
                } else if (messageTypes[0].equals("AfterSalesReturnOrder")) {
                    req.getRequestDispatcher("123").forward(request, response);
                } else if (messageTypes[0].equals("BackOrder")) {
                    req.getRequestDispatcher("/order/goodCang/order/acceptAbnormalOrderList").forward(request, response);
                } else if (messageTypes[0].equals("SendReceiving")) {
                    req.getRequestDispatcher("123").forward(request, response);
                } else if (messageTypes[0].equals("SendTakeStock")) {
                    req.getRequestDispatcher("123").forward(request, response);
                } else if (messageTypes[0].equals("StockChange")) {
//                    String serverName0 = request.getServerName();
//                    int serverPort = request.getServerPort();
//                    String ipAddress = RequestClientUtil.getIpAddress(request);
//                    _log.error("____________请求的URL为__________serverName:{}___________serverPort:{}__________", serverName0, serverPort);
//                    _log.error("____________请求的IP为__________{}___________", ipAddress);
                    HttpUtil.post("http://172.19.43.62:8105/supplier/provider/receiveGranaryInventory", new HashMap<String, String>() {{
                        this.put("Message", messages[0]);
                    }});
                    return;
                } else {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "API订阅请求错误。。。");
                }
            } else if (appTokens[0].equals(xsAppToken)) {
                if (!appKeys[0].equals(xsAppKey) || !versions[0].equals(version)) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "公共请求参数错误。。。");
                }
                request.setAttribute("distribution", Constants.DistributionType.DistributionType_QT);
                chain.doFilter(req, response);
            }
        }
    }

    private List<String> getFilterList() {
        List<String> list = new ArrayList<>();
        List<String> list0 = Arrays.asList(xsFilterURL.split("#"));
        List<String> list1 = Arrays.asList(gcFilterURL.split("#"));
        list.addAll(list0);
        list.addAll(list1);
        return list;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        List<String> urlList = Arrays.asList(MapsUtils.getProperty("xsFilterURL").split("#"));
        String requestURI = request.getRequestURI();
        if (urlList.contains(requestURI)) {
            _log.info("分销商请求的URL为{}", requestURI);
            String appToken = request.getParameter("AppToken");
            String appKey = request.getParameter("AppKey");
            String version = request.getParameter("version");
            request.setAttribute("distribution", Constants.DistributionType.DistributionType_QT);
            if (appToken == null || appKey == null || version == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "公共请求参数错误。。。");
            }

            ThirdAppDTO thirdAppDTO = systemOrderCommonService.getByAppKey(appKey);
            if (null == thirdAppDTO) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "公共请求参数错误。。。");
            }
            _log.info("用户返回的第三方认证信息是: {}", thirdAppDTO);

            request.setAttribute("thirdAppDTO", thirdAppDTO);
        }
        filterChain.doFilter(req, response);
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        _log.debug("初始化过滤器");
    }
}