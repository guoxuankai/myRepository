package com.rondaful.cloud.order.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.BuyerCountAndCountryCode;
import com.rondaful.cloud.order.entity.TheMonthOrderCount;
import com.rondaful.cloud.order.entity.TheMonthOrderSaleAndProfit;
import com.rondaful.cloud.order.entity.system.SysOrderNew;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysOrderNewMapper extends BaseMapper<SysOrderNew> {
    /**
     * 根据订单ID查询订单wujiachuang
     * @param sysOrderId
     * @return
     */
    SysOrderNew queryOrderByOrderId(@Param("sysOrderId") String sysOrderId);

    /**
     * 根据订单ID更改订单状态wujiachuang
     * @param sysOrderId
     * @param orderStatus
     */
    void updateOrdersStatus(@Param("sysOrderId") String sysOrderId, @Param("orderStatus") byte orderStatus);

    /**
     * 根据订单ID更改订单状态wujiachuang
     * @param splittedOrMerged
     */
    void updateOrdersSplittedOrMerged(@Param("sysOrderId") String sysOrderId, @Param("splittedOrMerged") String splittedOrMerged);

    /**
     * 更改订单支付方式（取消作废用）wujiachuang
     * @param orderNew
     */
    void updateOrderPayMethod(SysOrderNew orderNew);

    /**
     * 根据订单ID 更改订单支付ID和支付方式
     * @param sysOrderId
     */
    void updatePayIdAndPayMethod(@Param("sysOrderId") String sysOrderId);

    /**
     * 查询发货90天的订单
     * @return
     */
    List<SysOrderNew> selectShippedSysOrder();

    /**
     * 不定查询采购订单wujiachuang
     * @param errorOrder
     * @param payStatus
     * @param orderSource
     * @param recordNumber
     * @param orderTrackId
     * @param isAfterSaleOrder
     * @param sourceOrderId
     * @param isLogisticsAbnormal
     * @param splittedOrMergedStatus
     * @param shopNameIdLists
     * @param plAccountIdsList
     * @param sysOrderIdList
     * @param plOrderStatus
     * @param startDate
     * @param endDate
     * @param startTime
     * @param endTime
     * @param bindex
     * @param num
     * @return
     */
    List<SysOrderNew> selectSysOrderByMultiCondition(@Param("errorOrder")Byte errorOrder,@Param("payStatus")Byte payStatus,@Param("orderSource") Byte orderSource,
                                                     @Param("recordNumber") String recordNumber, @Param("orderTrackId") String orderTrackId,
                                                     @Param("isAfterSaleOrder") Byte isAfterSaleOrder, @Param("sourceOrderId") String sourceOrderId,
                                                     @Param("isLogisticsAbnormal") String isLogisticsAbnormal, @Param("splittedOrMergedStatus") String splittedOrMergedStatus,
                                                     @Param("shopNameIdLists") List<Integer> shopNameIdLists, @Param("plAccountIdsList") List<Integer> plAccountIdsList,
                                                     @Param("sysOrderIdList") List<String> sysOrderIdList, @Param("plOrderStatus") Byte plOrderStatus, @Param("startDate") String startDate,
                                                     @Param("endDate") String endDate, @Param("startTime") String startTime, @Param("endTime") String endTime,
                                                     @Param("bindex")Integer bindex,@Param("num")Integer num);

    /**
     * 批量插入订单
     * @param sysOrderDTOList {@link List<SysOrderDTO>}
     */
    void insertBatchSelective(@Param("orderList") List<SysOrderDTO> sysOrderDTOList);

    /**
     * 手工标记异常   wujiachuang
     * @param sysOrderId
     * @param markException
     */
    void updateMarkException(@Param("sysOrderId")String sysOrderId, @Param("markException")String markException);

    /**
     * 拦截订单:更改订单状态为已拦截并清空发货异常信息、跟踪单号、物流商单号_WJC
     * @param sysOrderId
     */
    void updateInterceptSystemOrder(String sysOrderId);

    /**
     * 获取查询条数wujiachuang
     * @param errorOrder
     * @param payStatus
     * @param orderSource
     * @param recordNumber
     * @param orderTrackId
     * @param isAfterSaleOrder
     * @param sourceOrderId
     * @param isLogisticsAbnormal
     * @param splittedOrMergedStatus
     * @param shopNameIdLists
     * @param plAccountIdsList
     * @param sysOrderIdList
     * @param plOrderStatus
     * @param startDate
     * @param endDate
     * @param startTime
     * @param endTime
     * @return
     */
    int getSysOrderNewCount(@Param("errorOrder")Byte errorOrder,@Param("payStatus")Byte payStatus,@Param("orderSource") Byte orderSource,
                            @Param("recordNumber") String recordNumber, @Param("orderTrackId") String orderTrackId, @Param("isAfterSaleOrder") byte isAfterSaleOrder,
                            @Param("sourceOrderId") String sourceOrderId, @Param("isLogisticsAbnormal") String isLogisticsAbnormal,
                            @Param("splittedOrMergedStatus") String splittedOrMergedStatus,
                            @Param("shopNameIdLists") List<Integer> shopNameIdLists, @Param("plAccountIdsList") List<Integer> plAccountIdsList,
                            @Param("sysOrderIdList") List<String> sysOrderIdList, @Param("plOrderStatus") byte plOrderStatus, @Param("startDate") String startDate,
                            @Param("endDate") String endDate, @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 根据来源订单号查找品连订单号集合wujiachuang
     * @param sourceOrderId
     * @return
     */
    List<String> queryPlOrderIdBySourceOrderId(@Param("sourceOrderId") String sourceOrderId);

    /**
     * 根据系统订单ID批量查询系统订单集合_ZJL
     *
     * @param sysOrderIds
     */
    List<SysOrderNew> selectBatchSysOrderListBySysOrderId(@Param("sysOrderIds") List<String> sysOrderIds);

    /**
     * 将订单变更为异常订单wujiachuang
     * @param sysOrderId
     */
    void updateToExceptionOrder(String sysOrderId);

    /**
     * 根据订单号更新售后订单状态_WJC
     * @param sysOrderId
     * @param status
     */
    void updateOrderAfterStatus(@Param("sysOrderId") String sysOrderId,@Param("status") byte status);

    void updateBySysOrderIdSelective(SysOrderNew sysOrderNew);

    /**
     * 编辑订单wujiachuang
     * @param orderNew
     */
    void updateOrder(SysOrderNew orderNew);

    /**
     * 清空订单异常标记wujiachuang
     * @param sysOrderId
     */
    void resetOrderErrorStatus(String sysOrderId);

    /* *功能描述 星商对外接口调整
     * @date 2019/07/29
     * @param [sysOrderId, sourceOrderId]
     * @return SysOrder
     * @author lz
     */
    SysOrderNew selectSysOrderBySysOrderIdAndSourceOrderId(@Param("sysOrderId") String sysOrderId, @Param("sourceOrderId") String sourceOrderId);

    /**
     * 根据系统订单号批量更新发货状态 wujiachuang
     * @param deliverStatus
     * @param list
     * @param updateBy
     */
    void updateOrderCompleteStatus(@Param("deliverStatus") Byte deliverStatus, @Param("list") List<String> list, @Param("updateBy") String updateBy);


    /**
     * 查询用户今日的订单数量_WJC
     *
     * @param loginName
     * @param shopNameId
     * @return
     */
    Long getOrderCountToday(@Param("loginName") String loginName, @Param("shopNameId") Integer shopNameId);

    /**
     * 查询用户昨日的订单数量_WJC
     *
     * @param loginName
     * @param shopNameId
     * @return
     */
    Long getOrderCountYesterday(@Param("loginName") String loginName, @Param("shopNameId") Integer shopNameId);

    /**
     * 查询卖家日订单量_WJC
     *
     * @param loginName
     * @param shopNameId
     * @return
     */
    List<TheMonthOrderCount> querySellerDayOrderCount(@Param("loginName") String loginName, @Param("shopNameId") Integer shopNameId);

    /**
     * 查询卖家总销售额_WJC
     *
     * @param loginName
     * @param shopNameId
     * @param time
     * @return
     */
    Double querySellerTotalSales(@Param("loginName") String loginName, @Param("shopNameId") Integer shopNameId, @Param("time") String time);

    /**
     * 查询卖家总利润_WJC
     *
     * @param loginName
     * @param shopNameId
     * @param time
     * @return
     */
    Double querySellerTotalProfit(@Param("loginName") String loginName, @Param("shopNameId") Integer shopNameId, @Param("time") String time);

    /**
     * 查询近一个月卖家销售额和利润_WJC
     *
     * @param loginName
     * @param shopNameId
     * @return
     */
    List<TheMonthOrderSaleAndProfit> queryDaySellerSalesAndProfit(@Param("loginName") String loginName, @Param("shopNameId") Integer shopNameId);

    /**
     * @param loginName
     * @param shopNameId
     * @return
     */
    List<BuyerCountAndCountryCode> queryTotalBuyerCountAndCountry(@Param("loginName") String loginName, @Param("shopNameId") Integer shopNameId);

    List<String> queryInterceptOrder();
}