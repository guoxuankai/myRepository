package com.rondaful.cloud.order.service;

import java.util.List;

public interface IAmazonEmpowerService {
    /*根据sellerID查询亚马逊授权token*/
    List<String> selectMWSTokenBySellerId(String sellerId, String marketplaceId);
}
