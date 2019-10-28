package com.rondaful.cloud.seller.entity;

import java.io.Serializable;
import java.util.Date;

public class upcResult implements Serializable {
   
	private String usedplatform;
	
	private Integer used;
	
	private Integer usable;
	
	
	public String getUsedplatform() {
		return usedplatform;
	}

	public void setUsedplatform(String usedplatform) {
		this.usedplatform = usedplatform;
	}

	public Integer getUsed() {
		return used;
	}

	public void setUsed(Integer used) {
		this.used = used;
	}

	public Integer getUsable() {
		return usable;
	}

	public void setUsable(Integer usable) {
		this.usable = usable;
	}
	
	
    
    
    
   
}