package com.rondaful.cloud.order.service.impl;

import com.rondaful.cloud.common.enums.OrderHandleEnum;
import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RedissLockUtil;
import com.rondaful.cloud.order.config.PropertyUtil;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.SysOrderDetail;
import com.rondaful.cloud.order.entity.SysOrderLog;
import com.rondaful.cloud.order.entity.eBay.EbayOrder;
import com.rondaful.cloud.order.entity.eBay.EbayOrderDetail;
import com.rondaful.cloud.order.enums.ConvertSysStatusEnum;
import com.rondaful.cloud.order.enums.OrderDeliveryStatusNewEnum;
import com.rondaful.cloud.order.enums.OrderHandleLogEnum;
import com.rondaful.cloud.order.enums.OrderSourceCovertToUserServicePlatformEnum;
import com.rondaful.cloud.order.enums.OrderSourceEnum;
import com.rondaful.cloud.order.mapper.EbayOrderDetailMapper;
import com.rondaful.cloud.order.mapper.EbayOrderMapper;
import com.rondaful.cloud.order.mapper.EbayOrderStatusMapper;
import com.rondaful.cloud.order.model.dto.syncorder.PreCovertEbayOrderDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDetailDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderTransferInsertOrUpdateDTO;
import com.rondaful.cloud.order.model.dto.syncorder.UpdateSourceOrderDTO;
import com.rondaful.cloud.order.model.dto.syncorder.UpdateSourceOrderDetailDTO;
import com.rondaful.cloud.order.quartz.QuartzSchedulerUtil;
import com.rondaful.cloud.order.seller.Empower;
import com.rondaful.cloud.order.service.IConverEbayOrderService;
import com.rondaful.cloud.order.service.IEbayOrderHandleService;
import com.rondaful.cloud.order.service.IEbayOrderService;
import com.rondaful.cloud.order.service.ISkuMapService;
import com.rondaful.cloud.order.service.ISysOrderLogService;
import com.rondaful.cloud.order.service.ISysOrderService;
import com.rondaful.cloud.order.service.ISystemOrderCommonService;
import com.rondaful.cloud.order.task.BatchDealConverSysOrderData;
import com.rondaful.cloud.order.utils.CronExpParser;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import com.rondaful.cloud.order.utils.OrderUtils;
import com.rondaful.cloud.order.utils.RateUtil;
import com.rondaful.cloud.order.utils.TimeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ConverEbayOrderServiceImpl implements IConverEbayOrderService {
    @Autowired
    private EbayOrderDetailMapper ebayOrderDetailMapper;
    @Autowired
    private EbayOrderStatusMapper ebayOrderStatusMapper;
    private final GetLoginUserInformationByToken loginUserInfo;
    private final IEbayOrderHandleService ebayOrderHandleService;
    private final BatchDealConverSysOrderData batchDealConverSysOrderData;
    private final ISkuMapService skuMapService;
    private final EbayOrderMapper ebayOrderMapper;
    private final QuartzSchedulerUtil quartzScheduler;
    private final RateUtil rateUtil;
    private final RedissLockUtil redissLockUtil;
    private final ISysOrderLogService sysOrderLogService;
    private ISystemOrderCommonService systemOrderCommonService;
    private IEbayOrderService ebayOrderService;
    private ISysOrderService sysOrderService;

    @Autowired
    public ConverEbayOrderServiceImpl(GetLoginUserInformationByToken loginUserInfo,
                                      IEbayOrderHandleService ebayOrderHandleService,
                                      ISkuMapService skuMapService,
                                      ISysOrderLogService sysOrderLogService,
                                      BatchDealConverSysOrderData batchDealConverSysOrderData,
                                      EbayOrderMapper ebayOrderMapper,
                                      QuartzSchedulerUtil quartzScheduler,
                                      RateUtil rateUtil,
                                      RedissLockUtil redissLockUtil,
                                      ISystemOrderCommonService systemOrderCommonService,
                                      IEbayOrderService ebayOrderService,
                                      ISysOrderService sysOrderService) {
        this.loginUserInfo = loginUserInfo;
        this.ebayOrderHandleService = ebayOrderHandleService;
        this.skuMapService = skuMapService;
        this.sysOrderLogService = sysOrderLogService;
        this.batchDealConverSysOrderData = batchDealConverSysOrderData;
        this.ebayOrderMapper = ebayOrderMapper;
        this.quartzScheduler = quartzScheduler;
        this.rateUtil = rateUtil;
        this.redissLockUtil = redissLockUtil;
        this.systemOrderCommonService = systemOrderCommonService;
        this.ebayOrderService = ebayOrderService;
        this.sysOrderService = sysOrderService;
    }

    private String converJobClassName = PropertyUtil.getProperty("converJobClassName");
    private String converJobName = PropertyUtil.getProperty("converJobName");
    private String converJobGroupName = PropertyUtil.getProperty("converJobGroupName");
    private String converCronExpression = PropertyUtil.getProperty("converCronExpression");
    private String daysOverdueStr = PropertyUtil.getProperty("daysOverdue");

    private static String rateCurrency = PropertyUtil.getProperty("rateCurrency");

    private static Logger LOGGER = LoggerFactory.getLogger(ConverEbayOrderServiceImpl.class);

    public void updateFailureStatus(List<SysOrderDTO> sysOrderDTOList) {
        // 需要更新的源订单
        List<UpdateSourceOrderDTO> updateSourceOrderDTOList = new ArrayList<>();
        List<UpdateSourceOrderDetailDTO> updateSourceOrderDetailDTOList = new ArrayList<>();
        for (SysOrderDTO sysOrderDTO : sysOrderDTOList) {
            UpdateSourceOrderDTO dto=new UpdateSourceOrderDTO();
            dto.setConverSysStatus(2);
            dto.setOrderId(sysOrderDTO.getSourceOrderId());
            dto.setUpdateBy(Constants.DefaultUser.SYSTEM);
            dto.setUpdateDate(new Date());
            updateSourceOrderDTOList.add(dto);
            for (SysOrderDetailDTO orderDetailDTO : sysOrderDTO.getSysOrderDetailList()) {
                UpdateSourceOrderDetailDTO detailDTO=new UpdateSourceOrderDetailDTO();
                detailDTO.setConverSysStatus(2);
                detailDTO.setOrderId(sysOrderDTO.getSourceOrderId());
                detailDTO.setOrderItemId(orderDetailDTO.getSourceOrderLineItemId());
                detailDTO.setUpdateBy(Constants.DefaultUser.SYSTEM);
                detailDTO.setUpdateDate(new Date());
                updateSourceOrderDetailDTOList.add(detailDTO);
            }
        }
        ebayOrderStatusMapper.updateConvertStatusBatch(updateSourceOrderDTOList);
        ebayOrderDetailMapper.updateConvertStatusStatusBatch(updateSourceOrderDetailDTOList);
    }

    @Override
    public String convertEbayToSysOrder() throws Exception {
        List<SysOrderDTO> sysOrderDTOList = ebayOrderService.getPreConvertEbayOrder();
        try {
            if (org.springframework.util.CollectionUtils.isEmpty(sysOrderDTOList)) {
                LOGGER.error("没有需要转入的ebay订单");
                return null;
            }
            LOGGER.error("进入转单前：sysOrderDTOList={}", FastJsonUtils.toJsonString(sysOrderDTOList));
            skuMapService.orderMapByOrderListNew(OrderRuleEnum.platformEnm.E_BAU.getPlatform(), sysOrderDTOList);
            LOGGER.error("进入转单（转单成功）后：sysOrderDTOList={}", FastJsonUtils.toJsonString(sysOrderDTOList));
        } catch (Exception e) {
            LOGGER.error("转系统订单全部映射失败", e);
            LOGGER.error("进入转单后（转单失败）：sysOrderDTOList={}", FastJsonUtils.toJsonString(sysOrderDTOList));
            try {
                this.updateFailureStatus(sysOrderDTOList);
            } catch (Exception ex) {
                LOGGER.error("EBAY自动转单，修改平台订单状态失败：{}", ex);
            }
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "转系统订单全部映射失败");
        }

        try {
            SysOrderTransferInsertOrUpdateDTO sysOrderTransferInsertOrUpdateDTO = sysOrderService.splitInsertSysOrderData(sysOrderDTOList);
            sysOrderService.insertSysOrderBatch(sysOrderTransferInsertOrUpdateDTO);
            ebayOrderService.updateEbayOrderBatchForConvert(sysOrderTransferInsertOrUpdateDTO);
        } catch (Exception e) {
            LOGGER.error("批量插入异常", e);
        }
        return null;
        //---
       /* LOGGER.error("开始转换Ebay订单");
        String message = OrderHandleEnum.SuccessOrFailure.SUCCESS.getTypeName();
        try {
            AutoConvertEbayOrderThread autoConvertEbayOrderThread = new AutoConvertEbayOrderThread();
            ThreadPoolUtil.executeThread(autoConvertEbayOrderThread);
        } catch (Exception e) {
            LOGGER.error("转换ebay订单异常", e);
            message = OrderHandleEnum.SuccessOrFailure.FAILURE.getTypeName();
        } finally {
            LOGGER.error("结束转换Ebay订单");
        }
        return message;*/
    }

    @Override
    public void autoConverEBayToSys() throws Exception {
        String jobInfo = quartzScheduler.getJobInfo(converJobName, converJobGroupName);
        if (StringUtils.isNotBlank(jobInfo))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "自动转单任务已开启，请勿重复操作。。。");
        Map<String, String> map = new HashMap<>();
        map.put("jobClassName", converJobClassName);
        map.put("jobName", converJobName);
        map.put("jobGroupName", converJobGroupName);
        map.put("cronExpression", converCronExpression);
        quartzScheduler.addJob(map);
        LOGGER.error("_________Start__________开启自动转单任务__________" + new Date() +
                "__________自动转单定时任务设置的频率为" + CronExpParser.translateToChinese(converCronExpression) + "执行一次__________");
    }

    @Override
    public void pauseAutoConverEbay() throws SchedulerException {
        quartzScheduler.pauseJob(converJobName, converJobGroupName);
    }

    @Override
    public String manualConverEbay(List<EbayOrder> ebayOrders) throws ParseException {
        List<PreCovertEbayOrderDTO> preCovertEbayOrderDTOList = this.packageManualConverSysList(ebayOrders);

        List<SysOrderDTO> sysOrderDTOList = new ArrayList<>();
        ebayOrderService.assembleConvertOrderData(sysOrderDTOList, preCovertEbayOrderDTOList);
        ebayOrderService.assembleConvertOrderFee(sysOrderDTOList);

        List<RLock> lockList = new ArrayList<>(preCovertEbayOrderDTOList.size());
        for (SysOrderDTO order : sysOrderDTOList) {
            lockList.add(redissLockUtil.lock(order.getSourceOrderId(), 60));
        }

        try {
            skuMapService.orderMapByOrderListNew(OrderRuleEnum.platformEnm.E_BAU.getPlatform(), sysOrderDTOList);
            LOGGER.debug("sysOrderDTOList={}", FastJsonUtils.toJsonString(sysOrderDTOList));
            for (SysOrderDTO order : sysOrderDTOList) {
                redissLockUtil.unlock(order.getSourceOrderId());
            }
        } catch (Exception e) {
            LOGGER.error("____________转系统订单全部映射失败___________", e);
            if ("ORDER_MAP_ERROR".equals(e.getMessage())) {
                SysOrderTransferInsertOrUpdateDTO sysOrderTransferInsertOrUpdateDTO = sysOrderService.splitInsertSysOrderData(sysOrderDTOList);
                ebayOrderService.updateEbayOrderBatchForConvert(sysOrderTransferInsertOrUpdateDTO);
                for (SysOrderDTO order : sysOrderDTOList) {
                    redissLockUtil.unlock(order.getSourceOrderId());
                }
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "转系统订单全部映射失败。。。");
            }
        }

        SysOrderTransferInsertOrUpdateDTO sysOrderTransferInsertOrUpdateDTO = sysOrderService.splitInsertSysOrderData(sysOrderDTOList);
        boolean insertError = false;
        boolean updateError = false;
        try {
            sysOrderService.insertSysOrderBatch(sysOrderTransferInsertOrUpdateDTO);
        } catch (Exception e) {
            LOGGER.error("手动转单，批量插入订单表异常", e);
            insertError = true;
            for (RLock lock : lockList) {
                lock.unlock();
            }
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "手动转单，批量插入订单表异常");
        }
        try {
            ebayOrderService.updateEbayOrderBatchForConvert(sysOrderTransferInsertOrUpdateDTO);
        } catch (Exception e) {
            LOGGER.error("手动转单，更新源订单表数据异常", e);
            updateError = true;
            for (RLock lock : lockList) {
                lock.unlock();
            }
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "手动转单，更新源订单表数据异常");
        }
        for (RLock lock : lockList) {
            lock.unlock();
        }

        if (insertError) {
            return "插入采购订单异常";
        }

        if (updateError) {
            return "更新源订单表数据异常";
        }

        List<SysOrderDetailDTO> sysOrderDetailDTOList = sysOrderTransferInsertOrUpdateDTO.getSysOrderDetailDTOList();
        List<UpdateSourceOrderDetailDTO> updateSourceOrderDetailDTOList = sysOrderTransferInsertOrUpdateDTO.getUpdateSourceOrderDetailDTOList();

        List<UpdateSourceOrderDetailDTO> successList = updateSourceOrderDetailDTOList.stream()
                .filter(updateSourceOrderDetailDTO ->
                        Objects.equals(updateSourceOrderDetailDTO.getConverSysStatus(), ConvertSysStatusEnum.CONVERT_SUCCESS.getValue()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(successList)) {
            return OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getMsg();
        } else {
            return OrderHandleEnum.ConverSysStatus.CONVER_SUCCESS.getMsg();
        }
//        if (CollectionUtils.isEmpty(successList)) {
//            return OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getMsg();
//        } else if (sysOrderDetailDTOList.size() == successList.size()) {
//            return OrderHandleEnum.ConverSysStatus.CONVER_SUCCESS.getMsg();
//        } else {
//            return OrderHandleEnum.ConverSysStatus.CONVER_PORTION_SUCCESS.getMsg();
//        }
    }

    public void transactionPersistData(Map<String, Object> map, String isAutoConver) {
        try {
            List<SysOrder> persistSysOrderList = (List<SysOrder>) map.get("persistSysOrderList");
            List<SysOrderDetail> persistSysOrderDetailList = (List<SysOrderDetail>) map.get("persistSysOrderDetailList");
            List<EbayOrderDetail> ebayOrderDetailList = (List<EbayOrderDetail>) map.get("ebayOrderDetailList");
            Connection conn = batchDealConverSysOrderData.getConnection();
            if (CollectionUtils.isNotEmpty(persistSysOrderList) && persistSysOrderList.size() != 0) {
                batchDealConverSysOrderData.insertBatchSysOrderList(conn, persistSysOrderList);
                for (SysOrder order : persistSysOrderList) {
                    sysOrderLogService.insertSelective(new SysOrderLog(order.getSysOrderId(), OrderHandleLogEnum.Content.NEW_ORDER.newOrder(order.getSysOrderId()),
                            OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(), order.getSellerPlAccount()));
                }
                LOGGER.error("_________插入insertBatchSysOrderList执行成功_________成功转化成系统订单的订单数量为:{}_________", persistSysOrderList.size());
            }
            if (CollectionUtils.isNotEmpty(persistSysOrderDetailList) && persistSysOrderDetailList.size() != 0) {
                batchDealConverSysOrderData.insertBatchSysOrderDetailList(conn, persistSysOrderDetailList);
                LOGGER.error("____________插入insertListSysOrderDetails执行成功_____________");
            }
            if (CollectionUtils.isNotEmpty(ebayOrderDetailList) && ebayOrderDetailList.size() != 0) {
                batchDealConverSysOrderData.updateBatchConverStatusByOrderItemId(conn, ebayOrderDetailList);
                LOGGER.error("____________更新updateBatchConverStatusByOrderItemId(ebayDetail)执行成功____________");
            }
            batchDealConverSysOrderData.commitTransaction(conn);
            //更新ebay平台订单状态表状态
            conn = batchDealConverSysOrderData.getConnection();
            if (CollectionUtils.isNotEmpty(ebayOrderDetailList)) {
                List<EbayOrder> ebayOrderStatusList = new ArrayList<>();
                List<String> ebayOrderIdList = ebayOrderDetailList.stream().map(x -> x.getOrderId()).distinct().collect(Collectors.toList());
                List<EbayOrder> ebayOrderList = ebayOrderMapper.selectBatchSysOrderByOrderId(ebayOrderIdList);
                for (EbayOrder ebayOrder : ebayOrderList) {
                    List<EbayOrderDetail> ebayOrderDetails = ebayOrder.getEbayOrderDetails();
                    long successCount = ebayOrderDetails.stream().filter(x -> OrderHandleEnum.ConverSysStatus.CONVER_SUCCESS.getValue() == x.getConverSysStatus()).count();
                    long failureCount = ebayOrderDetails.stream().filter(x -> OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue() == x.getConverSysStatus()).count();
                    long pendingCount = ebayOrderDetails.stream().filter(x -> OrderHandleEnum.ConverSysStatus.PENDING.getValue() == x.getConverSysStatus()).count();
                    if (ebayOrderDetails.size() == successCount) {
                        ebayOrder.setConverSysStatus(OrderHandleEnum.ConverSysStatus.CONVER_SUCCESS.getValue());
                    } else if (ebayOrderDetails.size() == failureCount) {
                        ebayOrder.setConverSysStatus(OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue());
                    } else if (ebayOrderDetails.size() == pendingCount) {
                        ebayOrder.setConverSysStatus(OrderHandleEnum.ConverSysStatus.PENDING.getValue());
                    } else {
                        ebayOrder.setConverSysStatus(OrderHandleEnum.ConverSysStatus.CONVER_PORTION_SUCCESS.getValue());
                    }
                    String username = null;
                    if (!"auto".equalsIgnoreCase(isAutoConver)) {
                        username = loginUserInfo.getUserInfo().getUser().getUsername();
                    }
                    String operator = username == null ? "SYSTEM" : username;
                    ebayOrder.setUpdateBy(operator);
                    ebayOrderStatusList.add(ebayOrder);
                }
                batchDealConverSysOrderData.updateBatchEbayOrderConverStatus(conn, ebayOrderStatusList);
                batchDealConverSysOrderData.commitTransaction(conn);
            }
            LOGGER.info("_________________本次转化ebay平台订单执行成功_________________");
        } catch (Exception e) {
            LOGGER.error("_________________ebay平台转系统订单持久化出错_________________", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "ebay平台转系统订单持久化出错。。。");
        }
    }

    @Override
    public void pauseAllJob() throws SchedulerException {
        quartzScheduler.pauseAllJob();
    }

    @Override
    public void deleteJob(String jobName, String jobGroup) throws SchedulerException {
        quartzScheduler.deleteJob(jobName, jobGroup);
    }

    /**
     * 接收手工转系统订单List<EbayOrder>，并将其转化为List<SysOrder>
     *
     * @param ebayOrders
     */
    private List<PreCovertEbayOrderDTO> packageManualConverSysList(List<EbayOrder> ebayOrders) throws ParseException {
        List<PreCovertEbayOrderDTO> converSysOrders = new ArrayList<>();
        if (CollectionUtils.isEmpty(ebayOrders)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请求参数为空。。。");
        }
        for (EbayOrder ebayOrder : ebayOrders) {
            boolean flag=false;
            if (StringUtils.isBlank(ebayOrder.getOrderId())) {
                continue;
            }
            for (EbayOrderDetail detail : ebayOrder.getEbayOrderDetails()) {
                if (StringUtils.isBlank(detail.getOrderLineItemId())) {
                    flag=true;
                    break;
                }
            }
            if(flag){
                continue;
            }
            PreCovertEbayOrderDTO preCovertEbayOrderDTO = ebayOrderMapper.selectConvertDataSysOrderByOrderId(ebayOrder);
            if (preCovertEbayOrderDTO != null) {
                converSysOrders.add(preCovertEbayOrderDTO);
            }

        }
        if(converSysOrders.isEmpty()){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "没有合法的订单可转换。。。");
        }
        return converSysOrders;
    }

    public void dealPendingConverEbayList(List<SysOrder> sysList) throws ParseException {
        for (SysOrder sysOrder : sysList) {
            this.setSysOrderSKU(sysOrder);
            String currency = sysOrder.getShippingServiceCostStr().split("#")[1];
            String exchangeRate = currency.equalsIgnoreCase(rateCurrency) ? 1 + "" : rateUtil.remoteExchangeRateByCurrencyCode(currency, rateCurrency);
            this.setEBaySYSOrderDeadline(sysOrder);
            sysOrder.setShippingServiceCost(ebayOrderHandleService.string2BigDecimal(sysOrder.getShippingServiceCostStr()).multiply(new BigDecimal(exchangeRate)));
            sysOrder.setCommoditiesAmount(ebayOrderHandleService.string2BigDecimal(sysOrder.getPlatformTotal()).multiply(new BigDecimal(exchangeRate)));
            List<SysOrderDetail> list = new ArrayList<>();
            BigDecimal rateTotal = new BigDecimal(0);
            Map<String, BigDecimal> map = new HashMap();
            for (SysOrderDetail x : sysOrder.getSysOrderDetails()) {
                String platformSKU = x.getPlatformSKU();
                BigDecimal platformSKUPrice = new BigDecimal(0);
                if (StringUtils.isNotEmpty(x.getPlatformSKUPriceStr())) {
                    platformSKUPrice = new BigDecimal(x.getPlatformSKUPriceStr().split("#")[0]);
                }
                Integer skuNum = x.getSkuQuantity();
                BigDecimal multiply = platformSKUPrice.multiply(new BigDecimal(skuNum));
                map.put(platformSKU, multiply);
                rateTotal = rateTotal.add(multiply);
                list.add(x);
            }
            for (SysOrderDetail detail : list) {
                String platformSKU = detail.getPlatformSKU();
                for (Map.Entry<String, BigDecimal> entry : map.entrySet()) {
                    String key = entry.getKey();
                    if (platformSKU.equals(key)) {
                        detail.setApportion(entry.getValue().divide(rateTotal, 2, BigDecimal.ROUND_DOWN));
                        break;
                    }
                }
            }
            sysOrder.setSysOrderDetails(list);
        }
    }

    private void setSysOrderSKU(SysOrder sysorder) {
        for (SysOrderDetail detail : sysorder.getSysOrderDetails()) {
            String variationSku = detail.getVariationSku();
            String itemSku = detail.getItemSku();
            if (StringUtils.isNotEmpty(variationSku)) detail.setPlatformSKU(variationSku);
            if (StringUtils.isEmpty(variationSku) && StringUtils.isNotEmpty(itemSku)) detail.setPlatformSKU(itemSku);
        }
    }

    private void setEBaySYSOrderDeadline(SysOrder order) {
        String deadline = "";
        for (SysOrderDetail detail : order.getSysOrderDetails()) {
            String deliverDeadline = detail.getDeliverDeadline();
            if (StringUtils.isNotBlank(deliverDeadline)) {
                if (StringUtils.isBlank(deadline)) {
                    deadline = deliverDeadline;
                    continue;
                }
                if (TimeUtil.stringToDate(deadline).compareTo(TimeUtil.stringToDate(deliverDeadline)) > 0)
                    deadline = deliverDeadline;
            }
        }
        if (StringUtils.isNotBlank(deadline))
            order.setDeliverDeadline(deadline);
    }

    /**
     * 将SKU映射结果分类
     *
     * @param afterMapSysList
     * @return
     */
    public Map<String, Object> splitResultData(List<SysOrder> afterMapSysList, String isAutoConver) {
        if (afterMapSysList == null || afterMapSysList.size() == 0) {
            LOGGER.error("________________进行SKU映射和邮寄规则匹配返回结果集合为空________________");
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "进行SKU映射和邮寄规则匹配返回结果集合为空。。。");
        }
        Map<String, Object> map = new HashMap<>(3);
        List<SysOrder> persistSysOrderList = new ArrayList<>();
        List<SysOrderDetail> persistSysOrderDetailList = new ArrayList<>();
        List<EbayOrderDetail> ebayOrderDetailList = new ArrayList<>();
        for (SysOrder sysOrder : afterMapSysList) {
            List<SysOrderDetail> detailList = sysOrder.getSysOrderDetails();
            if (CollectionUtils.isEmpty(detailList)) {
                continue;
            }
            String sysOrderId = OrderUtils.getPLOrderNumber();
            sysOrder.setSysOrderId(sysOrderId);
            sysOrder.setOrderTrackId(OrderUtils.getPLTrackNumber());//用于发货重复推单
            sysOrder.setOrderSource(OrderSourceEnum.CONVER_FROM_EBAY.getValue());
            sysOrder.setOrderDeliveryStatus(OrderDeliveryStatusNewEnum.WAIT_PAY.getValue());
            String username = null;
            if (!"auto".equalsIgnoreCase(isAutoConver)) {
                username = loginUserInfo.getUserInfo().getUser().getUsername();
            }
            String operator = username == null ? "SYSTEM" : username;
            if (sysOrder.getConverSysStatus() == (byte) 1 || sysOrder.getConverSysStatus() == (byte) 3) {
                sysOrder.setCreateBy(operator);
                sysOrder.setUpdateBy(operator);
                BigDecimal commoditiesAmount = sysOrder.getCommoditiesAmount();
                BigDecimal commoditiesAmountResult = new BigDecimal(0);
                BigDecimal shippingServiceCost = sysOrder.getShippingServiceCost();
                BigDecimal shippingServiceCostResult = new BigDecimal(0);
                for (SysOrderDetail detail : detailList) {
                    if (detail.getConverSysDetailStatus() == (byte) 1) {//映射成功
                        detail.setSysOrderId(sysOrderId);
                        detail.setOrderLineItemId(OrderUtils.getPLOrderItemNumber());
                        detail.setCreateBy(operator);
                        detail.setUpdateBy(operator);
                        commoditiesAmountResult.add(commoditiesAmount.multiply(detail.getApportion()));
                        shippingServiceCostResult.add(shippingServiceCost.multiply(detail.getApportion()));
                        persistSysOrderDetailList.add(detail);
                    }
                }
                sysOrder.setCommoditiesAmount(commoditiesAmountResult);
                sysOrder.setShippingServiceCost(shippingServiceCostResult);
                persistSysOrderList.add(sysOrder);
            }
            for (SysOrderDetail sysOrderDetail : detailList) {
                String sourceOrderID = sysOrderDetail.getSourceOrderId();
                String sourceItemID = sysOrderDetail.getSourceOrderLineItemId();
                Byte converStatus = sysOrderDetail.getConverSysDetailStatus();
                if (sourceItemID.contains("#")) {
                    String[] sourceItemIDArray = sourceItemID.split("#");
                    for (String orderItemID : sourceItemIDArray) {
                        EbayOrderDetail detail = new EbayOrderDetail();
                        detail.setOrderId(sourceOrderID);
                        detail.setOrderLineItemId(orderItemID);
                        detail.setConverSysStatus(converStatus);
                        detail.setUpdateBy(operator);
                        ebayOrderDetailList.add(detail);
                    }
                } else {
                    EbayOrderDetail detail = new EbayOrderDetail();
                    detail.setOrderId(sourceOrderID);
                    detail.setOrderLineItemId(sourceItemID);
                    detail.setConverSysStatus(converStatus);
                    detail.setUpdateBy(operator);
                    ebayOrderDetailList.add(detail);
                }
            }
        }
        map.put("persistSysOrderList", persistSysOrderList);
        map.put("persistSysOrderDetailList", persistSysOrderDetailList);
        map.put("ebayOrderDetailList", ebayOrderDetailList);
        return map;
    }

    public void dealConvertFailEbayOrder() {
        long startTime = System.currentTimeMillis();
        LOGGER.info("开始处理转换失败的ebay订单");

        try {
            List<EbayOrder> ebayOrders = ebayOrderMapper.selectCovertFailEbayOrder();
            LOGGER.debug("需要处理转换失败的订单条数为：{}", ebayOrders.size());
            if (CollectionUtils.isEmpty(ebayOrders)) {
                LOGGER.info("结束处理转换失败的ebay订单");
                return;
            }

            // 需要隐藏的ebayOrderId
            List<String> hideEbayOrderIds = this.getHideEbayOrderIds(ebayOrders);
            LOGGER.debug("需要隐藏转换失败的订单条数为：{}", hideEbayOrderIds.size());
            if (CollectionUtils.isEmpty(hideEbayOrderIds)) {
                LOGGER.info("结束处理转换失败的ebay订单");
                return;
            }

            // 隐藏ebay订单
            ebayOrderMapper.updateShowOrderToNoShow(hideEbayOrderIds);

        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
            LOGGER.info("结束处理转换失败的ebay订单");
            long endTime = System.currentTimeMillis();
            LOGGER.info("处理转换失败的ebay订单总耗时为：{}ms", (endTime - startTime));
        }

    }

    private List<String> getHideEbayOrderIds(List<EbayOrder> ebayOrders) {
        List<String> ebayOrderIds = new ArrayList<>();

        for (EbayOrder ebayOrder : ebayOrders) {
            List<EbayOrderDetail> ebayOrderDetailList = ebayOrder.getEbayOrderDetails();
            Empower empower = new Empower();
            try {
                empower = systemOrderCommonService.queryAuthorizationFromSellerBySellerAccount(ebayOrder.getSellerUserId(),
                        OrderSourceCovertToUserServicePlatformEnum.getOtherPlatformCode(OrderSourceEnum.CONVER_FROM_EBAY.getValue()));
            } catch (Exception e) {
                LOGGER.error("ebay订单{}获取授权信息有误，跳过该订单", ebayOrder.getOrderId());
                continue;
            }
            // 是否展示，默认不展示
            boolean show = false;
            for (EbayOrderDetail ebayOrderDetail : ebayOrderDetailList) {
                String sku = this.getSku(ebayOrderDetail);
                if (StringUtils.isBlank(sku)) {
                    continue;
                }
                String plSku = skuMapService.queryPlSku(OrderRuleEnum.platformEnm.E_BAU.getPlatform(), String.valueOf(empower.getEmpowerid()),
                        ebayOrderDetail.getVariationSku(), String.valueOf(empower.getPinlianid()));
                if (!StringUtils.isBlank(plSku)) {
                    // 如果一个sku有映射关系，则需要展示
                    show = true;
                }
            }

            // 不展示，则需要隐藏
            if (!show) {
                ebayOrderIds.add(ebayOrder.getOrderId());
            }
        }

        return ebayOrderIds;
    }

    private String getSku(EbayOrderDetail ebayOrderDetail) {
        String variationSku = ebayOrderDetail.getVariationSku();
        if (StringUtils.isNotEmpty(variationSku)) {
            return variationSku;
        }
        String itemSku = ebayOrderDetail.getSku();

        if (StringUtils.isEmpty(variationSku) && StringUtils.isNotEmpty(itemSku)) {
            return itemSku;
        }

        return null;
    }
}
