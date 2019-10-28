package com.rondaful.cloud.user.constants;

public class UserConstants {


	public static final String INITRESULTOK = "1";

	/**ridis登录token前缀**/
	@Deprecated
	public static final String REDIS_USER_KEY_fix = "_key";

	/** 响应头 RequestHeader **/
	public static final String REQUEST_HEADER_NAME = "token";
	
	/** 供应商响应头 RequestHeader **/
	//public static final String SUPPLIER_REQUEST_HEADER_NAME = "supplierAuthentication";
	
	/** token time out **/
	public static final Long REDIS_USER_TOKEN_TIMEOUT = 7200L;//一天

	/** 供应链公司代表枚举supplier platform 0 **/
	public static final Integer SUPPLYCHAINCOMPANY = 9;
	
	
	/** 供应商supplier platform 0 **/
	public static final Integer SUPPLIERPLATFORM = 0;
	
	/** 卖家seller platform 1 **/
	public static final Integer SELLERPLATFORM = 1;
	
	/** 品联后台管理  代表代码 2 **/
	public static final Integer MANAGEPLATFORMTYPE = 2;
	
	//验证码
	/** 验证码卖方注册类型 **/
	public static final Integer SELLER_REGISTERED_TYPE = 1;
	
	/** 验证码卖家忘记密码 **/
	public static final Integer SELLER_FORGET_PASSWORD_TYPE = 2;
	
	/** 验证码供应商忘记密码 **/
	public static final Integer SUPPLIER_FORGET_PASSWORD_TYPE = 3;

	/** 验证码供应商忘记密码 **/
	public static final Integer BINDINGPHONE = 4;
	
	/** 验证码忘记密码 **/
	public static final Integer FORGET_PASSWORD_TYPR = 4;
}
