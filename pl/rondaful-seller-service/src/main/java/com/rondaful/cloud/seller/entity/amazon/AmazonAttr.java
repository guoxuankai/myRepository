package com.rondaful.cloud.seller.entity.amazon;

import java.util.ArrayList;
import java.util.List;

public class AmazonAttr {
	private String attrName;
	
	private String attrType;
	
	private String genericFlag = "";
	
	private boolean required;

	private boolean haveOptions;
	
	private String attrNote;
	
	private List<String> defaultValue = new ArrayList<String>();
	
	private List<AmazonAttr> nextNodes = new ArrayList<AmazonAttr>();
	
	
	
	
	public String getAttrNote() {
		return attrNote;
	}

	public void setAttrNote(String attrNote) {
		this.attrNote = attrNote;
	}

	public String getGenericFlag() {
		return genericFlag;
	}

	public void setGenericFlag(String genericFlag) {
		this.genericFlag = genericFlag;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

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

	public boolean isHaveOptions() {
		return haveOptions;
	}

	public void setHaveOptions(boolean haveOptions) {
		this.haveOptions = haveOptions;
	}
}
