package com.rondaful.cloud.seller.constants;

/**
 * 提交到amazon接口类型
 * @author ouxiangfeng
 *
 */
public class AmazonPostMethod {
	
	/**
	 * 商品上报方法类型   _POST_PRODUCT_DATA_
	 */
	public static final String POST_PRODUCT_DATA =  "_POST_PRODUCT_DATA_";
	
	/**
	 * 关系上报方法类型   _POST_PRODUCT_RELATIONSHIP_DATA_
	 */
	public static final String POST_RELATIONSHIP_DATA = "_POST_PRODUCT_RELATIONSHIP_DATA_";
	
	/**
	 * 库存上报方法类型   _POST_INVENTORY_AVAILABILITY_DATA_
	 */
	public static final String POST_INVENTORY_DATA = "_POST_INVENTORY_AVAILABILITY_DATA_";
	
	/**
	 * 价格上报方法类型   _POST_PRODUCT_PRICING_DATA_
	 */
	public static final String POST_PRICING_DATA = "_POST_PRODUCT_PRICING_DATA_";
	
	/**
	 * 图片上报方法类型   _POST_PRODUCT_IMAGE_DATA_
	 */
	public static final String POST_IMAGE_DATA = "_POST_PRODUCT_IMAGE_DATA_";
	
	
	/** 上报状态 _SUBMITTED_ */
	public static final String UPLOAD_STATUS_SUBMITTED_ = "_SUBMITTED_";
	/** 上报状态  _DONE_*/
	public static final String UPLOAD_DONE_SUBMITTED_ = "_DONE_";
}
