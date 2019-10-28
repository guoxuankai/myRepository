package com.rondaful.cloud.seller.remote;

import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "rondaful-supplier-service", fallback = RemoteSupplierProviderService.RemoteLogisticsServiceImpl.class)
public interface RemoteSupplierProviderService {


    /**
     * 商品状态查询
     * @param warehouseId
     * @param pinlianSku
     * @return
     */
    @GetMapping("/provider/getSellerInv")
    String getSellerInv(@RequestParam(value="warehouseId")Integer warehouseId,@RequestParam(value="pinlianSku")String pinlianSku,@RequestParam(value="platform")Integer platform,@RequestParam(value="siteCode")String siteCode);

    @ApiOperation(value = "根据仓库Id查询仓库信息", notes = "")
    @GetMapping(value = "/provider/getWarehouseById")
    public Object getWarehouseById(@RequestParam("warehouseId")Integer warehouseId);

    /**
     * 断路降级
     */
    @Service
    class RemoteLogisticsServiceImpl implements RemoteSupplierProviderService {

        @Override
        public String getSellerInv(Integer warehouseId,String pinlianSku,Integer platform,String siteCode) {
            return null;
        }

        @Override
        public Object getWarehouseById(Integer warehouseId) {
            return null;
        }

    }


}
