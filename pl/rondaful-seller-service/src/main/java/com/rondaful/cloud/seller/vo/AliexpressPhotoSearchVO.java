package com.rondaful.cloud.seller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.List;

@ApiModel(value = "AliexpressPhotoSearchVO")
public class AliexpressPhotoSearchVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "这张图片在图片银行中名称")
	private String displayName;

	@ApiModelProperty(value = "刊登帐号id")
	private Long empowerId;

	@ApiModelProperty(value = "刊登用户的品连账号id")
	private Long plAccountId;
	
    @ApiModelProperty(value = "卖家")
    private Long groupId;

	@ApiModelProperty(value = "分页页数")
	private Integer page;

	@ApiModelProperty(value = "每页条数")
	private Integer row;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Long getEmpowerId() {
		return empowerId;
	}

	public void setEmpowerId(Long empowerId) {
		this.empowerId = empowerId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
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

	public Long getPlAccountId() {
		return plAccountId;
	}

	public void setPlAccountId(Long plAccountId) {
		this.plAccountId = plAccountId;
	}
}
