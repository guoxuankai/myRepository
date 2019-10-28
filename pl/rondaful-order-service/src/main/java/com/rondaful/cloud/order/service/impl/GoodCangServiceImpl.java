package com.rondaful.cloud.order.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.granary.GranaryUtils;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.entity.SysOrderLog;
import com.rondaful.cloud.order.entity.erpentity.WareHouseDeliverCallBack;
import com.rondaful.cloud.order.entity.goodcang.GoodCangOrder;
import com.rondaful.cloud.order.entity.goodcang.GoodCangSubscibe.GoodCangAccepDto;
import com.rondaful.cloud.order.entity.goodcang.GoodCangSubscibe.GoodCangAcceptFeeDetail;
import com.rondaful.cloud.order.entity.goodcang.GoodCangSubscibe.GoodCangAcceptVO;
import com.rondaful.cloud.order.entity.goodcang.GoodCangSubscibe.GoodCangBackOrderVo;
import com.rondaful.cloud.order.entity.supplier.WarehouseDTO;
import com.rondaful.cloud.order.entity.system.SysOrderPackage;
import com.rondaful.cloud.order.enums.ERPDeliverProcess;
import com.rondaful.cloud.order.enums.GCDeliverStatus;
import com.rondaful.cloud.order.mapper.SysOrderLogMapper;
import com.rondaful.cloud.order.mapper.SysOrderMapper;
import com.rondaful.cloud.order.mapper.SysOrderPackageMapper;
import com.rondaful.cloud.order.rabbitmq.OrderMessageSender;
import com.rondaful.cloud.order.remote.RemoteCmsService;
import com.rondaful.cloud.order.remote.RemoteSupplierService;
import com.rondaful.cloud.order.service.*;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import com.rondaful.cloud.order.utils.TimeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xiangdan
 * @description 拉取订阅谷仓相关接口
 * @date 2019/5/15
 */

@Service
public class GoodCangServiceImpl implements IGoodCangService {
    @Resource
    private SysOrderMapper sysOrderMapper;
    @Resource
    private SystemOrderCommonServiceImpl systemOrderCommonService;

    @Resource
    private SysOrderLogMapper sysOrderLogMapper;

    @Autowired
    private ISystemOrderService iSystemOrderService;

    @Autowired
    private OrderMessageSender orderMessageSender;

    @Autowired
    private ISystemOrderCommonService iSystemOrderCommonService;

    @Autowired
    private RemoteCmsService remoteCmsService;
    @Autowired
    private RemoteSupplierService remoteSupplierService;

    @Autowired
    private SysOrderPackageMapper sysOrderPackageMapper;

    @Autowired
    private ISysOrderExceptionHandelService sysOrderExceptionHandelService;

    @Value("${wsdl.url}")
    private String goodCangUrl;
    @Autowired
    GranaryUtils granaryUtils;

    private static Logger _log = LoggerFactory.getLogger(GoodCangServiceImpl.class);

    //查询谷仓单次最大条数
    private static Integer pageSize = 100;

    @Transactional
    public void getGoodCangOrderList() throws Exception {
        int page = 1;
        String result = "";
        String nextPage = "";
        //获取谷仓所有的仓库ID集合
        List<Integer> goodCangWarehouseIdList = systemOrderCommonService.getGoodCangWarehouseIdList();

        if (CollectionUtils.isEmpty(goodCangWarehouseIdList)) {
            _log.error("调用供应商服务查询谷仓所有的仓库ID集合返回Null,终止本次定时任务查询谷仓订单！");
        }

        //1,查询出谷仓所有配货中的订单id
        List<SysOrderPackage> orderPackageList = sysOrderPackageMapper.getPackageByWarehouseId(goodCangWarehouseIdList);
        if (CollectionUtils.isEmpty(orderPackageList)) {
            _log.error("没有谷仓订单,定时任务结束=============");
            return;
        }

        //谷仓账号名称
        List<String> warehouseIds = orderPackageList.stream().map(x -> String.valueOf(x.getDeliveryWarehouseId()))
                .collect(Collectors.toList());
        //谷仓仓库ID
        List<String> referenceId = orderPackageList.stream().map(SysOrderPackage::getReferenceId).collect(Collectors.toList());

        //2,调用谷仓接口
        do {
            JSONArray jsonArray = this.getGoodCang(page, referenceId, warehouseIds);
            if (CollectionUtils.isNotEmpty(jsonArray)) {
                for (int j = 0; j < jsonArray.size(); j++) {
                    JSONObject jsonObject = JSONObject.parseObject(jsonArray.getString(j));
                    if (jsonObject != null) {
                        if ("Success".equals(jsonObject.getString("ask"))) {
                            nextPage = jsonObject.getString("nextPage");
                            result = jsonObject.getString("data");
                            JSONArray objArray = JSONObject.parseArray(result);
                            if (CollectionUtils.isNotEmpty(objArray)) {
                                for (int i = 0; i < objArray.size(); i++) {
                                    JSONObject obj = (JSONObject) objArray.get(i);
                                    //3,组装回调谷仓数据
                                    WareHouseDeliverCallBack deliverCallBack = this.getGoodCangByObj(obj);
                                    //作废订单进行拦截
                                    if (obj.getString("order_status").equals("X")){
                                        _log.error("谷仓返回作废订单============={}", JSONObject.toJSONString(deliverCallBack));
                                        try {
                                            sysOrderExceptionHandelService.cancellationOrderHandel(deliverCallBack.getOrderTrackId());
                                        } catch (Exception e) {
                                            _log.error("谷仓返回作废订单处理失败============={}", deliverCallBack.getOrderTrackId());
                                        }
                                        continue;
                                    }

                                    boolean isCallWarehouseDeliver = this.isUpdateSysLog(deliverCallBack.getOrderTrackId(), deliverCallBack.getSpeed(),
                                            deliverCallBack.getShipTrackNumber(), null, Constants.WarehouseType.GC);
                                    //4,判断是否调用仓库发货回调方法
                                    if (isCallWarehouseDeliver) {
                                        _log.info("(定时任务获取订单列表)开始调用仓库发货回调方法================》,{}", deliverCallBack);
                                        iSystemOrderService.wareHouseDeliverCallBackNew(deliverCallBack);
                                    }
                                    _log.error("(定时任务获取订单列表)状态未改变无需调用仓库发货回调方法================》,{}", deliverCallBack);
                                }
                            }
                        } else {
                            _log.info("定时拉取谷仓订单异常");
                            throw new GlobalException(jsonObject.getString("errCode"), jsonObject.getString("errMessage"));
                        }
                    }
                }
            }
            page++;
        } while ("true".equals(nextPage));
    }

    /**
     * 根据谷仓订单code查询订单信息 并对比数据库更新信息
     *
     * @param orderCode
     * @return
     * @throws Exception
     */
    public void getOrderByCodeAndUpdateStatus(String orderCode, String referenceNo) throws Exception {
        //调用根据订单号获取单票订单信息
        JSONObject jsonObject = getOrderByCode(orderCode, referenceNo);
        JSONObject object = (JSONObject) jsonObject.get("data");
        // String data = jsonObject.getString("data");
        if (object != null) {
            WareHouseDeliverCallBack deliverCallBack = new WareHouseDeliverCallBack();
            //仓库类型
            deliverCallBack.setWarehouseType("GC");
            //跟踪号
            String trackingNo = object.getString("tracking_no");
            deliverCallBack.setShipTrackNumber(trackingNo == null ? null : trackingNo);
            //平台订单号
            deliverCallBack.setOrderTrackId(object.getString("reference_no") == null ? null : object.getString("reference_no"));
            //进度时间
            deliverCallBack.setUpdateTime(TimeUtil.DateToString2(new Date()));
            //异常原因
            deliverCallBack.setWarehouseShipException(object.getString("abnormal_problem_reason") == null ? null : object.getString("abnormal_problem_reason"));
            //实际物流费
            String fee_details = object.getString("fee_details");
            if (StringUtils.isNotEmpty(fee_details)) {
                JSONObject jsonObject1 = JSONObject.parseObject(fee_details);
                BigDecimal totalFee = jsonObject1.getBigDecimal("totalFee");
                deliverCallBack.setActualShipCost(totalFee);
            }

            //订单状态
            String orderStatus = object.getString("order_status");
            if (StringUtils.isNotEmpty(orderStatus)) {
                //如果有跟踪号并且发货状态为未发货，并且无异常===>修改订单为物流下单状态
                if (StringUtils.isNotEmpty(orderStatus)) {
                    //如果有跟踪号并且发货状态为未发货，并且无异常===>修改订单为物流下单状态
                    if (StringUtils.isNotEmpty(trackingNo) && !orderStatus.equals("D")
                            && !orderStatus.equals("N") && !orderStatus.equals("P")) {
                        deliverCallBack.setSpeed(GCDeliverStatus.PACKAGE_UPLOAD_STATUS.getProcess());
                    } else {
                        //调用匹配物流状态接口
                        deliverCallBack.setSpeed(getOrderStatus(orderStatus));
                    }
                }
            }
            //调用仓库发货回调方法
            _log.info("( 根据谷仓订单code查询订单信息)开始调用仓库发货回调方法================》,{}", deliverCallBack);
            //判断是否调用仓库发货回调方法
            if (this.isUpdateSysLog(deliverCallBack.getOrderTrackId(), deliverCallBack.getSpeed(), trackingNo, null, "GC")) {
                _log.info("(根据谷仓订单code查询订单信息)开始调用仓库发货回调方法================》,{}", deliverCallBack);
                iSystemOrderService.wareHouseDeliverCallBack(deliverCallBack);
            }
            _log.error("(根据谷仓订单code查询订单信息)状态未改变无需调用仓库发货回调方法================》,{}", deliverCallBack);

        }
    }

    /**
     * 接收谷仓订单异常推送
     *
     * @param goodCangAccepDto
     * @throws Exception
     */
    @Override
    public void getAcceptAbnormalOrderList(GoodCangAccepDto goodCangAccepDto) throws Exception {
        GoodCangBackOrderVo goodCangBackOrderVo = JSONObject.parseObject(goodCangAccepDto.getMessage(), GoodCangBackOrderVo.class);
        if (goodCangBackOrderVo != null) {
            WareHouseDeliverCallBack deliverCallBack = new WareHouseDeliverCallBack();
            //仓库类型
            deliverCallBack.setWarehouseType("GC");
            //异常信息
            deliverCallBack.setWarehouseShipException(goodCangBackOrderVo.getError_message() == null ? null : goodCangBackOrderVo.getError_message());
            //进度时间
            deliverCallBack.setUpdateTime(TimeUtil.DateToString2(new Date()));
            //跟踪号
            deliverCallBack.setShipTrackNumber(goodCangBackOrderVo.getTracking_number() == null ? null : goodCangBackOrderVo.getTracking_number());
            //平台订单号
            deliverCallBack.setOrderTrackId(goodCangBackOrderVo.getReference_no());
            //库存状态
            deliverCallBack.setSpeed(ERPDeliverProcess.DISTRIBUTION.getProcess());

            //调用仓库发货回调方法
            _log.info("(接收谷仓订单异常推送API订阅)开始调用仓库发货回调方法================》,{}", deliverCallBack);
            //判断是否调用仓库发货回调方法
            if (this.isUpdateSysLog(deliverCallBack.getOrderTrackId(), deliverCallBack.getSpeed(), deliverCallBack.getShipTrackNumber(), null, "GC")) {
                _log.info("(接收谷仓订单异常推送API订阅)开始调用仓库发货回调方法================》,{}", deliverCallBack);
                iSystemOrderService.wareHouseDeliverCallBack(deliverCallBack);
            }
            _log.error("(接收谷仓订单异常推送API订阅)状态未改变无需调用仓库发货回调方法================》,{}", deliverCallBack);
        }
    }

    /**
     * 接收谷仓订单推送
     *
     * @param goodCangAccepDto
     * @return
     */
    @Override
    public void acceptOrderList(GoodCangAccepDto goodCangAccepDto) throws Exception {
        WareHouseDeliverCallBack deliverCallBack = new WareHouseDeliverCallBack();
        GoodCangAcceptVO goodCangAcceptVO = JSONObject.parseObject(goodCangAccepDto.getMessage(), GoodCangAcceptVO.class);
        if (goodCangAcceptVO != null) {
            String fee_details1 = goodCangAcceptVO.getFee_details();
            GoodCangAcceptFeeDetail goodCangAcceptFeeDetail = JSONObject.parseObject(fee_details1, GoodCangAcceptFeeDetail.class);
            String totalFee = goodCangAcceptFeeDetail.getTotalFee();
            //实际物流费
            deliverCallBack.setActualShipCost(new BigDecimal(totalFee));
            //仓库类型
            deliverCallBack.setWarehouseType(Constants.WarehouseType.GC);
            //进度日期
            deliverCallBack.setUpdateTime(TimeUtil.DateToString2(new Date()));
            //平台订单号
            deliverCallBack.setOrderTrackId(goodCangAcceptVO.getReference_no());
            //跟踪号
            deliverCallBack.setShipTrackNumber(goodCangAcceptVO.getTracking_number());

            //订单状态
            int orderStatus = goodCangAcceptVO.getOrder_status();

            //如果有跟踪号，并且为未出库的状态===>物流下单
            if (StringUtils.isNotEmpty(goodCangAcceptVO.getTracking_number()) && orderStatus == 0) {
                deliverCallBack.setSpeed(GCDeliverStatus.PACKAGE_UPLOAD_STATUS.getProcess());//物流下单
            } else if (orderStatus == 0) {    //未出库
                deliverCallBack.setSpeed(GCDeliverStatus.PENDING_DELIVER.getProcess());
            } else if (orderStatus == 1) { //已出库
                deliverCallBack.setSpeed(GCDeliverStatus.DELIVERED.getProcess());
            }

            //调用仓库发货回调方法
            boolean isCallWarehouseDeliver = this.isUpdateSysLog(deliverCallBack.getOrderTrackId(), deliverCallBack.getSpeed(),
                    deliverCallBack.getShipTrackNumber(), null, Constants.WarehouseType.GC);

            if (isCallWarehouseDeliver) {
                _log.info("(接收谷仓订单推送)开始调用仓库发货回调方法,{}", FastJsonUtils.toJsonString(deliverCallBack));
                iSystemOrderService.wareHouseDeliverCallBackNew(deliverCallBack);
            } else {
                _log.info("(接收谷仓订单推送)状态未改变无需调用仓库发货回调方法,{}", FastJsonUtils.toJsonString(deliverCallBack));
            }
        }
    }


    /**
     * 远程调用谷仓拉取订单
     *
     * @param page
     * @param referenceId
     * @return
     * @throws Exception
     */
    private JSONArray getGoodCang(int page, List<String> referenceId, List<String> warehouseIds) throws Exception {
        //入参
        HashMap map = new HashMap();
        map.put("page", page);
        map.put("pageSize", pageSize);
        map.put("order_code_arr", CollectionUtils.isEmpty(referenceId) ? "[\"\"]" : referenceId);
        String request = JSONArray.toJSONString(map);
        _log.error("调用谷仓获取订单列表入参============》{}", request);
        //GranaryUtils granaryUtils = new GranaryUtils( request, serviceName);
        //granaryUtils.getInstance(request, "getOrderList").getCallService();
        //調用授權賬號接口
        List<WarehouseDTO> warehouseDTOList = new ArrayList<>();
        List<WarehouseDTO> list = new ArrayList<>();
        Map<String, WarehouseDTO> dtoMap = iSystemOrderCommonService.getGCAuthorizeByWarehouseId(warehouseIds);
        for (String key : dtoMap.keySet()) {
            WarehouseDTO warehouseDTO = dtoMap.get(key);
            warehouseDTOList.add(warehouseDTO);
        }
        Map<String, List<WarehouseDTO>> collect = warehouseDTOList.stream().collect(Collectors.groupingBy(WarehouseDTO::getAppToken));
        for (String key : collect.keySet()) {
            list.add(collect.get(key).get(0));
        }
        JSONArray array = new JSONArray();
        for (WarehouseDTO warehouseDTO : list) {
            String getOrderList = granaryUtils.getInstance(warehouseDTO.getAppToken(), warehouseDTO.getAppKey(), null, request, "getOrderList").getCallService();
            array.add(getOrderList);
        }
        _log.error("====================拉取谷仓列表返回====================>{}", array);
        return array;
    }


    /**
     * 根据订单号获取单票订单信息
     *
     * @param orderCode
     * @return
     * @throws Exception
     */
    private JSONObject getOrderByCode(String orderCode, String referenceNo) throws Exception {

        String serviceName = null;
        HashMap map = new HashMap();
        if (StringUtils.isNotEmpty(orderCode)) {
            serviceName = "getOrderByCode";
            map.put("order_code", orderCode);
        }
        if (StringUtils.isNotEmpty(referenceNo)) {
            serviceName = "getOrderByRefCode";
            map.put("reference_no", referenceNo);
        }
        if (StringUtils.isNotEmpty(orderCode) && StringUtils.isNotEmpty(referenceNo)) {
            serviceName = "getOrderByCode";
            map.put("order_code", orderCode);
        }

        //入参
        String request = JSONArray.toJSONString(map);
        _log.info("根据订单号获取单票订单信息============》{}", request);
        //GranaryUtils granaryUtils = new GranaryUtils( request, serviceName);
        String warehouseId = sysOrderMapper.findWarehouseIdById(orderCode, referenceNo);

        Map<String, WarehouseDTO> dtoMap = systemOrderCommonService.getGCAuthorizeByWarehouseId(new ArrayList<String>() {{
            add(warehouseId);
        }});
        WarehouseDTO dto = dtoMap.get(warehouseId);
        JSONObject jsonObject = JSONObject.parseObject(granaryUtils.getInstance(dto.getAppToken(), dto.getAppKey(), null, request, serviceName).getCallService());
        return jsonObject;
//        Map<String, AuthorizeDTO> maps = systemOrderCommonService.getGCAuthorizeByCompanyCode(new HashSet<String>() {{
//            this.add(warehouseCode);
//        }});
//        AuthorizeDTO dto = maps.get(warehouseCode.split("_")[1]);
//        JSONObject jsonObject = JSONObject.parseObject(granaryUtils.getInstance(dto.getAppToken(), dto.getAppKey(), null, request, serviceName).getCallService());
//        return jsonObject;
    }


    @Override
    public String deliverGoodToGoodCang(GoodCangOrder goodCangOrder) throws Exception {
//        String warehouseCode = goodCangOrder.getWarehouse_code();
        String warehouseId = goodCangOrder.getWarehouseId();
        Map<String, WarehouseDTO> map = systemOrderCommonService.getGCAuthorizeByWarehouseId(new ArrayList<String>() {{
            add(warehouseId);
        }});
        WarehouseDTO dto = map.get(warehouseId);

//        Map<String, AuthorizeDTO> map = systemOrderCommonService.getGCAuthorizeByCompanyCode(new HashSet<String>() {{
//            this.add(warehouseCode);
//        }});
//        AuthorizeDTO dto = map.get(warehouseCode.split("_")[1]);
//        goodCangOrder.setWarehouse_code(warehouseCode.substring(warehouseCode.lastIndexOf("_") + 1));
        String paramJson = FastJsonUtils.toJsonString(goodCangOrder);
        _log.info("_______________请求发货的订单JSON格式为：{}_______________", paramJson);
        String result = granaryUtils.getInstance(dto.getAppToken(), dto.getAppKey(), null, paramJson, "createOrder").getCallService();
        _log.info("________________谷仓发货返回结果为：{}____________________", result);
        JSONObject jsonObject = (JSONObject) JSONObject.parse(result);
        String ask = jsonObject.getString("ask");
        String message = jsonObject.getString("message");
        _log.info("___________推送发货订单至谷仓返回结果ask:{}_________Message:{}____________", ask, message);
        if (!"SUCCESS".equalsIgnoreCase(ask) || !"SUCCESS".equalsIgnoreCase(message)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, Utils.translation("发货谷仓创建订单失败，原因：" + message));
        }
        String referenceId = jsonObject.getString("order_code");
        return referenceId;
    }

  /*  @Override
    public void cancelGoodCangOrder(SysOrder sysOrder, String cancelReason) throws Exception {
        String warehouseCode = sysOrder.getDeliveryWarehouseCode();
        Map<String, AuthorizeDTO> map = systemOrderCommonService.getGCAuthorizeByCompanyCode(new HashSet<String>() {{
            this.add(warehouseCode);
        }});
        AuthorizeDTO dto = map.get(warehouseCode.split("_")[1]);
        String serviceName = "cancelOrder";
        HashMap paramMap = new HashMap();
        paramMap.put("order_code", sysOrder.getReferenceId());
        paramMap.put("reason", cancelReason);
        String result = granaryUtils.getInstance(dto.getAppToken(), dto.getAppKey(), null, FastJsonUtils.toJsonString(paramMap), serviceName).getCallService();
        _log.error("________________取消谷仓订单返回结果为：{}____________________", result);
        JSONObject jsonObject = (JSONObject) JSONObject.parse(result);
        String ask = jsonObject.getString("ask");
        String message = jsonObject.getString("message");
        _log.error("___________取消谷仓订单返回结果ask:{}_________Message:{}____________", ask, message);
        if (!"SUCCESS".equalsIgnoreCase(ask) || !"SUCCESS".equalsIgnoreCase(message)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "取消谷仓订单失败。。。");
        }
    }*/


    @Override
    public void goodCangAPISubscribe(GoodCangAccepDto dto) throws Exception {
        String messageType = dto.getMessageType();
        GoodCangBackOrderVo goodCangBackOrderVo = JSONObject.parseObject(dto.getMessage(), GoodCangBackOrderVo.class);
        if ("SendOrder".equalsIgnoreCase(messageType)) {//订单推送
            this.acceptOrderList(dto);
        } else if ("BackOrder".equalsIgnoreCase(messageType)) {//订单异常推送
            if (goodCangBackOrderVo.getReference_no().startsWith("TK")) { //订单异常
                this.getAcceptAbnormalOrderList(dto);
            }
            if (goodCangBackOrderVo.getReference_no().startsWith("SH")) {//售后异常
                //远程调用售后 异常相关接口
                remoteCmsService.getGCErrorOrder(goodCangBackOrderVo);
            }
        } else if ("SendReceiving".equalsIgnoreCase(messageType)) {
            //入库单推送
            JSONObject jsonObject = JSONObject.parseObject(dto.getMessage());
            if (jsonObject != null) {
                JSONArray jsonArray = jsonObject.getJSONArray("receivingDetail");
                for (int i = 0; i < jsonArray.size(); i++) {
                    this.remoteSupplierService.updateInventory(dto.getAppToken(), jsonArray.getJSONObject(i).getString("product_sku"), jsonObject.getString("warehouse_code"));
                }
            }
        } else if ("StockChange".equalsIgnoreCase(messageType)) {
            //库存变更推送
            JSONObject jsonObject = JSONObject.parseObject(dto.getMessage());
            if (jsonObject != null) {
                this.remoteSupplierService.updateInventory(dto.getAppToken(), jsonObject.getString("product_sku"), jsonObject.getString("warehouse_code"));
            }
        } else if ("SendTakeStock".equalsIgnoreCase(messageType)) {//盘点单推送
            _log.error("___________接收谷仓判断单推送___________{}_____________", dto);
        } else if ("AfterSalesReturnOrder".equalsIgnoreCase(messageType)) {//售后退件单推送
            _log.error("___________接收谷仓售后退件单推送___________{}_____________", dto);
        } else {
            _log.error("___________接收谷仓其他业务数据推送___________{}_____________", dto);
        }
    }

    /**
     * 匹配订单状态
     *
     * @param orderStatus
     * @param
     */
    private String getOrderStatus(String orderStatus) {
        switch (orderStatus) {
            case "C":
                return GCDeliverStatus.PENDING_CHECK.getProcess();
            case "W":
                return GCDeliverStatus.PENDING_DELIVER.getProcess();
            case "D":
                return GCDeliverStatus.DELIVERED.getProcess();
            case "H":
                return GCDeliverStatus.TEMPORARILY_SAVE.getProcess();
            case "N":
                return GCDeliverStatus.ABNORMAL_ORDER.getProcess();
            case "P":
                return GCDeliverStatus.PROBLEM_SHIPMENT.getProcess();
            case "X":
                return GCDeliverStatus.DISCARD.getProcess();
        }
        return null;
    }

    /**
     * 判断是否调用发货回传接口
     *
     * @param trackId
     * @param orderStatus
     * @return
     */
    public Boolean isUpdateSysLog(String trackId, String orderStatus, String shipTrackNumber, String shipOrderId, String warehouseType) {

        SysOrderPackage orderPackage = sysOrderPackageMapper.queryOrderPackageByOrderTrackId(trackId);

        //系统订单id
        String sysOrderId = orderPackage.getSysOrderId();

        //根据订单id查询日志信息
        SysOrderLog orderLogByOrderId = sysOrderLogMapper.getOrderLogByOrderId(sysOrderId);

        String speed = orderLogByOrderId.getSpeed();
        //erp判断是取物流商号 还是跟踪号
        if (warehouseType.equals(Constants.WarehouseType.ERP) && (StringUtils.isNotEmpty(shipOrderId)
                || StringUtils.isNotEmpty(shipTrackNumber))) {
            shipTrackNumber = StringUtils.isEmpty(shipTrackNumber) ? shipOrderId : shipTrackNumber;
        }

        //判断状态是否相同
        if (speed != null) {
            if (speed.equals(orderStatus) && StringUtils.isEmpty(shipTrackNumber)) {
                return false;
            }

            //截取跟踪号
            String content = orderLogByOrderId.getContent();
            String trackNum = null;
            if (content.contains("跟踪号【")) {
                trackNum = content.substring(content.lastIndexOf("【") + 1, content.lastIndexOf("】"));
            }

            if (StringUtils.isNotEmpty(shipTrackNumber) && StringUtils.isNotEmpty(trackNum)) {
                if (shipTrackNumber.equals(trackNum) && speed.equals(orderStatus)) {
                    return false;
                }
            }

            if (speed.equals(orderStatus) && StringUtils.isNotEmpty(shipTrackNumber)) {
                return true;
            }
        }
        return true;
    }

    /**
     * 组装谷仓数据
     *
     * @param obj
     * @return
     */
    private WareHouseDeliverCallBack getGoodCangByObj(JSONObject obj) {
        WareHouseDeliverCallBack deliverCallBack = new WareHouseDeliverCallBack();
        deliverCallBack.setWarehouseType(Constants.WarehouseType.GC);  //仓库类型
        //订单跟踪号
        String trackingNo = obj.getString("tracking_no");
        if (!trackingNo.isEmpty()) {
            deliverCallBack.setShipTrackNumber(trackingNo);
        }
        //订单状态
        String orderStatus = obj.getString("order_status");
        if (StringUtils.isNotEmpty(orderStatus)) {
            //如果有跟踪号并且发货状态为未发货，并且无异常===>修改订单为物流下单状态
            if (StringUtils.isNotEmpty(trackingNo) && !orderStatus.equals("D")
                    && !orderStatus.equals("N") && !orderStatus.equals("P")) {
                deliverCallBack.setSpeed(GCDeliverStatus.PACKAGE_UPLOAD_STATUS.getProcess());
            } else {
                //调用匹配物流状态接口
                deliverCallBack.setSpeed(this.getOrderStatus(orderStatus));
            }
        }
        //异常原因
        String abnormalProblemReason = obj.getString("abnormal_problem_reason");
        if (!abnormalProblemReason.isEmpty()) {
            deliverCallBack.setWarehouseShipException(abnormalProblemReason);
        }
        //平台号
        String referenceNo = obj.getString("reference_no");
        if (!referenceNo.isEmpty()) {
            deliverCallBack.setOrderTrackId(referenceNo);
        }
        //进度日期
        deliverCallBack.setUpdateTime(TimeUtil.DateToString2(new Date()));

        String feeDetails = obj.getString("fee_details");
        if (StringUtils.isNotEmpty(feeDetails)) {
            JSONObject feeDetails1 = JSONObject.parseObject(feeDetails);
            BigDecimal totalFee = feeDetails1.getBigDecimal("totalFee");
            //实际物流费
            deliverCallBack.setActualShipCost(totalFee);
        }
        return deliverCallBack;

    }
}