package com.rondaful.cloud.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.constant.ConstantAli;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.AliSMSUtils;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.model.dto.SmsDTO;
import com.rondaful.cloud.user.service.IMessageService;
import com.rondaful.cloud.user.utils.SendEmailUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: xqq
 * @Date: 2019/5/11
 * @Description:
 */
@Service
public class MessageServiceImpl implements IMessageService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private AliSMSUtils aliSMSUtils;

    private String CACHE_TIME="time";
    private String CACHE_CODE="code";

    /**
     * 发送短信验证码
     *
     * @param phone
     * @param sendType
     */
    @Override
    public void SendSms(String phone, Integer sendType) {
        String code= RandomStringUtils.randomNumeric(6);
        this.SendSms(phone,sendType,new SmsDTO(code));
    }

    /**
     * 发送带参数的短信
     *
     * @param phone
     * @param sendType
     * @param params
     */
    @Override
    public void SendSms(String phone, Integer sendType, SmsDTO params) {
        if (StringUtils.isEmpty(phone)){
            return;
        }
        String key=SMS_PHONE_CACHE+sendType+phone;
        HashOperations operations=redisTemplate.opsForHash();
        Map<String,Object> redisMap=operations.entries(key);
        if (redisMap!=null&&redisMap.size()>0&&((Instant.now().toEpochMilli()-Long.valueOf((String) redisMap.get(CACHE_TIME)))<360000L)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.operating.frequency");
        }
        this.send(phone,sendType,params);
        redisMap=new HashMap<>(2);
        redisMap.put(CACHE_TIME,String.valueOf(Instant.now().toEpochMilli()));
        if (StringUtils.isNotEmpty(params.getCode())){
            redisMap.put(CACHE_CODE,params.getCode());
        }
        operations.putAll(key,redisMap);
        this.redisTemplate.expire(key,2L, TimeUnit.MINUTES);
    }

    /**
     *
     * @param phone
     * @param sendType
     * @param dto
     */
    @Async
    public void send(String phone, Integer sendType, SmsDTO dto) {
        if (StringUtils.isEmpty(phone)){
            return;
        }
        JSONObject params=new JSONObject();
        switch (sendType){
            case 1:
                params.put("code",dto.getCode());
                this.aliSMSUtils.sendSms(phone, params, ConstantAli.SmsType.SELLER_ACCOUNT_APPLY);
                break;
            case 2:
            case 3:
                params.put("code",dto.getCode());
                this.aliSMSUtils.sendSms(phone, params, ConstantAli.SmsType.FORGET_PASSWORD_CODE);
                break;
            case 4:
            case 5:
                params.put("code",dto.getCode());
                this.aliSMSUtils.sendSms(phone, params, ConstantAli.SmsType.UPDATE_PHONE);
                break;
            case 6:
                params.put("name",dto.getName());
                params.put("passWord",dto.getPassWord());
                this.aliSMSUtils.sendSms(phone, params, ConstantAli.SmsType.CMS_ADD_SUPPLIER_ACOUNT);
                break;
            case 7:
                this.aliSMSUtils.sendSms(phone, params, ConstantAli.SmsType.SUPPLIER_AUDIT_SUCCED);
                break;
            case 8:
                this.aliSMSUtils.sendSms(phone, params, ConstantAli.SmsType.SUPPLIER_AUDIT_FILE);
                break;
            case 9:
                params.put("passWord",dto.getPassWord());
                this.aliSMSUtils.sendSms(phone, params, ConstantAli.SmsType.SUPPLIER_RESTE_PASSWORD_CODE);
                break;
            case 10:
                this.aliSMSUtils.sendSms(phone, params, ConstantAli.SmsType.SELLER_AUDIT_SUCCED);
                break;
            case 11:
                this.aliSMSUtils.sendSms(phone, params, ConstantAli.SmsType.SELLER_AUDIT_FILE);
                break;
            case 12:
                params.put("name",dto.getName());
                params.put("passWord",dto.getPassWord());
                this.aliSMSUtils.sendSms(phone, params, ConstantAli.SmsType.CMS_ADD_SELLER_ACOUNT);
                break;
            default:
                return;
        }
    }

    /**
     * 邮箱发送验证码
     *
     * @param email
     */
    @Override
    public void sendEmail(String email,Integer sendType) {
        if (StringUtils.isEmpty(email)){
            return;
        }
        String key=EMAIL_PASSWORD_CACHE+sendType+email;
        ValueOperations operations=redisTemplate.opsForValue();
        String code= RandomStringUtils.randomNumeric(6);
        String accounts= Utils.i18n("suppler.email.forget.password.account");
        String text=Utils.i18nParams("suppler.email.forget.password.text",new Object[]{code});
        if ( SELLER_FORGET_PASSWORD_TYPE.equals(sendType)) {
            accounts = Utils.i18n("seller.email.forget.password.account");
            text =  Utils.i18nParams("seller.email.forget.password.text",new Object[]{code});
        } else if ( SUPPLIER_FORGET_PASSWORD_TYPE.equals(sendType) ) {
            accounts =Utils.i18n("suppler.email.forget.password.account");
            text = Utils.i18nParams("suppler.email.forget.password.text",new Object[]{code});
        }else if(SELLER_REGISTERED_TYPE.equals(sendType)){
            accounts =Utils.i18n("seller.email.register.account");
            text = Utils.i18nParams("seller.email.register.text",new Object[]{code});
        }else if(SELLER_BINDING_EMAIL_CODE.equals(sendType)){
            accounts =Utils.i18n("seller.email.register.account");
            text = Utils.i18nParams("seller.email.bind",new Object[]{code});
        }else if(SUPPLIER_BINDING_EMAIL_CODE.equals(sendType)){
            accounts =Utils.i18n("suppler.email.register.account");
            text = Utils.i18nParams("supplier.email.bind",new Object[]{code});
        }
        SendEmailUtil pool = new SendEmailUtil(email, accounts, text);
        pool.send();
        operations.set(key,code,6L,TimeUnit.MINUTES);
    }

    /**
     * 校验验证码
     *
     * @param phone
     * @param sendType
     * @param code
     * @return
     */
    @Override
    public Boolean checkCode(String phone, Integer sendType, String code) {
        if (StringUtils.isEmpty(code)){
            return false;
        }
        String key=SMS_PHONE_CACHE+sendType+phone;
        HashOperations operations=redisTemplate.opsForHash();
        Map<String,Object> redisMap=operations.entries(key);
        if (redisMap==null||redisMap.size()==0){
            return false;
        }
        if (code.equals(redisMap.get(CACHE_CODE))){
            return true;
        }
        return false;
    }

    /**
     * 校验邮件验证码
     *
     * @param email
     * @param sendType
     * @param code
     * @return
     */
    @Override
    public Boolean checkEmailCode(String email, Integer sendType, String code) {
        if (StringUtils.isEmpty(code)){
            return false;
        }
        String key=EMAIL_PASSWORD_CACHE+sendType+email;
        ValueOperations operations=redisTemplate.opsForValue();
        if (code.equals(operations.get(key))){
            return true;
        }
        return false;
    }


}
