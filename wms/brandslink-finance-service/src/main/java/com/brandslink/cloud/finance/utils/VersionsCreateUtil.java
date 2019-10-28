package com.brandslink.cloud.finance.utils;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 版本号生成工具类
 *
 * @Author: zhangjinhua
 * @Date: 2019/6/20 16:09
 */
@Slf4j
public class VersionsCreateUtil {

    private static final String redisDir = "wms:finance:versions:";
    private static final Integer delta = 1;
    //配置版本号生成主键:
    // 1.1若“配置类型=货型分类”，则版本号格式：HX-V + 流水1.0依次递增，如：HX-V1.0
    //  1.2若“配置类型=存储费库龄”，则版本号格式：CC-V + 流水1.0依次递增，如：CC-V1.0
    //  1.3若“配置类型=卸货费”，则版本号格式：XH-V + 流水1.0依次递增，如：XH-V1.0
    //  1.3若“配置类型=打包费”，则版本号格式：DB-V + 流水1.0依次递增，如：DB-V1.0
    static final String HX = "HX-V", CC = "CC-V", XH = "XH-V", DB = "DB-V";

    /**
     * @param redisTemplate
     * @param key           类型键
     * @return 返回生成的订单号
     */
    public static String orderProduce(RedisTemplate redisTemplate, String key) {
        String serial = redisTemplate.opsForValue().increment(redisDir + key, delta) + "";
        Double finalbersion = versionsProcessor(Integer.decode(serial));
        log.info("此次生成的订单号为:" + key + finalbersion);
        return key + finalbersion;
    }

    private static Double versionsProcessor(Integer version) {
        return (version - 1) / 10.0 + 1.0;
    }

    public static String configVersionsProcessor(RedisTemplate redisTemplate, Byte type) {
        String key;
        switch (type) {
            case 1:
                key = HX;
                break;
            case 2:
                key = CC;
                break;
            case 3:
                key = XH;
                break;
            default:
                key = DB;
                break;
        }
        return orderProduce(redisTemplate, key);
    }



    /**
     * 给指定参数补0
     *
     * @param str       需要补0的参数
     * @param strLength 小于几位数开始补0
     * @return
     */
    public static String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer sb = new StringBuffer();
                //左补0
                sb.append("0").append(str);
                //sb.append(str).append("0");//右补0
                str = sb.toString();
                strLen = str.length();
            }
        }

        return str;
    }
    /**
     *
     * @param redisTemplate
     * @param key 类型键
     * @return 返回生成的订单号
     */
    public static String orderProduceDate(RedisTemplate redisTemplate, String key) {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String format = sdf.format(d);
        String serial = redisTemplate.opsForValue().increment(redisDir + key + format, 1) + "";

//        serial = serial.substring(redisDir.length());

        String serialStr = addZeroForNum(serial, 3);
        log.info("此次生成的订单号为:" + (key +format+ serialStr));
        return key +format+ serialStr;
    }
}
