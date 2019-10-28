package com.rondaful.cloud.seller.remote;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.model.vo.freight.LogisticsCostVo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "rondaful-supplier-service", fallback = RemoteLogisticsService.RemoteLogisticsServiceImpl.class)
public interface RemoteLogisticsService {


    /**
     * 调用查询物流信息列表（最终查询为erp系统）
     * @return 返回数据
     */
    @GetMapping("/logistics/getErpLogistics")
    String getErpLogistics();

    /**
     * 查询物流信息详情(最终查询erp系统)
     * @param code 物流方式code
     * @return 返回数据
     */
    @RequestMapping(value = "/logistics/getErpLogisticsDetail/{code}", method = RequestMethod.GET)
    String getErpLogisticsDetail(@PathVariable(value = "code") String code);

    /**
     * 查询某仓库支持的物流方式
     * @param warehouseId 仓库id
     * @return 返回数据
     */
    @RequestMapping(value = "/logistics/getErpLogisticsIncludeWH", method = RequestMethod.GET)
    String getErpLogisticsIncludeWH(@RequestParam("warehouseId") String warehouseId);

    /**
     * 取得可用仓库列表
     * @return 放回数据
     */
    @PostMapping("/warehouse/getValidWarehouseList")
    String getValidWarehouseList();

    /**
     * 根据条件查询物流方式列表
     * @param status 物流方式状态 0停用 1启用
     * @param warehouseName 仓库名称
     * @param shortName 物流方式名称
     * @param supplier 供应商名称
     * @param type 物流方式类型
     * @param warehouseCode 仓库编码
     * @param id 物流方式id
     * @return 返回数据
     */
    @GetMapping("/logisticsProvider/queryLogisticsList")
    String queryLogisticsList(@RequestParam(value="status",defaultValue="")String status,
                              @RequestParam(value="warehouseName",defaultValue="")String warehouseName,
                              @RequestParam(value="shortName",defaultValue="")String shortName,
                              @RequestParam(value="supplier",defaultValue="")String supplier,
                              @RequestParam(value="type",defaultValue="")String type,
                              @RequestParam(value="warehouseCode",defaultValue="")String warehouseCode,
                              @RequestParam(value="id",defaultValue="")String id);


    /**
     * 根据物流条件查询仓库信息
     * @param logisticsCode 物流方式编码
     * @return 返回信息
     */
    @GetMapping("/logistics/queryWarehouse")
    String queryWarehouse(@RequestParam(value="logisticsCode")String logisticsCode);


    /**
     * 根据sku列表获取库存
     * @param warehouseId 仓库id
     * @param skus sku数组,json.tostring
     * @return 结果
     */
    @PostMapping("/provider/getsBySku")
    String getsBySku(@RequestParam(value="warehouseId") Integer warehouseId, @RequestParam(value="skus") String skus);

    /**
     * 查询具体的运费
     * @param param c参数
     * @return 结果
     */
    @PostMapping("/freight/getFreightTrialByType")
    String getFreightTrialByType(@RequestBody JSONObject param);



    /**
     * 断路降级
     */
    @Service
    class RemoteLogisticsServiceImpl implements RemoteLogisticsService {

        @Override
        public String getErpLogistics() {
            return null;
        }

        @Override
        public String getErpLogisticsDetail(String code) {
            return null;
        }

        @Override
        public String getErpLogisticsIncludeWH(String warehouseId) {
            return null;
        }

        @Override
        public String getValidWarehouseList() {
            return null;
        }

        @Override
        public String queryLogisticsList(String status, String warehouseName, String shortName, String supplier,
                                         String type, String warehouseCode, String id) {
            return null;
        }

        @Override
        public String queryWarehouse(String logisticsCode) {
            return null;
        }

        @Override
        public String getsBySku(Integer warehouseId, String skus) {
            return null;
        }

        @Override
        public String getFreightTrialByType(JSONObject param) {
            return null;
        }
    }


}
