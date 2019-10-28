package com.amazonservices.mws.uploadData.entity.amazon;

import java.util.List;

public class AmazonNode {
	private String classPath;
	
	private List<AmazonNode> amazonNode;
	
	private String fieldName;

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
