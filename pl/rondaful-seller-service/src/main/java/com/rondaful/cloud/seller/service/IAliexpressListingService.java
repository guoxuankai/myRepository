package com.rondaful.cloud.seller.service;

import java.math.BigDecimal;

public interface IAliexpressListingService {

    /**
     * 商品刊登保存修改数据
     * @param empowerId
     * @param userId
     * @param userName
     * @param sellerId
     * @param id
     * @param itemId
     */
    public void updateAliexpressListing(Long empowerId, Long userId, String userName, Long sellerId, Long id, Long itemId, BigDecimal productMinPrice, BigDecimal productMaxPrice);

    /**
     * 同步全部商品数据
     * @param empowerId
     * @param sellerId
     * @return
     */
    public Long syncAliexpressPListingProductStatus(Long empowerId,Long sellerId,Long userId,String userName);
}
