package com.rondaful.cloud.order.service.impl.aliexpress;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.enums.OrderHandleEnum;
import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.common.utils.HttpUtil;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.SysOrderDetail;
import com.rondaful.cloud.order.entity.SysOrderLog;
import com.rondaful.cloud.order.entity.aliexpress.*;
import com.rondaful.cloud.order.entity.orderRule.SellerSkuMap;
import com.rondaful.cloud.order.enums.OrderDeliveryStatusNewEnum;
import com.rondaful.cloud.order.enums.OrderHandleLogEnum;
import com.rondaful.cloud.order.enums.OrderSourceEnum;
import com.rondaful.cloud.order.mapper.SysOrderDetailMapper;
import com.rondaful.cloud.order.mapper.aliexpress.AliexpressOrderChildMapper;
import com.rondaful.cloud.order.mapper.aliexpress.AliexpressOrderMapper;
import com.rondaful.cloud.order.mapper.aliexpress.AliexpressOrderMoneyMapper;
import com.rondaful.cloud.order.mapper.aliexpress.AliexpressOrderReceiptMapper;
import com.rondaful.cloud.order.model.PageDTO;
import com.rondaful.cloud.order.model.aliexpress.dto.ChildOrderDTO;
import com.rondaful.cloud.order.model.aliexpress.request.QueryPageDTO;
import com.rondaful.cloud.order.model.aliexpress.response.OrderDTO;
import com.rondaful.cloud.order.model.aliexpress.response.OrderExportDTO;
import com.rondaful.cloud.order.model.aliexpress.response.OrderOtherDTO;
import com.rondaful.cloud.order.model.dto.syncorder.*;
import com.rondaful.cloud.order.rabbitmq.OrderMessageSender;
import com.rondaful.cloud.order.rabbitmq.RabbitConfig;
import com.rondaful.cloud.order.remote.RemoteSellerService;
import com.rondaful.cloud.order.remote.RemoteUserService;
import com.rondaful.cloud.order.service.ISkuMapService;
import com.rondaful.cloud.order.service.ISysOrderLogService;
import com.rondaful.cloud.order.service.ISysOrderService;
import com.rondaful.cloud.order.service.aliexpress.IAliexpressOrderService;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import com.rondaful.cloud.order.utils.OrderUtils;
import com.rondaful.cloud.order.utils.RateUtil;
import com.rondaful.cloud.order.utils.TimeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * @Author: xqq
 * @Date: 2019/4/3
 * @Description:
 */
@Service("aliexpressOrderServiceImpl")
public class AliexpressOrderServiceImpl implements IAliexpressOrderService {
    private static Logger logger = LoggerFactory.getLogger(AliexpressOrderServiceImpl.class);

    @Autowired
    private AliexpressOrderMapper orderMapper;
    @Autowired
    private AliexpressOrderMoneyMapper orderMoneyMapper;
    @Autowired
    private AliexpressOrderChildMapper orderChildMapper;
    @Autowired
    private AliexpressOrderReceiptMapper orderReceiptMapper;
    @Autowired
    private ISkuMapService skuMapService;
    @Autowired
    private RemoteSellerService remoteSellerService;
    /*@Autowired
    private SysOrderMapper sysOrderMapper;*/
    @Autowired
    private SysOrderDetailMapper sysOrderDetailMapper;
    @Autowired
    private ISysOrderLogService logService;
    @Autowired
    private RateUtil rateUtil;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Value("${taobao.aliexpress.url}")
    private String aliexpressHost;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private ISysOrderService orderService;
    @Autowired
    private OrderMessageSender orderMessageSender;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initData(String createTime, String endTime, String loginId) {
        logger.info("开始拉取速卖通订单:createTime={},endTime={},loginId={}", createTime, endTime, loginId);
        String allRemote = this.remoteSellerService.findAllRemote(3, 1);
        JSONArray remoteJson = JSONObject.parseObject(allRemote).getJSONArray("data");
        if (remoteJson == null || remoteJson.size() < 1) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查不到速卖通卖家的授权信息！");
        }
        for (int a = 0; a < remoteJson.size(); a++) {
            JSONObject remote = JSONObject.parseObject(FastJsonUtils.toJsonString(remoteJson.get(a)));
            if (StringUtils.isNotEmpty(loginId) && !loginId.equals(remote.getString("parentAccount"))) {
                continue;
            }
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setHeader("startTime", createTime);
            messageProperties.setHeader("endTime", endTime);
            messageProperties.setHeader("token", remote.getString("token"));
            messageProperties.setHeader("pinlianId", remote.getInteger("pinlianId"));
            messageProperties.setHeader("pinlianAccount", remote.getString("pinlianAccount"));
            messageProperties.setHeader("empowerId", remote.getInteger("empowerId"));
            messageProperties.setHeader("account", remote.getString("account"));
            Message message = new Message(null, messageProperties);
            this.rabbitTemplate.send(RabbitConfig.EXCHANGE_ALIEXPRESS_CONVERT_ORDER, RabbitConfig.ROUTINGKEY_ALIEXPRESS_CONVERT_ORDER, message, new CorrelationData(UUID.randomUUID().toString()));
        }
    }


    /**
     * 分页查询速卖通订单
     *
     * @param dto
     * @return
     */
    @Override
    public PageDTO<OrderDTO> getPage(QueryPageDTO dto) {
        logger.info("分页查询速卖通订单:dto={}", dto.toString());
        PageHelper.startPage(dto.getCurrentPage(), dto.getPageSize());
        if (StringUtils.isNotEmpty(dto.getPlAccount())) {
            if (CollectionUtils.isEmpty(dto.getUserNames())) {
                dto.setUserNames(new ArrayList<>());
            }
            dto.getUserNames().add(dto.getPlAccount());
        }
        List<AliexpressOrder> list = this.orderMapper.pageByTime(dto);
        PageInfo<AliexpressOrder> pageInfo = new PageInfo<>(list);
        PageDTO<OrderDTO> result = new PageDTO<>(pageInfo.getTotal(), Long.valueOf(pageInfo.getPageNum()));
        List<OrderDTO> dataList = new ArrayList<>();
        for (AliexpressOrder order : pageInfo.getList()) {
            OrderDTO orderDTO = new OrderDTO();
            if (order.getOverTimeLeft() != null && new Date().after(order.getOverTimeLeft())) {
                order.setOverTimeLeft(null);
            }
            orderDTO.setOrder(order);
            orderDTO.setChildList(this.orderChildMapper.getByParentId(order.getOrderId()));
            dataList.add(orderDTO);
        }
        result.setList(dataList);
        return result;
    }

    /**
     * 导出订单
     *
     * @param dto
     * @return
     */
    @Override
    public PageDTO<OrderExportDTO> export(QueryPageDTO dto) {
        logger.info("导出订单:dto={}", dto.toString());
        PageDTO<OrderExportDTO> result = new PageDTO<>();
        List<OrderExportDTO> data = new ArrayList<>();
        PageHelper.startPage(dto.getCurrentPage(), dto.getPageSize());
        if (StringUtils.isNotEmpty(dto.getPlAccount())) {
            if (CollectionUtils.isEmpty(dto.getUserNames())) {
                dto.setUserNames(new ArrayList<>());
            }
            dto.getUserNames().add(dto.getPlAccount());
        }
        List<AliexpressOrder> list = this.orderMapper.pageByTime(dto);
        PageInfo<AliexpressOrder> pageInfo = new PageInfo<>(list);
        result.setTotalCount(pageInfo.getTotal());
        result.setCurrentPage(dto.getCurrentPage().longValue());
        for (AliexpressOrder order : pageInfo.getList()) {
            List<AliexpressOrderChild> childList = this.orderChildMapper.getByParentId(order.getOrderId());
            AliexpressOrderReceipt orderReceipt = this.orderReceiptMapper.getByOrderId(order.getOrderId());
            for (AliexpressOrderChild child : childList) {
                OrderExportDTO exportDTO = new OrderExportDTO();
                BeanUtils.copyProperties(child, exportDTO);
                exportDTO.setGmtCreate(order.getGmtCreate());
                exportDTO.setSellerSignerFullName(order.getSellerSignerFullName());
                exportDTO.setAmount(child.getCurrencyCode() + child.getAmount());
                exportDTO.setProductCount(child.getProductCount());
                exportDTO.setContactPerson(orderReceipt.getContactPerson());
                exportDTO.setCountry(orderReceipt.getCountry());
                exportDTO.setProvince(orderReceipt.getProvince());
                exportDTO.setCity(orderReceipt.getCity());
                StringBuilder address = new StringBuilder();
                if (StringUtils.isNotEmpty(orderReceipt.getDetailAddress())) {
                    address.append(orderReceipt.getDetailAddress());
                }
                if (StringUtils.isNotEmpty(orderReceipt.getAddress2())) {
                    address.append("|");
                    address.append(orderReceipt.getDetailAddress());
                }
                if (StringUtils.isNotEmpty(orderReceipt.getAddress())) {
                    address.append("|");
                    address.append(orderReceipt.getAddress());
                }

                switch (child.getPlProcessStatus()) {
                    case 1:
                        if ("en_us".equals(dto.getI18n())) {
                            exportDTO.setOrderStatus(Utils.translation("转入成功"));
                        } else {
                            exportDTO.setPlProcessStatus("转入成功");
                        }
                        break;
                    case 2:
                        if ("en_us".equals(dto.getI18n())) {
                            exportDTO.setOrderStatus(Utils.translation("转入失败"));
                        } else {
                            exportDTO.setPlProcessStatus("转入失败");
                        }
                        break;
                }
                exportDTO.setAddress(address.toString());
                exportDTO.setZip(orderReceipt.getZip());
                if (StringUtils.isEmpty(orderReceipt.getMobileNo())) {
                    exportDTO.setMobileNo(orderReceipt.getPhoneCountry() + " " + orderReceipt.getPhoneArea() + " " + orderReceipt.getPhoneNumber());
                } else {
                    exportDTO.setMobileNo(orderReceipt.getMobileNo());
                }
                switch (order.getOrderStatus()) {
                    case "PLACE_ORDER_SUCCESS":
                        if ("en_us".equals(dto.getI18n())) {
                            exportDTO.setOrderStatus(Utils.translation("等待买家付款"));
                        } else {
                            exportDTO.setOrderStatus("等待买家付款");
                        }
                        break;
                    case "IN_CANCEL":
                        if ("en_us".equals(dto.getI18n())) {
                            exportDTO.setOrderStatus(Utils.translation("买家申请取消"));
                        } else {
                            exportDTO.setOrderStatus("买家申请取消");
                        }
                        break;
                    case "WAIT_SELLER_SEND_GOODS":
                        if ("en_us".equals(dto.getI18n())) {
                            exportDTO.setOrderStatus(Utils.translation("等待您发货"));
                        } else {
                            exportDTO.setOrderStatus("等待您发货");
                        }
                        break;
                    case "SELLER_SEND_GOODS":
                        if ("en_us".equals(dto.getI18n())) {
                            exportDTO.setOrderStatus(Utils.translation("已发货"));
                        } else {
                            exportDTO.setOrderStatus("已发货");
                        }
                        break;
                    case "SELLER_PART_SEND_GOODS":
                        if ("en_us".equals(dto.getI18n())) {
                            exportDTO.setOrderStatus(Utils.translation("等待买家付款"));
                        }
                        exportDTO.setOrderStatus(Utils.translation("部分发货"));
                        break;
                    case "WAIT_BUYER_ACCEPT_GOODS":
                        if ("en_us".equals(dto.getI18n())) {
                            exportDTO.setOrderStatus(Utils.translation("等待买家收货"));
                        } else {
                            exportDTO.setOrderStatus("等待买家收货");
                        }
                        break;
                    case "FUND_PROCESSING":
                        if ("en_us".equals(dto.getI18n())) {
                            exportDTO.setOrderStatus(Utils.translation("买卖家达成一致，资金处理中"));
                        } else {
                            exportDTO.setOrderStatus("买卖家达成一致，资金处理中");
                        }
                        break;
                    case "IN_ISSUE":
                        if ("en_us".equals(dto.getI18n())) {
                            exportDTO.setOrderStatus(Utils.translation("含纠纷中的订单"));
                        } else {
                            exportDTO.setOrderStatus("含纠纷中的订单");
                        }
                        break;
                    case "WAIT_SELLER_EXAMINE_MONEY":
                        if ("en_us".equals(dto.getI18n())) {
                            exportDTO.setOrderStatus(Utils.translation("等待您确认金额"));
                        } else {
                            exportDTO.setOrderStatus("等待您确认金额");
                        }
                        break;
                    case "RISK_CONTROL":
                        if ("en_us".equals(dto.getI18n())) {
                            exportDTO.setOrderStatus(Utils.translation("订单处于风控24小时中"));
                        } else {
                            exportDTO.setOrderStatus("订单处于风控24小时中");
                        }
                        break;
                    case "FINISH":
                        if ("en_us".equals(dto.getI18n())) {
                            exportDTO.setOrderStatus(Utils.translation("已结束的订单"));
                        } else {
                            exportDTO.setOrderStatus("已结束的订单");
                        }
                        break;
                }
                data.add(exportDTO);
            }
        }
        result.setList(data);
        return result;
    }

    /**
     * 根据订单获取物流  金钱相关信息
     *
     * @param orderId
     * @return
     */
    @Override
    public OrderOtherDTO getOrderData(String orderId) {
        logger.info("根据订单编号:{} 查询订单款项与收货方详情", orderId);
        OrderOtherDTO result = new OrderOtherDTO();
        result.setOrderMoney(this.orderMoneyMapper.getByOrderId(orderId));
        result.setOrderReceipt(this.orderReceiptMapper.getByOrderId(orderId));
        return result;
    }

    /**
     * 根据订单号转入系统订单
     *
     * @param orderIds
     * @return
     */
    @Override
    public Integer toSysOrder(List<String> orderIds) {
        List<SysOrderDTO> sysOrders = this.convertToSysOrder(orderIds);
        if (CollectionUtils.isEmpty(sysOrders)) {
            this.fileStatus(orderIds);
            return 4;
        }
        try {
            sysOrders = this.skuMapService.orderMapByOrderListNew(OrderRuleEnum.platformEnm.ALIEXPRESS.getPlatform(), sysOrders);
        } catch (Exception e) {
            this.fileStatus(orderIds);
            if ("ORDER_MAP_ERROR".equals(e.getMessage())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "转系统订单全部映射失败。。。");
            }
            return 2;
        }
        SysOrderTransferInsertOrUpdateDTO dto = this.orderService.splitInsertSysOrderData(sysOrders);
        try {
            this.orderService.insertSysOrderBatch(dto);
        } catch (Exception e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "转系统订单全部映射失败。。。");
        }
        List<UpdateSourceOrderDetailDTO> detailStatus = dto.getUpdateSourceOrderDetailDTOList();

        List<AliexpressOrderChild> cStatus = new ArrayList<>(detailStatus.size());
        for (UpdateSourceOrderDetailDTO detailDTO : detailStatus) {
            AliexpressOrderChild childStatus = new AliexpressOrderChild();
            childStatus.setPlProcessStatus(detailDTO.getConverSysStatus());
            childStatus.setOrderId(detailDTO.getOrderItemId());
            cStatus.add(childStatus);
        }
        this.orderChildMapper.updateBatchStatus(cStatus);
        List<UpdateSourceOrderDTO> orderStatus = dto.getUpdateSourceOrderDTOList();
        Map<String, Byte> map = new HashMap<>();
        orderStatus.forEach(aa -> {
            map.put(aa.getOrderId(), aa.getConverSysStatus().byteValue());
        });
        this.orderMapper.updateBatchStatus(map);
        return 1;
    }

    /**
     * 转入失败时更改状态
     *
     * @param orderIds
     */
    private void fileStatus(List<String> orderIds) {
        if (CollectionUtils.isEmpty(orderIds)) {
            return;
        }
        this.orderChildMapper.updateStatusByParentId(orderIds);
        Map<String, Byte> status = new HashMap<>();
        for (String orderId : orderIds) {
            status.put(orderId, OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue());
        }
        this.orderMapper.updateBatchStatus(status);
    }


    /**
     * 异步同步订单
     *
     * @param paramsData
     * @return
     */
    @Override
    //@Transactional(rollbackFor = Exception.class)
    public void asyncOrder(String paramsData) {
        logger.info("开始拉订单:paramsData={}", paramsData);
        JSONObject remote = JSONObject.parseObject(paramsData);
        Map<String, String> params = new HashMap<>();
        params.put("loginId", null);
        params.put("createStartDate", remote.getString("startTime"));
        params.put("createEndDate", remote.getString("endTime"));

        //params.put("orderStatus","FINISH");
        List<String> orderStatusList = new ArrayList<>();
        orderStatusList.add("IN_CANCEL");
        orderStatusList.add("WAIT_SELLER_SEND_GOODS");
        orderStatusList.add("SELLER_PART_SEND_GOODS");
        orderStatusList.add("WAIT_BUYER_ACCEPT_GOODS");
        orderStatusList.add("FUND_PROCESSING");
        orderStatusList.add("IN_ISSUE");
        orderStatusList.add("IN_FROZEN");
        //orderStatusList.add("FINISH");
        params.put("orderStatusList", JSONObject.toJSONString(orderStatusList));

        Integer currentPage = 1;
        Boolean hasNext = false;
        do {
            params.put("currentPage", currentPage.toString());
            params.put("pageSize", "20");
            params.put("token", remote.getString("token"));

            String res = HttpUtil.post(aliexpressHost + "aliexpress/order/getPage", params);
            if (StringUtils.isEmpty(res)) {
                logger.error("无法调通聚石塔内服务");
                return;
            }

            JSONObject resJson = JSONObject.parseObject(res).getJSONObject("data");
            if (resJson == null) {
                resJson = JSONObject.parseObject(res);
                logger.error("调用服务异常:msg={}", resJson.getString("msg"));
                return;
            }
            if (resJson.getInteger("totalCount") > currentPage * resJson.getInteger("pageSize")) {
                currentPage++;
                hasNext = true;
            } else {
                hasNext = false;
            }
            List<String> orderIds = new ArrayList<>();
            JSONArray datas = resJson.getJSONArray("data");
            List<AliexpressOrder> orders = new ArrayList<>();
            List<AliexpressOrderChild> childList = new ArrayList<>();
            List<AliexpressOrderMoney> orderMoneys = new ArrayList<>();
            List<AliexpressOrderReceipt> orderReceipts = new ArrayList<>();
            if (datas != null && datas.size() > 0) {
                for (int i = 0; i < datas.size(); i++) {
                    JSONObject data = JSONObject.parseObject(FastJsonUtils.toJsonString(datas.get(i)));
                    AliexpressOrder aliexpressOrder = JSONObject.parseObject(data.toJSONString(), AliexpressOrder.class);
                    if (this.orderMapper.getByOrderId(data.getString("orderId")) != null) {
                        continue;
                    }

                    aliexpressOrder.setPlSellerId(remote.getInteger("pinlianId"));
                    aliexpressOrder.setPlAccount(remote.getString("pinlianAccount"));
                    aliexpressOrder.setCallBackStatus(OrderHandleEnum.MarkDeliverStatus.MARK_WAIT.getValue());
                    aliexpressOrder.setCreatTime(new Date());
                    aliexpressOrder.setSellerLoginId(remote.getString("account"));
                    aliexpressOrder.setUpdateTime(aliexpressOrder.getCreatTime());
                    aliexpressOrder.setEmpowerId(remote.getInteger("empowerId"));
                    aliexpressOrder.setPlProcessStatus(OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue());
                    AliexpressOrderMoney orderMoney = JSONObject.parseObject(data.getJSONObject("moneyStatusDTO").toJSONString(), AliexpressOrderMoney.class);
                    orderMoney.setLoanTime(StringUtils.isEmpty(data.getJSONObject("moneyStatusDTO").getString("loanTime")) ? null : DateUtils.parseDate(data.getJSONObject("moneyStatusDTO").getString("loanTime"), DateUtils.FORMAT_2));
                    orderMoney.setCreateTime(aliexpressOrder.getCreatTime());
                    orderMoney.setUpdateTime(aliexpressOrder.getCreatTime());
                    orderMoney.setOrderId(data.getString("orderId"));
                    aliexpressOrder.setGmtPayTime(orderMoney.getGmtPayTime());
                    AliexpressOrderReceipt receipt = JSONObject.parseObject(data.getJSONObject("addressDTO").toJSONString(), AliexpressOrderReceipt.class);
                    receipt.setOrderId(data.getString("orderId"));
                    receipt.setCreateTime(orderMoney.getCreateTime());
                    receipt.setUpdateTime(orderMoney.getCreateTime());
                    aliexpressOrder.setReceiptCountry(receipt.getCountry());
                    List<AliexpressOrderChild> orderChildList = JSONObject.parseArray(data.getJSONArray("childOrder").toJSONString(), AliexpressOrderChild.class);
                    for (AliexpressOrderChild childOrder : orderChildList) {
                        childOrder.setParentOrderId(data.getString("orderId"));
                        SellerSkuMap sellerSku = new SellerSkuMap();
                        sellerSku.setPlatform("aliexpress");
                        sellerSku.setPlatformSku(childOrder.getSkuCode());
                        sellerSku.setAuthorizationId(remote.getString("empowerId"));
                        sellerSku.setStatus(1);
                        sellerSku = skuMapService.selectByEntry(sellerSku);
                        childOrder.setCreateTime(aliexpressOrder.getCreatTime());
                        childOrder.setPlProcessStatus(Integer.valueOf(OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue()));
                        if (sellerSku != null) {
                            childOrder.setPlSkuCode(sellerSku.getPlSku());
                        }

                        /*if (StringUtils.isNotEmpty(childOrder.getSkuCode())&&!orderIds.contains(data.getString("orderId"))){
                            orderIds.add(data.getString("orderId"));
                        }*/
                        childOrder.setUpdateTime(aliexpressOrder.getCreatTime());
                    }
                    orderIds.add(aliexpressOrder.getOrderId());
                    orders.add(aliexpressOrder);
                    orderMoneys.add(orderMoney);
                    orderReceipts.add(receipt);
                    childList.addAll(orderChildList);
                }
            }
            if (CollectionUtils.isNotEmpty(orders)) {
                this.orderMapper.inserts(orders);
                this.orderMoneyMapper.inserts(orderMoneys);
                this.orderChildMapper.inserts(childList);
                this.orderReceiptMapper.inserts(orderReceipts);
//
                if (CollectionUtils.isNotEmpty(orderIds)) {

                    // 发送转单消息
                    try {
                        this.toSysOrder(orderIds);
//                        orderMessageSender.sendBaseConvertOrder(orderIds, OrderSourceEnum.CONVER_FROM_ALIEXPRESS);
                    } catch (Exception e) {
                        logger.error("Aliexpress发送转单消息异常", e);
                    }
                }
            }
        } while (hasNext);
    }

    /**
     * 根据父订单号获取子订单列表
     *
     * @param orderId
     * @return
     */
    @Override
    public List<AliexpressOrderChild> getChilds(String orderId) {
        return this.orderChildMapper.getByParentId(orderId);
    }

    /**
     * 更新订单状态
     *
     * @param orderId
     * @param orderStatus
     * @return
     */
    @Override
    public Integer syncOrder(String orderId, String orderStatus, String loginId) {
        logger.info("订单:orderId={},orderStatus={}", orderId, orderStatus);
        Integer result = null;
        AliexpressOrder aliexpressOrder = new AliexpressOrder();
        aliexpressOrder.setOrderId(orderId);
        aliexpressOrder.setUpdateTime(new Date());
        switch (orderStatus) {
            case "aliexpress_order_WaitSellerSendGoods":
                result = this.newOrder(orderId, loginId);
                if (result == 1) {
                    List<String> orderIds = new ArrayList<>();
                    orderIds.add(orderId);
                    this.toSysOrder(orderIds);
                }
                return result;
            case "aliexpress_order_Finish":
                aliexpressOrder.setOrderStatus(OrderHandleEnum.aliexpressOrderStatus.FINISH.toString());
                break;
            case "aliexpress_order_WaitBuyerAcceptGoods":
                aliexpressOrder.setOrderStatus(OrderHandleEnum.aliexpressOrderStatus.WAIT_BUYER_ACCEPT_GOODS.toString());
                break;
            case "aliexpress_order_SellerPartSendGoods":
                aliexpressOrder.setOrderStatus(OrderHandleEnum.aliexpressOrderStatus.SELLER_PART_SEND_GOODS.toString());
                break;
            default:
                return 0;
        }
        return this.orderMapper.updateByOrderId(aliexpressOrder);
    }

    /**
     * 订单物流回传
     *
     * @param logisticsOrderId
     * @param serviceName
     * @param orderId
     * @param token
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void callBack(String logisticsOrderId, String serviceName, String orderId, String token, String sendType) {
        logger.info("修改物流状态：logisticsOrderId={},orderId={}", logisticsOrderId, orderId);
//        AliexpressOrder aliexpressOrder = this.orderMapper.getByOrderId(orderId);
//        if (aliexpressOrder == null) {
//            return;
//        }
//        String allRemote = this.remoteSellerService.findOneEmpowByAccount(3, null, null, aliexpressOrder.getEmpowerId().toString());
//        JSONObject remoteJson = JSONObject.parseObject(allRemote).getJSONObject("data");
//        if (remoteJson == null || remoteJson.size() < 1) {
//            logger.error("查不到速卖通卖家的授权信息:empowerId={}", aliexpressOrder.getEmpowerId().toString());
//            return;
//        }
        Map<String, String> params = new HashMap<>();
        params.put("sendType", sendType);
        params.put("logisticsNo", logisticsOrderId);
        params.put("serviceName", serviceName);
        params.put("outRef", orderId);
        params.put("token", token);
        String res = HttpUtil.post(aliexpressHost + "aliexpress/logistics/ship", params);
        Byte callStatus = 2;
        if (StringUtils.isNotEmpty(res)) {
            if ("200".equals(JSONObject.parseObject(res).getString("code"))) {
                callStatus = 1;
            }
        }
        AliexpressOrderChild child = new AliexpressOrderChild();
        child.setParentOrderId(orderId);
        child.setCallBack(callStatus);
        child.setLogisticsOrderId(logisticsOrderId);
        child.setLogisticsServiceName(serviceName);
        child.setPlProcessStatus(1);
        this.orderChildMapper.updateBatchLog(child);
        AliexpressOrder order = new AliexpressOrder();
        order.setOrderId(orderId);
        order.setCallBackStatus(callStatus);
        order.setLogisticsStatus("5");
        order.setPlLogisticsStatus("5");
        order.setOrderStatus("SELLER_SEND_GOODS");
        order.setPlLogisticsTime(new Date());
        this.orderMapper.updateByPrimaryKeySelective(order);
    }

    /**
     * 更改sku绑定
     *
     * @param list
     * @return
     */
    @Override
    public Integer updatesSku(List<AliexpressOrderChild> list, List<String> unList) {
        if (CollectionUtils.isNotEmpty(unList)) {
            this.orderChildMapper.handUntieSku(unList);
        }
        if (CollectionUtils.isNotEmpty(list)) {
            List<String> pOrderId = this.orderChildMapper.getPOrderId(list);
            if (CollectionUtils.isNotEmpty(pOrderId)) {
                logger.info("查出速卖通待转入的订单数据：{}", pOrderId);
                this.toSysOrder(pOrderId);
            }
            return this.orderChildMapper.handInsertSku(list);
        }
        return 0;
    }

    /**
     * 根据子订单号查询订单信息
     *
     * @param orderId
     * @return
     */
    @Override
    public ChildOrderDTO getByChild(String orderId) {
        if (StringUtils.isEmpty(orderId)) {
            return null;
        }
        AliexpressOrderChild childDO = this.orderChildMapper.getByOrderId(orderId);
        if (childDO == null) {
            return null;
        }
        ChildOrderDTO result = new ChildOrderDTO();
        BeanUtils.copyProperties(childDO, result);
        AliexpressOrder orderDO = this.orderMapper.getByOrderId(childDO.getParentOrderId());
        result.setOverTimeLeft(orderDO.getOverTimeLeft());
        result.setOrderStatus(orderDO.getOrderStatus());
        result.setSellerLoginId(orderDO.getSellerLoginId());
        return result;
    }

    /**
     * 根据订单id获取平台订单对象
     *
     * @param orderIds
     * @return
     */
    private List<SysOrderDTO> convertToSysOrder(List<String> orderIds) {
        if (CollectionUtils.isEmpty(orderIds)) {
            return null;
        }
        List<SysOrderDTO> result = new ArrayList<>();
        List<AliexpressOrder> aliexpressOrders = this.orderMapper.getsByOrderId(orderIds);
        for (AliexpressOrder aliexpressOrder : aliexpressOrders) {
            if (aliexpressOrder.getPlProcessStatus() != null && aliexpressOrder.getPlProcessStatus() == OrderHandleEnum.ConverSysStatus.CONVER_SUCCESS.getValue()) {
                continue;
            }
            //SysOrder order = new SysOrder();
            SysOrderDTO order = new SysOrderDTO();
            order.setSourceOrderId(aliexpressOrder.getOrderId());
            order.setSysOrderPackageList(new ArrayList<>());

            String allRemote = this.remoteSellerService.findOneEmpowByAccount(3, null, null, aliexpressOrder.getEmpowerId().toString());
            JSONObject remoteJson = JSONObject.parseObject(allRemote).getJSONObject("data");
            order.setShopType(remoteJson.getString("rentStatus"));


            order.setSellerPlId(aliexpressOrder.getPlSellerId());
            order.setEmpowerId(aliexpressOrder.getEmpowerId());
            order.setPlatformShopId(aliexpressOrder.getEmpowerId());
            //order.setSite(aliexpressOrder.getSellerSignerFullName());

            /**
             * 平台订单号id  暂时不想传
             * order.setSysOrderId(null);
             */
            //order.setSysOrderId(aliexpressOrder.getOrderId());

            //order.setMainOrderId(aliexpressOrder.getOrderId());
            // todo  最迟发货时间
            AliexpressOrderReceipt orderReceipt = this.orderReceiptMapper.getByOrderId(aliexpressOrder.getOrderId());
            order.setOrderSource((int) OrderSourceEnum.CONVER_FROM_ALIEXPRESS.getValue());
            order.setPlatformSellerAccount(aliexpressOrder.getSellerLoginId());
            order.setBuyerName(aliexpressOrder.getBuyerSignerFullName());
            order.setPlatformSellerId(aliexpressOrder.getSellerLoginId());
            order.setSellerPlAccount(aliexpressOrder.getPlAccount());
            order.setShippingServiceCost(aliexpressOrder.getLogisticsAmount());
            order.setDeliverDeadline(TimeUtil.DateToString2(aliexpressOrder.getOverTimeLeft()));
            order.setCreateBy(aliexpressOrder.getPlAccount());
            order.setUpdateBy(aliexpressOrder.getPlAccount());


            AliexpressOrderMoney orderMoney = this.orderMoneyMapper.getByOrderId(aliexpressOrder.getOrderId());
            //todo 这个地方付款金额转成人名币
            String rate = this.rateUtil.remoteExchangeRateByCurrencyCode(orderMoney.getSettlementCurrency(), "USD");
//            order.setCommoditiesAmount(StringUtils.isEmpty(rate) ? orderMoney.getPayAmountBySettlementCur() : orderMoney.getPayAmountBySettlementCur().multiply(new BigDecimal(rate)));
            order.setPlatformTotalPrice(StringUtils.isEmpty(rate) ? OrderUtils.calculateMoney(orderMoney.getPayAmountBySettlementCur(),true) :
                    OrderUtils.calculateMoney(orderMoney.getPayAmountBySettlementCur().multiply(new BigDecimal(rate)),true));
            order.setOrderTime(TimeUtil.DateToString2(aliexpressOrder.getGmtCreate()));

            //TODO  地址
            SysOrderReceiveAddressDTO addressDTO = new SysOrderReceiveAddressDTO();
            addressDTO.setShipToName(orderReceipt.getContactPerson());
            addressDTO.setShipToCountryName(orderReceipt.getCountry());
            addressDTO.setShipToCountry(orderReceipt.getCountry());
            addressDTO.setShipToState(orderReceipt.getProvince());
            addressDTO.setShipToCity(orderReceipt.getCity());
            addressDTO.setShipToAddrStreet1(orderReceipt.getDetailAddress());
            addressDTO.setShipToAddrStreet2(orderReceipt.getAddress2());
            addressDTO.setShipToAddrStreet3(orderReceipt.getAddress());
            addressDTO.setShipToPostalCode(orderReceipt.getZip());
            if (StringUtils.isEmpty(orderReceipt.getMobileNo())) {
                addressDTO.setShipToPhone(orderReceipt.getPhoneCountry() + " " + orderReceipt.getPhoneArea() + " " + orderReceipt.getPhoneNumber());
            } else {
                addressDTO.setShipToPhone(orderReceipt.getMobileNo());
            }

            order.setSysOrderReceiveAddress(addressDTO);
            order.setPayId(orderMoney.getOrderId());


            //支付状态不用管  我也不知道
            /*if ("NO_FROZEN".equals(orderMoney.getFundStatus())) {
                order.setPayStatus((byte) 11);
            } else if (aliexpressOrder.getGmtPayTime() != null) {
                order.setPayStatus((byte) 21);
            } else {
                order.setPayStatus((byte) 20);
            }
            order.setPayMethod((byte) 5);*/

            order.setCreateDate(new Date());
            order.setUpdateDate(order.getCreateDate());
            order.setPayTime(TimeUtil.DateToString2(aliexpressOrder.getGmtCreate()));
            order.setBuyerUserId(aliexpressOrder.getBuyerLoginId());
            order.setBuyerName(aliexpressOrder.getBuyerSignerFullName());
            order.setBuyerCheckoutMessage(aliexpressOrder.getMemo());

            /**
             * 收货人没有邮箱
             * order.setShipToEmail(null);
             */
            List<SysOrderDetailDTO> sysOrderDetails = new ArrayList<>();

            List<AliexpressOrderChild> childList = this.orderChildMapper.getByParentId(aliexpressOrder.getOrderId());

            StringBuilder childIds = new StringBuilder();

            BigDecimal amount = new BigDecimal("0.00");

            for (AliexpressOrderChild child : childList) {
                if (StringUtils.isEmpty(child.getPlSkuCode())) {
                    SellerSkuMap sellerSku = new SellerSkuMap();
                    sellerSku.setPlatform("aliexpress");
                    sellerSku.setPlatformSku(child.getSkuCode());
                    sellerSku.setAuthorizationId(aliexpressOrder.getEmpowerId().toString());
                    sellerSku.setStatus(1);
                    sellerSku = skuMapService.selectByEntry(sellerSku);
                    if (sellerSku != null) {
                        child.setPlSkuCode(sellerSku.getPlSku());
                        this.orderChildMapper.updateByPrimaryKeySelective(child);
                    }

                }
                if (child.getPlProcessStatus() != null && child.getPlProcessStatus() == 1) {
                    amount = amount.add(child.getAmount().multiply(new BigDecimal(child.getProductCount())));
                    continue;
                }
                SysOrderDetailDTO sysOrderDetail = new SysOrderDetailDTO();
                sysOrderDetail.setRemark(child.getBuyerMemo());
                /**
                 * 品连的订单id暂时不想传
                 * sysOrderDetail.setSysOrderId(null);
                 */
                sysOrderDetail.setSourceOrderId(aliexpressOrder.getOrderId());
                sysOrderDetail.setSourceOrderLineItemId(StringUtils.isEmpty(child.getOrderId()) ? aliexpressOrder.getOrderId() : child.getOrderId());
                sysOrderDetail.setCreateDate(order.getCreateDate());
                sysOrderDetail.setUpdateDate(order.getCreateDate());
                /**
                 * 不想传单项的id
                 * sysOrderDetail.setOrderLineItemId(null);
                 */
                sysOrderDetail.setPlatformSKU(child.getSkuCode());
                sysOrderDetail.setSkuQuantity(child.getProductCount());
                sysOrderDetail.setCreateBy(order.getCreateBy());
                sysOrderDetail.setUpdateBy(order.getUpdateBy());
                sysOrderDetail.setRemark(child.getBuyerMemo());

                sysOrderDetails.add(sysOrderDetail);
                childIds.append(child.getOrderId());
                childIds.append("#");
            }
            if (CollectionUtils.isEmpty(sysOrderDetails)) {
                continue;
            }
            /**
             * 包含多个子订单  部分订单转单时分摊运费及实付
             */
//            if (childList.size() != sysOrderDetails.size()) {
//                BigDecimal childRate = new BigDecimal("1.0").subtract(amount.divide(orderMoney.getPayAmountBySettlementCur()));
//                order.setShippingServiceCost(aliexpressOrder.getLogisticsAmount().multiply(childRate));
//                order.setCommoditiesAmount(orderMoney.getPayAmountBySettlementCur().multiply(childRate));
//            }
            //order.setChildIds(childIds.toString());
            order.setSysOrderDetailList(sysOrderDetails);
            order.setSysOrderPackageList(new ArrayList<SysOrderPackageDTO>() {{
                add(new SysOrderPackageDTO());
            }});
            result.add(order);
        }
        return result;
    }

    /**
     * 将SKU映射结果分类并存储
     *
     * @param sysOrdersAfterMap
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Byte> splitResultData(List<SysOrder> sysOrdersAfterMap, List<String> orderIds) {
        if (CollectionUtils.isEmpty(sysOrdersAfterMap)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "进行SKU映射和邮寄规则匹配返回结果集合为空。。。");
        }
        //接收转入成功的系统订单
        List<SysOrder> persistSysOrderList = new ArrayList<>();
        //接收转入成功的系统订单项
        List<SysOrderDetail> persistSysOrderDetailList = new ArrayList<>();
        //插入日志
        List<SysOrderLog> logs = new ArrayList<>();

        List<AliexpressOrderChild> childList = new ArrayList<>();
        Map<String, Byte> status = new HashMap<>();
        try {
            for (SysOrder sysOrder : sysOrdersAfterMap) {
                List<SysOrderDetail> sysOrderDetails = sysOrder.getSysOrderDetails();
                if (CollectionUtils.isEmpty(sysOrderDetails)) {
                    AliexpressOrderChild child = new AliexpressOrderChild();
                    child.setPlProcessStatus((int) OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue());
                    child.setOrderId(sysOrder.getSourceOrderId());
                    child.setParentOrderId(sysOrder.getSourceOrderId());
                    childList.add(child);
                    status.put(sysOrder.getSourceOrderId(), OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue());
                    logger.info("平台订单{}进行SKU映射，和邮寄规则匹配返回结果List中系统订单项为空。。。", sysOrder.getSourceOrderId());
                    continue;
                }
                sysOrder.setSysOrderId(OrderUtils.getPLOrderNumber());
                //用于发货重复推单
                sysOrder.setOrderTrackId(OrderUtils.getPLTrackNumber());
                sysOrder.setOrderSource(OrderSourceEnum.CONVER_FROM_ALIEXPRESS.getValue());
                sysOrder.setOrderDeliveryStatus(OrderDeliveryStatusNewEnum.WAIT_PAY.getValue());
                for (SysOrderDetail detail : sysOrderDetails) {
                    //映射成功
                    AliexpressOrderChild child = new AliexpressOrderChild();
                    child.setParentOrderId(sysOrder.getSourceOrderId());
                    child.setOrderId(detail.getSourceOrderLineItemId());
                    if (detail.getConverSysDetailStatus() == (byte) 1) {
                        detail.setSysOrderId(sysOrder.getSysOrderId());
                        detail.setOrderLineItemId(OrderUtils.getPLOrderItemNumber());
                        persistSysOrderDetailList.add(detail);
                        child.setPlProcessStatus(detail.getConverSysDetailStatus().intValue());
                        if (status.containsKey(sysOrder.getSourceOrderId())) {
                            if (!status.get(sysOrder.getSourceOrderId()).equals(detail.getConverSysDetailStatus())) {
                                status.put(sysOrder.getSourceOrderId(), OrderHandleEnum.ConverSysStatus.CONVER_PORTION_SUCCESS.getValue());
                            }
                        } else {
                            status.put(sysOrder.getSourceOrderId(), detail.getConverSysDetailStatus());
                        }

                        childList.add(child);
                    } else {
                        child.setPlProcessStatus((int) OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue());
                        childList.add(child);
                        if (status.containsKey(sysOrder.getSourceOrderId())) {
                            if (!status.get(sysOrder.getSourceOrderId()).equals(detail.getConverSysDetailStatus())) {
                                status.put(sysOrder.getSourceOrderId(), OrderHandleEnum.ConverSysStatus.CONVER_PORTION_SUCCESS.getValue());
                            }
                        } else {
                            status.put(sysOrder.getSourceOrderId(), OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue());
                        }
                    }
                }
                if (sysOrder.getConverSysStatus() == (byte) 1 || sysOrder.getConverSysStatus() == (byte) 3) {
                    persistSysOrderList.add(sysOrder);
                    SysOrderLog log = new SysOrderLog(sysOrder.getSysOrderId(), OrderHandleLogEnum.Content.NEW_ORDER.newOrder(sysOrder.getSysOrderId()), OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(), sysOrder.getSellerPlAccount());
                    log.setCreateDate(new Date());
                    logs.add(log);
                }
            }
        } catch (Exception e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "SKU映射结果分类出错。。。");
        }
        if (CollectionUtils.isNotEmpty(persistSysOrderList)) {
            //sysOrderMapper.inserts(persistSysOrderList);
            sysOrderDetailMapper.insertBatch(persistSysOrderDetailList);
            this.logService.inserts(logs);
            this.orderChildMapper.updateBatchStatus(childList);
            this.orderMapper.updateBatchStatus(status);
            return status;
        }
        return status;
    }

    /**
     * 新来的订单插入数据
     *
     * @param orderId
     * @param loginId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    protected Integer newOrder(String orderId, String loginId) {
        JSONObject remoteJson = null;
        String resp = null;
        String sellerLoginId = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(aliexpressHost + "aliexpress/order/get");
            uriBuilder.addParameter("orderId", orderId);

            String allRemote = this.remoteSellerService.findOneEmpowByAccount(3, loginId, null, null);
            remoteJson = JSONObject.parseObject(allRemote).getJSONObject("data");
            if (remoteJson == null) {
                logger.error("获取{}的授权token为空", loginId);
                return 0;
            }
            uriBuilder.addParameter("token", remoteJson.getString("token"));
            sellerLoginId = remoteJson.getString("account");
            resp = HttpUtil.get(uriBuilder.toString());
        } catch (Exception e) {
            logger.error("监听转换订单异常", e.getMessage(), e);
            return 0;
        }
        if (StringUtils.isEmpty(resp)) {
            logger.error("未获取到远程订单");
            return 0;
        }
        JSONObject respJson = JSONObject.parseObject(resp);
        JSONObject data = respJson.getJSONObject("data");
        if (data == null) {
            logger.error("远程订单异常:msg={}", data.getString("msg"));
            return 0;
        }
        AliexpressOrder order = JSONObject.parseObject(data.toJSONString(), AliexpressOrder.class);
        AliexpressOrderMoney orderMoney = new AliexpressOrderMoney();
        AliexpressOrderReceipt receipt = new AliexpressOrderReceipt();
        List<AliexpressOrderChild> orderChildList = new ArrayList<>();
        if (this.orderMapper.getByOrderId(data.getString("orderId")) != null) {
            return 2;
        }
        order.setPlSellerId(remoteJson.getInteger("pinlianId"));
        order.setPlAccount(remoteJson.getString("pinlianAccount"));
        order.setEmpowerId(remoteJson.getInteger("empowerId"));
        order.setCallBackStatus(OrderHandleEnum.MarkDeliverStatus.MARK_WAIT.getValue());
        order.setCreatTime(new Date());
        order.setUpdateTime(order.getCreatTime());
        order.setVersion(0L);
        order.setPlProcessStatus(OrderHandleEnum.ConverSysStatus.PENDING.getValue());
        orderMoney = JSONObject.parseObject(data.getJSONObject("moneyStatusDTO").toJSONString(), AliexpressOrderMoney.class);
        orderMoney.setLoanTime(StringUtils.isEmpty(data.getJSONObject("moneyStatusDTO").getString("loanTime")) ? null : DateUtils.parseDate(data.getJSONObject("moneyStatusDTO").getString("loanTime"), DateUtils.FORMAT_2));
        orderMoney.setCreateTime(order.getCreatTime());
        orderMoney.setUpdateTime(order.getCreatTime());
        orderMoney.setOrderId(data.getString("orderId"));
        order.setGmtPayTime(orderMoney.getGmtPayTime());
        receipt = JSONObject.parseObject(data.getJSONObject("addressDTO").toJSONString(), AliexpressOrderReceipt.class);
        receipt.setOrderId(data.getString("orderId"));
        receipt.setCreateTime(orderMoney.getCreateTime());
        receipt.setUpdateTime(orderMoney.getCreateTime());
        order.setReceiptCountry(receipt.getCountry());
        order.setSellerLoginId(sellerLoginId);
        orderChildList = JSONObject.parseArray(data.getJSONArray("childOrder").toJSONString(), AliexpressOrderChild.class);
        for (AliexpressOrderChild childOrder : orderChildList) {
            childOrder.setParentOrderId(data.getString("orderId"));
            SellerSkuMap sellerSku = new SellerSkuMap();
            sellerSku.setPlatform("aliexpress");
            sellerSku.setPlatformSku(childOrder.getSkuCode());
            sellerSku.setAuthorizationId(order.getEmpowerId().toString());
            sellerSku.setStatus(1);
            sellerSku = skuMapService.selectByEntry(sellerSku);
            childOrder.setCreateTime(order.getCreatTime());
            childOrder.setPlSkuCode(sellerSku == null ? null : sellerSku.getPlSku());
            childOrder.setUpdateTime(order.getCreatTime());
            childOrder.setPlProcessStatus(Integer.valueOf(OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue()));
        }
        this.orderMapper.insert(order);
        this.orderChildMapper.inserts(orderChildList);
        this.orderReceiptMapper.insert(receipt);
        this.orderMoneyMapper.insert(orderMoney);
        return 1;
    }

    @Override
    public AliExpressOrderInfoDTO findAliExpressOrderByOrderId(String orderId) {
        AliexpressOrder aliexpressOrder = orderMapper.getByOrderId(orderId);
        AliexpressOrderMoney aliexpressOrderMoney = orderMoneyMapper.getByOrderId(orderId);
        List<AliexpressOrderChild> orderChildList = orderChildMapper.getOrderChildListByOrderId(orderId);
        AliexpressOrderReceipt aliexpressOrderReceipt = orderReceiptMapper.getByOrderId(orderId);

        AliExpressOrderInfoDTO aliExpressOrderInfoDTO = new AliExpressOrderInfoDTO();
        aliExpressOrderInfoDTO.setAliexpressOrder(aliexpressOrder);
        aliExpressOrderInfoDTO.setAliexpressOrderMoney(aliexpressOrderMoney);
        aliExpressOrderInfoDTO.setAliexpressOrderReceipt(aliexpressOrderReceipt);
        aliExpressOrderInfoDTO.setAliexpressOrderChildList(orderChildList);
        return aliExpressOrderInfoDTO;
    }
}
