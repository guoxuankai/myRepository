package com.rondaful.cloud.seller.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.seller.entity.Upcmanage;
import com.rondaful.cloud.seller.entity.upcResult;

public interface UpcmanageService {

	/**
	 * 通过id停用stautus
	 * @param status
	 * @param id
	 * @return
	 */
	void updateStatusById(Integer status,Integer id);


	/**
	 * 停用或启用全部upc码
	 * @return
	 */
	void  updateAllStatus(Integer status,String account);


	/**
	 * 通过批次停用或启用全部upc码
	 * @param status
	 * @param id
	 * @return
	 */
	void updateStatusByNumberBatch(Integer status,String numberBatch,String account);


	/**
	 * 通过类型停用或启用全部upc码
	 * @param status
	 * @param id
	 * @return
	 */
	void updateStatusByNumberType(Integer status,String numberType,String account);


	/**
	 * 模糊查询upc列表信息
	 * @param numberBatch
	 * @param numberType
	 * @param numberType2
	 * @return
	 */
	Page<Upcmanage> fuzzyFindAll(String numberBatch,String numberType, String number, String account);


	/**
	 * 查询upc列表信息
	 * @param numberBatch
	 * @param numberType
	 * @return
	 */
	Page<Upcmanage> findAll(String account);


	/**
	 * 插入upc数据
	 * @param upcmanage
	 * @return
	 */
	int insertAllObject(Upcmanage upcmanage);


	/**
	 * 统计数据查询
	 * @return
	 */
	List<Integer> selectUpcResult(String account);


	/**
	 * 查询upc数据
	 * @param status
	 * @param numbertype
	 * @return
	 */
	List<String> selectObject(String numbertype,Integer number,String account,Integer usedplatform,Integer oneselfType);


	/**
	 * 查询UPC可用数量
	 * @param status
	 * @param numbertype
	 * @return
	 */
	Integer selectEableCounts(String numberType,String account,Integer usedplatform,Integer oneselfType);



	/**
	 * 修改已使用的upc码状态
	 * @param status
	 * @param numbers
	 */
	void updateStatusNumbers(List numbers);


	/**
	 * 刊登时upc码的状态修改
	 */
	void updateUPCStatus(String number,Integer status,Integer usedplatform);

	/**
	 * 查询UPC使用平台信息
	 * @return
	 */
	Integer selectUsedplatform(String number);

	/**
	 * 校验upc码是否重复
	 * @param number
	 * @return
	 */
	Integer checkNumber(String number);

	
	/**
     * 查询所有商品编码批次
     * @param account
     * @return
     */
	List<String> checkNumberBatch(String account);
	
}
