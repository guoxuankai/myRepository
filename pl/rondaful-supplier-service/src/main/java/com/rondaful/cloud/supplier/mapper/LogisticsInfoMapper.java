package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.supplier.dto.LogisticsDTO;
import com.rondaful.cloud.supplier.entity.Logistics.LogisticsMapping;
import com.rondaful.cloud.supplier.entity.LogisticsInfo;
import com.rondaful.cloud.supplier.entity.PlatformLogistics;
import com.rondaful.cloud.supplier.model.dto.logistics.ThirdAppLogisticsDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LogisticsInfoMapper  {
	
	 /**
	 *  条件查询物流方式列表
	 * @param param
	 * @return
	 */
	List<LogisticsDTO> queryLogisticsListPage(LogisticsInfo param);
	
	/**
	 * 
	 * 通过仓库code和物流code查询物流信息
	 *
	 * @param logisticsCode
	 * @param warehouseId
	 * @return
	 */
	LogisticsDTO queryLogisticsByCode(@Param("logisticsCode") String logisticsCode, @Param("warehouseId") Integer warehouseId);
	
	/**
	 * 第三方支持的物流方式查询（Amazon,Ebay）
	 * @return
	 */
	List<PlatformLogistics> queryThirdLogistics();
	
	/**
	 * 查询速卖通的物流方式code
	 * @return
	 */
	List<String> selectAliexpressCode();
	
	/**
	 * 新增数据
	 * @param list
	 */
	void insertLogisticsInfoList(List<LogisticsInfo> list);
	
	/**
	 * 新增数据
	 * @param logisticsInfo
	 */
	void insertLogisticsInfo(LogisticsInfo logisticsInfo);
	
	/**
	 * 更新物流映射
	 * @param param
	 */
	void updateLogisticsMapping(LogisticsInfo param);
	
	/**
	 * 更新物流方式启停用
	 * @param logisticsInfo
	 */
	int updateStatusById(LogisticsInfo logisticsInfo);
	
	/**
	 *	 导入速卖通物流方式映射
	 * @param logisticsInfo
	 */
	void updateAliexpressCode(LogisticsInfo logisticsInfo);
	
	/**
	 * 	通过仓库code和物流code集合查询物流信息
	 */
	LogisticsDTO getLogisticsByCode(@Param("warehouseId") Integer warehouseId, @Param("code") String code);

	/**
	 * 根据仓库code更新仓库状态
	 * @param warehouseCode
	 * @param status
	 */
	void updateWarehouseStatus(@Param("warehouseCode") String warehouseCode, @Param("status") String status);

	/**
	 * 查询物流方式名称
	 * @return
	 */
	List<LogisticsDTO> queryLogisticsList();


	List<ThirdAppLogisticsDTO> queryLogisticsListById(LogisticsInfo logisticsInfo);

	LogisticsMapping selectLogisticsMapping(@Param("granaryLogisticsCode")String granaryLogisticsCode, @Param("erpLogisticsCode")String erpLogisticsCode,@Param("warehouseId")Integer warehouseId);
}