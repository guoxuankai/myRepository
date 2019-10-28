package com.rondaful.cloud.supplier.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 *  problem
 * @author xieyanbin
 * @date 2018-12-18 10:13:23
 */
@ApiModel(value ="Problem")
public class Problem implements Serializable {
    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "来源 0供应商 1卖家")
    private String source;

	@ApiModelProperty(value = "分类 0系统配置 1商品管理 2仓库管理 3订单管理  6财务管理 7售后管理（客户服务） 8物流管理 9采购管理 10 用户管理")
    private String type;

    @ApiModelProperty(value = "创建ID")
    private Long createId;

    @ApiModelProperty(value = "答案")
    private String answer;

    @ApiModelProperty(value = "״̬ 状态 0已删除 1未删除")
    private String status;

    private String updateTime;

    private String createTime;

    private static final long serialVersionUID = 1L;
    
    private String startDate;
    
    private String endDate;
    
    private String content;
    
    private String dateType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getCreateId() {
		return createId;
	}

	public void setCreateId(Long createId) {
		this.createId = createId;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDateType() {
		return dateType;
	}

	public void setDateType(String dateType) {
		this.dateType = dateType;
	}

	@Override
	public String toString() {
		return "Problem [id=" + id + ", title=" + title + ", source=" + source + ", type=" + type + ", createId="
				+ createId + ", answer=" + answer + ", status=" + status + ", updateTime=" + updateTime
				+ ", createTime=" + createTime + ", startDate=" + startDate + ", endDate=" + endDate + ", content="
				+ content + ", dateType=" + dateType + "]";
	}


}