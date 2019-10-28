package com.rondaful.cloud.user.service;

import com.rondaful.cloud.user.model.dto.SmsDTO;

/**
 * @Author: xqq
 * @Date: 2019/5/11
 * @Description:
 */
public interface IMessageService {

    public static String SMS_PHONE_CACHE="user:sms.by.phone.v0";
    public static String EMAIL_PASSWORD_CACHE="user:password.by.email.v0";


    /** 验证码卖方注册类型 **/
    public static final Integer SELLER_REGISTERED_TYPE = 1;

    /** 验证码卖家忘记密码 **/
    public static final Integer SELLER_FORGET_PASSWORD_TYPE = 2;

    /** 验证码供应商忘记密码 **/
    public static final Integer SUPPLIER_FORGET_PASSWORD_TYPE = 3;

    /** 卖家绑定手机验证码通知 **/
    public static final Integer SELLER_BINDING_PHONE_CODE = 4;

    /** 供应商绑定手机验证码短信 **/
    public static final Integer SUPPLIER_BINDING_PHONE_CODE = 5;
    /**
     * 供应商新增账号
     */
    public static final Integer CMS_ADD_SUPPLIER_ACOUNT = 6;
    /**
     * 供应商激活账号审核成功
     */
    public static final Integer SUPPLIER_AUDIT_SUCCED = 7;
    /**
     * 供应商激活账号审核失败
     */
    public static final Integer SUPPLIER_AUDIT_FILE = 8;
    /**
     * 供应商账号重置密码
     */
    public static final Integer SUPPLIER_RESTE_PASSWORD_CODE = 9;
    /**
     * 卖家账号激活成功
     */
    public static final Integer SELLER_AUDIT_SUCCED = 10;
    /**
     * 卖家账号审核失败
     */
    public static final Integer SELLER_AUDIT_FILE = 11;
    /**
     * 卖家账号手动新增
     */
    public static final Integer CMS_ADD_SELLER_ACOUNT = 12;

    /**
     * 卖家邮箱绑定
     */
    public static final Integer SELLER_BINDING_EMAIL_CODE = 13;
    /**
     * 供应商邮箱绑定
     */
    public static final Integer SUPPLIER_BINDING_EMAIL_CODE = 14;

    /**
     * 发送短信验证码
     * @param phone
     * @param sendType
     */
    void SendSms(String phone,Integer sendType);

    /**
     * 发送带参数的短信
     * @param phone
     * @param sendType
     * @param params
     */
    void SendSms(String phone, Integer sendType, SmsDTO params);

    /**
     * 邮箱发送验证码
     * @param email
     */
    void sendEmail(String email,Integer sendType);

    /**
     * 校验验证码
     * @param phone
     * @param sendType
     * @param code
     * @return
     */
    Boolean checkCode(String phone,Integer sendType,String code);

    /**
     * 校验邮件验证码
     * @param email
     * @param sendType
     * @param code
     * @return
     */
    Boolean checkEmailCode(String email,Integer sendType,String code);
}
