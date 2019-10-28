package com.rondaful.cloud.order.service;

import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.SysOrderDetail;
import com.rondaful.cloud.order.entity.orderRule.SellerSkuMap;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDTO;

import java.util.List;

public interface ISkuMapService {

    /**
     * 订单转入映射批处理
     * @param platform  卖家所属平台名称[amazon, eBay, wish, aliexpress]
     * @param orders 订单列表
     * @return 订单列表
     */
    List<SysOrder> orderMapByOrderList(String platform, List<SysOrder> orders);

    /**
     * 订单转入映射批处理
     * @param platform 卖家所属平台名称[amazon, eBay, wish, aliexpress]
     * @param sysOrderDTOList 待转化的订单列表
     * @return {@link List<SysOrderDTO>}
     */
    List<SysOrderDTO> orderMapByOrderListNew(String platform, List<SysOrderDTO> sysOrderDTOList);

    /**
     * 根据授权ID和平台SKU查询品连SKU
     *
     * @param platform        卖家所属平台名称[amazon, eBay, wish, aliexpress,other]
     * @param authorizationId 授权id
     * @param platformSku     卖家平台商品sku
     * @return
     */
    String queryPlSku(String platform, String authorizationId, String platformSku, String sellerId);

    /**
     * 调用授权服务是转换平台格式
     *
     * @param platform 平台字符串格式
     * @return 平台Integer格式
     */
    Integer getPlatform(String platform);

    /**
     * 平台SKU查询品连SKU
     *
     * @param platform
     * @param authorizationId
     * @param platformSku
     * @param sellerId
     * @return
     */
    SellerSkuMap getSellerSkuMapByPlatformSku(String platform, String authorizationId, String platformSku, String sellerId);

    SellerSkuMap selectByEntry(SellerSkuMap map);

    /**
     * 通知采购系统-新增采购单
     *
     * @param sysOrderDetails       系统订单sku列表
     * @param deliveryWarehouseCode 发货仓库
     */
    void notifyPurchasingSystem(List<SysOrderDetail> sysOrderDetails, String deliveryWarehouseCode);


}
