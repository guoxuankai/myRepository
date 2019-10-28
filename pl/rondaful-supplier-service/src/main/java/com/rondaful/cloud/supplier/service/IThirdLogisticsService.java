package com.rondaful.cloud.supplier.service;

import com.rondaful.cloud.supplier.dto.FreightTrialDTO;
import com.rondaful.cloud.supplier.dto.LogisticsDTO;
import com.rondaful.cloud.supplier.entity.*;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * 第三方物流方式业务操作
 * @author xieyanbin
 *
 * @2019年4月26日 
 * @version v2.2
 */
public interface IThirdLogisticsService {

    /**
     * 	插入erp物流方式
     */
    void insertErpLogistics() throws Exception;
    
    /**
     * 	插入谷仓物流方式集合
     * @param serviceId
     */
	void insertGuLogisticsList(Integer serviceId);
    
    /**
     * 	匹配物流方式
     * @param param
     * @return
     */
    List<LogisticsDTO> matchLogistics(FreightTrial param) throws Exception;
   
	/**
	 * 	获取速卖通物流方式
	 * @param list
	 */
	void insertAliexpressLogistics(List<AliexpressLogistics> list);
	
	/**
	 *	导入速卖通物流方式映射
	 * @param logistics
	 */
	void updateLogisticsMappingList(List<LogisticsInfo> logistics);

	/**
	 * 更新erp物流方式
	 */
	void updateErpLogistics()  throws Exception;

	/**
	 * 更新谷仓物流方式
	 */
	void updateGranaryLogistics();

	/**
	 *  @author: xieyanbin
	 *  @Date: 2019/7/16 2019/7/16
	 *  @Description: 导入国家信息
	 */
	void importCountry(Workbook wb);

	/**
	* @Description 获取所有国家信息
	* @Author  xieyanbin
	* @Param
	* @Return List<CountryMap>
	* @Exception
	*
	*/
	List<CountryMap> getCountry(CountryMap countryMap);

	/**
	* @Description 初始化wms物流方式
	* @Author  xieyanbin
	* @Param  warehouseId 仓库id
	* @Return
	* @Exception
	*
	*/
	void initWmsLogistics(Integer warehouseId);

	/**
	* @Description 调用wms的运费试算
	* @Author  xieyanbin
	* @Param  freightTrial
	* @Return
	* @Exception
	*
	*/
	List<FreightTrialDTO> queryWmsFreight(FreightTrial freightTrial);
	
	/**
	* @Description
	* @Author  xieyanbin
	* @Param  同步更新wms物流方式
	* @Return      
	* @Exception   
	* 
	*/
	void updateWmsLogistics();

	/**
	* @Description
	* @Author  xieyanbin
	* @Param  erp废弃接口
	* @Return
	* @Exception   
	* 
	*/
	void noticeLogisticsDiscard(List<LogisticsInfo> param);

}
