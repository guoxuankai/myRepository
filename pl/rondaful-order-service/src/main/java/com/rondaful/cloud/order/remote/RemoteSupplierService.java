package com.rondaful.cloud.order.remote;


import com.rondaful.cloud.common.entity.Result;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.model.vo.freight.LogisticsCostVo;
import com.rondaful.cloud.common.model.vo.freight.SearchLogisticsListDTO;
import com.rondaful.cloud.order.entity.OrderTrackIdAndSkuDTO;
import com.rondaful.cloud.order.entity.supplier.DeliveryRecord;
import com.rondaful.cloud.order.entity.supplier.FreightTrial;
import net.sf.json.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "rondaful-supplier-service",fallback = RemoteSupplierService.RemoteSupplierServiceImpl.class)
public interface RemoteSupplierService {

    /**
     * 根据包裹号和sku查询售后订单详情(批量)
     * @param list
     * @return
     */
    @PostMapping("/afterSales/getOrderAfterSaleBetchByOrderTrackIdAndSku")
    String getOrderAfterSaleBetchByOrderTrackIdAndSku(@RequestBody List<OrderTrackIdAndSkuDTO> list);

    /**
     * 通过物流方式code和仓库ID查询物流信息
     * @param logisticsCode
     * @param warehouseId
     * @return
     */
    @GetMapping("/logisticsProvider/queryLogisticsByCode")
    String queryLogisticsByCode(@RequestParam("logisticsCode")String logisticsCode,@RequestParam("warehouseId") Integer warehouseId);

    /**
     * 修改本地待出货数量
     * @param warehouseId
     * @param pinlianSku
     * @param qty
     * @return
     */
    @PostMapping("/provider/updateLocalShipping")
    String updateLocalShipping(@RequestParam("warehouseId")Integer warehouseId, @RequestParam("pinlianSku")String pinlianSku, @RequestParam("qty")Integer qty);


    /**
     * 根据服务商编码获取下属所有仓库id
     * @param serviceCode   RONDAFUL   GOODCANG
     * @return
     */
    @PostMapping("/provider/basic/getsByType")
    String getsByType(@RequestParam("serviceCode") String serviceCode);

    /**
     * 1.运费试算
     * @param param   TODO  返回实体类 FreightTrialDTO   ----------OK     物流规则的getFreightTrialObject方法没改正!!!!!!!!!!!!!!!!!!
     * @return
     */
    @PostMapping("/freight/getFreightTrial")
    String getFreightTrial(@RequestBody FreightTrial param);

    /**
     * 根据仓库id获取仓库信息   TODO  (替代上面 2 接口)
     * @param warehouseId  仓库ID
     * @return
     */
    @GetMapping("/provider/getWarehouseById")
    String getWarehouseById(@RequestParam("warehouseId") Integer warehouseId);

    /**
     * 根据sku列表获取库存              TODO （替代上面 3和4 接口）
     * @param warehouseId  仓库ID
     * @param skus   SKU数组字符串：["A-2-E062737D-723488","90968320001"]
     * @return
     */
    @PostMapping(value = "/provider/getsBySku")
    String getsBySku(@RequestParam("warehouseId") Integer warehouseId, @RequestParam("skus") String skus);

    /**
     * 根据sku列表获取库存   TODO   替代上面  5  接口---------------------------订单规则相关代码还没改
     * @param skus
     * @return
     */
    @PostMapping("/provider/getsInvBySku")
    String getsInvBySku(@RequestParam("skus") String skus);

    /**
     * 根据仓库ID批量获取仓库信息   TODO 替代上面 6 接口
     * @param ids   仓库ID数组字符串：["182","191"]
     * @return
     */
    @GetMapping(value = "/provider/getWarehouseByIds")
    String getWarehouseByIds(@RequestParam("ids") String ids);


    /**
     * 7.仓库匹配物流方式
     * @param freightTrial    TODO  传FreightTrial类  小寂寞
     * @return
     */
    @PostMapping(value = "/logisticsProvider/matchLogistics")
    String matchLogistics(@RequestBody FreightTrial freightTrial);

    /**
     * 生成供应商出库记录   TODO  传LIST<DeliveryRecord>  --------------------------------------暂不更改
     * @param deliveryList
     * @return
     */
    @RequestMapping(value = "/provider/syncDeliveryRecord", method = RequestMethod.POST)
    String syncDeliveryRecord(@RequestBody List<DeliveryRecord> deliveryList);

    @PostMapping("/provider/updateInventory")
    String updateInventory(@RequestParam("appToken") String appToken,@RequestParam("pinlianSku") String pinlianSku,@RequestParam("warehouseCode") String warehouseCode);

    @PostMapping("/freight/getSuitLogisticsByType")
    String getSuitLogisticsByType(@RequestBody SearchLogisticsListDTO searchLogisticsListDTO);

    @PostMapping("/freight/queryFreightByLogisticsCode")
    String queryFreightByLogisticsCode(@RequestBody LogisticsCostVo logisticsCostVo);

    @Service
    class RemoteSupplierServiceImpl implements RemoteSupplierService{

        public String fallback() {
            return String.valueOf(JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "供应商服务异常")));
        }
        @Override
        public String getsBySku(Integer warehouseId, String skus) {
            return null;
        }

        @Override
        public String getsInvBySku(String skus) {
            return null;
        }

        @Override
        public String syncDeliveryRecord(List<DeliveryRecord> deliveryList) {
            return null;
        }

        @Override
        public String getWarehouseById(Integer warehouseId) {
            return null;
        }

        @Override
        public String getWarehouseByIds(String ids) {
            return null;
        }

        @Override
        public String getsByType(String serviceCode) {
            return null;
        }

        @Override
        public String getOrderAfterSaleBetchByOrderTrackIdAndSku(List<OrderTrackIdAndSkuDTO> list) {
            return null;
        }

        @Override
        public String queryLogisticsByCode(String logisticsCode, Integer warehouseId) {
            return null;
        }

        @Override
        public String updateLocalShipping(Integer warehouseId, String pinlianSku, Integer qty) {
            return null;
        }


        @Override
        public String getFreightTrial(FreightTrial param) {
            return null;
        }

        @Override
        public String matchLogistics(@RequestBody FreightTrial freightTrial) {
            return null;
        }

        @Override
        public String updateInventory(String appToken, String pinlianSku, String warehouseCode) {
            return null;
        }

        @Override
        public String getSuitLogisticsByType(SearchLogisticsListDTO searchLogisticsListDTO) {
            return null;
        }

        @Override
        public String queryFreightByLogisticsCode(LogisticsCostVo logisticsCostVo) {
            return null;
        }
    }
}
