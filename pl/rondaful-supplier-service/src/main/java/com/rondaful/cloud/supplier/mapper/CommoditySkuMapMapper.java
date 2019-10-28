package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.inventory.CommoditySkuMap;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommoditySkuMapMapper extends BaseMapper<CommoditySkuMap> {

    /**
     * 批量插入sku映射
     * @param list
     * @return
     */
    Integer insertBatch(@Param("list") List<CommoditySkuMap> list);

    /**
     * 获取平台sku
     * @param tableName
     * @return
     */
    List<String> getsSupplierSku(@Param("tableName") String tableName);

    /**
     * 供应商sku
     * @param supplierSku
     * @return
     */
    String getBySSku(@Param("supplierSku") String supplierSku);
}