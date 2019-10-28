package com.amazonservices.mws.uploadData.constants;

public interface AmazonConstants {
	public final String REDIS_AMAZON_PREFIX = "amazon_upload_xml_"; 
	
	public final String BASE_CLASS_PATH = "com.rondaful.cloud.seller.generated.";
	
	// 1：已完成，2：失败，3：需要继续上报，4：下一个接口 ///
	/** 已完成 */
	public final Integer COMPLETE_STATUS_COMPLETE = 1;
	/** 失败 */
	public final Integer COMPLETE_STATUS_FAILED = 2;
	/** 处理中 */
	public final Integer COMPLETE_STATUS_PRESSING = 3;
	/** 需要继续上报 */
	//public final Integer COMPLETE_STATUS_AGAIN =3;
	/** 下一个接口 */
	//public final Integer COMPLETE_STATUS_NEXT = 4;
	
	
	public final String RESPORT_RESULT_UPLOADING = "uploading";
	public final String RESPORT_RESULT_SUCCESS = "success";
	
	public final String RESPORT_RESULT_ERROR = "500";
}
