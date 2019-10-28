package com.rondaful.cloud.supplier.remote;


import java.util.List;

import com.rondaful.cloud.supplier.model.dto.FeignResult;
import org.apache.ibatis.annotations.Param;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rondaful.cloud.supplier.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.entity.Result;


import net.sf.json.JSONObject;


@FeignClient(name = "rondaful-commodity-service", fallback = RemoteCommodityService.RemoteCommodityServiceImpl.class)
public interface RemoteCommodityService {


    @PostMapping("micro/commodity/getSystemSkuByUserSku")
    Object getSystemSkuByUserSku(@RequestParam("skus") List<String> skus);
    
    @GetMapping("/micro/commodity/getSkuList")
    Object getSkuList(@RequestParam("page")String page, @RequestParam("row")String row,@RequestParam("commodityName") String commodityName);

    @GetMapping("/micro/commodity/getCommoditySpecBySku")
    Object getBySku(@RequestParam("sku")String sku,@RequestParam("platform") Integer platform,@RequestParam("siteCode") String siteCode);

    //商品搜索,调减筛选
    @GetMapping("/operate/commodity/search")
    Object search(@RequestParam("page")String page, @RequestParam("row")String row, @RequestParam("category_level_1")Long category_level_1,@RequestParam("category_level_2")Long category_level_2,@RequestParam("category_level_3") Long category_level_3, @RequestParam("brand_id")Long brand_id, @RequestParam("vendibility_platform")String vendibility_platform,@RequestParam("spus") List<String> ids,@RequestParam("commodityName") String commodityName,@RequestParam("sortType")Long sortType,@RequestParam("systemSku")String systemSku,@RequestParam("SPU")String SPU);

    @GetMapping("micro/commodity/getSkuListByPage")
    Object getsPageSku(@RequestParam("page") String page,@RequestParam("row") String row,@RequestParam("state") Integer state);

    @GetMapping("micro/commodity/getSkuSellerList")
    FeignResult getSkuSellerList(@RequestParam("sku") String sku);

    @GetMapping("micro/commodity/getSkuListBySupplierSku")
    FeignResult getTwoSku(@RequestParam("supplierSku") String supplierSku);


    @Service
    class RemoteCommodityServiceImpl implements RemoteCommodityService {

    	 public Object fallback() {
             return JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "商品服务异常"));
         }

        public FeignResult feignResultError() {
            return new FeignResult(false,ResponseCodeEnum.RETURN_CODE_100500.getCode(), "商品服务异常");
        }

        @Override
        public Object getSystemSkuByUserSku(@RequestParam("skus") List<String> skus) {
        	
			return fallback();
        }
		@Override
		public Object getSkuList(@RequestParam("page")String page, @RequestParam("row")String row,@RequestParam("commodityName") String commodityName) {
			return fallback();
		}


        @Override
        public Object search(String page, String row, Long category_level_1, Long category_level_2, Long category_level_3, Long brand_id, String vendibility_platform, List<String> ids, String commodityName, Long sortType, String systemSku, String SPU) {
            return fallback();
        }

        @Override
        public Object getBySku(String sku,Integer platform,String siteCode) {
            return fallback();
        }

        @Override
        public Object getsPageSku(String page, String row, Integer state) {
            return fallback();
        }

        @Override
        public FeignResult getSkuSellerList(String sku) {
            return feignResultError();
        }

        @Override
        public FeignResult getTwoSku(String supplierSku) {
            return feignResultError();
        }
    }

}
