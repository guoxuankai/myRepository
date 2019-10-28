package com.rondaful.cloud.commodity.service;

import com.rondaful.cloud.commodity.entity.CommodityBase;
import com.rondaful.cloud.commodity.entity.CommodityDetails;
import com.rondaful.cloud.commodity.entity.CommoditySpec;
import com.rondaful.cloud.commodity.entity.SiteCategory;
import com.rondaful.cloud.commodity.entity.SpuCategory;
import com.rondaful.cloud.commodity.vo.CodeAndValueVo;
import com.rondaful.cloud.commodity.vo.QuerySkuBelongSellerVo;
import com.rondaful.cloud.common.entity.Page;

import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.document.StringField;

import java.util.List;
import java.util.Map;

public interface ICommodityService {
    void addCommodity(CommodityBase commodityBase, CommodityDetails commodityDetails, List<CommoditySpec> commoditySpec);
    void updateCommodity(CommodityBase commodityBase, CommodityDetails commodityDetails, List<CommoditySpec> commoditySpec);
    Map detailsCommodity(CommodityBase commodityBase);
    //void updateForPrice(String id, List<CommoditySpec> commoditySpec);
    Page selectCommodityListBySpec(Map map);
    List selectBySPUS(List<String> list);
    List selectBySKUS(QuerySkuBelongSellerVo vo);
    void deleteCommodity(Long id, String type);
    void focusCommodity(List<String> list, Long id);
    void cancelFocusCommodity(List<String> list, Long id);
    void constructionSupplierForSpec(List<CommoditySpec> commoditySpecs);
    void inventoryForDetail(List<CommoditySpec> commoditySpecs);
    Page selectSkuList(Map map);
    Page selectCommodityListBySpecBatch(Map map);
    
    String download(List<Long> list, HttpServletResponse response) throws Exception;
    
    void updateAuditSku(List<String> ids, String audit, String auditDesc);
    
    /**
    * @Description: 新增或更新SPU分类映射
    * @param spu
    * @param platform 平台名称
    * @param siteCode 站点编码
    * @param platCategoryId 平台商品分类ID
    * @param categoryPath 分类路径
    * @return void
    * @author:范津
     */
    void saveOrUpdateSpuCategory(String spu,String platform,String siteCode,Long platCategoryId,String categoryPath);
    
    /**
     * @Description:查询SPU的分类映射
     * @param spu
     * @param platform
     * @param siteCode
     * @return SiteCategory
     * @author:范津
     */
    SiteCategory querySpuSiteCategory(String spu,String platform,String siteCode);
    
    /**
     * @Description:根据spu查询spu分类映射
     * @param spu
     * @return List
     * @author:范津
     */
    List<SpuCategory> querySpuCategoryList(String spu);
    
    /**
	 * @Description:查询平台的站点分类映射
	 * @param platform
	 * @param categoryLevel3 品连商品分类ID
	 * @return List<SiteCategory>
	 * @author:范津
	 */
    List<SiteCategory> querySiteCategoryList(String platform,Long categoryLevel3);
    
    /**
	 * @Description:清除站点的分类信息
	 * @param platform
	 * @return void
	 * @author:范津
	 */
    void cleanUp(String platform,Long categoryLevel3);
    
    /**
     * @Description:更新站点分类信息
     * @param
     * @return void
     * @author:范津
     */
    void updateSiteCategory(List<SiteCategory> siteCategoryList);
    
    /**
     * @Description:复制商品
     * @param commondityId
     * @return Long 返回复制后的商品ID
     * @author:范津
     */
    Long copyCommodity(Long commondityId);
    
    /**
     * @Description:商品上下架
     * @param ids
     * @param type
     * @return void
     * @author:范津
     */
    void upperAndLowerFrames(List<String> ids,Boolean type);
    
    /**
     * @Description:初始化ES文档
     * @param map
     * @return void
     * @author:范津
     */
    void initCommodityIndex(Map<String,Object> map);
    
    
    /**
     * @Description:根据系统sku或者供应商sku查询
     * @param sku
     * @return CommoditySpec
     * @author:范津
     */
    CommoditySpec getCommoditySpecBySku(String sku,Integer platform,String siteCode);
    
    /**
     * @Description:更新sku已售数量or刊登数
     * @param
     * @return void
     * @author:范津
     */
    void updateSpecNum(List<CodeAndValueVo> data,int type);

    /**
     * @Description:分页查询sku
     * @param map
     * @return
     * @author:范津
     */
    Page getSkuListByPage(Map map);
    
    /**
     * @Description:所有商品刊登包
     * @return void
     * @author:范津
     */
    void getAllPublishPack(Map<String,Object> param);
    
    /**
     * @Description:仓库服务定制，一个供应商sku对应多个品连sku的业务
     * @param supplierSku
     * @return
     * @author:范津
     */
    List<CommoditySpec> getSkuListBySupplierSku(String supplierSku);
}
