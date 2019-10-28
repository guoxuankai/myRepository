package com.rondaful.cloud.supplier.service;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.supplier.dto.LogisticsDTO;
import com.rondaful.cloud.supplier.dto.LogisticsResponseDTO;
import com.rondaful.cloud.supplier.entity.LogisticsInfo;
import com.rondaful.cloud.supplier.entity.WarehouseMsg;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 物流方式主表操作
 * @author xieyanbin
 *
 * @2019年4月26日 
 * @version v1.0
 */
public interface ILogisticsInfoService {

	/**
	 * 	分页查询物流方式信息
	 * @param param
	 * @return
	 */
	Page queryLogisticsListPage(LogisticsInfo param);
	
	/**
	 * 	查询物流方式名称
	 * @return
	 */
	List<LogisticsDTO> queryLogisticsList();
	
	/**
	 * 	物流方式状态更新
	 * @param logistics
	 */
	void updateStatusByCode(LogisticsInfo logistics);
	
	/**
	 *	 第三方支持的物流方式查询
	 * @return
	 */
	Map<String, Object> queryThirdLogistics();
	
	/**
	 * 	更新平台物流映射
	 * @param
	 */
	void updateLogisticsMapping(LogisticsInfo param);
	
	/**
	 * 	新增数据
	 * @param list
	 */
	void insertLogisticsInfoList(List<LogisticsInfo> list);

	/**
	 * 通过仓库code和物流方式code查询物流信息
	 * @param logisticsCode
	 * @param warehouseId
	 */
	LogisticsDTO queryLogisticsByCode(String logisticsCode,Integer warehouseId);

	/**
	 * 根据物流方式编码查询仓库信息
	 * @param logisticsCode 物流方式编码
	 * @return
	 */
	Set<WarehouseMsg> queryWarehouse(String logisticsCode);
	
	/**
	 * 根据仓库code更新仓库状态
	 * @param warehouseCode
	 * @param status
	 */
	void updateWarehouseStatus(String warehouseCode,String status);

	/**
	 * 通过物流方式名称和类型查询物流方式和仓库信息
	 * @param param
	 */
	List<LogisticsResponseDTO> queryLogisticsByName(LogisticsInfo param);


	/**
	* @Description 通过仓库id查询物流方式
	* @Author  xieyanbin
	* @Param  logisticsInfo
	* @Return
	* @Exception
	*
	*/
	Page queryLogisticsListById(LogisticsInfo logisticsInfo);
	
}
