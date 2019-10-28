package com.rondaful.cloud.seller.remote;


import com.rondaful.cloud.seller.entity.SellerSkuMap;
import com.rondaful.cloud.seller.vo.CodeAndValueVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;


@FeignClient(name = "rondaful-commodity-service", fallback = RemoteCommodityService.RemoteCommodityServiceImpl.class)
public interface RemoteCommodityService {

    /**
     * 查询商品分类列表
     *
     * @param page 页码
     * @param row  叶容量
     * @return 返回数据
     */
    @GetMapping("/operate/category/list")
    String listCategory(@RequestParam("page") String page, @RequestParam("row") String row);

    /**
     * 查询商品列表
     *
     * @param page             页码
     * @param row              页容量
     * @param category_level_1 一级分类id
     * @param category_level_2 二级分类id
     * @param category_level_3 三级分类id
     * @param startTime        创建开始时间
     * @param endTime          创建结束时间
     * @param autiState        商品状态
     * @param commodityName    商品名称
     * @param systemSku        系统sku（品连）
     * @param SPU              系统spu
     * @return 返回数据
     */
    @GetMapping("/operate/commodity/list/manager")
    String managerCommodity(@RequestParam("page") String page, @RequestParam("row") String row, @RequestParam("category_level_1") Long category_level_1,
                            @RequestParam("category_level_2") Long category_level_2, @RequestParam("category_level_3") Long category_level_3, @RequestParam("startTime") String startTime,
                            @RequestParam("endTime") String endTime, @RequestParam("autiState") Integer autiState, @RequestParam("commodityName") String commodityName,
                            @RequestParam("isUp") Boolean isUp, @RequestParam("systemSku") String systemSku, @RequestParam("SPU") String SPU,@RequestParam("supplierId") Long supplierId,
                            @RequestParam("vendibilityPlatform") String vendibilityPlatform);

    /**
     * 根据系统sku码或者供应商sku码查询sku
     * @param sku sku
     * @param platform 平台，1：eBay，2：Amazon，3：wish，4：AliExpress
     * @param siteCode  查侵权时传，站点编码
     * @return duixiang
     */
    @GetMapping("/micro/commodity/getCommoditySpecBySku")
    String getCommoditySpecBySku(@RequestParam("sku")String sku,@RequestParam("platform")Integer platform,@RequestParam("siteCode")String siteCode);


    /**
     * 根据系统sku码或者供应商sku码查询sku
     * @param sku sku
     * @return duixiang
     */
    @GetMapping("/micro/commodity/getCommoditySpecBySku")
    String getCommoditySpecBySku(@RequestParam("sku")String sku);

    /**
     * @param page             页码
     * @param row              页容量
     * @param category_level_1 一级分类
     * @param category_level_2 二级分类
     * @param category_level_3 三级分类
     * @param commodityName    商品名称
     * @param systemSku        系统sku
     * @param supplierSku      供应商sku
     * @param SPU              系统spu
     * @return 返回参数
     */
    @GetMapping("/micro/commodity/getSkuList")
    String getSkuList(@RequestParam("page") String page, @RequestParam("row") String row, @RequestParam("category_level_1") Long category_level_1,
                      @RequestParam("category_level_2") Long category_level_2, @RequestParam("category_level_3") Long category_level_3, @RequestParam("commodityName") String commodityName,
                      @RequestParam("systemSku") String systemSku, @RequestParam("supplierSku") String supplierSku, @RequestParam("SPU") String SPU,@RequestParam("vendibilityPlatform") String vendibilityPlatform);

    /**
     * 通过条件查询分类映射表信息
     *
     * @param spu      spu
     * @param platform 平台名称 Amazon ebay
     * @param siteCode 站点编码 站点编码
     * @return 返回值
     */
    @GetMapping("/micro/commodity/querySpuSiteCategory")
    String querySpuSiteCategory(@RequestParam("spu") String spu,
                                @RequestParam("platform") String platform,
                                @RequestParam("siteCode") String siteCode);

    /**
     * 新增或更新SPU分类映射
     *
     * @param spu            spu值
     * @param platform       平台名称(Amazon ebay)
     * @param siteCode       站点编码
     * @param platCategoryId 平台商品分类ID
     * @param categoryPath   分类路径
     * @return 返回值
     */
    @PostMapping("micro/commodity/saveOrUpdateSpuCategory")
    String saveOrUpdateSpuCategory(@RequestParam("spu") String spu,
                                   @RequestParam("platform") String platform,
                                   @RequestParam("siteCode") String siteCode,
                                   @RequestParam("platCategoryId") Long platCategoryId,
                                   @RequestParam("categoryPath") String categoryPath);

    /**
     * 根据spu查询商品信息
     * @param spus
     * @return
     */
    @PostMapping("micro/commodity/getCommodityListBySPU")
    String getCommodityForSPU(@RequestBody List<String> spus);

    /**
     * 根据品连sku获取信息
     * @param skus品连sku多个用,分割
     * @return
     */
    @PostMapping("commodity/micro/commodity/getSystemListSkuBySystemSku")
    String getSystemListSkuBySystemSku(@RequestParam("skus") String[] skus);


    /**
     * 更新sku刊登数量
     * @param spus
     * @return
     */
    @PostMapping("micro/commodity/updateSkuPublishNum")
    String updateSkuPublishNum(@RequestBody List<CodeAndValueVo> data);

    /**
     *
     * @param platform 平台 aliexpress，eBay，Amazon，with
     * @param authorizationId 授权id
     * @param platformSku 平台sku
     * @return
     */
    @GetMapping("micro/commodity/getSkuMapByPlatformSku")
    String getSkuMapByPlatformSku(@RequestParam("platform") String platform,
                                        @RequestParam("authorizationId")String authorizationId, @RequestParam("platformSku")String platformSku);

    /**
     * 批量新增平台sku映射
     * @param voList sku映射列表
     */
    @PostMapping("micro/commodity/addSkuMap")
    String addSkuMap(@RequestBody List<SellerSkuMap> voList);

    @Service
    class RemoteCommodityServiceImpl implements RemoteCommodityService {


        @Override
        public String listCategory(String page, String row) {
            return null;
        }

        @Override
        public String managerCommodity(String page, String row, Long category_level_1, Long category_level_2,
                                       Long category_level_3, String startTime, String endTime, Integer autiState,
                                       String commodityName, Boolean isUp, String SKU, String SPU,Long supplierId,String vendibilityPlatform) {
            return null;
        }

        @Override
        public String getCommoditySpecBySku(String sku, Integer platform, String siteCode) {
            return null;
        }

        @Override
        public String getCommoditySpecBySku(String sku) {
            return null;
        }

        @Override
        public String getSkuList(String page, String row, Long category_level_1, Long category_level_2, Long category_level_3,
                                 String commodityName, String systemSku, String supplierSku, String SPU,String vendibilityPlatform) {
            return null;
        }

        @Override
        public String querySpuSiteCategory(String spuId, String platform, String siteCode) {
            return null;
        }

        @Override
        public String saveOrUpdateSpuCategory(String spu, String platform, String siteCode, Long platCategoryId,String categoryPath) {
            return null;
        }

        @Override
        public String getCommodityForSPU(List<String> spus) {
            return null;
        }

		@Override
		public String getSystemListSkuBySystemSku(String[] skus) {
			return null;
		}

        @Override
        public String updateSkuPublishNum(List<CodeAndValueVo> data) {
            return null;
        }

        @Override
        public String getSkuMapByPlatformSku(String platform, String authorizationId, String platformSku) {
            return null;
        }

        @Override
        public String addSkuMap(List<SellerSkuMap> voList) {
            return null;
        }
    }
}
