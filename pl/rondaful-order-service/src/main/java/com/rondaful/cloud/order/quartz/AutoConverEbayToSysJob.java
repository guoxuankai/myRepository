package com.rondaful.cloud.order.quartz;

import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.RedissLockUtil;
import com.rondaful.cloud.order.config.PropertyUtil;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.SysOrderDetail;
import com.rondaful.cloud.order.mapper.EbayOrderMapper;
import com.rondaful.cloud.order.service.impl.*;
import com.rondaful.cloud.order.utils.ApplicationContextProvider;
import com.rondaful.cloud.order.utils.TimeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

public class AutoConverEbayToSysJob implements BaseJob {
    public int searchminute = Integer.valueOf(PropertyUtil.getProperty("searchminute"));//分钟
    private ConverEbayOrderServiceImpl converEbayOrderServiceImpl;
    private EbayOrderHandleServiceImpl ebayOrderHandleServiceImpl;
    private EbayOrderMapper ebayOrderMapper;
    private SkuMapServiceImpl skuMapService;
    private RedissLockUtil redissLockUtil;

    private static Logger _log = LoggerFactory.getLogger(AutoConverEbayToSysJob.class);

    public AutoConverEbayToSysJob() {
        ebayOrderMapper = (EbayOrderMapper) ApplicationContextProvider.getBean("ebayOrderMapper");
        converEbayOrderServiceImpl = (ConverEbayOrderServiceImpl) ApplicationContextProvider.getBean("converEbayOrderServiceImpl");
        ebayOrderHandleServiceImpl = (EbayOrderHandleServiceImpl) ApplicationContextProvider.getBean("ebayOrderHandleServiceImpl");
        skuMapService = (SkuMapServiceImpl) ApplicationContextProvider.getBean("skuMapServiceImpl");
        redissLockUtil = (RedissLockUtil) ApplicationContextProvider.getBean("redissLockUtil");
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        _log.info("____________________本次转化订单Job开始____________________");
        List<SysOrder> sysOrders = this.selectIntervalEbayOrders();
        if (CollectionUtils.isEmpty(sysOrders)) {
            return;
        }
        List<RLock> lockList = new ArrayList<>(sysOrders.size());
        for (SysOrder order : sysOrders) {
            lockList.add(redissLockUtil.lock(order.getSourceOrderId(), 60));
        }
        List<SysOrder> sysOrdersAfterMap = null;
        try {
            _log.info("_____________本次查出待转化的订单数量为:__________{}_________", sysOrders.size());
            sysOrdersAfterMap = skuMapService.orderMapByOrderList(OrderRuleEnum.platformEnm.E_BAU.getPlatform(), sysOrders);
        } catch (Exception e) {
            if ("ORDER_MAP_ERROR".equals(e.getMessage())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "本次转系统订单全部映射失败。。。");
            }
        }

        Map<String, Object> map = converEbayOrderServiceImpl.splitResultData(sysOrdersAfterMap, "auto");

        converEbayOrderServiceImpl.transactionPersistData(map, "auto");
        for (RLock lock : lockList) {
            lock.unlock();
        }
        _log.info("___________________本次转化任务处理完成___________________");
    }

    private List<SysOrder> selectIntervalEbayOrders() {
        Date modTimeTo = new Date();
        Date modTimeFrom = TimeUtil.dateAddSubtract(modTimeTo, searchminute);
        Map<String, String> map = new HashMap<>();
        map.put("modTimeFrom", TimeUtil.dateToString(modTimeFrom, "yyyy-MM-dd HH:mm:ss"));
        map.put("modTimeTo", TimeUtil.dateToString(modTimeTo, "yyyy-MM-dd HH:mm:ss"));
        List<SysOrder> list = ebayOrderMapper.selectEbayOrderInterval(map);
        for (SysOrder order : list) {
            BigDecimal rateTotal = new BigDecimal(0);
            Map<String, BigDecimal> map0 = new HashMap();
            order.setShippingServiceCost(ebayOrderHandleServiceImpl.string2BigDecimal(order.getShippingServiceCostStr()));
            order.setCommoditiesAmount(ebayOrderHandleServiceImpl.string2BigDecimal(order.getPlatformTotal()));
            for (SysOrderDetail detail : order.getSysOrderDetails()) {
                String variationSku = detail.getVariationSku();
                String itemSku = detail.getItemSku();
                if (StringUtils.isNotEmpty(variationSku)) {
                    detail.setPlatformSKU(variationSku);
                }
                if (StringUtils.isEmpty(variationSku) && StringUtils.isNotEmpty(itemSku)) {
                    detail.setPlatformSKU(itemSku);
                }
                BigDecimal platformSKUPrice = new BigDecimal(0);
                if (StringUtils.isNotEmpty(detail.getPlatformSKUPriceStr())) {
                    platformSKUPrice = new BigDecimal(detail.getPlatformSKUPriceStr().split("#")[0]);
                }
                Integer skuNum = detail.getSkuQuantity();
                BigDecimal multiply = platformSKUPrice.multiply(new BigDecimal(skuNum));
                map0.put(detail.getPlatformSKU(), multiply);
                rateTotal = rateTotal.add(multiply);
            }
            for (SysOrderDetail detail : order.getSysOrderDetails()) {
                String platformSKU = detail.getPlatformSKU();
                for (Map.Entry<String, BigDecimal> entry : map0.entrySet()) {
                    String key = entry.getKey();
                    if (platformSKU.equals(key)) {
                        detail.setApportion(entry.getValue().divide(rateTotal, 2, BigDecimal.ROUND_DOWN));
                        break;
                    }
                }
            }
        }
        return list;
    }
}
