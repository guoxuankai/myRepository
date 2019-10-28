package com.rondaful.cloud.seller.vo;

import java.io.Serializable;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 亚马逊刊登模板查询请求相关参数
 * @author dingshulin
 *
 */
@ApiModel(description = "亚马逊刊登模板查询VO")
public class AmazonTemplateRuleVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "分页页数")
	private String page;
	
	@ApiModelProperty(value = "每页条数")
	private String row;
	
	@ApiModelProperty(value = "开始时间yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private String startCreateTime;
	
	@ApiModelProperty(value = "结束时间yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private String endCreateTime;
	
	@ApiModelProperty(value = "授权账号ID")
	private Long empowerId;
	
	@ApiModelProperty(value = "是否默认模板 0 ：是 1：否  2:全局默认模板")
	private Integer defaultTemplate;
	
	@ApiModelProperty(value = "模板名字")
	private String templateName;
	
	@ApiModelProperty(value = "时间范围查询类型[0:创建时间 1:发布时间　2：上线时间 3：更新时间 ] 查询参数")
	private Integer timeType;

	
	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getRow() {
		return row;
	}

	public void setRow(String row) {
		this.row = row;
	}

	public String getStartCreateTime() {
		return startCreateTime;
	}

	public void setStartCreateTime(String startCreateTime) {
		this.startCreateTime = startCreateTime;
	}

	public String getEndCreateTime() {
		return endCreateTime;
	}

	public void setEndCreateTime(String endCreateTime) {
		this.endCreateTime = endCreateTime;
	}

	public Long getEmpowerId() {
		return empowerId;
	}

	public void setEmpowerId(Long empowerId) {
		this.empowerId = empowerId;
	}

	public Integer getDefaultTemplate() {
		return defaultTemplate;
	}

	public void setDefaultTemplate(Integer defaultTemplate) {
		this.defaultTemplate = defaultTemplate;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public Integer getTimeType() {
		return timeType;
	}

	public void setTimeType(Integer timeType) {
		this.timeType = timeType;
	}


	

}
