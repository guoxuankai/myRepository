package com.rondaful.cloud.order.service;

import com.alibaba.fastjson.JSONArray;
import com.codingapi.tx.annotation.TxTransaction;
import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.service.BaseService;
import com.rondaful.cloud.order.entity.Amazon.AmazonDelivery;
import com.rondaful.cloud.order.entity.BuyerCountAndCountryCode;
import com.rondaful.cloud.order.entity.OrderInfoVO;
import com.rondaful.cloud.order.entity.OrderRecord;
import com.rondaful.cloud.order.entity.PLOrderInfoDTO;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.SystemExport;
import com.rondaful.cloud.order.entity.TheMonthOrderCount;
import com.rondaful.cloud.order.entity.TheMonthOrderSaleAndProfit;
import com.rondaful.cloud.order.entity.system.SysOrderNew;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderTransferInsertOrUpdateDTO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface ISysOrderService extends BaseService<SysOrder> {


    /*获取导出结果*/
    List<SystemExport> getExportResults(String sku,boolean isSeller, Byte errorOrder, Byte payStatus, Byte orderSource, String recordNumber, String orderTrackId, String isAfterSaleOrder1, String sourceOrderId, String isLogisticsAbnormal, String splittedOrMerged, String platformSellerAccount, String sellerPlAccount, String sysOrderId, String orderStatus, String startDate, String endDate, String startTime, String endTime) throws Exception;

    Map<String,Object> getGrossMargin(SysOrder sysOrder);

    /*亚马逊发货*/
    void amazonDelivery();

    /**
     * 更改已发货90天的订单为已完成状态
     */
    void updateSysOrderStatus();

    void test();

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    void getUploadResultAndInsertAndUpdate(List<AmazonDelivery> amazonOrderList, String sellerId, String marketplaceId, String mwsToken, String
            xmlString) throws ParseException;

    Page<SysOrder> selectSysOrdersByPlatformSellerAccount(String orderStatus);

    Map<String, Object> findUserGrossMargin(String userName, String type);

    String addSysOrder(SysOrder sysOrder);

    void insertBulk(SysOrder sysOrder, SysOrderNew sysOrderNew);

    String addSysOrderNew(SysOrderNew sysOrderNew);

    PageInfo<SysOrderNew> selectSysOrderByMultiCondition(String sku,Byte errorOrder, Byte payStatus, Byte orderSource, String recordNumber, String orderTrackId, String isAfterSaleOrder, String sourceOrderId, String isLogisticsAbnormal, String
            splittedOrMerged, String platformSellerAccount, String sellerPlAccount, String sysOrderId,
                                                         String orderStatus, String startDate, String endDate, String startTime, String endTime, Integer bindex, Integer num);

    Page<SysOrder> selectSysOrderBySellerPlAccount(String sellerPlAccount);

    SysOrderNew getSysOrderDetailByPlOrderId(String orderId);

    SysOrderNew getSysOrderContainAllSkuByOrderId(String orderId);

    SysOrderNew queryOrder(String orderId);

    SysOrderNew queryOrderForDetails(String orderId);

    SysOrderNew queryOrderByOther(String orderId);

    String updateSysOrder(SysOrderNew orderNew, String area);

    void commonDeal(SysOrderNew orderNew);

    String addErpCreateSysOrder(SysOrder sysOrder);

    String deleteInvalidOrders(String sysOrderId);

    @Transactional(rollbackFor = Exception.class)
    void sendMsgAndAddLog(SysOrderNew orderNew, String orderId);

    @Transactional(rollbackFor = Exception.class)
    @TxTransaction(isStart = true)
    void sendMsgAndAddLogByOther(SysOrderNew orderNew, String orderId);

    void cancelMoney(String orderId);

    void judgeOrderStatusIsHandleAndLock(SysOrderNew orderNew);

    /**
     * 拦截拆分包裹的订单(发货内部调用)wujiachuang
     * @param sysOrderNew
     * @return
     */
    void InterceptSplitPackageOrderByDelievryUse(SysOrderNew sysOrderNew);

    @Transactional(rollbackFor = Exception.class)
    void updatePackageOrderIdOrSetExceptionInfo(SysOrderNew orderNew);

    void updatePackageOrderIdOrSetExceptionInfo2(SysOrderNew orderNew);

    @Transactional(rollbackFor = Exception.class)
    void updateOrderStatusAndEmptyTrackInfoAndSendMsg(SysOrderNew orderNew, String sysOrderId);

    String judgeWarehouseByWarehouseId(String warehouseId);

    String interceptSystemOrder(String sysOrderId) throws Exception;

    @Transactional(rollbackFor = Exception.class)
    void updateInterceptStatus(SysOrderNew orderNew);

    String cancelGoodCangOrder(String sysOrderId, SysOrderNew orderNew, String deliveryWarehouseId, String orderTrackId);

    @Transactional(rollbackFor = Exception.class)
    String cancelErpOrder(String sysOrderId, SysOrderNew orderNew, String orderTrackId);

    @Transactional(rollbackFor = Exception.class)
    String cancelWmsOrder(String sysOrderId, SysOrderNew orderNew, String orderTrackId, String warehouseId);

    String returnSplitPackageInterceptFailure(String sysOrderId, SysOrderNew orderNew);

    @Transactional(rollbackFor = Exception.class)
    void updateOperateTrackId(SysOrderNew orderNew);

    boolean dealSplitPackage(String sysOrderId, SysOrderNew orderNew, boolean flag);

    String returnInterceptFailure(String sysOrderId, String orderTrackId, String operateOrderId);

    String returnInterceptSuccessful(String sysOrderId, SysOrderNew orderNew);

    void setAndSendDataAfterInterceptOrder(String sysOrderId, SysOrderNew order);

    @Transactional(rollbackFor = Exception.class)
    @TxTransaction(isStart = true)
    void updateOrderStatusAndSendMsg(String sysOrderId, SysOrderNew order);

    void updateOrderTrackId(String sysOrderId, SysOrderNew order);

//    String deleteErpInvalidOrders(String sysOrderId);

    List<SystemExport> setData(List<SysOrderNew> orderNewList, boolean isSeller);

    JSONArray export(List<SystemExport> systemExportList);

    OrderRecord getOrderRecord(String sellerPlId);

    void updateOrderStatus(String sysOrderId, byte status);

    void updateOrderItemStatus(String sysOrderId, String sku, byte status);

    Map<String, BigDecimal> getUserProfit(String username);

    Map<String, Object> getOrderCountTodayAndYesterday(String loginName, String shopName);

    List<TheMonthOrderCount> querySellerDayOrderCount(String loginName, String shopName);

    Map<String, Object> querySellerTotalSalesAndTotalProfit(String loginName, String shopName);

    List<TheMonthOrderSaleAndProfit> querySellerDayTotalSalesAndTotalProfit(String loginName, String shopName);

    List<BuyerCountAndCountryCode> queryTotalBuyerCountAndCountry(String loginName, String shopName);

    Map<String, Object> queryBuyerCountAndBuyerCountBuyAgain(String loginName, String shopName);

    List<SysOrder> selectSysOrdersBySourceOrderIdAndType(String sourceOrderId, Integer orderSource);

    void updateMarkException(String orderId, String text);

    String cancelInvalidOrders(String orderId);

    @Transactional(rollbackFor = Exception.class)
    void updateCancelStatus(SysOrderNew orderNew);

    @Transactional(rollbackFor = Exception.class)
    void updateStatusAndAddLog(String orderId);

    @Transactional(rollbackFor = Exception.class)
    void reactivationOrder(SysOrderNew orderNew);

    /**
     * 查询系统订单
     * @param sysOrderId
     * @return
     */
    SysOrder selectSysOrderBySysOrderId(String sysOrderId);

    /**
     * 根据来源订单ID获取系统订单
     *
     * @param sourceOrderId 来源订单ID
     * @return List<SysOrder>
     */
    List<SysOrder> findSysOrderBySourceOrderId(String sourceOrderId);


    /**
     * 新版插入系统单+自动发货
     * @param sysOrderInsertDTO
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    void insertSysOrderBatch(SysOrderTransferInsertOrUpdateDTO sysOrderInsertDTO) throws Exception;

    /**
     * 分拣订单插入的数据
     * 以及更新ebay表的数据
     *
     * @param sysOrderDTOList {@link List<SysOrderDTO>}
     * @return {@link SysOrderTransferInsertOrUpdateDTO}
     */
    SysOrderTransferInsertOrUpdateDTO splitInsertSysOrderData(List<SysOrderDTO> sysOrderDTOList);

    /**
     * 根据来源订单ID查找品连订单号
     * @param sourceOrderIds
     * @return
     */
    Map<String, List<String>> queryPlOrderIdBySourceOrderId(List<String> sourceOrderIds);

    /**
     * 根据包裹号、sku更改售后状态wujiachuang
     * @param orderTrackId
     * @param sku
     * @param status
     */
    void updateOrderPackageItemStatus(String orderTrackId, String sku, byte status);

    /**
     * 供财务，获取订单一系列信息wujiachuang
     * @param sysOrderIds
     * @return
     */
    List<PLOrderInfoDTO> getPLOrderInfo(List<String> sysOrderIds);

    /**
     * 供财务，判断订单状态wujiachuang
     * @param sysOrderIds
     * @return
     */
    boolean judgePLOrderStatus(List<String> sysOrderIds);

    /**
     * 供财务，获取订单一系列信息wujiachuang
     * @param sysOrderIds
     * @return
     */
    List<PLOrderInfoDTO> getPLOrderInfoBatch(List<String> sysOrderIds);

    /**
     * 内部：供应商服务调用
     * @param packageId
     * @return
     */
    OrderInfoVO getOrderInfoToSupplier(String packageId);

    Object intercept();
}
