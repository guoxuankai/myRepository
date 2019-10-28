package com.amazonservices.mws.uploadData.entity.amazon;

import java.util.ArrayList;
import java.util.List;

public class AmazonAttr {
	private String attrName;
	
	private String attrType;
	
	private List<String> defaultValue = new ArrayList<String>();
	
	
	private List<AmazonAttr> nextNodes = new ArrayList<AmazonAttr>();
	
	
	public List<AmazonAttr> getNextNodes() {
		return nextNodes;
	}

	public void setNextNodes(List<AmazonAttr> nextNodes) {
		this.nextNodes = nextNodes;
	}

	public List<String> getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(List<String> defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public String getAttrType() {
		return attrType;
	}

	public void setAttrType(String attrType) {
		this.attrType = attrType;
	}
	
	
}
