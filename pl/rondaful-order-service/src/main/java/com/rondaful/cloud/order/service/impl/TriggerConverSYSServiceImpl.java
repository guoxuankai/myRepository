package com.rondaful.cloud.order.service.impl;

import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.RedissLockUtil;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.aliexpress.AliexpressOrderChild;
import com.rondaful.cloud.order.entity.orderRule.SKUMapMailRuleDTO;
import com.rondaful.cloud.order.mapper.EbayOrderMapper;
import com.rondaful.cloud.order.model.dto.syncorder.PreCovertEbayOrderDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderTransferInsertOrUpdateDTO;
import com.rondaful.cloud.order.service.IAmazonOrderItemService;
import com.rondaful.cloud.order.service.IAmazonOrderService;
import com.rondaful.cloud.order.service.IConverEbayOrderService;
import com.rondaful.cloud.order.service.IEbayOrderService;
import com.rondaful.cloud.order.service.ISkuMapService;
import com.rondaful.cloud.order.service.ISysOrderService;
import com.rondaful.cloud.order.service.TriggerConverSYSService;
import com.rondaful.cloud.order.service.impl.aliexpress.AliexpressOrderServiceImpl;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TriggerConverSYSServiceImpl implements TriggerConverSYSService {
    @Autowired
    private IAmazonOrderItemService amazonOrderItemService;
    @Autowired
    private IConverEbayOrderService converEbayOrderService;
    @Autowired
    private IAmazonOrderService amazonOrderService;
    @Autowired
    private EbayOrderMapper ebayOrderMapper;
    @Autowired
    private ISkuMapService skuMapService;
    @Autowired
    private AliexpressOrderServiceImpl aliexpressOrderService;
    @Autowired
    private RedissLockUtil redissLockUtil;
    @Autowired
    private IEbayOrderService ebayOrderService;
    @Autowired
    private ISysOrderService sysOrderService;

    private static Logger _log = LoggerFactory.getLogger(TriggerConverSYSServiceImpl.class);

    @Override
    public void triggerSKUMapAndMailRuleMate(List<SKUMapMailRuleDTO> list) throws ParseException {
        _log.info("SKU映射触发转单参数：{}",FastJsonUtils.toJsonString(list));
        Map<String, Object> pendingConverMap = this.queryPendingConverPlatformOrderList(list);
        converPlatformOrderList(pendingConverMap);
    }

    @Override
    public Map<String, Object> queryPendingConverPlatformOrderList(List<SKUMapMailRuleDTO> list) throws ParseException {
        List<SysOrderDTO> ebayPendingList = new ArrayList<>();
        List<SysOrderDTO> amazonPendingList = new ArrayList<>();
        List<AliexpressOrderChild> aliPendingList = new ArrayList<>();
        for (SKUMapMailRuleDTO dto : list) {
            String platform = dto.getPlatform();
            Map<String, String> skuMap = dto.getSkuRelationMap();
            Integer empowerID = dto.getEmpowerID();
            if (StringUtils.isBlank(platform) || skuMap == null || empowerID == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请求参数不正确。。。");
            }
            List<String> platformSKUList = new ArrayList<>(skuMap.keySet());
            if ("ebay".equalsIgnoreCase(platform)) {
                List<PreCovertEbayOrderDTO> ebaySYSList = new ArrayList<>();
                for (String sku : platformSKUList) {
                    List<PreCovertEbayOrderDTO> variationList = ebayOrderMapper.getPendingConvertEbayByVariationSKU(empowerID, sku);
                    if (CollectionUtils.isNotEmpty(variationList)) {
                        ebaySYSList.addAll(variationList);
                    } else {
                        List<PreCovertEbayOrderDTO> skuList = ebayOrderMapper.getPendingConvertEbayBySKU(empowerID, sku);
                        if (CollectionUtils.isNotEmpty(skuList)) {
                            ebaySYSList.addAll(skuList);
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(ebaySYSList)) {
                    //TODO 找出完整的EBAY订单
                    Set<String> ebayOrderIds = ebaySYSList.stream().collect(Collectors.groupingBy(PreCovertEbayOrderDTO::getSourceOrderId)).keySet();
                    ebaySYSList.clear();
                    for (String orderId : ebayOrderIds) {
                        ebaySYSList.add(ebayOrderMapper.MappingConvertDataSysOrderByOrderId(orderId));
                    }
                    ebayOrderService.assembleConvertOrderData(ebayPendingList, ebaySYSList);
                    ebayOrderService.assembleConvertOrderFee(ebayPendingList);
                }
            }
            if ("amazon".equalsIgnoreCase(platform)) {
                List<SysOrderDTO> amazonSYSList = amazonOrderItemService.getPendingConverAmazonListByCondition(platformSKUList);
                if (CollectionUtils.isNotEmpty(amazonSYSList)) {
                    amazonPendingList.addAll(amazonSYSList);
                }
            }
            if ("aliexpress".equalsIgnoreCase(platform)) {
                for (Map.Entry<String, String> entry : skuMap.entrySet()) {
                    AliexpressOrderChild relation = new AliexpressOrderChild(entry.getKey(), entry.getValue());
                    aliPendingList.add(relation);
                }
            }
        }
        Map<String, Object> map = new HashMap<>(3);
        map.put("ebayPendingList", ebayPendingList);
        map.put("amazonPendingList", amazonPendingList);
        map.put("aliPendingList", aliPendingList);
        return map;
    }

    private void dealNoShowOnListEbayOrder(List<SysOrder> ebaySYSList) {
        List<String> ebayOrderIdList = new ArrayList<>();
        for (SysOrder ebaySysOrder : ebaySYSList) {
            String ebayOrderId = ebaySysOrder.getSourceOrderId();
            if (!ebayOrderIdList.contains(ebayOrderId)) {
                ebayOrderIdList.add(ebayOrderId);
            }
        }
        // 得到所有不展示订单的所有ID
        List<String> ebayNoShowOrderIds = ebayOrderMapper.getNoShowEbayOrderIds(ebayOrderIdList);

        _log.info("需要重新展示的ebay订单有:{}", ebayNoShowOrderIds);
        ebayOrderMapper.updateNoShowOrderToShow(ebayNoShowOrderIds);
    }

    public void converPlatformOrderList(Map<String, Object> pendingConverMap) {
        List<SysOrderDTO> ebayPendingList = (List<SysOrderDTO>) pendingConverMap.get("ebayPendingList");
        List<SysOrderDTO> amazonPendingList = (List<SysOrderDTO>) pendingConverMap.get("amazonPendingList");
        List<AliexpressOrderChild> aliPendingList = (List<AliexpressOrderChild>) pendingConverMap.get("aliPendingList");
        if (CollectionUtils.isNotEmpty(ebayPendingList)) {
            this.ebayConverSYS(ebayPendingList);
        }
        if (CollectionUtils.isNotEmpty(amazonPendingList)) {
            amazonOrderService.amazonConverSYS(amazonPendingList);
        }
        if (CollectionUtils.isNotEmpty(aliPendingList)) {
            this.asyncUpdateSku(aliPendingList, null);
        }
    }

    public void ebayConverSYS(List<SysOrderDTO> ebayPendingList) {
        List<RLock> lockList = new ArrayList<>(ebayPendingList.size());
        for (SysOrderDTO order : ebayPendingList) {
            lockList.add(redissLockUtil.lock(order.getSourceOrderId(), 60));
        }
        try {
            if (CollectionUtils.isEmpty(ebayPendingList)) {
                _log.info("没有需要转入的ebay订单");
                return;
            }
            skuMapService.orderMapByOrderListNew(OrderRuleEnum.platformEnm.E_BAU.getPlatform(), ebayPendingList);
            _log.debug("sysOrderDTOList={}", FastJsonUtils.toJsonString(ebayPendingList));
        } catch (Exception e) {
            SysOrderTransferInsertOrUpdateDTO sysOrderTransferInsertOrUpdateDTO = sysOrderService.splitInsertSysOrderData(ebayPendingList);
            ebayOrderService.updateEbayOrderBatchForConvert(sysOrderTransferInsertOrUpdateDTO);
            for (SysOrderDTO order : ebayPendingList) {
                redissLockUtil.unlock(order.getSourceOrderId());
            }
            _log.error("转系统订单全部映射失败", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "转系统订单全部映射失败");
        }
        try {
            SysOrderTransferInsertOrUpdateDTO sysOrderTransferInsertOrUpdateDTO = sysOrderService.splitInsertSysOrderData(ebayPendingList);
            sysOrderService.insertSysOrderBatch(sysOrderTransferInsertOrUpdateDTO);
            ebayOrderService.updateEbayOrderBatchForConvert(sysOrderTransferInsertOrUpdateDTO);
        } catch (Exception e) {
            _log.error("批量插入出错", e);
        }

        for (RLock lock : lockList) {
            lock.unlock();
        }
    }

    /**
     * sku变更时同步到平台订单
     *
     * @param list
     * @param unList
     */
    @Async
    public void asyncUpdateSku(List<AliexpressOrderChild> list, List<String> unList) {
        if (CollectionUtils.isEmpty(list) && CollectionUtils.isEmpty(unList)) {
            return;
        }
        aliexpressOrderService.updatesSku(list, null);
    }
}
