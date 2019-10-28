package com.rondaful.cloud.seller.common.task;


/**
 * 数据统计
 * @author ouxiangfeng
 *
 */
public class StatisticsPublishReport {

	/** 总数量 */
	private Long totalCount;
	
	/** 成功数量 */
	private Long successCount;
	
	/** 错误数量 */
	private Long errorCount;
	
	/** 刊登中数量 */
	private Long pubingCount;
	
	/** 等数量 */
	private Long wiatCount;
	
	/** id */
	private Long id;

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	public Long getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(Long successCount) {
		this.successCount = successCount;
	}

	public Long getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(Long errorCount) {
		this.errorCount = errorCount;
	}

	public Long getPubingCount() {
		return pubingCount;
	}

	public void setPubingCount(Long pubingCount) {
		this.pubingCount = pubingCount;
	}

	public Long getWiatCount() {
		return wiatCount;
	}

	public void setWiatCount(Long wiatCount) {
		this.wiatCount = wiatCount;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	
}
