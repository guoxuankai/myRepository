package com.rondaful.cloud.order.quartz;

import com.ebay.soap.eBLBaseComponents.OrderType;
import com.rondaful.cloud.order.config.PropertyUtil;
import com.rondaful.cloud.order.seller.Empower;
import com.rondaful.cloud.order.service.IEbayOrderHandleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Component
public class SyncTask {
    @Autowired
    private IEbayOrderHandleService ebayOrderHandleService;

    private int syncEbayOrderInterval = Integer.valueOf(PropertyUtil.getProperty("syncEbayOrderInterval"));

    private static Logger _log = LoggerFactory.getLogger(SyncTask.class);

    /**
     * 执行任务
     */
    public void execute(Empower empower) throws ParseException {
        String account = empower.getAccount();
        _log.info("抓单的品连账号是：{}, 同步的eBay店铺为: {}", empower.getPinlianaccount(), account);
        Calendar calendarFrom = Calendar.getInstance();
//        calendarFrom.setTime(DateUtils.strToDate("2019-07-24 05:00:00", DateUtils.FORMAT_2));
        calendarFrom.add(Calendar.HOUR, syncEbayOrderInterval);
        empower.setModTimeFrom(calendarFrom);
        Calendar calendarTo = Calendar.getInstance();
        empower.setModTimeTo(calendarTo);

//        Calendar calendarFrom = Calendar.getInstance();
//        calendarFrom.add(Calendar.HOUR, -32);
//        empower.setModTimeFrom(calendarFrom);
//        Calendar calendarTo = Calendar.getInstance();
//        calendarTo.add(Calendar.HOUR, -8);
//        empower.setModTimeTo(calendarTo);


        OrderType[] orders = ebayOrderHandleService.sendReqGetEbayResp(empower);
        _log.info("店铺{} 本次从ebay获取的订单数量为: {}", account, orders.length);
        if (orders == null || orders.length == 0) {
            return;
        }

        List<OrderType> orderTypes = ebayOrderHandleService.filterOrders(orders);
        _log.info("店铺{} 本次请求需要处理的订单数量为: {}", account, orderTypes.size());

        empower.setOperator("SYSTEM");
        Map<String, Object> stringObjectMap = ebayOrderHandleService.classifyOrders(empower, orderTypes);

        ebayOrderHandleService.centryDealData(stringObjectMap);

        _log.info("店铺{} 本次请求下来的所有订单处理完毕", account);
    }
}
