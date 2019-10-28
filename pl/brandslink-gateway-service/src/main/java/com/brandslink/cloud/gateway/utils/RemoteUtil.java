package com.brandslink.cloud.gateway.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;


/**
 *远程调用处理类
 */
public class RemoteUtil {

    private final static Logger log = LoggerFactory.getLogger(RemoteUtil.class);

    private static ThreadLocal threadLocal = new ThreadLocal();

    public static void invoke(Object o) {
        try {
            Map mm  = (Map) o;
            Object oo = mm.get("data");
            if (oo instanceof Map) {
                mm.put("map", oo);
            } else if (oo instanceof List) {
                mm.put("list", oo);
            } else {
                mm.put("object", oo);
            }
            threadLocal.remove();
            threadLocal.set(mm);
        } catch (Exception e) {
            log.error("远程调用异常,{}", e);
        }
    }


    public static List getList() {
        List list = null;
        try {
            list = (List) ((Map)threadLocal.get()).get("list");
            if (list != null && !list.isEmpty())
                ((Map)threadLocal.get()).remove("list", list);
        } catch (Exception e) {
            log.error("远程调用异常,{}", e);
        }
        return list;
    }


    public static Map getMap() {
        Map map = null;
        try {
            map = (Map) ((Map)threadLocal.get()).get("map");
            if (map != null && !map.isEmpty())
                ((Map)threadLocal.get()).remove("map", map);
        } catch (Exception e) {
            log.error("远程调用异常,{}", e);
        }
        return map;
    }


    public static Object getObject() {
        Object object = null;
        try {
            object = ((Map)threadLocal.get()).get("object");
            if (object != null)
                ((Map)threadLocal.get()).remove("object", object);
        } catch (Exception e) {
            log.error("远程调用异常,{}", e);
        }
        return object;
    }


    public static String getErrorCode() {
        String errorCode = null;
        try {
            errorCode = (String) ((Map)threadLocal.get()).get("errorCode");
        } catch (Exception e) {
            log.error("远程调用异常,{}", e);
        }
        return errorCode;
    }


    public static String getMsg() {
        String msg = null;
        try {
            msg = (String) ((Map)threadLocal.get()).get("msg");
        } catch (Exception e) {
            log.error("远程调用异常,{}", e);
        }
        return msg;
    }


    public static boolean getSuccess() {
        boolean success = false;
        try {
            success = (boolean) ((Map)threadLocal.get()).get("success");
        } catch (Exception e) {
            log.error("远程调用异常,{}", e);
        }
        return success;
    }


    /**
     * map转object
     * @param map
     * @param beanClass
     * @return
     * @throws Exception
     */
    public static <T> T mapToObject(Map<String, Object> map, Class<T> beanClass) throws Exception {
        if (map == null) return null;
        Object obj = beanClass.newInstance();
        org.apache.commons.beanutils.BeanUtils.populate(obj, map);
        return (T) obj;
    }

}
