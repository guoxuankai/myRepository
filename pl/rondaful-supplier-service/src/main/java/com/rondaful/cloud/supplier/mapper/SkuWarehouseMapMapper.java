package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.inventory.SkuWarehouseMap;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SkuWarehouseMapMapper extends BaseMapper<SkuWarehouseMap> {

    /**
     * 批量插入关联关系
     * @param list
     * @return
     */
    Integer insertBatch(@Param("list") List<SkuWarehouseMap> list);

    /**
     * 根据乒联sku获取仓库列表
     * @param pinlianSku
     * @return
     */
    List<Integer> getsByPinsku(@Param("pinlianSku") List<String> pinlianSku);

    /**
     * 根据乒联sku获取仓库列表对象
     * @param pinlianSku
     * @return
     */
    List<SkuWarehouseMap> getCombineSku(@Param("pinlianSku") List<String> pinlianSku);

    /**
     * 获取sku列表
     * @param tableName
     * @return
     */
    List<String> getsSku(@Param("tableName") String tableName);
}