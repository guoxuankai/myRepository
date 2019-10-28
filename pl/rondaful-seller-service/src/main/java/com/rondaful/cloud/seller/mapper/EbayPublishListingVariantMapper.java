package com.rondaful.cloud.seller.mapper;

import java.util.List;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AliexpressPublishListingProduct;
import com.rondaful.cloud.seller.entity.EbayPublishListingVariant;
import org.apache.ibatis.annotations.Param;

public interface EbayPublishListingVariantMapper extends BaseMapper<EbayPublishListingVariant> {
	
	void deleteByValue(Integer lingId);
	
	void insertBatchList(List<EbayPublishListingVariant> recordList);

	public List<EbayPublishListingVariant> getEbayPublishListingVariantByListingId(@Param("listingId")Long listingId);
	/**
	 * 验证平台sku是否重复
	 * @param list
	 * @return
	 */
	public List<EbayPublishListingVariant> getVariantByPlatformSku(@Param("platformSku") String platformSku, @Param("listingId")Long listingId);

	/**
	 * 订单要获取平台sku的 图片
	 * @param itemId
	 * @param platformSku
	 * @return
	 */
	public List<EbayPublishListingVariant> getListingVariantByItemIdPlatformSku(@Param("itemId") String itemId,@Param("platformSku") String platformSku);

}