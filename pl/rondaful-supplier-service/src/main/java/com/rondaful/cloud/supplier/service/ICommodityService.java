package com.rondaful.cloud.supplier.service;

import com.rondaful.cloud.supplier.dto.PageDTO;
import com.rondaful.cloud.supplier.model.dto.inventory.CommodityDTO;
import com.rondaful.cloud.supplier.model.dto.inventory.InventoryDTO;
import com.rondaful.cloud.supplier.model.dto.inventory.InventoryQueryDTO;

/**
 * @Author: xqq
 * @Date: 2019/6/17
 * @Description:
 */
@Deprecated
public interface ICommodityService {

    public static String COMMODITY_BY_SUPPLIER_SKU="supplier.commodity.suppliersku.v0.";
    public static String COMMODITY_BY_PINLIAN_SKU="supplier.commodity.pinliansku.v0.";

    /**
     * 迁移商品服务数据
     */
    void init();

    /**
     * 分页获取平台sku
     * @param tableIndex
     * @param currentPage
     * @param pageSize
     * @return
     */
    PageDTO<String> getsSupplierSku(Integer tableIndex,Integer currentPage,Integer pageSize);

    /**
     * 根据平台sku查询商品信息
     * @param supplierSku
     * @return
     */
    CommodityDTO getsBySSku(String supplierSku);

    /**
     * 根据品连sku查询商品信息
     * @param pinlianSku
     * @return
     */
    CommodityDTO getsByPSku(String pinlianSku);



}
