package com.rondaful.cloud.order.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.order.entity.BuyerCountAndCountryCode;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.TheMonthOrderCount;
import com.rondaful.cloud.order.entity.TheMonthOrderSaleAndProfit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysOrderMapper extends BaseMapper<SysOrder> {
    /**
     * 查询用户当月或者上个月的毛利_WJC
     *
     * @param userName
     * @param type
     * @return
     */
    Map<String, Object> findUserGrossMargin(@Param("userName") String userName, @Param("type") String type);

    /**
     * 根据订单ID修改订单跟踪号_WJC
     *
     * @param sysOrderId
     * @param orderTrackId
     * @return
     */
    int updateOrderTrackIdByOrderId(@Param("sysOrderId") String sysOrderId, @Param("orderTrackId") String orderTrackId);

    /**
     * 根据系统订单号查询ERP订单跟踪号_ZJL
     *
     * @param sysOrderId
     * @return
     */
    String selectOrderTrackIdByOrderId(String sysOrderId);

//    /**
//     * 查询配货中的ERP订单
//     *
//     * @return
//     */
//    List<SysOrder> getErpOrderId(String warehouseCode);

    /**
     * 根据系统订单Id查询系统订单实体类_ZJL
     *
     * @param fatherSysOrderId
     * @return
     */
    SysOrder selectSysOrderBySysOrderId(String fatherSysOrderId);
	
	/**
     * 根据系统订单Id与订单来源ID查询系统订单实体类
     *
     * @param sysOrderId  原始订单号
     * @param sourceOrderId     来源订单号
     * @return
     */
    SysOrder selectSysOrderBySysOrderIdAndSourceOrderId(@Param("sysOrderId") String sysOrderId,@Param("sourceOrderId") String sourceOrderId);

    /**
     * 通过卖家平台账号查询系统订单_WJC
     *
     * @param platformShopId
     * @return
     */
    List<SysOrder> selectSysOrdersByPlatformSellerAccount(Integer platformShopId);

    /**
     * 不定条件查询系统订单_WJC
     *
     * @param recordNumber
     * @param orderTrackId
     * @param isAfterSaleOrder
     * @param sourceOrderId
     * @param isLogisticsAbnormal
     * @param splittedOrMergedStatus
     * @param shopNameIdLists
     * @param plAccountIdsList
     * @param sysOrderId
     * @param plOrderStatus
     * @param startDate
     * @param endDate
     * @param startTime
     * @param endTime
     * @return
     */
    List<SysOrder> selectSysOrderByMultiCondition(@Param("errorOrder")Byte errorOrder,@Param("payStatus")Byte payStatus,@Param("orderSource") Byte orderSource,@Param("recordNumber") String recordNumber, @Param("orderTrackId") String orderTrackId, @Param("isAfterSaleOrder") Byte isAfterSaleOrder, @Param("sourceOrderId") String sourceOrderId, @Param("isLogisticsAbnormal") String isLogisticsAbnormal, @Param("splittedOrMergedStatus") Byte splittedOrMergedStatus,
                                                  @Param("shopNameIdLists") List<Integer> shopNameIdLists, @Param("plAccountIdsList") List<Integer> plAccountIdsList, @Param("sysOrderId") String sysOrderId,
                                                  @Param("plOrderStatus") Byte plOrderStatus, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("startTime") String startTime, @Param("endTime") String endTime,@Param("bindex")Integer bindex,@Param("num")Integer num);

    /**
     * 获取不定查询的记录条数
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
     * @param sysOrderId
     * @param plOrderStatus
     * @param startDate
     * @param endDate
     * @param startTime
     * @param endTime
     * @return
     */
    Integer getSysOrderCount(@Param("errorOrder")Byte errorOrder,@Param("payStatus")Byte payStatus,@Param("orderSource") Byte orderSource,@Param("recordNumber") String recordNumber, @Param("orderTrackId") String orderTrackId, @Param("isAfterSaleOrder") byte isAfterSaleOrder, @Param("sourceOrderId") String sourceOrderId, @Param("isLogisticsAbnormal") String isLogisticsAbnormal, @Param("splittedOrMergedStatus") byte splittedOrMergedStatus,
                                                  @Param("shopNameIdLists") List<Integer> shopNameIdLists, @Param("plAccountIdsList") List<Integer> plAccountIdsList, @Param("sysOrderId") String sysOrderId,
                                                  @Param("plOrderStatus") byte plOrderStatus, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 通过卖家品连账号查询订单_WJC
     *
     * @param sellerPlAccount
     * @return
     */
    List<SysOrder> selectSysOrderBySellerPlAccount(String sellerPlAccount);

    /**
     * 通过订单ID查询系统订单详情_WJC
     *
     * @param orderId
     * @return
     */
    SysOrder getSysOrderDetailByPlOrderId(String orderId);

    /**
     * 通过订单ID更改系统订单_WJC_ZJL
     *
     * @param sysOrder
     * @return
     */
    int updateSysOrder(@Param("sysOrder") SysOrder sysOrder);

    /**
     * 通过订单ID更改系统订单收货地址_WJC
     *
     * @param sysOrder
     * @return
     */
    int updateSysOrderAddress(@Param("sysOrder") SysOrder sysOrder);

    /**
     * 通过订单ID更改系统订单物流信息_WJC
     *
     * @param sysOrder
     * @return
     */
    int updateSysOrderLogistics(@Param("sysOrder") SysOrder sysOrder);

    /**
     * 选择性更新系统订单
     *
     * @param sysOrder
     * @return
     */
    int updateSysOrderSelective(@Param("sysOrder") SysOrder sysOrder);

    /**
     * 根据sysOrderId(main_order_id)查询所有子系统订单状态和系统订单号_ZJL
     *
     * @param sysOrderId
     * @return
     */
    List<SysOrder> selectDeliveryStatusAndSysIdByMainOrderId(String sysOrderId);

    /**
     * 根据sysOrderId(main_order_id主订单ID)删除订单_ZJL
     *
     * @param mainOrderId
     * @return
     */
    int deleteBySysOrderId(String mainOrderId);

    /**
     * 根据系统订单ID批量查询系统订单集合_ZJL
     *
     * @param sysOrderIds
     */
    List<SysOrder> selectBatchSysOrderListBySysOrderId(@Param("sysOrderIds") List<String> sysOrderIds);

    /**
     * 根据系统系统订单ID查询订单发货状态_ZJL
     *
     * @param sysOrderId
     */
    String selectDeliveryStatusBySysOrderId(String sysOrderId);


    List<SysOrder> selectShippedSysOrder();

    /**
     * 根据sysOrderId删除订单_ZJL
     *
     * @param sysOrderId
     * @return
     */
    int deleteSysOrderBySysOrderId(String sysOrderId);

    /**
     * 根据系统订单ID  sysOrderId更新系统订单数据_ZJL
     *
     * @param sysOrder
     * @return
     */
    int updateBySysOrderIdSelective(SysOrder sysOrder);

    /**
     * 通过订单ID作废订单：更改订单状态为已作废_WJC
     *
     * @param sysOrderId
     * @return
     */
    int updateInvalidOrders(String sysOrderId);

    /**
     * 更改订单状态_WJC
     * @param orderStatus
     * @param sysOrderId
     * @return
     */
    int updateOrdersStatus(@Param("orderStatus")Byte orderStatus, @Param("sysOrderId")String sysOrderId);

    /**
     * 通过订单ID查询订单状态_WJC
     *
     * @param sysOrderId
     * @return
     */
    Byte selectSysOrderStatus(String sysOrderId);

    /**
     * 通过订单ID拦截订单更改为已拦截状态并清空发货异常信息_WJC
     *
     * @param sysOrderId
     * @return
     */
    int updateInterceptSystemOrder(String sysOrderId);

    /**
     * 根据mainOrderId查询系统订单数据集合_ZJL
     *
     * @param mainOrderId
     * @return
     */
    List<SysOrder> selectSysOrderByMainOrderId(String mainOrderId);

    /**
     * 通过子订单ID批量查询订单_ZJL
     *
     * @param sysOrderIds
     * @return
     */
    List<SysOrder> selectSysChildOrdersByChildOrderIds(@Param("sysOrderIds") List<String> sysOrderIds);

    /***
     * 根据ERP订单追踪号查询系统订单对象_ZJL
     * @param orderTrackId
     * @return
     */
    SysOrder selectSysOrderByOrderTrackId(String orderTrackId);

    /**
     * 根据ERP订单追踪号更新系统订单信息_ZJL
     *
     * @param sysOrder
     */
    int updateByOrderTrackIdSelective(SysOrder sysOrder);

    /**
     * 根据系统订单ID更新异常发货信息和订单发货状态_ZJL
     *
     * @param sysOrder
     */
    int updateStatusAndShipExceptionBySysOrderId(SysOrder sysOrder);

    /**
     * 通过卖家品连ID查询订单数量_WJC
     *
     * @param sellerPlId
     * @return
     */
    Long selectOrderCount(String sellerPlId);

    /**
     * 通过卖家品连ID查询订单总销售额_WJC
     *
     * @param sellerPlId
     * @return
     */
    Double selectOrderSaleroom(String sellerPlId);

    /**
     * 根据系统订单ID更新PayId，OrderDeliveryStatus
     *
     * @param sysOrderBean
     */
    void updateDataBySysOrderIdSelective(SysOrder sysOrderBean);

    /**
     * 根据系统订单号更新订单状态
     *
     * @param sysOrderId
     */
    void updateOrderAfterStatus(@Param("sysOrderId") String sysOrderId,@Param("status") byte status);

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
     * @param loginName
     * @param shopNameId
     * @return
     */
    List<BuyerCountAndCountryCode> queryTotalBuyerCountAndCountry(@Param("loginName") String loginName, @Param("shopNameId") Integer shopNameId);

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
    Long queryBuyerCount(@Param("loginName") String loginName, @Param("shopNameId") Integer shopNameId);

    /**
     * @param loginName
     * @param shopNameId
     * @return
     */
    Long queryBuyerCountBuyAgain(@Param("loginName") String loginName, @Param("shopNameId") Integer shopNameId);

    /**
     * 重置支付ID和支付方式_WJC
     *
     * @param sysOrderId
     * @return
     */
    void updatePayIdAndPayMethod(@Param("sysOrderId") String sysOrderId);

    /**
     * 根据系统订单号查询系统订单（不查询订单项数据）_ZJL
     *
     * @param sysOrderId
     * @return
     */
    SysOrder selectSysOrderByPrimaryKey(String sysOrderId);

    /**
     * 批量插入系统订单
     *
     * @param list
     * @return
     */
    Integer inserts(List<SysOrder> list);

    /**
     * 根据来源订单ID及订单来源类型查询系统订单
     *
     * @param sourceOrderId
     * @param orderSource
     * @return com.rondaful.cloud.order.entity.SysOrder
     * @Author chenjunhua
     **/

    List<SysOrder> selectSysOrdersBySourceOrderIdAndType(@Param("sourceOrderId") String sourceOrderId, @Param("orderSource") Integer orderSource);

/*    *//**
     * 查询配货中的谷仓订单
     *
     * @return
     *//*
    List<SysOrder> getGoodCangOrderId(String warehouseCode);*/
    /**
     * 根据仓库ID集合查询配货中的订单
     *
     * @return
     */
    List<SysOrder> getOrderByWarehouseId(@Param("warehouseIds") List<Integer> warehouseIds);

    /**
     * 根据系统订单ID更新异常发货信息和订单发货状态和物流跟踪信息_XD
     *
     * @param sysOrder
     */
    int updateOrderInfoBySysOrderId(SysOrder sysOrder);

    /**
     * 根据系统订单ID更新正常发货信息和订单发货状态和物流跟踪信息_XD
     *
     * @param sysOrder
     */
    int updateBySysOrderId(SysOrder sysOrder);

    /**
     * 根据订单跟踪号查询系统订单号xd
     *
     * @param trackId
     * @return
     */
    String findSysOrderIdByTrackId(String trackId);

    /**
     * 根据谷仓id或者跟踪号查询谷仓ID
     *
     * @param orderCode
     * @param referenceNo
     * @return
     */
    String findWarehouseIdById(@Param("orderCode") String orderCode, @Param("referenceNo") String referenceNo);

    /**
     * 手工标记异常   wujiachuang
     * @param sysOrderId
     * @param markException
     */
    void updateMarkException(@Param("sysOrderId")String sysOrderId, @Param("markException")String markException);

    /**
     * 根据系统订单号批量更新发货状态
     * @param deliverStatus
     * @param list
     * @param updateBy
     */
    void updateBatchDeliverStatus(@Param("deliverStatus") Byte deliverStatus, @Param("list") List<String> list, @Param("updateBy") String updateBy);

    /**
     * 根据来源订单ID获取系统订单
     * @param sourceOrderId 来源订单ID
     * @return List<SysOrder>
     */
    List<SysOrder> findSysOrderBySourceOrderId(@Param("sourceOrderId") String sourceOrderId);
}