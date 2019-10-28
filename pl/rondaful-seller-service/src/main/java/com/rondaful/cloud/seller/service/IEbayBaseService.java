package com.rondaful.cloud.seller.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.ebay.sdk.ApiContext;
import com.rondaful.cloud.seller.entity.EbayCountry;
import com.rondaful.cloud.seller.entity.EbayProductCategory;
import com.rondaful.cloud.seller.entity.EbaySite;
import com.rondaful.cloud.seller.entity.EbaySiteDetail;
import com.rondaful.cloud.seller.entity.ebay.store.Store;
import com.rondaful.cloud.seller.vo.PublishListingVO;

public interface IEbayBaseService {
	
	/**
	 * 获取站点
	 * @param site
	 * @return
	 */
	List<EbaySite> findSiteByValue(EbaySite site);
	
	/**
	 * 获取站点详情
	 * @param record
	 * @return
	 */
	List<EbaySiteDetail> findSiteDetail(EbaySiteDetail record);
	
	/**
	 * 获取国家
	 * @param country
	 * @return
	 */
	List<EbayCountry> findCountryByValue(EbayCountry country);
	
	/**
	 * 获取产品分类
	 * @param category
	 * @return
	 */
	List<EbayProductCategory> findCategoryByValue(EbayProductCategory category);
	
	/**
	 * 通过分类id 站点值加载数据
	 * @param categoryId
	 * @param site
	 * @return
	 */
	String loadCateory(String categoryId,String site);
	
	/**
	 * 通过categoryId 与站点名称。获取。该站点下的分类属性详细信息
	 * 首先判断数据库(缓存 组件中)是否有值 。没有则从ebay解析获取，在返回
	 * @param category
	 * @return
	 * @throws Exception
	 */
	Map<String,Object> getCategoryAttributeAndFeatures(EbayProductCategory category);
	
	/**
	 * 向ebay 刊登数据。 成功后返回itemId
	 * @param vo
	 * @return
	 */
	String addItem(PublishListingVO vo);
	
	/**
	 * 向ebay刊登多属性数据。 成功后返回itemId
	 * @param vo
	 * @return
	 */
	String addFixedPriceItem(PublishListingVO vo);
	
	/**
	 * 单属性重刊登
	 * @param vo
	 * @return
	 */
	String relistItem(PublishListingVO vo);
	
	/**
	 * 多属性重刊登
	 * @param vo
	 * @return
	 */
	String relistFixedPriceItem(PublishListingVO vo);
	
	/**
	 * ebay 产品下架
	 * @param listingId;
	 * @param itemId
	 * @throws Exception
	 */
	String endItem(PublishListingVO vo);
	
	
	/**
	 * ebay 多属性产品下架
	 * @param listingId
	 * @param itemId
	 * @return
	 */
	String endFixedPriceItem(PublishListingVO vo);
	
	/**
	 * ebay 单属性 拍卖类型数据修改
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	String reviseItem(PublishListingVO vo);
	
	/**
	 * 固价多属性数据修改
	 * @param vo
	 * @return
	 */
	String reviseFixedPriceItem(PublishListingVO vo);
	/**
	 * 费用检测 
	 * @param vo
	 * @return
	 */
	Double verifyAddItem(PublishListingVO vo);
	
	/**
	 * 多属性费用检测
	 * @param vo
	 * @return
	 */
	Double verifyAddFixedPriceItem(PublishListingVO vo);
	
	/**
	 * 重刊登费用检测
	 * @param vo
	 * @return
	 */
	Double verifyRelistItem(PublishListingVO vo);
	
	/**
	 * 获取ebay apiContext
	 * @return
	 */
	ApiContext  getApiContext(String empowerId) throws IOException;
	
	/**
	 * 从ebay获取数据
	 * @param itemId
	 * @return
	 */
	String getItem(String itemId);
	
	/**
	 * 从ebay上模糊匹配查找数据
	 * @param site
	 * @param query
	 * @return
	 */
	List<Map<String,String>> fuzzyQuery(String empowerId,String site,String title);
	
	/**
	 * 同步店铺数据  
	 * @param userId  ebay帐号的用户名
	 * @return
	 */
	Store getStore(String userId);

	void updateEbaySiteDetailshipping();
	
}
