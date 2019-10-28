package com.rondaful.cloud.seller.entity.amazon;

public class AmazonPublishListStatus {
	
	/**
	 * 草稿
	 */
	public static final Integer AMAZON_PUBLISH_STATUS_DRAFT = 1;
	/**
	 * 刊登中
	 */
	public static final Integer AMAZON_PUBLISH_STATUS_PUBLISHING = 2;
	/**
	 * 在线
	 */
	public static final Integer AMAZON_PUBLISH_STATUS_ONLINE = 3;
	/**
	 * 刊登失败
	 */
	public static final Integer AMAZON_PUBLISH_STATUS_FAIL = 4;
	/**
	 * 已下线
	 */
	public static final Integer AMAZON_PUBLISH_STATUS_OUTLINE = 5;
	
	/**
	 * 等待刊登
	 */
	public static final Integer AMAZON_PUBLISH_STATUS_AWAIT = 6;
	
	/**
	 * 在线状态图片重新更新和批量更新状态
	 */
	public static final Integer AMAZON_PUBLISH_STATUS_REST_PUSH = 7;


	/**
	 * 刊登子项状态：疑似删除
	 */
	public static final Integer AMAZON_PUBLISH_SUB_STATUS_DELETE = 100;



	
	public static String getStatus(Integer status)
	{
		switch (status) {
		case 1:
			return "草稿";
		case 2:
			return "刊登中";
		case 3:
			return "在线";
		case 4:
			return "刊登失败";
		case 5:
			return "已下线";
		default:
			return "";
		}
	}
}
