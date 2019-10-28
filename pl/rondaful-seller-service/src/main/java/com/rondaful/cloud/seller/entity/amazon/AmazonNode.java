package com.rondaful.cloud.seller.entity.amazon;

import java.util.List;

public class AmazonNode {
	private String classPath;
	
	private List<AmazonNode> amazonNode;
	
	private String fieldName;

	private String type;
	
	private boolean required = Boolean.FALSE;
	
	
	
	
	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getClassPath() {
		return classPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

 

	public List<AmazonNode> getAmazonNode() {
		return amazonNode;
	}

	public void setAmazonNode(List<AmazonNode> amazonNode) {
		this.amazonNode = amazonNode;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	
}
