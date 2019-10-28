package com.brandslink.cloud.logistics.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotEmpty;

@FeignClient(name = "brandslink-center-service", fallback = RemoteCenterService.RemoteCenterServiceImpl.class)
public interface RemoteCenterService {

    /**
     * 根据SKU数组获取SKU详情
     * @param sku
     * @return
     */
    @GetMapping("/sku/skuInfo")
    String getSkuInfoBySku(@RequestParam("sku") @NotEmpty(message = "SKU数组不能为空") String[] sku);

    @Service
    class RemoteCenterServiceImpl implements RemoteCenterService {

        @Override
        public String getSkuInfoBySku(String[] sku) {
            return null;
        }
    }
}
