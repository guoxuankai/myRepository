package com.rondaful.cloud.supplier.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.service.BaseService;
import com.rondaful.cloud.supplier.entity.InventoryDynamics;
import com.rondaful.cloud.supplier.entity.WarehouseInventory;
import com.rondaful.cloud.supplier.entity.WarehouseInventoryReport;
import com.rondaful.cloud.supplier.vo.GranaryInventoryVO;

/**
 * 
* @ClassName: IWarehouseInventoryService
* @Description: 仓库库存服务
* @author Administrator
* @date 2018年12月5日
*
 */
public interface IWarehouseInventoryService{
	 
	/**
	*  @throws Exception
	* @Title: insertWarehouseInventory
	* @Description: 同步ERP仓库库存
	* @param     参数
	* @return void    返回类型
	* @throws
	 */
	public void syncWarehouseInventory(List<WarehouseInventory> inventoryList);
	
	public void syncERPInventory();
	
	public List<WarehouseInventory> getAvailableQty(Map<String, Object> param); 
	
	public void insertSupplierSkuMap();
	
	public void exportInventoryExcel(WarehouseInventory param,String ids,HttpServletResponse response);
	
	public void exportInventoryExcelByCms(WarehouseInventory param,String ids, HttpServletResponse response);
	
	public void updateBatchWarnVal(Map<String, Object> params);
	
	public  Map<String,Object> getInventoryListBySkus(List<String> skus);
	
	public  Map<String,Object> getInventoryListByParams(List<WarehouseInventory> invList);
	
	public Page<WarehouseInventory> page(WarehouseInventory warehouseInventory);

	public Page<InventoryDynamics> pageDynamics(InventoryDynamics inventoryDynamics);
	
	public void exportInventoryDynamics(InventoryDynamics inventoryDynamics, String ids, HttpServletResponse response);
	
	public void  inventoryWarnNotice();
	
	public void syncInventoryBySupplierSku(String skus);
	
	public WarehouseInventoryReport getInvCommidtyReport();

	public void syncGranaryInventory();

	Integer getInvAvailableQtyByParam(Map<String, String> param);
	

	
}
