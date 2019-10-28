package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.supplier.entity.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ThirdLogisticsMapper {
	
	/**
	 * 	插入erp物流方式
	 * @param list
	 */
	void insertErpLogistics(List<ErpLogistics> list);
	
	/**
	 *	 插入谷仓物流方式集合
	 * @param list
	 */
	void insertGuLogisticsList(List<GranaryLogistics> list);
	
	/**
	 *	 插入速卖通物流方式
	 * @param list
	 */
	void insertAliexpressLogistics(List<AliexpressLogistics> list);
	
	/**
	 * 	导入物流方式映射
	 * @param list
	 */
	void updateLogisticsMappingList(@Param("list")List<LogisticsInfo> list);

	/**
	 * 根据物流方式code和仓库code查询数量
	 * @param warehouseId
	 * @param logisticsCode
	 */
	Integer queryLogisticsCountByCode(@Param("warehouseId")Integer warehouseId,@Param("logisticsCode")String logisticsCode);


	/**
	* @Description  查询国家信息
	* @Author  xieyanbin
	* @Param  countryMap
	* @Return      List<CountryMap>
	* @Exception
	*
	*/
	List<CountryMap> queryCountryByCode(CountryMap countryMap);


	/**
	* @Description 通过国家更新国家信息
	* @Author  xieyanbin
	* @Param  countryMap
	* @Return
	* @Exception
	*
	*/
	void updateCountryByCode(CountryMap countryMap);

	/**
	* @Description 插入国家信息
	* @Author  xieyanbin
	* @Param countryMap
	* @Return
	* @Exception
	*
	*/
	void insertCountry(CountryMap countryMap);
	
	/**
	* @Description
	* @Author  xieyanbin
	* @Param  查询邮编
	* @Return      List
	* @Exception   
	* 
	*/
	List<String> queryPostCode();
}