package com.rondaful.cloud.commodity.utils;


import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializer;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 通用工具类
 * */
public class Utils {

    /**
     * 响应输出
     * */
    public static void print(Object object) throws IOException {
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = requestAttributes.getRequest();
            HttpServletResponse response = requestAttributes.getResponse();
            response.addHeader("Cache-Control", "no-cache");
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.print(object);
            out.flush();
            out.close();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }


    /**
     * 判断用户是否移动端访问
     * android : 所有android设备
     * mac os : iphone ipad
     * windows phone:Nokia等windows系统的手机
     */
    public static boolean  isMobileDevice() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String requestHeader = request.getHeader("user-agent");
        String[] deviceArray = {"android", "mac os", "windows phone"};
        if (requestHeader == null)
            return false;
        requestHeader = requestHeader.toLowerCase();
        for (String mobile : deviceArray) {
            if (requestHeader.indexOf(mobile) > 0) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取客户端请求IP地址
     */
    public static String getIpAddress()  {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


    /**
     * 随机流水号UUID
     * */
    public static String getSerialNumber() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    /**
     * 判断是否为javabean
     * @param type
     * @return
     */
    public static final boolean isJavaBean(Type type){
        if(null == type ) throw new NullPointerException();
        return ParserConfig.global.getDeserializer(type) instanceof JavaBeanDeserializer;
    }

    /**
     * 获取32位随机码
     * */
    public static String uuid(){
        return UUID.randomUUID().toString().toUpperCase();
    }
    
    /**
     * @Description:生成新的文件名
     * @param originalFilename 原文件名
     * @return
     * @author:范津
     */
    public static String createFileName(String originalFilename) {
		String str = UUID.randomUUID().toString().replaceAll("-", "");
		return new StringBuilder().append(str.substring(0, 15))
				.append(originalFilename.substring(originalFilename.lastIndexOf("."),originalFilename.length())).toString();
	}
    
    /**
     * @Description:获取两个list不同的值
     * @param list1
     * @param list2
     * @return
     * @author:范津
     */
    public static List<String> getDiffStringList(List<String> list1, List<String> list2) {
		Map<String, Integer> map = new HashMap<String, Integer>(list1.size() + list2.size());
		List<String> diff = new ArrayList<String>();
		List<String> maxList = list1;
		List<String> minList = list2;
		if (list2.size() > list1.size()) {
			maxList = list2;
			minList = list1;
		}

		for (String string : maxList) {
			map.put(string, 1);
		}

		for (String string : minList) {
			Integer cc = map.get(string);
			if (cc != null) {
				map.put(string, ++cc);
				continue;
			}
			map.put(string, 1);
		}

		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			if (entry.getValue() == 1) {
				diff.add(entry.getKey());
			}
		}
		return diff;
	}
    
}
