package com.rondaful.cloud.order.service;

import com.rondaful.cloud.common.service.BaseService;
import com.rondaful.cloud.order.entity.aliexpress.AliexpressOrderChild;
import com.rondaful.cloud.order.entity.orderRule.SKUMapMailRuleDTO;
import com.rondaful.cloud.order.entity.orderRule.SellerSkuMap;
import com.rondaful.cloud.order.entity.SysOrder;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public interface ISellerSkuMapService extends BaseService<SellerSkuMap> {

    /**
     * 品连插入sku映射
     * @param maps 插入的sku数据
     * @return 重复的平台sku列表
     */
    HashMap<String, List<SellerSkuMap>> inserts(List<SellerSkuMap> maps);

    /**
     * 品连插入sku映射(遇错返回)
     * @param maps 插入的sku数据
     * @return 重复的平台sku列表
     */
     List<SellerSkuMap> insertsNotError(List<SellerSkuMap> maps);

    /**
     * 通过品连sku将相关映射停用
     *
     * @param plSku 品连sku
     */
    void discardMap(String plSku);

    /**
     * 通过 map对象查询map对象
     *
     * @param map map对象
     * @return map对象
     */
    SellerSkuMap selectByEntry(SellerSkuMap map);


    Integer getPlatform(String platform);

    /**
     * 不分页查询所有的
     * @param model map对象
     * @return maplist
     */
    List<SellerSkuMap> findAll(SellerSkuMap model);

    /**
     * 平台SKU查询品连SKU
     * @param platform
     * @param authorizationId
     * @param platformSku
     * @param sellerId
     * @return
     */
    SellerSkuMap getSellerSkuMapByPlatformSku(String platform, String authorizationId, String platformSku, String sellerId);

    /**
     * @Description:根据品连sku删除映射
     * @param plSku
     * @return void
     * @author:范津
     */
    void deleteByPlSku(String plSku);
}
