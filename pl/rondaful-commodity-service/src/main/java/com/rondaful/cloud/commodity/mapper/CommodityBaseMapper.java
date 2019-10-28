package com.rondaful.cloud.commodity.mapper;

import com.rondaful.cloud.commodity.entity.CommodityBase;
import com.rondaful.cloud.commodity.vo.ApiSpuResponse;
import com.rondaful.cloud.common.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CommodityBaseMapper extends BaseMapper<CommodityBase> {
    CommodityBase selectCommodityDetailsById(Long id);
    List<CommodityBase> selectCommodityListBySpec(Map map);
    List<CommodityBase> selectBySPUS(List<String> list);
    void deleteCommodity(Long id, String type);
    List<CommodityBase> selectBySupplierAndSku(@Param("supplier_id")Long supplier_id, @Param("supplier_sku")String supplier_sku);
    
    List<CommodityBase> queryBySupplierAndSku(@Param("supplierId")Long supplierId, @Param("supplierSku")String supplierSku);
    
    List<CommodityBase> selectBySupplierAndSpu(@Param("supplierId")Long supplierId, @Param("supplierSpu")String supplierSpu);
    
    int selectExportCount(Map<String,Object> map);
    
    List<ApiSpuResponse> selectApiSpu(Map<String,Object> map);
    
    List<CommodityBase> selectListForEs(Map map);
}