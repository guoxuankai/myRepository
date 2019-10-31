package com.brandslink.cloud.common.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.utils.MD5;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;


/**
 * 百度翻译服务
 */
@Service
public class TranslationService {

    @Value("${translation.baidu.url}")
    private String url;

    @Value("${translation.baidu.appid}")
    private String appid;

    @Value("${translation.baidu.secretkey}")
    private String secretkey;


    @Cacheable(value="transcation", keyGenerator = "keyGenerator")
    public String transcation(String query, String from, String type) throws Exception {
        String appKey = appid;
        String salt = String.valueOf(System.currentTimeMillis());
        //String from = "zh"; //默认中文
        String sign = MD5.md5Password(appKey + query + salt + secretkey);
        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", type);
        params.put("sign", sign);
        params.put("salt", salt);
        params.put("appid", appKey);
        String requestForHttpRet = requestForHttp(url, params);
        return requestForHttpRet;
    }


    public static String requestForHttp(String url,Map<String,String> requestParams) throws Exception{
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        /**HttpPost*/
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        Iterator<Map.Entry<String, String>> it = requestParams.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> en = it.next();
            String key = en.getKey();
            String value = en.getValue();
            if (value != null) {
                params.add(new BasicNameValuePair(key, value));
            }
        }
        httpPost.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
        /**HttpResponse*/
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        try{
            HttpEntity httpEntity = httpResponse.getEntity();
            result = EntityUtils.toString(httpEntity, "utf-8");
            EntityUtils.consume(httpEntity);
        }finally{
            try{
                if(httpResponse!=null){
                    httpResponse.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        com.alibaba.fastjson.JSONObject object = JSONArray.parseObject(result);
        JSONArray object2 = (JSONArray) object.get("trans_result");
        JSONObject jsonObject = (JSONObject) object2.get(0);
        return jsonObject.getString("dst");
    }

}
