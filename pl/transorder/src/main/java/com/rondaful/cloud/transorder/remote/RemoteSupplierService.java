package com.rondaful.cloud.transorder.remote;


import com.rondaful.cloud.common.model.vo.freight.LogisticsCostVo;
import com.rondaful.cloud.common.model.vo.freight.SearchLogisticsListDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "rondaful-supplier-service", fallback = RemoteSupplierService.RemoteSupplierServiceImpl.class)
public interface RemoteSupplierService {

    @PostMapping("/provider/getsInvBySku")
    String getsInvBySku(@RequestParam("skus") String skus);

    // 过时方法
    @PostMapping("/freight/getSuitLogisticsByType")
    String getSuitLogisticsByType(@RequestBody SearchLogisticsListDTO searchLogisticsListDTO);






    /**
     * 根据仓库id获取仓库信息   TODO  (替代上面 2 接口)
     * @param warehouseId  仓库ID
     * @return
     */
    @GetMapping("/provider/getWarehouseById")
    String getWarehouseById(@RequestParam("warehouseId") Integer warehouseId);


    @PostMapping("/freight/queryFreightByLogisticsCode")
    String queryFreightByLogisticsCode(@RequestBody LogisticsCostVo logisticsCostVo);


    @Service
    class RemoteSupplierServiceImpl implements RemoteSupplierService {
        @Override
        public String getsInvBySku(String skus) {
            return null;
        }

        @Override
        public String getSuitLogisticsByType(SearchLogisticsListDTO searchLogisticsListDTO) {
            return null;
        }

        @Override
        public String getWarehouseById(Integer warehouseId) {
            return null;
        }

        @Override
        public String queryFreightByLogisticsCode(LogisticsCostVo logisticsCostVo) {
            return null;
        }
    }


}
