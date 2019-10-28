package com.rondaful.cloud.order.remote;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.enums.ERPOrderEnum;
import com.rondaful.cloud.common.enums.ERPWarehouseEnum;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.HttpUtil;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.order.utils.ErpHttpUtils;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RemoteErpService {

    /**
     * 远程调用erp服务获取获取仓库列表
     *
     * @return 仓库列表
     */

    String getWarehouse() throws Exception;

    /**
     * 远程调用erp服务获取物流信息
     *
     * @return 物流信息
     */
    String getCarrier() throws Exception;

    /**
     * 远程调用erp服务获取库存列表
     *
     * @param warehouse_code   仓库CODE（选填）
     * @param page             当前页（必填）
     * @param pageSize         每页长度(默认是10)（选填）
     * @param skuList          Sku 传数组 比如：["ZK0000601","ZK0000602"]（选填）
     * @param last_update_time 上次更新时间 比如 1543546768（选填）
     * @param warehouse_type   仓库类型:2-本地仓;7-谷仓（选填）
     * @return
     * @throws Exception
     */
    String getInventory(String warehouse_code, Integer page, Integer pageSize, List<String> skuList, Integer last_update_time, Integer warehouse_type) throws Exception;

    /**
     * 远程调用erp服务，获取仓库支持的物流渠道（弃用）
     *
     * @param warehouseId 仓库id（仓库在erp的id）
     * @return 物流信息
     */
    String getWarehouseShipping(String warehouseId) throws Exception;

    /**
     * 调用erp计算物流费 以重量的方式
     *
     * @param warehouse_code    仓库code
     * @param country_code      国家双字母简写
     * @param shipping_code_arr 物流方式列表
     * @param weight            重量 g
     * @return 返回数据
     */
    String erpTrialByWeight(String warehouse_code, String country_code, List<String> shipping_code_arr, Integer weight) throws Exception;

    /**
     * 调用ERP订单接受接口  进行发货
     *
     * @param erpOrderMap 调用ERP发货传的订单Map
     * @return
     * @throws IOException
     */
    String orderReceive(Map<String, Object> erpOrderMap) throws Exception;

    /**
     * 调用ERP订单申请取消接口 取消发货
     *
     * @param channelOrderNumber 取消订单的渠道订单号
     * @return
     * @throws IOException
     */
    String orderCannel(String channelOrderNumber) throws Exception;

    /**
     * 调用ERP获取订单物流相关信息
     *
     * @param channelOrderNumber 获取物流相关信息的渠道订单号
     * @return
     * @throws IOException
     */
    String orderShippingInfo(String channelOrderNumber) throws Exception;

    /**
     * 获取订单进度信息
     *
     * @param channelOrderNumber 获取订单进度信息传的渠道订单号
     * @return
     * @throws IOException
     */
    String orderSpeedInfo(String channelOrderNumber) throws Exception;

    /**
     * 定时任务获取订单进度信息
     *
     * @param channelOrderNumber 获取订单进度信息传的渠道订单号["TK1033370129b7NBaEZ","TK193914931ohDbitQW"]
     * @return
     * @throws IOException
     */
    String getOrderSpeedInfo(String channelOrderNumber) throws Exception;


    /**
     * 调用erp计算物流费 以sku列表的方式
     *
     * @param warehouse_code    仓库code
     * @param country_code      国家双字母简写
     * @param shipping_code_arr 物流方式列表
     * @param skus              sku列表  String 的格式为： {"sku":"DI0302801","num":"6"}
     * @return 返回数据
     */
    String erpTrialBySKUS(String warehouse_code, String country_code, List<String> shipping_code_arr, List<Map<String, Object>> skus, int channel_id) throws Exception;

    @Service
    class RemoteErpServiceImpl implements RemoteErpService {

        private final static Logger _log = LoggerFactory.getLogger(RemoteErpServiceImpl.class);

        @Value("${erp.url}")
        private String erpUrl;

//        @Value("${erp.freight_trial}")
//        private String freight_trial;

        @Override
        public String getInventory(String warehouse_code, Integer page, Integer pageSize, List<String> skuList, Integer last_update_time, Integer warehouse_type) {
            if (page == null)
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "查询ERP可用库存参数page必传。。。");
            HashMap<String, String> params = new HashMap<>();
            params.put("warehouse_code", warehouse_code);
            params.put("page", page.toString());
            params.put("pageSize", pageSize == null ? null : pageSize.toString());
            params.put("sku", FastJsonUtils.toJsonString(skuList));
            params.put("last_update_time", last_update_time == null ? null : last_update_time.toString());
            params.put("Warehouse_type", warehouse_type == null ? null : warehouse_type.toString());//仓库类型:2-本地仓;7-谷仓
            _log.info("请求的ERP地址为: {}", erpUrl);
            String result = HttpUtil.postSendByFormData(erpUrl, ERPWarehouseEnum.methods.INVENTORY.getMethod(), params);
            _log.error("____________获取库存ERP返回结果为___________{}___________", result);
            if (StringUtils.isBlank(result))
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "获取可用库存，调用ERP服务异常。。。");
            JSONObject jsonObject = JSONObject.parseObject(result);
            Integer status = jsonObject.getInteger("status");
            String message = jsonObject.getString("message");
            if (status != 1 || !"success".equalsIgnoreCase(message)) {
                _log.error("____________获取库存,调用ERP服务异常___________{}___________", message);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, Utils.translation("获取可用库存，调用ERP服务异常:" + message));
            }
            return jsonObject.getString("data");
        }

        @Override
        public String getWarehouse() {
            _log.info("请求的ERP地址为: {}", erpUrl);
            String result = HttpUtil.postSendByFormData(erpUrl, ERPWarehouseEnum.methods.WAREHOUSE_LIST.getMethod(), null);
            _log.error("____________获取仓库列表ERP返回结果为___________{}___________", result);
            if (StringUtils.isBlank(result))
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "获取仓库列表，调用ERP服务异常。。。");
            JSONObject jsonObject = JSONObject.parseObject(result);
            Integer status = jsonObject.getInteger("status");
            String message = jsonObject.getString("message");
            if (status != 1 || !"success".equalsIgnoreCase(message)) {
                _log.error("____________获取仓库列表,调用ERP服务异常___________{}___________", message);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, Utils.translation("获取仓库列表，调用ERP服务异常:" + message));
            }
            return jsonObject.getString("data");
        }

        @Override
        public String getCarrier() {
            _log.info("请求的ERP地址为: {}", erpUrl);
            String result = HttpUtil.postSendByFormData(erpUrl, ERPWarehouseEnum.methods.LOGISTICS_LIST.getMethod(), null);
            _log.error("____________获取物流方式列表ERP返回结果为___________{}___________", result);
            if (StringUtils.isBlank(result))
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "获取物流方式列表，调用ERP服务异常。。。");
            JSONObject jsonObject = JSONObject.parseObject(result);
            Integer status = jsonObject.getInteger("status");
            String message = jsonObject.getString("message");
            if (status != 1 || !"success".equalsIgnoreCase(message)) {
                _log.error("____________获取物流方式列表,调用ERP服务异常___________{}___________", message);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, Utils.translation("获取物流方式列表，调用ERP服务异常:" + message));
            }
            return jsonObject.getString("data");
        }

        @Override
        public String getWarehouseShipping(String warehouseId) {
            Map<String, String> postMap = new HashMap<>();
            postMap.put("warehouse_id", warehouseId);
            _log.info("请求的ERP地址为: {}", erpUrl);
            String result = HttpUtil.postSendByFormData(erpUrl, ERPWarehouseEnum.methods.WAREHOUSE_SHIP.getMethod(), postMap);
            _log.info("____________获取仓库支持物流渠道ERP返回结果为___________{}___________", result);
            if (StringUtils.isBlank(result)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "获取仓库支持物流渠道，调用ERP服务异常。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(result);
            Integer status = jsonObject.getInteger("status");
            String message = jsonObject.getString("message");
            if (status != 1 || !"success".equalsIgnoreCase(message)) {
                _log.error("____________获取仓库支持物流渠道,调用ERP服务异常___________{}___________", message);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, Utils.translation("获取仓库支持物流渠道，调用ERP服务异常:" + message));
            }
            return jsonObject.getString("data");
        }

        @Override
        public String erpTrialByWeight(String warehouse_code, String country_code, List<String> shipping_code_arr, Integer weight) {
            HashMap<String, String> postParam = new HashMap<>();
            postParam.put("search_type", "1");
            postParam.put("warehouse_code", warehouse_code);
            postParam.put("country_code", country_code);
            postParam.put("shipping_code_arr", FastJsonUtils.toJsonString(shipping_code_arr));
            postParam.put("weight", weight.toString());
            _log.info("请求的ERP地址为: {}", erpUrl);
            String result = HttpUtil.postSendByFormData(erpUrl, ERPWarehouseEnum.methods.TRAIL.getMethod(), postParam);
            _log.info("____________计算预估物流费（按重量）ERP返回结果为___________{}___________", result);
            if (StringUtils.isBlank(result)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "计算预估物流费（按重量），调用ERP服务异常。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(result);
            Integer status = jsonObject.getInteger("status");
            String message = jsonObject.getString("message");
            if (status != 1 || !"success".equalsIgnoreCase(message)) {
                _log.error("____________计算预估物流费（按重量）,调用ERP服务异常___________{}___________", message);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, Utils.translation("计算预估物流费（按重量），调用ERP服务异常:" + message));
            }
            return jsonObject.getString("data");
        }

        @Override
        public String erpTrialBySKUS(String warehouse_code, String country_code, List<String> shipping_code_arr, List<Map<String, Object>> skus, int channel_id) throws Exception {
            HashMap<String, String> postParam = new HashMap<>();
            postParam.put("search_type", "2");
            postParam.put("warehouse_code", warehouse_code);
            postParam.put("country_code", country_code);
            postParam.put("shipping_code_arr", FastJsonUtils.toJsonString(shipping_code_arr));
            postParam.put("skus", FastJsonUtils.toJsonString(skus));
            postParam.put("channel_id", FastJsonUtils.toJsonString(channel_id));
            _log.info("请求的ERP地址为: {}", erpUrl);
            String result = HttpUtil.postSendByFormData(erpUrl, ERPWarehouseEnum.methods.TRAIL.getMethod(), postParam);
            _log.info("____________计算预估物流费（SKU）ERP返回结果为___________{}___________", result);
            if (StringUtils.isBlank(result)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "计算预估物流费（SKU），调用ERP服务异常。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(result);
            Integer status = jsonObject.getInteger("status");
            String message = jsonObject.getString("message");
            if (status != 1 || !"success".equalsIgnoreCase(message)) {
                _log.error("____________计算预估物流费（SKU）,调用ERP服务异常___________{}___________", message);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, Utils.translation("计算预估物流费（SKU），调用ERP服务异常:" + message));
            }
            return jsonObject.getString("data");
        }

        @Override
        public String orderReceive(Map<String, Object> erpOrderMap) throws Exception {
            Map<String, String> map = new HashMap<>();
            map.put("param", FastJsonUtils.toJsonString(erpOrderMap));
            _log.error("_______________请求发货地址为：{}，请求发货的订单JSON格式为：{}_______________", erpUrl, map.get("param"));
            String result = HttpUtil.postSendByFormDataForOrder(erpUrl, ERPOrderEnum.methods.ORDER_RECEIVE.getMethod(), map);
            _log.error("____________发货推单至ERP返回结果为___________{}___________", result);
            if (StringUtils.isBlank(result)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "发货推单至ERP，调用ERP服务异常。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(result);
            Integer status = jsonObject.getInteger("status");
            String message = jsonObject.getString("message");
            if (status != 1 || !"success".equalsIgnoreCase(message)) {
                _log.error("____________发货推单至ERP,调用ERP服务异常___________{}___________", message);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, Utils.translation("发货推单至ERP，调用ERP服务异常:" + message));
            }
            return jsonObject.getString("data");
        }

        @Override
        public String orderCannel(String channelOrderNumber) throws Exception {
            HashMap<String, String> postParam = new HashMap<>();
            postParam.put("channel_order_number", channelOrderNumber);
            _log.info("请求的ERP地址为: {}", erpUrl);
            String result = HttpUtil.postSendByFormDataForOrder(erpUrl, ERPOrderEnum.methods.ORDER_CANNEL.getMethod(), postParam);
            _log.error("____________取消ERP发货订单ERP返回结果为___________{}___________", result);
            if (StringUtils.isBlank(result))
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "取消ERP发货订单，调用ERP服务异常。。。");
            JSONObject jsonObject = JSONObject.parseObject(result);
            Integer status = jsonObject.getInteger("status");
            String message = jsonObject.getString("message");
            if (status != 1 || !"success".equalsIgnoreCase(message)) {
                _log.error("____________取消ERP发货订单,调用ERP服务异常___________{}___________", message);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, Utils.translation("取消ERP发货订单，调用ERP服务异常:" + message));
            }
            return jsonObject.getString("data");
        }

        @Override
        public String orderShippingInfo(String channelOrderNumber) throws Exception {
            HashMap<String, String> postParam = new HashMap<>();
            postParam.put("channel_order_number", channelOrderNumber);
            _log.info("请求的ERP地址为: {}", erpUrl);
            String result = HttpUtil.postSendByFormDataForOrder(erpUrl, ERPOrderEnum.methods.ORDER_SHIPPING_INFO.getMethod(), postParam);
            _log.error("____________获取订单物流相关信息ERP返回结果为___________{}___________", result);
            if (StringUtils.isBlank(result))
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "获取订单物流相关信息，调用ERP服务异常。。。");
            JSONObject jsonObject = JSONObject.parseObject(result);
            Integer status = jsonObject.getInteger("status");
            String message = jsonObject.getString("message");
            if (status != 1 || !"success".equalsIgnoreCase(message)) {
                _log.error("____________获取订单物流相关信息,调用ERP服务异常___________{}___________", message);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, Utils.translation("获取订单物流相关信息，调用ERP服务异常:" + message));
            }
            return jsonObject.getString("data");
        }

        @Override
        public String orderSpeedInfo(String channelOrderNumber) throws Exception {
            HashMap<String, String> postParam = new HashMap<>();
            postParam.put("channel_order_number", channelOrderNumber);
            _log.info("请求的ERP地址为: {}", erpUrl);
            String result = HttpUtil.postSendByFormDataForOrder(erpUrl, ERPOrderEnum.methods.ORDER_SPEED_INFO.getMethod(), postParam);
            _log.error("____________获取订单进度信息ERP返回结果为___________{}___________", result);
            if (StringUtils.isBlank(result))
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "获取订单进度信息，调用ERP服务异常。。。");
            JSONObject jsonObject = JSONObject.parseObject(result);
            Integer status = jsonObject.getInteger("status");
            String message = jsonObject.getString("message");
            if (status != 1 || !"success".equalsIgnoreCase(message)) {
                _log.error("____________获取订单进度信息,调用ERP服务异常___________{}___________", message);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, Utils.translation("获取订单进度信息，调用ERP服务异常:" + message));
            }
            return jsonObject.getString("data");
        }

        public String getOrderSpeedInfo(String channelOrderNumber) throws Exception {
            HashMap<String, String> postParam = new HashMap<>();
            postParam.put("channel_order_number", channelOrderNumber);
            _log.info("请求的ERP地址为: {}", erpUrl);
            String result = ErpHttpUtils.postSendByFormDataForOrder(erpUrl, ERPOrderEnum.methods.ORDER_SPEED_INFO.getMethod(), postParam);
            _log.error("===========定时任务获取订单状态ERP返回结果为==========》{}", result);
            if (StringUtils.isEmpty(result)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "定时任务获取订单状态信息，调用ERP服务异常。。。");
            }
            JSONObject jsonObject = JSONObject.parseObject(result);
            Integer status = jsonObject.getInteger("status");
            String message = jsonObject.getString("message");
            if (status != 1 || !"success".equalsIgnoreCase(message)) {
                _log.error("____________定时任务获取订单状态信息，调用ERP服务异常___________{}___________", message);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, Utils.translation("定时任务获取订单状态信息，调用ERP服务异常:" + message));
            }
            return jsonObject.getString("data");
        }
    }
}
