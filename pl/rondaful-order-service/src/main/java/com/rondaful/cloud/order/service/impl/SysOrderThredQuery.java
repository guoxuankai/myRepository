package com.rondaful.cloud.order.service.impl;

import com.rondaful.cloud.order.entity.SystemExport;
import com.rondaful.cloud.order.entity.system.SysOrderNew;
import com.rondaful.cloud.order.mapper.SysOrderMapper;
import com.rondaful.cloud.order.mapper.SysOrderNewMapper;
import com.rondaful.cloud.order.mapper.SysOrderPackageMapper;
import com.rondaful.cloud.order.service.ISysOrderService;
import com.rondaful.cloud.order.utils.ApplicationContextProvider;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.concurrent.Callable;

@Mapper
public class SysOrderThredQuery implements Callable<List<SystemExport>> {
    private String splittedOrMergedStatus;
    private byte plOrderStatus;
    private ISysOrderService myService;//需要通过够早方法把对应的业务service传进来 实际用的时候把类型变为对应的类型
    private int bindex;//分页index
    private int num;//数量
    private Byte errorOrder;
    private Byte payStatus;
    private Byte orderSource;
    private String recordNumber;
    private String orderTrackId;
    private String sourceOrderId;
    private String isLogisticsAbnormal;
    private String sysOrderId;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private Byte isAfterSaleOrder;
    private List<Integer> userIds;  //账号ID集合
    private List<Integer> empIds;  //店铺ID集合
    private boolean isSeller;//是否是卖家导出报表
    private List<String> sysOrderIdList;//订单ID集合
    private SysOrderMapper sysOrderMapper = ApplicationContextProvider.getApplicationContext().getBean(SysOrderMapper.class);
    private SysOrderNewMapper sysOrderNewMapper = ApplicationContextProvider.getApplicationContext().getBean(SysOrderNewMapper.class);
    private SysOrderPackageMapper sysOrderPackageMapper = ApplicationContextProvider.getApplicationContext().getBean(SysOrderPackageMapper.class);
    private SysOrderServiceImpl sysOrderServiceImpl = ApplicationContextProvider.getApplicationContext().getBean(SysOrderServiceImpl.class);

    public SysOrderThredQuery(ISysOrderService myService, boolean isSeller, Byte errorOrder, Byte payStatus, Byte orderSource, String recordNumber, String orderTrackId, String sourceOrderId,
                              String isLogisticsAbnormal, List<String> sysOrderIdList, String startDate, String endDate, String startTime, String endTime,
                              List<Integer> userIds, List<Integer> empIds, byte isAfterSaleOrder, String splittedOrMergedStatus, byte plOrderStatus, Integer bindex, Integer num) {
        this.sysOrderIdList=sysOrderIdList;
        this.myService = myService;
        this.bindex = bindex;
        this.num = num;
        this.errorOrder = errorOrder;
        this.payStatus = payStatus;
        this.orderSource = orderSource;
        this.recordNumber = recordNumber;
        this.orderTrackId = orderTrackId;
        this.isAfterSaleOrder = isAfterSaleOrder;
        this.sourceOrderId = sourceOrderId;
        this.isLogisticsAbnormal = isLogisticsAbnormal;
        this.splittedOrMergedStatus = splittedOrMergedStatus;
        this.sysOrderId = sysOrderId;
        this.plOrderStatus = plOrderStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.userIds = userIds;
        this.empIds = empIds;
        this.isSeller = isSeller;
    }

    @Override
    public List<SystemExport> call() throws Exception {
        List<SysOrderNew> list = sysOrderNewMapper.selectSysOrderByMultiCondition(errorOrder, payStatus, orderSource, recordNumber, orderTrackId, isAfterSaleOrder, sourceOrderId, isLogisticsAbnormal, splittedOrMergedStatus, empIds, userIds, sysOrderIdList, plOrderStatus, startDate, endDate, startTime, endTime, bindex, num);
        list.forEach(sysOrderNew -> {
            sysOrderServiceImpl.queryOrderShowPackageAndDetailInfo(sysOrderNew, true);
//            sysOrderServiceImpl.setGrossMarginAndProfitMarginNullIfLessZero(sysOrderNew); //如果利润<0设置NUll
            sysOrderServiceImpl.setPlatformOrderInfo(sysOrderNew);
        });
        if (list==null||list.isEmpty()){
            return null;
        }
        List<SystemExport> systemExports = sysOrderServiceImpl.setData(list, isSeller);
        return systemExports;

    }

}

