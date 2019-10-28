package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.WarehouseSync;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface WarehouseSyncMapper extends BaseMapper<WarehouseSync> {
	
	public int syncWarehouse(List<WarehouseSync> warehouseList);
	public List<WarehouseSync> selectByWarehouseType(String warehouseType);
	public List<WarehouseSync> selectValidWarehouse(Map<String,Object> params);
	public List<WarehouseSync> selectWarehouseByParam(Map<String, String> param);
	
	public List<WarehouseSync> selectWarehouseListByObjectParam(WarehouseSync wareHouseSync);
	
	public List<WarehouseSync> selectAvailableWareHouse();

	/**
	 * 根据供应商获取对应可用仓库
	 * @param userId
	 * @return
	 */
	List<WarehouseSync> getsHouseName(@Param("userId") Integer userId);

	/**
	 * 根据仓库codep批量查询绑定名称
	 * @param codes
	 * @return
	 */
	List<WarehouseSync> getsNameByCode(@Param("codes") List<String> codes);



	public List<WarehouseSync> selectAvailableWareHouse(@Param("wareHouseName") String wareHouseName);

}