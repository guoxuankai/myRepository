package com.brandslink.cloud.common.utils;

import net.sf.json.JSONObject;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HttpUtil {

    private static CloseableHttpClient httpclient;

    //编码格式
    private static String ENCODING = "UTF-8";

    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        //设置连接池的最大连接数
        cm.setMaxTotal(200);
        //设置每个路由上的默认连接个数
        cm.setDefaultMaxPerRoute(100);
        httpclient = HttpClients.custom().setConnectionManager(cm).build();
    }

    /**
     * Http_Get请求方法
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static String get(String url) throws Exception {
        HttpGet get = new HttpGet(url);
        CloseableHttpResponse response = httpclient.execute(get);
        HttpEntity entity = response.getEntity();
        String body = EntityUtils.toString(entity);
        response.close();
        return body;
    }

    /**
     * Http_Get请求方法，带请求头，带请求参数
     *
     * @param url
     * @param param     请求参数Map
     * @param headerMap 请求头参数Map
     * @return
     * @throws Exception
     */
    public static String get(String url, Map<String, String> param, Map<String, String> headerMap) throws Exception {
        URIBuilder builder = new URIBuilder(url);
        if (param != null) {
            for (String key : param.keySet()) {
                builder.addParameter(key, param.get(key));
            }
        }
        URI uri = builder.build();
        HttpGet httpGet = new HttpGet(uri);
        if (headerMap != null && !headerMap.isEmpty()) {
            for (String key : headerMap.keySet()) {
                httpGet.addHeader(key, headerMap.get(key));
            }
        }
        CloseableHttpResponse response = httpclient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        String body = EntityUtils.toString(entity);
        response.close();
        return body;
    }

    /**
     * Http_Post请求方法，带请求参数
     *
     * @param url
     * @param paramsMap
     * @return
     */
    public static String post(String url, Map<String, String> paramsMap) {
        String responseText = "";
        CloseableHttpResponse response = null;
        try {
            HttpPost method = new HttpPost(url);
            if (paramsMap != null) {
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> param : paramsMap.entrySet()) {
                    NameValuePair pair = new BasicNameValuePair(param.getKey(), param.getValue());
                    paramList.add(pair);
                }
                method.setEntity(new UrlEncodedFormEntity(paramList, ENCODING));
            }
            response = httpclient.execute(method);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseText = EntityUtils.toString(entity, ENCODING);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return responseText;
    }

    /**
     * Http_Post请求方法，带请求头，带请求参数
     *
     * @param url
     * @param headerMap
     * @param param
     * @return
     */
    public static String post(String url, Map<String, String> headerMap, Map<String, String> param) {
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            if (headerMap != null) {
                for (String key : headerMap.keySet()) {
                    httpPost.setHeader(key, headerMap.get(key));
                }
            }
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "utf-8");
                httpPost.setEntity(entity);
            }
            response = httpclient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    /**
     * Http_Post请求方法，带XML参数
     *
     * @param url
     * @param xml
     * @return
     * @throws Exception
     */
    public static String postXML(String url, String xml) throws Exception {
        HttpPost post = new HttpPost(url);
        StringEntity se = new StringEntity(xml, ENCODING);
        post.setEntity(se);
        CloseableHttpResponse response = httpclient.execute(post);
        HttpEntity entity = response.getEntity();
        String body = EntityUtils.toString(entity, ENCODING);
        response.close();
        return body;
    }

    /**
     * 发送json请求，带请求头，带请求参数
     *
     * @param url
     * @param map
     * @return
     * @throws Exception
     */
    public static String postJson(String url, Map map) throws Exception {
        HttpPost post = new HttpPost(url);
        JSONObject jsonParam = new JSONObject();
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            jsonParam.put(key, map.get(key));
        }
        //解决中文乱码问题
        StringEntity entity = new StringEntity(jsonParam.toString(), ENCODING);
        entity.setContentEncoding(ENCODING);
        entity.setContentType("application/json");
        post.setEntity(entity);
        CloseableHttpResponse response = httpclient.execute(post);
        HttpEntity res = response.getEntity();
        String body = EntityUtils.toString(res, ENCODING);
        response.close();
        return body;
    }

    /**
     * Http_Post请求方法，带请求头，带请求参数
     *
     * @param url
     * @param jsonStr
     * @param headerMap
     * @return
     * @throws Exception
     */
    public static String postJson(String url, String jsonStr, Map<String, String> headerMap) throws Exception {
        HttpPost post = new HttpPost(url);
        if (headerMap != null) {
            for (String key : headerMap.keySet()) {
                post.setHeader(key, headerMap.get(key));
            }
        }
        //解决中文乱码问题
        StringEntity entity = new StringEntity(jsonStr, ENCODING);
        entity.setContentEncoding(ENCODING);
        entity.setContentType("application/json");
        post.setEntity(entity);
        CloseableHttpResponse response = httpclient.execute(post);
        HttpEntity res = response.getEntity();
        String body = EntityUtils.toString(res, ENCODING);
        response.close();
        return body;
    }

    /**
     * Http_Post请求方法，上传文件
     *
     * @param url
     * @param file
     * @param filePath
     * @param fileName
     * @return
     * @throws Exception
     */
    public static String postUpload(String url, CommonsMultipartFile file, String filePath, String fileName) throws Exception {
        HttpPost post = new HttpPost(url);
        StringBody filepath = new StringBody(filePath, ContentType.create("text/plain", ENCODING));
        StringBody filename = new StringBody(fileName, ContentType.create("text/plain", ENCODING));

        CommonsMultipartFile cf = (CommonsMultipartFile) file;
        DiskFileItem fi = (DiskFileItem) cf.getFileItem();
        // 把文件转换成流对象FileBody
        FileBody file1 = new FileBody(fi.getStoreLocation());
        //相当于<input type="file" name="file"/>
        HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("file", file1).addPart("filePath", filepath).addPart("fileName", filename).build();

        post.setEntity(reqEntity);
        CloseableHttpResponse response = httpclient.execute(post);
        HttpEntity entity = response.getEntity();
        String body = EntityUtils.toString(entity, ENCODING);
        response.close();
        return body;
    }
}
