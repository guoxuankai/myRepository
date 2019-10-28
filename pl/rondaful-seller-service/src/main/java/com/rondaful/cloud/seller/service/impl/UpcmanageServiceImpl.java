package com.rondaful.cloud.seller.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.seller.entity.Empower;
import com.rondaful.cloud.seller.entity.Upcmanage;
import com.rondaful.cloud.seller.entity.upcResult;
import com.rondaful.cloud.seller.mapper.UpcmanageMapper;
import com.rondaful.cloud.seller.service.UpcmanageService;


@Service
public class UpcmanageServiceImpl  implements  UpcmanageService{

	@Autowired
	private UpcmanageMapper upcmanageMapper;

	/**
	 * 通过id停用stautus
	 */
	public void updateStatusById(Integer status,Integer id) {
		if (id == null)
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"商品id值不能为空");
		try {
			upcmanageMapper.updateStatusById(status, id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
		}
	}




	/**
	 * 停用或启用全部upc码
	 */
	public void updateAllStatus(Integer status,String account) {
		try {
			upcmanageMapper.updateAllStatus(status,account);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
		}
	}


	/**
	 * 通过批次停用或启用全部upc码
	 */
	public void updateStatusByNumberBatch(Integer status,String numberBatch,String account) {
		if(StringUtils.isBlank(numberBatch))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"号码类型不能为空");
		String[] string = numberBatch.split(",");
		upcmanageMapper.updateStatusByNumberBatch(status,string,account);
	}


	/**
	 * 通过类型停用或启用全部upc码
	 */
	public void updateStatusByNumberType(Integer status, String numberType,String account) {
		if(StringUtils.isBlank(numberType))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403,"号码类型不能为空");
		upcmanageMapper.updateStatusByNumberType(status,numberType,account);
	}


	/**
	 * 模糊查询upc列表信息
	 */
	public Page<Upcmanage> fuzzyFindAll(String numberBatch, String numberType,String number,String account) {

		List<Upcmanage> fuzzyFindAll = upcmanageMapper.fuzzyFindAll(numberBatch, numberType,number,account);
		PageInfo<Empower> pageInfo = new PageInfo(fuzzyFindAll);
		return new Page(pageInfo);

	}



	/**
	 * 查询upc列表信息
	 */
	public Page<Upcmanage> findAll(String account) {
		List<Upcmanage> findAll = upcmanageMapper.findAll(account);
		PageInfo<Upcmanage> pageInfo = new PageInfo(findAll);
		return new Page(pageInfo);

	}






	/**
	 * 插入upc数据
	 */
	public int insertAllObject(Upcmanage upcmanage) {

		if(StringUtils.isBlank(upcmanage.getNumbertype()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403);
		if(StringUtils.isBlank(upcmanage.getNumberbatch()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403);
		if(StringUtils.isBlank(upcmanage.getNumber()))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403);
		return upcmanageMapper.insert(upcmanage);

	}


	/**
	 * 统计数据查询
	 */
	public List<Integer> selectUpcResult(String account) {

		ArrayList<Integer> list = new ArrayList<>();

		Integer selectTotal = upcmanageMapper.selectTotal(account); //查询upc总数   

		Integer selectEbayUPCUsed = upcmanageMapper.selectEbayUPCUsed(account); //查询ebayUPC已用码  
		Integer selectEbayUPCUseable = upcmanageMapper.selectEbayUPCUseable(account); //查询EbayUPC可用码 
		Integer selectEbayEANUsed = upcmanageMapper.selectEbayEANUsed(account); //查询ebayEAN已用码 
		Integer selectEbayEANUseable = upcmanageMapper.selectEbayEANUseable(account);//查询ebayEAN可用码 
		Integer selectEbayISANUsed = upcmanageMapper.selectEbayISANUsed(account); //查询ebayISBN已用码 
		Integer selectEbayISANUseable = upcmanageMapper.selectEbayISANUseable(account);//查询ebayISBN可用码 


		Integer selectAmazonUsed = upcmanageMapper.selectAmazonUPCUsed(account); //查询amazonUPC已用码 
		Integer selectAmazonUseable = upcmanageMapper.selectAmazonUPCUseable(account); //查询amazonUPC可用码 
		Integer selectAmazonEANUsed = upcmanageMapper.selectAmazonEANUsed(account); //查询amazonEAN已用码 
		Integer selectAmazonEANUseable = upcmanageMapper.selectAmazonEANUseable(account); //查询amazonEAN可用码 
		Integer selectAmazonISANUsed = upcmanageMapper.selectAmazonISANUsed(account); //查询amazonISBN已用码 
		Integer selectAmazonISANUseable = upcmanageMapper.selectAmazonISANUseable(account); //查询amazonISBN可用码 

		Integer selectUPCDead = upcmanageMapper.selectUPCDead(account); //查询UPC停用码
		Integer selectEANDead = upcmanageMapper.selectEANDead(account); //查询EAN停用码
		Integer selectISBNDead = upcmanageMapper.selectISBNDead(account); //查询ISBN停用码
		
		list.add(selectTotal);
		list.add(selectEbayUPCUsed);
		list.add(selectEbayUPCUseable);
		list.add(selectEbayEANUsed);
		list.add(selectEbayEANUseable);
		list.add(selectEbayISANUsed);
		list.add(selectEbayISANUseable);
		list.add(selectAmazonUsed);
		list.add(selectAmazonUseable);
		list.add(selectAmazonEANUsed);
		list.add(selectAmazonEANUseable);
		list.add(selectAmazonISANUsed);
		list.add(selectAmazonISANUseable);
		list.add(selectUPCDead);
		list.add(selectEANDead);
		list.add(selectISBNDead);
		
		return list;
	}



	/**
	 * 查询一条数据
	 * @param status
	 * @param numbertype
	 * @return
	 */
	public List<String> selectObject(String numbertype,Integer number,String account,Integer usedplatform,Integer oneselfType) {
		return upcmanageMapper.selectObject(numbertype,number,account,usedplatform,oneselfType);
	}



	public void updateStatusNumbers(List numbers) {
		Integer status = 1;
		upcmanageMapper.updateStatusNumbers(status, numbers);
	}


	/**
	 * 刊登时upc码的状态修改
	 */
	public void updateUPCStatus(String number,Integer status,Integer usedplatform) {
		upcmanageMapper.updateUPCStatus(status, usedplatform, number);
	}


	/**
	 * 查询UPC使用平台信息
	 */
	public Integer selectUsedplatform(String number) {
		return upcmanageMapper.selectUsedplatform(number);
	}




	public Integer checkNumber(String number) {
		return upcmanageMapper.checkNumber(number);
	}



	public Integer selectEableCounts(String numberType, String account, Integer usedplatform,Integer oneselfType) {
		return upcmanageMapper.selectEableCounts(numberType, account, usedplatform,oneselfType);
	}




	@Override
	public List<String> checkNumberBatch(String account) {
		return upcmanageMapper.checkNumberBatch(account);
	}


}
