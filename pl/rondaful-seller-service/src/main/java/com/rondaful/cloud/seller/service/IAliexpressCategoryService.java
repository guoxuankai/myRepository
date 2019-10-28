package com.rondaful.cloud.seller.service;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.seller.dto.AliexpressPublishListingDTO;
import com.rondaful.cloud.seller.entity.*;
import com.rondaful.cloud.seller.entity.amazon.AmazonCategory;
import com.rondaful.cloud.seller.vo.AliexpressPublishListingSearchVO;

import java.util.List;

public interface IAliexpressCategoryService {
	/**
	 * 同步分类
	 * @param jsonStr
	 * @param categoryParentId
	 * @return
	 */
	public String insertAliexpressCategory(String jsonStr,Long categoryParentId);

	/**
	 * 修改尺寸是否必填
	 * @param aliexpressCategory
	 * @return
	 */
	public int updateByPrimaryKeySelective(AliexpressCategory aliexpressCategory);
	/**
	 * 同步分类属性
	 * @param jsonStr
	 * @param categoryId
	 * @return
	 */
	public List<AliexpressCategoryAttribute> insertAliexpressCategoryAttribute(String jsonStr,Long categoryId,Long empowerId);

	/**
	 * 同步当前刊登账号分类的品牌
	 * @param jsonStr
	 * @param categoryId
	 * @return
	 */
	public List<AliexpressCategoryAttributeSelect> insertAliexpressCategoryAttributeBrand(String jsonStr, Long categoryId, Long empowerId, Long createId);

	/**
	 * 同步服务模板表
	 * @param jsonStr
	 * @param empowerId
	 * @return
	 */
	public List<AliexpressPromiseTemplate> insertAliexpressPromiseTemplate(String jsonStr, Long empowerId, String plAccount);

	/**
	 * 同步分组
	 * @param jsonStr
	 * @param empowerId
	 * @return
	 */
	public List<AliexpressGroup> insertAliexpressGroup(String jsonStr,Long empowerId,String plAccount);
	/**
	 * 同步运费模板
	 * @param jsonStr
	 * @param empowerId
	 * @return
	 */
	public List<AliexpressFreightTemplate> insertAliexpressFreightTemplate(String jsonStr, Long empowerId, String plAccount);

	/**
	 * 根据账号获取品牌
	 * @param attributeId
	 * @param empowerId
	 * @return
	 */
	public List<AliexpressCategoryAttributeSelect> getCategoryAttributeSelectByList(Long categoryId,Long attributeId, Long empowerId);

	/**
	 * 属性选择值查询()
	 * @param currentPage
	 * @param pageSize
	 * @param attributeId
	 * @param selectName
	 * @return
	 * @throws Exception
	 */
	Page<AliexpressCategoryAttributeSelect> findSelectPage(Integer currentPage, Integer pageSize,Long attributeId,String selectName) throws Exception;


	AliexpressCategory getCategoryByCategoryId(Long categoryId);
}
