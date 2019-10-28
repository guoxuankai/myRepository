package com.rondaful.cloud.supplier.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.DeliveryDetail;

public interface DeliveryDetailMapper extends BaseMapper<DeliveryDetail> {
	public int insertBatchDeliveryDetail(List<DeliveryDetail> deliveryDetail);
	public List<DeliveryDetail> getDeliveryDetail(Map<String,Object> param);
	public List<String> getDeliveryIdsBySupplier(@Param("supplierId")Integer supplierId);
	
	public Integer selectDeliveryDetailCount(@Param("supplierId")Integer supplierId);
	
	public Integer getSkuCount(@Param("supplierId")Integer supplierId);
	
	public String getSkuPriceTotal(@Param("supplierId")Integer supplierId);
}