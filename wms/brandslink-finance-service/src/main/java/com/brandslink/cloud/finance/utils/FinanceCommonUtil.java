package com.brandslink.cloud.finance.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author yangzefei
 * @Classname FinanceCommonUtil
 * @Description 财务系统公共类
 * @Date 2019/9/3 10:37
 */
@Component
public class FinanceCommonUtil {
    private static DateFormat dateFormat=new SimpleDateFormat("yyyyMMdd");
    private static final String  redisDir = "wms:finance:billNo:";
    private static final Long stepValue = 1L;
    private static final Integer length=4;
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 获取账单号
     * @param prefix 账单类型前缀
     * @return
     */
    public String getOrderNo(String prefix){
        return getOrderNo(prefix,this.length);
    }

    public String getOrderNo(String prefix,int length){
        String day= dateFormat.format(new Date());
        Long serial = redisTemplate.opsForValue().increment(redisDir + prefix+day, stepValue);
        if(serial.equals(stepValue)){
            //删除昨天的
            redisTemplate.delete(redisDir+prefix+(Integer.parseInt(day)-1));
        }
        return  prefix+day+String.format("%0"+length+"d", serial);
    }

    /**
     * 获取操作人
     * @return
     */
    public String getOperate(){
        return "";
    }

    /**
     * 获取登录用户的客户编码
     * @return
     */
    public String getCustomerCode(){return "";}
}
