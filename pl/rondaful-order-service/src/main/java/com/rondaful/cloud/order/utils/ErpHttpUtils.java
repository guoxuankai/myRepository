package com.rondaful.cloud.order.utils;

import com.rondaful.cloud.common.enums.ERPOrderEnum;
import com.rondaful.cloud.common.utils.HttpUtil;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Blade
 * @date 2019-08-09 16:54:40
 **/
public class ErpHttpUtils {

    //编码格式
    private static String ENCODING = "UTF-8";

    private static Logger LOGGER = LoggerFactory.getLogger(ErpHttpUtils.class);

    /**
     * 以表单形势提交post请求   该方法只能用来请求erp接口
     *
     * @param url     请求url
     * @param method  需要添加在url后的参数
     * @param postMap 以post(表单)方式传输的参数
     * @return 返回结果
     * @throws ParseException 解析异常
     * @throws IOException    io异常
     */
    public static String postSendByFormDataForOrder(String url, String method, Map<String, String> postMap) throws Exception {

        List<NameValuePair> getNvps = new ArrayList<>();
        Map<String, String> uriParams = new HashMap<String, String>();
        uriParams.put("url", method);
        uriParams.put("version", ERPOrderEnum.SystemParam.VERSION.getParam());
        uriParams.put("app", ERPOrderEnum.SystemParam.APP.getParam());
        uriParams.put("mark", ERPOrderEnum.SystemParam.MARK.getParam());
        uriParams.put("sign", ERPOrderEnum.SystemParam.SIGN.getParam());
        for (Map.Entry<String, String> entry : uriParams.entrySet()) {
            getNvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        String paramsStr = "";
        try {
            paramsStr = EntityUtils.toString(new UrlEncodedFormEntity(getNvps, ENCODING));
        } catch (ParseException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        url = url + "/post?" + paramsStr;
        LOGGER.info("访问地址: {}", url);
        return HttpUtil.postJson(url, postMap);
    }
}
