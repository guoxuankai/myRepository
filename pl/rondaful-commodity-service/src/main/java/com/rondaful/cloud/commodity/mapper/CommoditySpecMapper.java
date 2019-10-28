package com.rondaful.cloud.commodity.mapper;

import com.rondaful.cloud.commodity.entity.CommoditySpec;
import com.rondaful.cloud.commodity.vo.ApiSkuResponse;
import com.rondaful.cloud.commodity.vo.CodeAndValueVo;
import com.rondaful.cloud.common.mapper.BaseMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface CommoditySpecMapper extends BaseMapper<CommoditySpec> {
    List selectCommoditySpecByCommodityId(Map map);
    
    List selectCommoditySpecBySku(@Param("list")List<String> list);
    
    void deleteCommoditySpecWhereNoUpByCommodityId(Long id);
    List getSystemSkuByUserSku(List<String> list);
    List getSystemSkuBySystemSku(List<String> list);
    List selectSkuList(Map map);
    
    Long statistics(Map<String, Object> param);
    
    int getSpecCount(@Param("commodityId")Long commodityId,@Param("id")Long id,@Param("state")Integer state);
    
    /**
     * @Description:根据供应商sku查询
     * @param supplierId
     * @param supplierSku
     * @return CommoditySpec
     * @author:范津
     */
    CommoditySpec getSkuBySupplierIdAndSku(@Param("supplierId")Long supplierId,@Param("supplierSku")String supplierSku);
    
    /**
     * @Description:根据商品ID获取最大sku值
     * @param commodityId
     * @return String
     * @author:范津
     */
    String getMaxSystemSkuByCommodityId(@Param("commodityId")Long commodityId);
    
    /**
     * @Description:根据商品ID和商品规格属性查询个数
     * @param commodityId
     * @param commoditySpec
     * @return int
     * @author:范津
     */
    int getSkuByCommodityIdAndSpec(@Param("commodityId")Long commodityId,@Param("commoditySpec")String commoditySpec);
    
    /**
	 * @Description:获取指定账号未推送过的sku
	 * @param account
	 * @return
	 * @author:范津
	 */
    List<CommoditySpec> getUnPushSystemSku(Map<String, Object> skuMap);
    
    int getUnPushSystemSkuNum(Map<String, Object> skuMap);
    
    String getSupplierSkuBySystemSku(@Param("systemSku")String systemSku);
    
    /**
     * @Description:获取供应商已上架的sku数
     * @param supplierId
     * @param state
     * @return
     * @author:范津
     */
    int getSkuNumBySupplierId(@Param("supplierId")Long supplierId,@Param("state")Integer state);
    
    
    List<CommoditySpec> getAllSkuBySupplierId(Map<String, Object> param);
    
    int getAllSkuCountBySupplierId(Map<String, Object> param);
    
    
    String getMaxSystemSkuBySpu(@Param("SPU")String SPU);
    
    List getSkuListByPage(Map map);
    
    
    List<ApiSkuResponse> selectApiSku(Map<String, Object> map);
    
    int getUnAuditNum();
    
    /**
     * @Description:获取有仓库价格的
     * @return
     * @author:范津
     */
    List<CommoditySpec> selectAllWarehousePrice();
    
    int getErpSkuNum(@Param("supplierSku")String supplierSku);
}