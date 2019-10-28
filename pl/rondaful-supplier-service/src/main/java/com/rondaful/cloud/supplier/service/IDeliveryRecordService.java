package com.rondaful.cloud.supplier.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.supplier.entity.DeliveryDetail;
import com.rondaful.cloud.supplier.entity.DeliveryRecord;

/**
 * 
* @ClassName: IDeliveryRecordService
* @Description: 出库记录服务
* @author Administrator
* @date 2019年1月2日
*
 */
public interface IDeliveryRecordService{
	 
	
	/**
	* @Title: insertBatchDeliveryRecord
	* @Description: 批量插入出库记录
	* @param @param deliveryRecord    参数
	* @return void    返回类型
	* @throws
	*/
		public String insertBatchDeliveryRecord(List<DeliveryRecord> deliveryRecord);
		
		public Page<DeliveryRecord> page(DeliveryRecord dr);
		
		public void exportDeliveryRecordExcel(DeliveryRecord param, HttpServletResponse response);
		
		public List<DeliveryDetail> getDeliveryDetail(String deliveryId);
		
		public void updateOrderStatusBySourceOrder(Map<String,Object> param);
		
		public DeliveryRecord getDeliveryCount();
		
	}
