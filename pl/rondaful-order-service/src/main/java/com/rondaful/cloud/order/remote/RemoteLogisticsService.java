package com.rondaful.cloud.order.remote;

import com.rondaful.cloud.order.entity.supplier.Logistics;
import com.rondaful.cloud.order.entity.supplier.LogisticsProvider;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * 远程调用物流管理相关接口
 */
@FeignClient(name = "rondaful-supplier-service", fallback = RemoteLogisticsService.RemoteLogisticsServiceImpl.class)
public interface RemoteLogisticsService {

    /**
     * 调用查询物流信息列表（最终查询为erp系统）
     *
     * @return 返回数据
     */
    @GetMapping("/logistics/getErpLogistics")
    String getErpLogistics();

    /**
     * 查询物流信息详情(最终查询erp系统)
     *
     * @param code 物流方式code
     * @return 返回数据
     */
    @GetMapping("/logistics/getErpLogisticsDetail/{code}")
    String getErpLogisticsDetail(@PathVariable(value = "code") String code);

    /**
     * 查询某仓库支持的物流方式
     *
     * @param warehouseId 仓库id
     * @return 返回数据
     */
    @GetMapping("/logistics/getErpLogisticsIncludeWH")
    String getErpLogisticsIncludeWH(@RequestParam("warehouseId") String warehouseId);

    /**
     * 取得可用仓库列表
     *
     * @return 放回数据
     */
    @GetMapping("/getValidWarehouseList")
    String getValidWarehouseList();


    /**
     * 根据条件查询物流方式列表
     *
     * @param status        物流方式状态 0停用 1启用
     * @param warehouseName 仓库名称
     * @param shortName     物流方式名称
     * @param supplier      供应商名称
     * @param type          物流方式类型
     * @param warehouseCode 仓库编码
     * @param id            物流方式id
     * @return 返回数据
     */
    @GetMapping("/logistics/queryLogisticsList")
    String queryLogisticsList(@RequestParam(value = "status", defaultValue = "") String status,
                              @RequestParam(value = "warehouseName", defaultValue = "") String warehouseName,
                              @RequestParam(value = "shortName", defaultValue = "") String shortName,
                              @RequestParam(value = "supplier", defaultValue = "") String supplier,
                              @RequestParam(value = "type", defaultValue = "") String type,
                              @RequestParam(value = "warehouseCode", defaultValue = "") String warehouseCode,
                              @RequestParam(value = "id", defaultValue = "") String id,
                              @RequestParam(value = "code") String code);

    /**
     * 根据条件查询物流方式(给内部服务调用)
     * @param param
     * @return
     */
    @PostMapping("/logistics/queryCarrierCodeList")
    String queryCarrierCodeList(@RequestBody List<LogisticsProvider> param);


    /**
     * 根据物流条件查询仓库信息
     *
     * @param logisticsCode 物流方式编码
     * @return 返回信息
     */
    @GetMapping("/logistics/queryWarehouse")
    String queryWarehouse(@RequestParam(value = "logisticsCode") String logisticsCode);


    /**
     * 分页及模糊查询物流方式
     *
     * @param currentPage
     * @param pageSize
     * @param status
     * @param warehouseName
     * @param shortName
     * @param supplier
     * @param type
     * @param warehouseCode
     * @param id
     * @return
     */
    @GetMapping("/logistics/queryLogisticsListPage")
    String queryLogisticsListPage(@RequestParam(value = "currentPage", defaultValue = "") String currentPage,
                                  @RequestParam(value = "pageSize", defaultValue = "") String pageSize,
                                  @RequestParam(value = "status", defaultValue = "") String status,
                                  @RequestParam(value = "warehouseName", defaultValue = "") String warehouseName,
                                  @RequestParam(value = "shortName", defaultValue = "") String shortName,
                                  @RequestParam(value = "carrierName", defaultValue = "") String carrierName,
                                  @RequestParam(value = "supplier", defaultValue = "") String supplier,
                                  @RequestParam(value = "type", defaultValue = "") String type,
                                  @RequestParam(value = "warehouseCode", defaultValue = "") String warehouseCode,
                                  @RequestParam(value = "id", defaultValue = "") String id);


    /**
     * 物流方式管理查询条件
     *
     * @return
     */
    @GetMapping("/logistics/queryLogisticsTerm")
    String queryLogisticsTerm();

    /**
     * 更新平台物流映射
     *
     * @param param
     * @return
     */
    @GetMapping("/logistics/updateLogisticsMapping")
    String updateLogisticsMapping(Logistics param);

    /**
     * 查询第三方支持的物流方式
     *
     * @return
     */
    @GetMapping("/logistics/queryThirdLogistics")
    String queryThirdLogistics();

    /**
     * 远程通过邮寄方式CODE查询邮寄方式名称
     *
     * @param logisticsCode 邮寄方式code
     * @param warehouseId 仓库ID
     * @return 返回结果
     */
    @GetMapping("/logisticsProvider/queryLogisticsByCode")
    String queryLogisticsByCode(@RequestParam(value = "logisticsCode") String logisticsCode,
                                @RequestParam(value = "warehouseId") Integer warehouseId);

    /**
     * 远程通过邮寄方式code查询邮寄方式名称
     *
     * @param logisticsCode 邮寄方式code
     * @return 返回结果
     */
    @PostMapping("/LogisticsProvider/queryLogisticsMsg")
//    String queryLogisticsMsg(@RequestParam(value = "supplierIdList") List<String> supplierIdList,
//                             @RequestParam(value = "warehouseCode") String warehouseCode,
//                             @RequestParam(value = "logisticsCode") String logisticsCode);

    String queryLogisticsMsg(Map map);

    /**
     * 查询符合调价的仓库列表
     * @param warehouseName 仓库名称
     * @param warehouseCode 仓库code
     * @param companyCode 客户编码
     * @return 列表
     */
    @PostMapping("/warehouse/getWarehouseInfoByParam")
    String getWarehouseInfoByParam(@RequestParam(value = "warehouseName") String warehouseName,
                                   @RequestParam(value = "warehouseCode") String warehouseCode,
                                   @RequestParam(value = "companyCode") String companyCode);


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
                                         String type, String warehouseCode, String id,String code) {
            return null;
        }

        @Override
        public String queryCarrierCodeList(List<LogisticsProvider> param) {
            return null;
        }

        @Override
        public String queryWarehouse(String logisticsCode) {
            return null;
        }

        @Override
        public String queryLogisticsListPage(String currentPage, String pageSize, String status, String warehouseName,
                                             String shortName, String carrierName, String supplier, String type, String warehouseCode, String id) {
            return null;
        }

        @Override
        public String queryLogisticsTerm() {
            return null;
        }

        @Override
        public String updateLogisticsMapping(Logistics param) {
            return null;
        }

        @Override
        public String queryThirdLogistics() {
            return null;
        }

        @Override
        public String queryLogisticsByCode(String carrierCode,Integer warehouseCode) {
            return null;
        }

        @Override
//        public String queryLogisticsMsg(List<String> supplierIdList, String warehouseCode, String logisticsCode) {
        public String queryLogisticsMsg(Map map) {
            return null;
        }

        @Override
        public String getWarehouseInfoByParam(String warehouseName, String warehouseCode,String companyCode) {
            return null;
        }
    }
}
