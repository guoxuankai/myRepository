package com.rondaful.cloud.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.enums.OrderHandleEnum;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.order.config.PropertyUtil;
import com.rondaful.cloud.order.entity.eBay.EbayOrder;
import com.rondaful.cloud.order.entity.eBay.EbayOrderDetail;
import com.rondaful.cloud.order.mapper.EbayOrderMapper;
import com.rondaful.cloud.order.quartz.QuartzSchedulerUtil;
import com.rondaful.cloud.order.remote.RemoteSellerService;
import com.rondaful.cloud.order.seller.Empower;
import com.rondaful.cloud.order.seller.EmpowerMapper;
import com.rondaful.cloud.order.service.ISyncEbayOrderService;
import com.rondaful.cloud.order.syncorder.SyncEbayOrder;
import com.rondaful.cloud.order.task.SyncOrderQueue;
import com.rondaful.cloud.order.thread.AutoSyncEbayOrderThread;
import com.rondaful.cloud.order.utils.CronExpParser;
import com.rondaful.cloud.order.utils.ThreadPoolUtil;
import com.rondaful.cloud.order.utils.TimeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SyncEbayOrderServiceImpl extends BaseServiceImpl<EbayOrder> implements ISyncEbayOrderService {
    @Autowired
    private EmpowerMapper empowerMapper;
    @Autowired
    private EbayOrderMapper ebayOrderMapper;
    @Autowired
    private RemoteSellerService remoteSellerService;
    @Autowired
    private QuartzSchedulerUtil quartzScheduler;
    @Autowired
    private GetLoginUserInformationByToken loginUserInfo;

    private String syncJobClassName = PropertyUtil.getProperty("syncJobClassName");
    private String syncJobName = PropertyUtil.getProperty("syncJobName");
    private String syncJobGroupName = PropertyUtil.getProperty("syncJobGroupName");
    private String syncCronExpression = PropertyUtil.getProperty("syncCronExpression");
    private int manualSyncEbayOrderInterval = Integer.valueOf(PropertyUtil.getProperty("manualSyncEbayOrderInterval"));

    private static Logger _log = LoggerFactory.getLogger(SyncEbayOrderServiceImpl.class);

    @Override
    public String syncEbayOrders() throws Exception {
        _log.info("开始同步ebay订单");
        String message = OrderHandleEnum.SuccessOrFailure.SUCCESS.getTypeName();
        List<Empower> empowers = empowerMapper.selectEbayAvailableToken();
        if (CollectionUtils.isEmpty(empowers)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询店铺授权表获取可用ebay平台token异常。。。");
        }

        try {

            for (Empower empower : empowers) {
                AutoSyncEbayOrderThread autoSyncEbayOrderThread = new AutoSyncEbayOrderThread(empower);
                ThreadPoolUtil.executeThread(autoSyncEbayOrderThread);
            }

//            // 沿用以前的抓单逻辑
//            AutoSyncEbayOrdersJob autoSyncEbayOrdersJob = new AutoSyncEbayOrdersJob();
//            List<Empower>[] empowerListPerThread = autoSyncEbayOrdersJob.distributeTasks(empowers,
//                    autoSyncEbayOrdersJob.getAutoSyncEbayThreadNum());
//            autoSyncEbayOrdersJob.startAllThread(empowerListPerThread);
        } catch (Exception e) {
            _log.error("同步ebay订单异常", e);
            message = OrderHandleEnum.SuccessOrFailure.FAILURE.getTypeName();
        } finally {
            _log.info("结束同步ebay订单");
        }
        return message;
    }

    @Override
    public void autoSyncEBayOrders() throws Exception {
        String jobInfo = quartzScheduler.getJobInfo(syncJobName, syncJobGroupName);
        if (StringUtils.isNotBlank(jobInfo))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "自动同步ebay订单任务已开启，请勿重复操作。。。");
        Map<String, String> map = new HashMap<>();
        map.put("jobClassName", syncJobClassName);
        map.put("jobName", syncJobName);
        map.put("jobGroupName", syncJobGroupName);
        map.put("cronExpression", syncCronExpression);
        quartzScheduler.addJob(map);
        _log.error("_________Start__________开启自动同步ebay订单任务__________{}__________自动同步ebay订单定时任务设置的频率为{}执行一次__________",
                new Date(), CronExpParser.translateToChinese(syncCronExpression));
    }

    @Override
    public void pauseAutoSyncEbay() throws SchedulerException {
        quartzScheduler.pauseJob(syncJobName, syncJobGroupName);
    }

    @Override
    public void manualSycEbay(String modTimeFrom, String modTimeTo) {
        try {
            String username = loginUserInfo.getUserInfo().getUser().getUsername();
            _log.error("______________申请手工同步订单的账号为______________{}______________", username);
            String result = remoteSellerService.selectObjectByAccount(null, username, 1, null, 1);
            String data = Utils.returnRemoteResultDataString(result, "获取授权调用卖家微服务异常 。。。");
            List<Empower> empowers = JSONObject.parseArray(data, Empower.class);
            if (CollectionUtils.isEmpty(empowers) || empowers.size() == 0)
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "根据登录人信息查出卖家信息为空。。。");
            Map<String, Calendar> map = this.setModTimeFromTo(modTimeFrom, modTimeTo);
            for (Empower empower : empowers) {
                empower.setModTimeFrom(map.get("modTimeFrom"));
                empower.setModTimeTo(map.get("modTimeTo"));
                empower.setOperator(username);
                SyncOrderQueue.getInstance().put(empower);
            }
            _log.error("______________请求同步的对象塞入ebayManualSyncOrderQueue队列成功，塞入到队列的对象数为{}______________", empowers.size());
        } catch (Exception e) {
            _log.error("______________手工同步ebay平台订单出错_______{}_______", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
        }
    }

    private Map<String, Calendar> setModTimeFromTo(String modTimeFrom, String modTimeTo) throws Exception {
        Map<String, Calendar> map = new HashMap<>();
        if ((StringUtils.isBlank(modTimeFrom) && StringUtils.isBlank(modTimeTo)) || ("null".equals(modTimeFrom) && "null".equals(modTimeTo))) {
            Calendar to = Calendar.getInstance();
            map.put("modTimeTo", to);
            Calendar from = Calendar.getInstance();
            from.add(Calendar.HOUR, -manualSyncEbayOrderInterval);//默认modTimeFrom为当前时间减去xx小时，modTimeTo为当前时间
            map.put("modTimeFrom", from);
        } else if (("null".equals(modTimeFrom) || StringUtils.isBlank(modTimeFrom)) && StringUtils.isNotBlank(modTimeTo)) {
            map.put("modTimeTo", TimeUtil.strToCalendar(modTimeTo));
            Calendar from = TimeUtil.strToCalendar(modTimeTo);
            from.add(Calendar.HOUR, -manualSyncEbayOrderInterval);
            map.put("modTimeFrom", from);
        } else if (StringUtils.isNotBlank(modTimeFrom) && (StringUtils.isBlank(modTimeTo) || "null".equals(modTimeTo))) {
            map.put("modTimeFrom", TimeUtil.strToCalendar(modTimeFrom));
            Calendar to = TimeUtil.strToCalendar(modTimeFrom);
            to.add(Calendar.HOUR, -manualSyncEbayOrderInterval);
            map.put("modTimeTo", to);
        } else {
            if (modTimeFrom.compareTo(modTimeTo) == 1)
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "开始时间不能大于结束时间。。。");
            Calendar calendarFrom = TimeUtil.strToCalendar(modTimeFrom);
            map.put("modTimeFrom", calendarFrom);
            Calendar calendarTo = TimeUtil.strToCalendar(modTimeTo);
            map.put("modTimeTo", calendarTo);
        }
        return map;
    }

    public List<EbayOrder> queryEbayOrderList(Map<String, Object> map) {
        String beginDate = (String) map.get("beginDate");
        String endDate = (String) map.get("endDate");
        if (StringUtils.isNotBlank(beginDate))
            if (TimeUtil.stringToDate(beginDate).compareTo(new Date()) == 1) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "开始时间不能大于当前时间。。。");
            }
        if (StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate))
            if (TimeUtil.stringToDate(beginDate).compareTo(TimeUtil.stringToDate(endDate)) == 1) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "开始时间不能大于结束时间。。。");
            }
        List<EbayOrder> ebayOrders = new ArrayList<>();
        try {
            ebayOrders = ebayOrderMapper.queryEbayOrderList(map);
            if (ebayOrders != null) {
                for (EbayOrder ebayOrder : ebayOrders) {
                    List<EbayOrderDetail> ebayOrderDetails = ebayOrder.getEbayOrderDetails();
                    Integer productNum = ebayOrderDetails.stream().map(EbayOrderDetail::getQuantityPurchased).reduce(0, Integer::sum);
                    ebayOrder.setProductNum(productNum);
                    ebayOrder.setOrderTotalAmount(BigDecimal.valueOf(0));

                    for (EbayOrderDetail ebayOrderDetail : ebayOrderDetails) {
                        ebayOrder.setOrderTotalAmount(ebayOrder.getOrderTotalAmount()
                                .add(this.totalBigDecimal(ebayOrderDetail.getQuantityPurchased(), ebayOrderDetail.getTransactionPrice())));
                    }
                }
            }
        } catch (Exception e) {
            _log.error("________________根据条件查询ebay平台订单号出错________{}________", e);
        }
        return ebayOrders;
    }

    private BigDecimal totalBigDecimal(Integer skuNum, String itemPrice) {
        if (StringUtils.isBlank(itemPrice) || skuNum == null || skuNum == 0)
            return BigDecimal.valueOf(0);
        String[] split = StringUtils.split(itemPrice, "#");
        return new BigDecimal(split[0]).multiply(BigDecimal.valueOf(skuNum));
        /*if (split.length < 3) {
            return new BigDecimal(split[0]).multiply(BigDecimal.valueOf(skuNum));
        } else {
            return new BigDecimal(split[0]).multiply(new BigDecimal(split[2] == null ? "1.00" : split[2])).multiply(BigDecimal.valueOf(skuNum));
        }*/
    }

    @Override
    public EbayOrder queryEbayOrderDetail(String orderId) {
        if (StringUtils.isBlank(orderId))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "订单号不能为空。。。");
        return ebayOrderMapper.queryEbayOrderDetail(orderId);
    }

    public List<Empower> queryEmpowerListByPLAccount() {
        String username = loginUserInfo.getUserInfo().getUser().getUsername();
        if (StringUtils.isBlank(username)) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请先登录。。。");
//        _log.error("______________________获取的登录人品连账号_________________" + username + "____________________");
        if ("GLHT@qq.com".equalsIgnoreCase(username)) return null;
        String data = remoteSellerService.selectObjectByAccount(null, username, 1, null, 1);
        String result = Utils.returnRemoteResultDataString(data, "调用卖家微服务异常 。。。");
        List<Empower> empowerList = JSONObject.parseArray(result, Empower.class);
        return empowerList;
    }

    @Override
    public void testSyncEbayOrder() {
        List<Empower> empowers = empowerMapper.selectEbayAvailableToken();
        if (CollectionUtils.isEmpty(empowers)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询店铺授权表获取可用ebay平台token异常。。。");
        }

        empowers.forEach(empower -> {
            SyncEbayOrder syncEbayOrder = new SyncEbayOrder(empower);
            Thread thread = syncEbayOrder.new ExecuteSyncEbayOrder(syncEbayOrder);
            ThreadPoolUtil.executeThread(thread);
        });
    }
}
