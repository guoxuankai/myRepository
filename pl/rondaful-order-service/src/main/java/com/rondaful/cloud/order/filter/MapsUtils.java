package com.rondaful.cloud.order.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Url映射配置加载辅助工具类
 *
 * @author zhangjinglei
 * @date 2019年05月06日上午11:31:52
 */
@Slf4j
public class MapsUtils {

    private static Map<String, String> propertiesMap = null;

    private static void processProperties(Properties properties) {
        propertiesMap = new HashMap<String, String>();
        for (Object key : properties.keySet()) {
            String keyStr = key.toString();
            try {
                propertiesMap.put(keyStr, new String(properties.getProperty(keyStr).getBytes("ISO-8859-1"), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
//                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void loadAllProperties(String propertiesFileName) {
        try {
            Properties properties = PropertiesLoaderUtils.loadAllProperties(propertiesFileName);
            processProperties(properties);
        } catch (IOException e) {
//            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public static String getProperty(String name) {
        return propertiesMap.get(name) == null ? "" : propertiesMap.get(name);
    }

    public static Map<String, String> getAllProperty() {
        return propertiesMap;
    }
}
