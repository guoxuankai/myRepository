package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.WarehouseOperateInfo;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface WarehouseOperateInfoMapper  extends BaseMapper<WarehouseOperateInfo> {
	public List<WarehouseOperateInfo> selectOperateInfoBySupplier(Map<String,Object> params);
	public int insertOperateInfo(WarehouseOperateInfo warehouseOperateInfo);
	public int updateWarehouseStatusById(Map<String, String> param);
	/**
	 * 查询列表
	 * */
	public List<WarehouseOperateInfo> pageBySupplier(WarehouseOperateInfo t);
	public List<WarehouseOperateInfo> pageByCms(WarehouseOperateInfo t);
	public List<WarehouseOperateInfo> selectCountry();
	public List<WarehouseOperateInfo> selectCountryByCode(String countryCode);
}