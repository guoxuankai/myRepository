package com.rondaful.cloud.seller.service;

import com.ebay.soap.eBLBaseComponents.ItemType;
import com.rondaful.cloud.seller.entity.EbayPublishListingNew;

public interface IEbayListingService {
	/**
	 * 批量同步listing
	 * @param empowerId
	 * @param userId
	 * @param userName
	 * @param sellerId
	 */
	public Integer getEbayListingList(Long empowerId, Long userId, String userName, String sellerId);

	/**
	 * 商品刊登保存修改数据
	 * @param empowerId
	 * @param userId
	 * @param userName
	 * @param sellerId
	 * @param id
	 * @param itemId
	 */
	public void saveEbayListing(Long empowerId, Long userId, String userName, String sellerId, Long id, String itemId);


	public void insertEbayPublishListing(ItemType item, Long userId, String userName, String sellerId, Long empowerId) throws Exception;

	public void updateEbayPublishListing(ItemType item, Long userId, String userName, String sellerId, Long empowerId, EbayPublishListingNew queryListing) throws Exception;

	/**
	 * 根据sku获取spu
	 * @param plSku
	 * @return
	 */
	public String getPlSpu(String plSku);
}
