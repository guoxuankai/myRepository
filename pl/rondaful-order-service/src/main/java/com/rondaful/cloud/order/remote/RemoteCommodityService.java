package com.rondaful.cloud.order.remote;


import com.rondaful.cloud.common.entity.Result;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.order.entity.QuerySkuMapForOrderVo;
import com.rondaful.cloud.order.entity.commodity.CodeAndValueVo;
import com.rondaful.cloud.order.model.dto.remoteCommodity.GetCommodityBySkuListDTO;
import net.sf.json.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(name = "rondaful-commodity-service", fallback = RemoteCommodityService.RemoteCommodityServiceImpl.class)
public interface RemoteCommodityService {
    /**
     * 订单获取平台sku映射
     * @param voList
     * @return
     */
    @PostMapping("micro/commodity/getSkuMapForOrder")
    String getSkuMapForOrder(@RequestBody List<QuerySkuMapForOrderVo> voList);

    @GetMapping("/micro/commodity/getSkuList")
    String test(@RequestParam("page") String page, @RequestParam("row")String row,@RequestParam("category_level_1") Long category_level_1,
                @RequestParam("category_level_2")Long category_level_2, @RequestParam("category_level_3")Long category_level_3,
                @RequestParam("commodityName")String commodityName, @RequestParam("systemSku")String systemSku, @RequestParam("supplierSku")String supplierSku,
                @RequestParam("SPU")String SPU);

    /**
     * 通过商品的品连sku查询商品列表
     * @param skus 商品的sku逗号分隔的字符串
     * @return 返回数据
     */
    @PostMapping("/micro/commodity/getCommodityListBySystemSKU")
    String getCommodityListBySystemSKU(@RequestBody List<String> skus);

    /**
     * 通过商品的品连SKU和品连卖家ID查询商品
     * @param getCommodityBySkuListDTO {@link GetCommodityBySkuListDTO}
     * @return String
     */
    @PostMapping("/micro/commodity/getCommodityBySkuList")
    String getCommodityBySkuList(@RequestBody GetCommodityBySkuListDTO getCommodityBySkuListDTO);

    /**
     *
     * @param page 页码
     * @param row 页容量
     * @param category_level_1 一级分类
     * @param category_level_2 二级分类
     * @param category_level_3 三级分类
     * @param commodityName 商品名称
     * @param systemSku 系统sku
     * @param supplierSku 供应商sku
     * @param SPU 系统spu
     * @return 返回参数
     */
    @GetMapping("/micro/commodity/getSkuList")
    String getSkuList(@RequestParam("page") String page,@RequestParam("row") String row, @RequestParam("category_level_1") Long category_level_1,
                      @RequestParam("category_level_2") Long category_level_2,@RequestParam("category_level_3") Long category_level_3, @RequestParam("commodityName") String commodityName,
                      @RequestParam("systemSku") String systemSku, @RequestParam("supplierSku") String supplierSku,@RequestParam("SPU") String SPU,@RequestParam("vendibilityPlatform") String vendibilityPlatform);

    /**
     * 根据系统sku码或者供应商sku码查询sku
     * @param sku sku
     * @return duixiang
     */
    @GetMapping("micro/commodity/getCommoditySpecBySku")
    String getCommoditySpecBySku(@RequestParam("sku")String sku);

    /**
     * 推送商品销售统计
     * @param data {"code:'SKU',value:1"}
     * @return
     */
    @PostMapping("micro/commodity/updateSkuSaleNum")
    String updateSkuSaleNum(@RequestBody List<CodeAndValueVo> data);

    @Service
    class RemoteCommodityServiceImpl implements RemoteCommodityService {


        @Override
        public String getSkuMapForOrder(List<QuerySkuMapForOrderVo> voList) {
            return null;
        }

        @Override
        public String test(String page, String row, Long category_level_1, Long category_level_2, Long category_level_3, String commodityName, String
                systemSku, String supplierSku, String SPU) {
            return "";
        }

        @Override
        public String getCommodityListBySystemSKU(List<String> skus) {
            return null;
        }

        @Override
        public String getCommodityBySkuList(GetCommodityBySkuListDTO getCommodityBySkuListDTO) {
            return null;
        }

        @Override
        public String getSkuList(String page, String row, Long category_level_1, Long category_level_2, Long category_level_3,
                                 String commodityName, String systemSku, String supplierSku, String SPU,String vendibilityPlatform) {
            return null;
        }

        @Override
        public String getCommoditySpecBySku(String sku) {
            return null;
        }

        @Override
        public String updateSkuSaleNum(List<CodeAndValueVo> data) {
            return null;
        }

        public String fallback() {
            return String.valueOf(JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "商品服务异常")));
        }
    }
}
