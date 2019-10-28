package com.rondaful.cloud.seller.service;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.seller.dto.EbayHistoryDTO;
import com.rondaful.cloud.seller.dto.EbayPublishListingAPPDTO;
import com.rondaful.cloud.seller.dto.EbayPublishListingDTO;
import com.rondaful.cloud.seller.entity.EbayPublishListingError;
import com.rondaful.cloud.seller.entity.EbayPublishListingNew;
import com.rondaful.cloud.seller.entity.EbayPublishListingVariant;
import com.rondaful.cloud.seller.vo.*;

import java.util.List;
import java.util.Map;

public interface IEbayPublishListingService {
	
	/**
	 * 数据刊登
	 * @param vo
	 */
	Long insertPublishListing(PublishListingVO vo) throws Exception;

	int updateByPrimaryKeySelective(EbayPublishListingNew ebayPublishListingNew);
	/**
	 * 列表信息
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	Page<EbayPublishListingDTO> find(PublishListingSearchVO vo) throws Exception;


	/**
	 * 列刊登spu历史数据
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	Page<EbayHistoryDTO> getEbayHistoryPage(PublishListingSearchVO vo) throws Exception;
	
	/**
	 * 重新刊登
	 * @param listingId
	 * @throws Exception
	 */
	void relistItem(Integer listingId) throws Exception;
	
	/**
	 * 编辑刊登
	 * @param vo
	 * @throws Exception
	 */
	void updateListing(PublishListingVO vo) throws Exception;
	
	/**
	 * 备注编辑
	 * @param vo
	 * @throws Exception
	 */
	void updateListingRemarks(Integer id,String remarks) throws Exception;
	
	
	/**
	 * 刊登查看
	 * @param listingId
	 * @return
	 * @throws Exception
	 */
	PublishListingVO findListingById(Integer listingId) throws Exception;
	
	/**
	 * 刊登删除
	 * @param listingId
	 * @throws Exception
	 */
	void delListing(Integer listingId) throws Exception;
	
	/**
	 * 刊登复制
	 * @param listingId
	 * @throws Exception
	 */
	void insertListingCopy(Integer listingId) throws Exception;
	
	/**
	 * 日志查看
	 * @param listingId
	 * @return
	 * @throws Exception
	 */
	List<EbayPublishListingError> listingErrorView(Integer listingId) throws Exception;
	
	/**
	 * 费用检测 (单属性刊登,多属性刊登)
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	Double verify(PublishListingVO vo)throws Exception;
	
	/**
	 * 费用检测 (重刊登)
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	Double verifyRelist(Integer listingId)throws Exception;
	
	/**
	 * app
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	Page<EbayPublishListingAPPDTO> findToAppByPage(PublishListingAppSearchVO vo) throws Exception;
	
	int getOnlineCount(Long sellerId) throws Exception;

	/**
	 * 更加平台sku 和账号获取刊登最大发货时间
	 * @param vo
	 * @return
	 */
	Map<String, Object> getDispatchTimeMax(EbayMaxTimeVO vo);

	/**
	 * ebay刊登数量统计
	 * @return
	 */
	List<Map<String, Object>> getEbaySkuNumber();

	EbayPublishListingNew getListingById(Integer listingId);

	/**
	 * 查询状态下的数据
	 * @param status
	 * @return
	 */
	List<Long> getByStatusTask(Integer status);

	/**
	 * 异常
	 * @param error
	 */
	void insertListingError(EbayPublishListingError error);

	/**
	 *商品库存状态查询
	 * @param warehouseId
	 * @param sku
	 * @param platform 查侵权时传，平台，1：eBay，2：Amazon，3：wish，4：AliExpress
	 * @return
	 */
	CommodityStatusVO getCommodityStatusVOBySku(String warehouseId, String sku,Integer platform,String siteCode);

	/**
	 * 获取平台sku对应的仓库id发货类型
	 * @param site
	 * @param platformSkus
	 * @return
	 */
	List<ResultPublishListingVO> getEbayResultPublishListingVO(Integer empowerId,List<String> platformSkus);

	/**
	 *当前卖家的用户名称
	 * @param userId
	 * @return
	 */
	Map<Long,String> getUsers(List<Integer> userIds);

	/**
	 * 订单要获取平台sku的 图片
	 * @param itemId
	 * @param platformSku
	 * @return
	 */
	List<EbayPublishListingVariant> getListingVariantByItemIdPlatformSku(String itemId, String platformSku);

	public void setRemoteOrderRule(PublishListingVO vo);
}
