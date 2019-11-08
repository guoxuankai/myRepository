package com.brandslink.cloud.common.constant;

import org.apache.commons.lang3.StringUtils;

public class ConstantAli {

	public final static String[] FILE_TYPE = { "product", "file", "system", "app", "college", "wms" };

	/**
	 * 环境
	 */
	public enum EnvironmentType {
		/** 生产环境 */
		ENV_PRD("生产环境", "prd"),

		/** 开发环境 */
		ENV_DEV("开发环境", "dev"),

		/** 测试环境 */
		ENV_TEST("测试环境", "test");

		private String description;
		private String type;

		private EnvironmentType(String description, String type) {
			this.description = description;
			this.type = type;
		}

		public String getDescription() {
			return description;
		}

		public String getType() {
			return type;
		}
	}

	/**
	 * 文件服务器存储目录
	 */
	public enum BucketType {
		/** 生产环境文件目录 */
		BUCKET("生产环境文件目录", "rondaful"),

		/** 开发环境文件目录 */
		BUCKET_FILE_DEV("开发环境文件目录", "rondaful-file-dev"),

		/** 测试环境文件目录 */
		BUCKET_FILE_TEST("测试环境文件目录", "rondaful-file-test");

		private String description;
		private String type;

		private BucketType(String description, String type) {
			this.description = description;
			this.type = type;
		}

		public String getDescription() {
			return description;
		}

		public String getType() {
			return type;
		}
	}

	/**
	 * 文件子目录目录,根据自己模块进行建立<br>
	 * 不够自己添加,尽量分详细
	 */
	public enum FolderType {
		/** 存储于产品相关的 */
		PRODUCT_FOLDER("商品", "product/"),

		/** 存储文件 */
		FILE_FOLDER("文件", "file/"),

		/** 系统相关的图片 */
		SYSTEM_IMG_FOLDER("系统相关", "system/"),

		/** APP相关 */
		SYSTEM_APP("系统相关", "app/"),

		/** 品连学院文件上传 */
		SYSTEM_COLLEGE("品连学院文件上传", "college/"),

		/** wms文件上传 */
		WMS("wms文件上传", "wms/"),

		/** wms面单上传 */
		WMS_OUTBOUND("wms面单上传", "wms/outbound/"),

		/** temp */
		TEMP("临时文件", "temp/");

		private String description;
		private String type;

		private FolderType(String description, String type) {
			this.description = description;
			this.type = type;
		}

		public String getDescription() {
			return description;
		}

		public String getType() {
			return type;
		}
	}

	public static FolderType getFolder(String folder) {
		switch (folder) {
		case "product":
			return FolderType.PRODUCT_FOLDER;
		case "file":
			return FolderType.FILE_FOLDER;
		case "system":
			return FolderType.SYSTEM_IMG_FOLDER;
		case "app":
			return FolderType.SYSTEM_APP;
		case "college":
			return FolderType.SYSTEM_COLLEGE;
		case "wms":
			return FolderType.WMS;
		default:
			return FolderType.FILE_FOLDER;
		}
	}

	public static BucketType getEnv(String env) {
		if (StringUtils.isBlank(env))
			return BucketType.BUCKET_FILE_DEV;
		switch (env) {
		case "test":
			return BucketType.BUCKET_FILE_TEST;
		case "prod":
			return BucketType.BUCKET;
		case "dev":
			return BucketType.BUCKET_FILE_DEV;
		default:
			return BucketType.BUCKET_FILE_DEV;
		}
	}

	public static String getEnvStr(String env) {
		if (StringUtils.isBlank(env))
			return "dev";
		switch (env) {
		case "test":
			return "test";
		case "prod":
			return "prod";
		case "dev":
			return "dev";
		default:
			return "dev";
		}
	}

	/**
	 * 平台模块
	 */
	public enum PlatformType {
		/** 订单 */
		ORDER("订单", "order"),

		/** 卖家 */
		SELLER("卖家", "seller"),

		/** 供应商 */
		SUPPLIER("供应商", "supplier"),

		/** 后台 */
		ADMIN("后台", "admin"),

		/** 商品 */
		COMMODITY("商品", "commodity"),

		/** 财务 */
		FINANCE("财务", "finance");

		private String description;
		private String type;

		private PlatformType(String description, String type) {
			this.description = description;
			this.type = type;
		}

		public String getDescription() {
			return description;
		}

		public String getType() {
			return type;
		}
	}

	/**
	 * 阿里短信模板
	 */
	public enum SmsType {

		/**
		 * 更换手机号
		 */
		UPDATE_PHONE("更换手机号码", "SMS_166370328"),
		/**
		 * 后台新增卖家账号
		 */
		CMS_ADD_SELLER_ACOUNT("后台新增卖家账号", "SMS_166375381"),
		/**
		 * 卖家账号审核失败
		 */
		SELLER_AUDIT_FILE("卖家账号审核失败", "SMS_166375379"),
		/**
		 * 卖家账号审核成功
		 */
		SELLER_AUDIT_SUCCED("卖家账号审核成功", "SMS_166375378"),
		/**
		 * 卖家账号注册验证码短信内容
		 */
		SELLER_ACCOUNT_APPLY("卖家账号注册验证码短信内容", "SMS_166370320"),

		/** 忘记密码验证码短信内容 */
		FORGET_PASSWORD_CODE("忘记密码验证码短信内容", "SMS_166375373"),
		/**
		 * 供应商重置密码成功
		 */
		SUPPLIER_RESTE_PASSWORD_CODE("供应商忘记密码验证码短信内容", "SMS_166375371"),

		/**
		 * 后台新增供应商账号
		 */
		CMS_ADD_SUPPLIER_ACOUNT("后台新增供应商账号", "SMS_166370305"),
		/**
		 * 供应商账号审核失败
		 */
		SUPPLIER_AUDIT_FILE("供应商账号审核失败", "SMS_166370316"),
		/**
		 * 供应商账号审核成功
		 */
		SUPPLIER_AUDIT_SUCCED("供应商账号审核成功", "SMS_166375364"),

		/** 卖家注册验证码短信内容 */
		SELLER_REGIN_CODE("卖家注册验证码短信内容", "SMS_161570185"),

		/** 卖家忘记密码验证码短信内容 */
		SELLER_FORGET_PASSWORD_CODE("卖家忘记密码验证码短信内容", "SMS_161570194"),

		/** 供应商绑定手机验证码短信内容 */
		SUPPLIER_BINDING_PHONE_CODE("供应商绑定手机验证码短信内容", "SMS_161570201"),

		/** 卖家绑定手机验证码通知内容 */
		SELLER_BINDING_PHONE_CODE("卖家绑定手机验证码通知内容", "SMS_161570225"),

		/** 卖家创建成功手机通知短信 */
		SELLER_CREATE_OK_CODE("卖家创建成功手机通知短信", "SMS_161570244"),

		/** 供应商创建成功手机通知短信 */
		SUPPLIER_CREATE_OK_CODE("供应商创建成功手机通知短信", "SMS_161570269"),

		/*** 供应商激活申请成功短信通知内容 */
		SUPPLIER_ACTIVATE_OK_CODE("供应商激活申请成功短信通知内容", "SMS_161575117"),

		/** 忘记密码验证码 */
		FORGET_PASSWORD("忘记密码验证码", "SMS_156276474"),

		/** 供应商短信注册验证码 */
		SUPPLIER_REGISTERED("供应商短信注册验证码", "SMS_156281615"),

		/** 卖家短信注册验证码 */
		SELLER_REGISTERED("卖家短信注册验证码", "SMS_156281616"),

		/** 银行卡绑定验证码 */
		BANK_CARD("银行卡绑定验证码", "SMS_158945270"),

		/** 品连学院验证码 **/
		COLLEGE_CODE("品连学院验证码", "SMS_165417821"),

		/** 客户端OMS用户更改手机号验证码 **/
		OMS_CHANGE_MOBILE_CODE("客户端OMS用户更改手机号验证码", "SMS_173405096"),

		/** 客户端OMS手机号登录验证码 **/
		OMS_MOBILE_LOGIN_CODE("客户端OMS手机号登录验证码", "SMS_173349990"),

		/** 客户端OMS客户注册账号验证码 **/
		OMS_CUSTOMER_SIGN_IN_CODE("客户端OMS客户注册账号验证码", "SMS_173405091");

		private String description;
		private String type;

		private SmsType(String description, String type) {
			this.description = description;
			this.type = type;
		}

		public String getDescription() {
			return description;
		}

		public String getType() {
			return type;
		}
	}

}
