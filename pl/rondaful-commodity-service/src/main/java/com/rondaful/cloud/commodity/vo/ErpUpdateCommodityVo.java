package com.rondaful.cloud.commodity.vo;

import java.io.Serializable;
import java.util.List;

/**
* @Description:erp更新商品状态请求类
* @author:范津 
* @date:2019年4月10日 下午5:36:40
 */
public class ErpUpdateCommodityVo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	//供应商sku
	private List<String> supplierSkuList;
	
	//操作类型，1：上架，-1：下架
	private String optType;
	
	//供应商ID
	private String supplierId;

	
	public List<String> getSupplierSkuList() {
		return supplierSkuList;
	}

	public void setSupplierSkuList(List<String> supplierSkuList) {
		this.supplierSkuList = supplierSkuList;
	}

	public String getOptType() {
		return optType;
	}

	public void setOptType(String optType) {
		this.optType = optType;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}
	
}
