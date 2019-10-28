package com.rondaful.cloud.seller.common.aliexpress;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.*;

public class SignMd5 {
    private final static Logger logger = LoggerFactory.getLogger(SignMd5.class);
    private final static String key="rodfullaliexpresschenwwwwwdacg";
    public static void main(String[] ars){
        Map<String,Object> map = Maps.newHashMap();
        //map.put("categoryId","0");
//36047AE6C64C6DD820246E4554911029
//63EF5E4C9E56CF84AECBE57FD25B0969
        map.put("sessionKey","50002001538bfvwdirdWtccjcxBstxcJiwLvHTu9D1lQwExwx919d33c46PqKUk9Gjg");
        System.out.println(SignMd5.createSign(map));
        //18D0585CBD8E358D371230A3535609D4
        //18D0585CBD8E358D371230A3535609D4

    }
    /**
     * 签名算法sign
     * @param map
     * @return
     */
    public static String createSign(Map<String,Object> map){
        SortedMap<String,Object> parameters = new TreeMap<String,Object>(map);
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();//所有参与传参的参数按照accsii排序（升序）
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            if(null != v && v!=null && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }

        sb.append("key=" + key);
        try {
            logger.info(sb.toString());

            String strdecode = URLEncoder.encode(sb.toString(),"UTF-8");
            logger.info(strdecode);
            String sign = md5Password(strdecode).toUpperCase();
            logger.info(sign);
            return sign;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public static String md5Password(String password) {

        try {
            //得到一个信息摘要器
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(password.getBytes("UTF-8"));
            StringBuffer buffer = new StringBuffer();
            // 把没一个byte 做一个与运算 0xff;
            for (byte b : result) {
                // 与运算
                int number = b & 0xff;// 加盐
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    buffer.append("0");
                }
                buffer.append(str);
            }
            // 标准的md5加密后的结果
            return buffer.toString();
        } catch (Exception e) {
            //e.printStackTrace();
            return "";
        }
    }
}
