package com.rondaful.cloud.supplier.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.GoodcangAccountinfo;
import com.rondaful.cloud.supplier.entity.WarehouseInventory;

public interface WarehouseInventoryMapper extends BaseMapper<WarehouseInventory> {
	
	public int syncWarehouseInventory(List<WarehouseInventory> warehouseInventoryList);
	
	public int updateBysupplierSku(WarehouseInventory warehouseInventory);
	
	public int updateCommodityByPinlianSku(Map<String,String> params);
	
	public List<WarehouseInventory> getInventoryListByPlSku(Map<String,Object> params);
	
	public int insertSupplierCommodity(List<WarehouseInventory> warehouseInventoryList);
	
	public List<String> getAllSupplierSkus(Map<String,Integer> pgInfo); 
	
	public Integer getAllSupplierSkusCount();
	
	List<WarehouseInventory> selectByPrimaryKey(Map<String,Object> params);
	
	public Integer updateBatchWarnVal(@Param(value = "list")List<WarehouseInventory> list);
	
	public List<WarehouseInventory> getInventoryListByIds(Map<String,Object> params);
	
	public Integer getCommityCountBySku(@Param(value = "pinlianSku")String pinlianSku);
	
	public WarehouseInventory  getInventoryListByParams(Map<String,Object> params);
	
	public List<WarehouseInventory>  getInventoryListBySku(String pinlianSku);	
	
	public List<WarehouseInventory> getInventoryListByWarn();
	
	public Integer getSkuMapCountBysupplierSkus(@Param("supplierSku") String supplierSku);
	
	public Integer getInvCommidtyTotal(@Param("supplierId")Integer supplierId);
	
	public Integer getWarnInvCommidtyTotal(@Param("supplierId")Integer supplierId);
	
	public Integer getEntInvWareHouseCount(@Param("supplierId")Integer supplierId);
	
	public List<GoodcangAccountinfo> getGranaryAccountInfo(Map<String, String> param); 
	
	public Integer syncGranaryInventory(List<WarehouseInventory> warehouseInventoryList);
	
	public Integer insertGranaryInventory(WarehouseInventory warehouseInventory);
	
	public Integer updateGranaryInventory(WarehouseInventory warehouseInventory);
	
	public int getInvByPinlianSku(Map<String,String> params);
	
	public int updateByPinlianSku(WarehouseInventory warehouseInventory);
	
	public WarehouseInventory getCommodityBySupplierSku(String supplierSku);
	
	public WarehouseInventory getCommodityByPinlianSku(String pinlianSku); 
	
	Integer getInvAvailableQtyByParam(Map<String, String> param);
	
}