package com.rondaful.cloud.seller.service;

import com.rondaful.cloud.seller.entity.*;

import java.util.List;

public interface IAliexpressBaseService {

	/**
	 * 获取速卖通商品分类
	 * @param categoryParentId 分类父id
	 * @return
	 */
	public List<AliexpressCategory> getAliexpressCategoryByList(Long categoryParentId);
	/**
	 * 获取速卖通商品分类
	 * @param categoryId
	 * @return
	 */
	public AliexpressCategory getCategoryByCategoryId(Long categoryId);

	/**
	 * 获取速卖通商品分类
	 * @param categoryId 分类id
	 * @return
	 */
	public List<AliexpressCategoryAttribute> getAliexpressCategoryAttributeByCategoryIdList(Long categoryId,Long empowerId);


	/**
	 * 获取速卖通调价区域
	 *
	 * @return
	 */
	public List<AliexpressCountry> getAliexpressCountryByList();
	/**
	 * 获取速卖通运费模板
	 * @param plAccount 账号
	 * @return
	 */
	public List<AliexpressFreightTemplate> getAliexpressFreightTemplateByPlAccountList(Long empowerId,String plAccount,Long templateId);
	/**
	 * 获取速卖通服务模板
	 * @param plAccount 账号
	 * @return
	 */
	public List<AliexpressPromiseTemplate> getAliexpressPromiseTemplateByPlAccountList(Long empowerId,String plAccount,Long promiseTemplateId);


	/**
	 * 获取速卖通分组
	 * @param plAccount 账号
	 * @param groupId 分组id
	 * @return
	 */
	public List<AliexpressGroup> getAliexpressGroupByPlAccountList(String plAccount,Long groupId,Long empowerId);

	/**
	 * 递归获取分类的名称
	 * @param categoryId
	 * @param boolEn
	 * @return
	 */
	public String getCategoryName(Long categoryId,boolean boolEn);
}
