package com.rondaful.cloud.seller.vo;

/** 
 * 返回给订单
 * @author 嘻嘻
 *
 */
public class ResultPublishListingVO {
	   //发货仓库
    private Integer warehouseId;
    
    //1价格最低  2综合排序  3物流速度
    private Integer logisticsType;

    
    //物流方式code
    private String logisticsCode;


	public Integer getWarehouseId() {
		return warehouseId;
	}


	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}


	public Integer getLogisticsType() {
		return logisticsType;
	}


	public void setLogisticsType(Integer logisticsType) {
		this.logisticsType = logisticsType;
	}


	public String getLogisticsCode() {
		return logisticsCode;
	}


	public void setLogisticsCode(String logisticsCode) {
		this.logisticsCode = logisticsCode;
	}
    
    
}


