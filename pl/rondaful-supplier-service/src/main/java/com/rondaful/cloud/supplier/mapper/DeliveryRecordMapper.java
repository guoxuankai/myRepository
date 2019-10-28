package com.rondaful.cloud.supplier.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.rondaful.cloud.supplier.entity.DeliveryRecord;

public interface DeliveryRecordMapper {
	
	public int insertBatchDeliveryRecord(List<DeliveryRecord> deliveryRecord);
	public List<DeliveryRecord> getDeliveryRecord(DeliveryRecord deliveryRecord);
	public void updateOrderStatusBySourceOrder(Map<String,Object> param);
	
}