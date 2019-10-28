package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.inventory.Inventory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InventoryMapper extends BaseMapper<Inventory> {


    /**
     * 批量插入
     * @param list
     * @return
     */
    Integer insertBatch(@Param("list") List<Inventory> list);

    /**
     * 库存分页查询
     * @param warehouseId
     * @param pinlianSku
     * @param supplierSku
     * @param status
     * @return
     */
    List<Inventory> getsPage(@Param("warehouseIds") List<Integer> warehouseIds,@Param("pinlianSkus") List<String> pinlianSkus,@Param("status") Integer status,@Param("supplierIds") List<Integer> supplierIds,@Param("sellerId") Integer sellerId);

    /**
     * 根据品连sku查询指定仓库的库存信息
     * @param warehouseId
     * @param pinlianSku
     * @return
     */
    Inventory getByWPinlianSku(@Param("warehouseId") Integer warehouseId,@Param("pinlianSku") String pinlianSku);

    /**
     * 根据品连sku修改属性
     * @param inventory
     * @return
     */
    Integer updateByPinlianSku(Inventory inventory);

    /**
     * 修改预警值
     * @param warehouseId
     * @param pinlianSku
     * @param warnVal
     * @param updateBy
     * @return
     */
    Integer updateWarnVal(@Param("warehouseId") Integer warehouseId,@Param("pinlianSkus") List<String> pinlianSkus,
                          @Param("warnVal") Integer warnVal,@Param("updateBy") String updateBy);

    /**
     * 根据仓库列表sku及仓库列表获取有库存的
     * @param warehouseIds
     * @param skus
     * @return
     */
    List<Inventory> getsWIdSku(@Param("warehouseIds") List<Integer> warehouseIds,@Param("skus") List<String> skus);

    /**
     * 根据sku查询库存
     * @param warehouseIds
     * @param skus
     * @return
     */
    List<Inventory> getBySku(@Param("warehouseIds") List<Integer> warehouseIds,@Param("skus") List<String> skus);

    /**
     * 根据仓库id及sku列表获取
     * @param warehouseId
     * @param skus
     * @return
     */
    List<Inventory> getsBySku(@Param("warehouseId") Integer warehouseId,@Param("skus") List<String> skus);

    /**
     * 获取指定仓库下有库存的商品总数
     * @param warehouseId
     * @return
     */
    Integer getCount(@Param("warehouseId") Integer warehouseId);

    /**
     * 修改本地待出库
     * @param warehouseId
     * @param pinlianSku
     * @param qty
     * @return
     */
    Integer updateLocalShipping(@Param("warehouseId") Integer warehouseId,@Param("pinlianSku") String pinlianSku,@Param("qty") Integer qty);

    /**
     * 修改绑定的卖家
     * @param sellerId
     * @param warehouseId
     * @return
     */
    Integer updateSellerId(@Param("sellerId") String sellerId,@Param("warehouseId") Integer warehouseId,@Param("pinlianSku") String pinlianSku);

    /**
     * 查询首页数据统计
     * @param warehouseIds
     * @param supplierId
     * @param type
     * @return
     */
    Integer getAppCount(@Param("warehouseIds") List<Integer> warehouseIds,@Param("supplierId") Integer supplierId,@Param("type") Integer type);

}