package com.rondaful.cloud.commodity.vo;

import java.io.Serializable;

/**
* @Description:通途详细描述列表
* @author:范津 
* @date:2019年9月12日 下午4:56:42
 */
public class TongToolDetailDescriptions implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String title;

	private String descLanguage;
	
	private String content;

	
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescLanguage() {
		return descLanguage;
	}

	public void setDescLanguage(String descLanguage) {
		this.descLanguage = descLanguage;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
