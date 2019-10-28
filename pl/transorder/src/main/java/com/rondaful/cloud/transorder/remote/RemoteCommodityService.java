package com.rondaful.cloud.transorder.remote;


import com.rondaful.cloud.transorder.entity.dto.GetCommodityBySkuListDTO;
import com.rondaful.cloud.transorder.entity.vo.QuerySkuMapForOrderVo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(name = "rondaful-commodity-service", fallback = RemoteCommodityService.RemoteCommodityServiceImpl.class)
public interface RemoteCommodityService {
    /**
     * 订单获取平台sku映射
     *
     * @param voList
     * @return
     */
    @PostMapping("/micro/commodity/getSkuMapForOrder")
    String getSkuMapForOrder(@RequestBody List<QuerySkuMapForOrderVo> voList);


    /**
     * 通过商品的品连SKU和品连卖家ID查询商品
     *
     * @param getCommodityBySkuListDTO {@link GetCommodityBySkuListDTO}
     * @return String
     */
    @PostMapping("/micro/commodity/getCommodityBySkuList")
    String getCommodityBySkuList(@RequestBody GetCommodityBySkuListDTO getCommodityBySkuListDTO);


    @Service
    class RemoteCommodityServiceImpl implements RemoteCommodityService {


        @Override
        public String getSkuMapForOrder(List<QuerySkuMapForOrderVo> voList) {
            return null;
        }


        @Override
        public String getCommodityBySkuList(GetCommodityBySkuListDTO getCommodityBySkuListDTO) {
            return null;
        }

    }
}
