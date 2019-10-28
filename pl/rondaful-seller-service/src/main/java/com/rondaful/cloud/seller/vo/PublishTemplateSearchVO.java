package com.rondaful.cloud.seller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

@ApiModel(value = "PublishTemplateSearchVO")
public class PublishTemplateSearchVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "模板类型 1:物流设置2:买家限制3:商品所在地4:议价5:退货政策6:收款说明7:橱窗展示8:屏蔽目的地10生成平台sku模板({'ruleOne':'name 授权店铺名称 或者sku ','ruleTwo':'@ 符号','ruleThree':'8 长度'})  aliexpress速卖通40:产品模板 41区域调价模板")
	private Integer templateType;

	@ApiModelProperty(value = "平台站点")
	private String site;

	@ApiModelProperty(value = "平台 1:amazon 2:eBay 3:wish 4:aliexpress")
	private Integer platform;

	@ApiModelProperty(value = "店铺账号id")
	private String empowerId;

	@ApiModelProperty(value = "名称")
	private String templateName;

	@ApiModelProperty(value = "是否默认")
	private Boolean defaultIs;

	@ApiModelProperty(value = "是否系统模板")
	private Boolean systemIs;

	@ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String cStartTime;
    
    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String cEndTime;

	@ApiModelProperty(value = "开始时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private String uStartTime;

	@ApiModelProperty(value = "结束时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private String uEndTime;

	@ApiModelProperty(value = "分页页数")
	private Integer page;

	@ApiModelProperty(value = "每页条数")
	private Integer row;

	@ApiModelProperty(value = "账号")
	private String plAccount;

	public Integer getTemplateType() {
		return templateType;
	}

	public void setTemplateType(Integer templateType) {
		this.templateType = templateType;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public Integer getPlatform() {
		return platform;
	}

	public void setPlatform(Integer platform) {
		this.platform = platform;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public Boolean getDefaultIs() {
		return defaultIs;
	}

	public void setDefaultIs(Boolean defaultIs) {
		this.defaultIs = defaultIs;
	}

	public Boolean getSystemIs() {
		return systemIs;
	}

	public void setSystemIs(Boolean systemIs) {
		this.systemIs = systemIs;
	}

	public String getcStartTime() {
		if (StringUtils.isNotBlank(cStartTime))
			return cStartTime +" 00:00:00";
		return cStartTime;
	}

	public void setcStartTime(String cStartTime) {
		this.cStartTime = cStartTime;
	}

	public String getcEndTime() {
		if (StringUtils.isNotBlank(cEndTime))
			return cEndTime +" 23:59:59";
		return cEndTime;
	}

	public void setcEndTime(String cEndTime) {
		this.cEndTime = cEndTime;
	}

	public String getuStartTime() {
		if (StringUtils.isNotBlank(uStartTime))
			return uStartTime +" 00:00:00";
		return uStartTime;
	}

	public void setuStartTime(String uStartTime) {
		this.uStartTime = uStartTime;
	}

	public String getuEndTime() {
		if (StringUtils.isNotBlank(uEndTime))
			return uEndTime +" 23:59:59";
		return uEndTime;
	}

	public void setuEndTime(String uEndTime) {
		this.uEndTime = uEndTime;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getRow() {
		return row;
	}

	public void setRow(Integer row) {
		this.row = row;
	}

	public String getPlAccount() {
		return plAccount;
	}

	public void setPlAccount(String plAccount) {
		this.plAccount = plAccount;
	}

	public String getEmpowerId() {
		return empowerId;
	}

	public void setEmpowerId(String empowerId) {
		this.empowerId = empowerId;
	}
}
