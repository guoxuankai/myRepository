package com.rondaful.cloud.order.task;

import com.ebay.soap.eBLBaseComponents.OrderType;
import com.rondaful.cloud.order.seller.Empower;
import com.rondaful.cloud.order.service.IEbayOrderHandleService;
import com.rondaful.cloud.order.service.impl.EbayOrderHandleServiceImpl;
import com.rondaful.cloud.order.utils.ApplicationContextProvider;
import com.rondaful.cloud.order.utils.TimeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ManualSyncEbayOrderTesk implements Runnable {

    private IEbayOrderHandleService ebayOrderHandleService;

    private static Logger _log = LoggerFactory.getLogger(ManualSyncEbayOrderTesk.class);

    public ManualSyncEbayOrderTesk() {
        this.ebayOrderHandleService = (EbayOrderHandleServiceImpl) ApplicationContextProvider.getBean("ebayOrderHandleServiceImpl");
    }

    @Override
    public void run() {
        _log.info("___________项目启动开始执行获取Queue任务，执行手动同步ebay平台订单___________");
        while (true) {
            Empower empower = null;
            try {
                empower = SyncOrderQueue.getInstance().take();
            } catch (InterruptedException e) {
                _log.error("___________手工同步订单从队列中获取任务出错________{}_______", e);
            }
            if (empower != null) {
                _log.error("______本次请求同步的店铺账号为______{}______同步从______{}______到______{}_______时间内的订单_______",
                        empower.getAccount(), TimeUtil.calendarToStr(empower.getModTimeFrom()), TimeUtil.calendarToStr(empower.getModTimeTo()));
                OrderType[] orders = ebayOrderHandleService.sendReqGetEbayResp(empower);
                if (CollectionUtils.isEmpty(new ArrayList<>(Arrays.asList(orders))) || orders.length == 0) return;

                List<OrderType> orderTypes = ebayOrderHandleService.filterOrders(orders);
                _log.error("______________本次请求需要处理的订单数量为______________{}______________", orderTypes.size());

                Map<String, Object> stringObjectMap = null;
                try {
                    stringObjectMap = ebayOrderHandleService.classifyOrders(empower, orderTypes);
                } catch (ParseException e) {
                    _log.error("______________ebay同步订单操作，分类出错______________", e);
                }

                try {
                    ebayOrderHandleService.centryDealData(stringObjectMap);
                } catch (Exception e) {
                    _log.error("___________持久化ebay订单数据出错________{}_______", e);
                }
                _log.info("---------------本次请求下来的所有订单处理完毕-------------------");
            }
        }
    }
}
