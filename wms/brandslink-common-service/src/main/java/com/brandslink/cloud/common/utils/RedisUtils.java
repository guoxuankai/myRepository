package com.brandslink.cloud.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {

    @SuppressWarnings("rawtypes")
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 读取缓存(不建议使用)
     *
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public Object get(final String key) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        if (result == null) return null;
        return objectDeserialization((String) result);
    }

    /**
     * 写入缓存(不建议使用)
     *
     * @param key
     * @param value
     * @return
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, objectSerializable(value));
            result = true;
        } catch (Exception e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
        }
        return result;
    }

    /**
     * 写入缓存(不建议使用)
     *
     * @param key
     * @param value
     * @return
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, objectSerializable(value));
            result = redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
        }
        return result;
    }

    /**
     * 对象序列化为字符串
     **/
    @Deprecated
    private String objectSerializable(Object obj) {
        String serStr = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            serStr = byteArrayOutputStream.toString("ISO-8859-1");
            serStr = java.net.URLEncoder.encode(serStr, "UTF-8");

            objectOutputStream.close();
            byteArrayOutputStream.close();
        } catch (UnsupportedEncodingException e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
        } catch (IOException e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
        }
        return serStr;
    }

    /**
     * 字符串反序列化为对象
     */
    @Deprecated
    private Object objectDeserialization(String serStr) {
        Object newObj = null;
        try {
            String redStr = java.net.URLDecoder.decode(serStr, "UTF-8");
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(redStr.getBytes("ISO-8859-1"));
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            newObj = objectInputStream.readObject();
            objectInputStream.close();
            byteArrayInputStream.close();
        } catch (Exception e) {
            return null;
        }
        return newObj;
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public void stringSet(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "缓存写入异常");
        }
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public void stringSet(String key, Object value, Long expireTime) {
        try {
            redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "时效缓存写入异常");
        }
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public String stringGet(String key) {
        try {
            Object result = redisTemplate.opsForValue().get(key);
            if (result == null || StringUtils.isEmpty(result.toString())) {
                return "";
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "读取缓存异常");
        }
    }

    /**
     * 读取缓存
     *
     * @param key
     * @param tClass
     * @return
     */
    public <T> T stringGet(String key, Class<T> tClass) {
        try {
            Object result = redisTemplate.opsForValue().get(key);
            if (result == null || StringUtils.isEmpty(result.toString())) {
                return null;
            }
            return new Gson().fromJson(result.toString(), tClass);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "读取缓存异常");
        }
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public void hashSet(String key, String field, Object value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "缓存写入异常");
        }
    }

    /**
     * 读取缓存
     *
     * @param key
     * @param field
     * @return
     */
    public String hashGet(String key, String field) {
        try {
            Object result = redisTemplate.opsForHash().get(key, field);
            if (result == null || StringUtils.isEmpty(result.toString())) {
                return "";
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "读取缓存异常");
        }
    }

    /**
     * 读取缓存
     *
     * @param key
     * @param field
     * @param tClass
     * @return
     */
    public <T> T hastGet(String key, String field, Class<T> tClass) {
        try {
            Object result = redisTemplate.opsForHash().get(key, field);
            if (result == null || StringUtils.isEmpty(result.toString())) {
                return null;
            }
            return new Gson().fromJson(result.toString(), tClass);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "读取缓存异常");
        }
    }


    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除key
     *
     * @param pattern
     */
    @SuppressWarnings("unchecked")
    public void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0)
            redisTemplate.delete(keys);
    }

    /**
     * 删除对应的value
     *
     * @param key
     */
    @SuppressWarnings("unchecked")
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 根据 key 获取自增
     *
     * @param key
     * @return
     */
    public Long incrt(String key) {
        return redisTemplate.opsForValue().increment(key, 1);
    }

    /**
     * @param key
     * @return
     */
    public synchronized void setIncrt(String key, Long value) {
        redisTemplate.opsForValue().set(key, value);
    }


    /**
     * 从右边（末尾）取出一个元素
     *
     * @param key key值
     * @return 元素值 JSONObject 对象)
     */
    public JSONObject getListJsonMessageFromRight(String key) {
        Object result;
        result = redisTemplate.opsForList().rightPop(key);
        if (result == null) return null;
        String result1 = (String) result;
        if (StringUtils.isBlank(result1))
            return null;
        return JSONObject.parseObject(result1);
    }


    /**
     * 从左边（表头）插入一个元素
     *
     * @param key   key值
     * @param value 元素(JSONObject 对象)
     * @return 是否插入成功 注意，如果value值为空，不会执行插入操作，当时会显示操作成功
     */
    public boolean setListJsonMessageFromLift(String key, JSONObject value) {
        boolean result;
        try {
            if (value != null) {
                redisTemplate.opsForList().leftPush(key, JSONObject.toJSONString(value));
            }
            result = true;
        } catch (Exception e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
        }
        return result;
    }

    /**
     * 清空某个列表中的值
     *
     * @param key key值
     */
    public void removeList(String key) {
        try {
            redisTemplate.opsForList().trim(key, 1, 0);
        } catch (Exception e) {
            // logger.error("清空列表 "+key+" 中的值异常",e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
        }
    }

    /**
     * 更新过期时间
     *
     * @param key
     * @param expireTime
     * @return
     */
    public void expireTimeSet(String key, Long expireTime) {
        try {
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "更新过期时间异常");
        }
    }

}
