package com.brandslink.cloud.gateway.entity;


import java.util.Map;


/**
 * 远程调用结果处理
 */
public class ResultMap {

    private Map map;

    public ResultMap(Object obj) {
        this.map = (Map) obj;
    }

    /**
     * 获取指定对象
     * @param c
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T getObject(Class<T> c) {
        try {
            return (T) mapToObject((Map)map.get("data"), c);
        } catch (Exception e) {
            return null;
        }
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


    public Object get(String key) {
        return this.map.get(key);
    }


    public Object set(String key, Object object) {
        return this.map.put(key, object);
    }

}
