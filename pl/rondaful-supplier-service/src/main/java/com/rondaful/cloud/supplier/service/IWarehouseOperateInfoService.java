package com.rondaful.cloud.supplier.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.supplier.entity.WarehouseOperateInfo;
import com.rondaful.cloud.supplier.entity.WarehouseSync;

/**
 * 
* @ClassName: IWarehouseSyncService
* @Description: 仓库列表
* @author Administrator
* @date 2018年12月5日
*
 */
public interface IWarehouseOperateInfoService{
	
	/**
	 * @throws Exception 
	 * 
	* @Title: insertWarehouse
	* @Description: 同步ERP仓库列表
	* @param @param warehouseList
	* @param @return    参数
	* @return void    返回类型
	* @throws
	 */
	public void  insertWarehouse();
	
	public List<WarehouseSync> getValidWarehouseList();
	public void updateWarehouseById(Map<String,String> param);
	public Page<WarehouseOperateInfo> pageBySupplier(WarehouseOperateInfo operateInfo);
	public Page<WarehouseOperateInfo> pageByCms(WarehouseOperateInfo operateInfo);
	
	public void insertOperateInfo();
	public List<WarehouseSync> selectWarehouseByParam(Map<String, String> param);
	public List<WarehouseOperateInfo> getCountryList();
	public List<WarehouseOperateInfo> queryWarehouseMsg(WarehouseOperateInfo operateInfo );
	
	public Page<WarehouseSync> getValidWarehousePage();
	
	List<WarehouseOperateInfo> getCountryListByCode(String countryCode);

}
