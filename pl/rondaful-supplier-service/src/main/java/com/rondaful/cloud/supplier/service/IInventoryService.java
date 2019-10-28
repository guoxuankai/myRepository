package com.rondaful.cloud.supplier.service;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.model.dto.KeyValueDTO;
import com.rondaful.cloud.supplier.model.dto.basics.WarehouseInitDTO;
import com.rondaful.cloud.supplier.model.dto.basics.WarehouseListDTO;
import com.rondaful.cloud.supplier.model.dto.inventory.*;

import java.util.List;
import java.util.Map;

/**
 * @Author: xqq
 * @Date: 2019/6/14
 * @Description: 库存明细
 */
public interface IInventoryService {

    /**
     * 库存明细初始化
     */
    void init();

    /**
     * 分页查询库存明细
     * @param dto
     * @return
     */
    PageDTO<InventoryDTO> getsPage(InventoryQueryDTO dto);

    /**
     * 根据品联sku查询指定仓库商品
     * @param warehouseId
     * @param pinlianSku
     * @return
     */
    InventoryDTO getByWPinlianSku(Integer warehouseId,String pinlianSku);

    /**
     *
     * @param warehouseId
     * @param pinlianSkus
     * @param warnVal
     * @return
     */
    Integer updateWarnVal(Integer warehouseId, List<String> pinlianSkus, Integer warnVal, String updateBy);

    /**
     * 根据sku列表获取仓库列表(必须在同一个仓库)
     * @param sku
     * @return
     */
    List<CombineSelectDTO> getCombineSku(List<String> sku, String languageType, JSONObject skuNum);

    /**
     * 根据sku列表获取仓库列表(有一个存在就返回,且库存大于0)
     * @param sku
     * @return
     */
    List<KeyValueDTO> getOrSku(List<String> sku,String languageType);

    /**
     * 根据sku列表及仓库id获取明细
     * @param warehouseId
     * @param skus
     * @return
     */
    List<InventoryDTO> getsBySku(Integer warehouseId,List<String> skus);

    /**
     * 根据sku获取库存明细
     * @param skus
     * @return
     */
    List<InventoryDTO> getBySku(List<String> skus);


    /**
     * 根据sku列表查询库存明细,且都在同一个仓库
     * @param skus
     * @return
     */
    List<OrderInvDTO> getsInvBySku(List<String> skus);

    /**
     * 谷仓库存监听
     * @param appToken
     * @param pinlianSku
     * @param warehouseCode
     */
    void updateGInventory(String appToken, String pinlianSku, String warehouseCode);

    /**
     * 根据sku拉去erp库存
     * @param sku
     */
    void change(String sku);

    /**
     * 获取所有有商品的仓库
     * @return
     */
    List<KeyValueDTO> getAll(String languageType);

    /**
     * 导出指定仓库数据
     * @param warehouseId
     * @param userId
     * @param currentPage
     * @param languageType
     * @return
     */
    List<Object> export(Integer warehouseId,Integer userId,Integer currentPage,String languageType);

    /**
     * 童虎谷仓  erp库存   @Async调用方与被动用方不能在同个类中
     * @param initDTO
     */
    void goodCangInit(WarehouseInitDTO initDTO);
    void erpInit(WarehouseListDTO dto);

    /**
     * wms 库存监听
     * @param appKey
     * @param sku
     * @param warehouseCode
     */
    void monitorWms(String appKey,String sku,String warehouseCode);

    /**
     * 修改本地待出货数量
     * @param warehouseId
     * @param pinlianSku
     * @param qty
     * @return
     */
    Integer updateLocalShipping(Integer warehouseId,String pinlianSku,Integer qty);

    /**
     * 更改sku绑定的卖家
     * @param skus
     * @return
     */
    void bindSeller(List<String> skus);

    /**
     * 初始化卖家绑定的关系
     */
    void initBindSeller();

    /**
     * 查询app的首页统计
     * @param supplierId
     * @param warehouseIds
     * @return
     */
    AppCountDTO getCount(Integer supplierId,List<Integer> warehouseIds);
}
