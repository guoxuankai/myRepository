package com.rondaful.cloud.seller.vo;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public class EabyPublishListingVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Integer> ids = Lists.newArrayList();
	private Long id;
	private int type=0;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Integer> getIds() {
		return ids;
	}

	public void setIds(List<Integer> ids) {
		this.ids = ids;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
