package com.rondaful.cloud.seller.constants;

public interface AmazonConstants {
	public final String REDIS_AMAZON_PREFIX = "amazon_upload_xml_"; 
	
	public final String BASE_CLASS_PATH = "com.rondaful.cloud.seller.generated.";
	
	/** 1：已完成，2：失败，3：刊登中，4：等待刊登  (子表数状态)*/
	/** 已完成 */
	public final Integer COMPLETE_STATUS_COMPLETE = 1;
	/** 失败 */
	public final Integer COMPLETE_STATUS_FAILED = 2;
	/** 刊登中 */
	public final Integer COMPLETE_STATUS_PRESSING = 3;
	/** 等待刊登 */
	public final Integer COMPLETE_STATUS_AWAIT = 4;
	
	
	//是否是父级的xml，0：是，1：否
	/** 是 */
	public static final Integer PARENT_TYPE_YES=0;
	/** 否 */
	public static final Integer PARENT_TYPE_NO=1;
	
	
	/**  正在处理该请求，但需要等待外部信息才能完成。 */
	public final String _AWAITING_ASYNCHRONOUS_REPLY_ = "_AWAITING_ASYNCHRONOUS_REPLY_";
	/**  请求因严重错误而中止。 */
	public final String _CANCELLED_	= "_CANCELLED_";
	/** 请求已处理。您可以调用 GetFeedSubmissionResult 操作来接收处理报告，该报告列出了上传数据中成功处理的记录以及产生错误的记录。 */
	public final String _DONE_ = "_DONE_";
	/** 请求正在处理。 */
	public final String _IN_PROGRESS_ = "_IN_PROGRESS_";
	/** 请求正在处理，但系统发现上传数据可能包含潜在错误（例如，请求将删除卖家账户中的所有库存）。亚马逊卖家支持团队将联系卖家，以确认是否应处理该上传数据 */
	public final String _IN_SAFETY_NET_ = "_IN_SAFETY_NET_";
	/** 已收到请求，但尚未开始处理。 */
	public final String _SUBMITTED_ = "_SUBMITTED_";
	/** *请求等待中。 */
	public final String _UNCONFIRMED_ = "_UNCONFIRMED_";
	
	
	/** 需要继续上报 */
	//public final Integer COMPLETE_STATUS_AGAIN =3;
	/** 下一个接口 */
	//public final Integer COMPLETE_STATUS_NEXT = 4;
	
	/**
	 * 当前数据流程结束
	 */
	public final String RESPORT_RESULT_COMPLETE = "Complete";
	public final String RESPORT_RESULT_OVER = "press_over";
	public final String RESPORT_RESULT_UPLOADING = "uploading";
	public final String RESPORT_RESULT_SUCCESS = "success";
	public final String RESPORT_RESULT_REQUESTTHROTTLED = "RequestThrottled"; //扼杀
	
	/** 刊登中 */
	public final String RESPORT_RESULT_PUBLISHING = "publishIng";
	/**
	 * 等待刊登
	 */
	public final String RESPORT_RESULT_AWAIT = "await";
	
	public final String RESPORT_RESULT_ERROR = "500";
	
	/** redis 中的后缀标识，主要用户接交后的json参数  */
	public final String  REDIS_PUBLISHMESSAGE = "_PublishMessage";
	public final String  REDIS_EXT = "_ext";
	
	public final String REDIS_XML_MESSAGE_ID = "redis_template_xml_message_id";
	
	/* ----------- amazon刊登开发者区域标识  begin ---------------------------- */
	/** 欧洲所有站点 */
	public final String amazon_developer_Europe = "Europe";
	
	/** 北美 */
	public final String amazon_developer_NorthAmerica = "NorthAmerica";
	
	/** Japan 小日本 */
	public final String amazon_developer_Japan = "Japan";
	
	/** Australian 澳洲  ruijp*/
	public final String amazon_developer_Australian = "Australian";
	
	/* ----------- amazon刊登开发者区域标识  end ---------------------------- */
	
	/* ----------- amazon刊登单多属性  begin ---------------------------- */
	
	/** 单属性 **/
	public static final int PUBLISH_TYPE_ONLY=2;
	/** 多属性 **/
	public static final int PUBLISH_TYPE_MORE=1;
	
	/* ----------- amazon刊登单多属性  end ---------------------------- */
	
	/** 未生成报告 */
	public static final int GET_REPORT_PROCESS_STATYS_NOTANY = 0;
	/** 生成报告成功，无任何错误信息	 */
	public static final int GET_REPORT_PROCESS_STATYS_SUCCESS = 1;
	
	/** 生成报告成功，但有错误信息 */
	public static final int GET_REPORT_PROCESS_STATYS_SUCC_HAVE_FAIL = 2;
	/** 获取报告异常了 */
	public static final int GET_REPORT_PROCESS_STATYS_EXCEPTION = 3;

	/**导出状态:导出中*/
	public static final String EXPORT_STATUS_INIT ="1";
	/**导出状态:导出成功*/
	public static final String EXPORT_STATUS_SUCCESS ="2";
	/**导出状态:导出失败*/
	public static final String EXPORT_STATUS_FAIL ="3";

	/**导出状态:搜索项导出*/
	public static final String EXPORT_TYPE_SEARCH ="1";
	/**导出状态:选中项导出*/
	public static final String EXPORT_TYPE_CHOOSE ="2";


}
