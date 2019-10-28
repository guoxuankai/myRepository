package com.rondaful.cloud.order.service;

import com.rondaful.cloud.common.model.vo.freight.LogisticsCostEnum;
import com.rondaful.cloud.common.model.vo.freight.LogisticsCostVo;
import com.rondaful.cloud.common.model.vo.freight.LogisticsDetailVo;
import com.rondaful.cloud.common.model.vo.freight.SearchLogisticsListDTO;
import com.rondaful.cloud.order.entity.Amazon.AmazonOrderDetail;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.eBay.EbayOrder;
import com.rondaful.cloud.order.entity.erpentity.WareHouseDeliverCallBack;
import com.rondaful.cloud.order.entity.finance.OrderRequestVo;
import com.rondaful.cloud.order.entity.goodcang.GoodCangOrder;
import com.rondaful.cloud.order.entity.system.SysOrderNew;
import com.rondaful.cloud.order.entity.system.SysOrderPackage;
import com.rondaful.cloud.order.model.dto.syncorder.SplitPackageDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDTO;
import com.rondaful.cloud.order.model.dto.sysorder.DeliveryPackageDTO;
import com.rondaful.cloud.order.model.vo.sysorder.CalculateLogisticsResultVO;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ISystemOrderService {
    /**
     * 保存拆分后的子订单集合
     *
     * @param sysOrders
     */
    void saveSplittedSysOrder(List<SysOrder> sysOrders) throws Exception;

    /**
     * 保存拆分后的子包裹集合
     *
     * @param splitPackageDTO
     */
    void saveSplittedSysPackage(SplitPackageDTO splitPackageDTO) throws Exception;

    /**
     * 撤销已拆分的包裹
     *
     * @param sysOrderId
     * @return
     */
    void cancelSplittedSysPackage(String sysOrderId);

    /**
     * 撤销已拆分的系统订单
     *
     * @param sysOrderId
     * @return
     */
    void cancelSplittedSysOrder(String sysOrderId);

    /**
     * 保存合并后的订单
     *
     * @param sysOrderIds
     * @return
     */
    String saveMergedSysOrder(List<String> sysOrderIds) throws Exception;

    /**
     * 取消合并系统订单
     *
     * @param sysOrderId
     * @return
     */
    void cancelMergedSysOrder(String sysOrderId);

    /**
     * 单个订单发货
     *
     * @param sysOrderId
     * @return
     */
    void deliverGoodSingle(String sysOrderId) throws Exception;

    /**
     * 订单批量发货
     *
     * @param sysOrderIds
     * @return
     */
    List<SysOrder> deliverGoodBatch(List<String> sysOrderIds) throws Exception;

    /**
     * 仓库发货回调
     *
     * @param deliverCallBack
     * @throws JSONException
     */
    void wareHouseDeliverCallBack(WareHouseDeliverCallBack deliverCallBack) throws JSONException;

    /**
     * 仓库发货回调
     *
     * @param deliverCallBack {@link WareHouseDeliverCallBack} 回调信息
     * @throws Exception
     */
    void wareHouseDeliverCallBackNew(WareHouseDeliverCallBack deliverCallBack) throws Exception;

    /**
     * 导出ebay平台订单Excel表格
     *
     * @param hashMap
     * @return
     */
    void exportEbayOrderListExcel(HashMap hashMap);

    /**
     * 订单补发货:目前售后补发货调用
     *
     * @param sysOrder
     * @return
     */
    String replenishDeliverGood(SysOrder sysOrder) throws Exception;

    /**
     * 根据系统订单号或ERP订单跟踪号查询ERP发货进度信息
     *
     * @param hashMap
     * @return
     */
    Map<Object, Object> querySysOrderERPSpeedInfo(HashMap<String, String> hashMap) throws Exception;

    /**
     * 根据系ERP订单跟踪号查询ERP发货信息
     *
     * @return
     */
    void getSysOrderERPSpeedInfo() throws Exception;

    /**
     * 修改ebay订单项发货状态
     *
     * @param orderLineItemId
     */
    void updateEbayDetailDeliverStatus(String orderLineItemId);

    /**
     * 更新订单支付状态 | 内部调用接口
     *
     * @param sysOrderId
     * @param payStatus
     */
    void updatePayStatus(String sysOrderId, String payStatus);

    /**
     * 发货构造财务数据
     *
     * @param sysOrder
     * @return
     */
    OrderRequestVo constructOrderRequestVoData(SysOrder sysOrder);

    /**
     * 发货构造谷仓数据
     *
     * @param sysOrder
     * @return
     */
    GoodCangOrder constructGoodCangOrderData(SysOrder sysOrder);

    /**
     * 发货构造ERP数据
     *
     * @param sysOrder
     * @return
     */
    Map<String, Object> constructERPOrderData(SysOrder sysOrder) throws Exception;

    /**
     * 冻结金额发货核心事务方法ERP
     *
     * @param sysOrder
     * @param orderRequestVo
     * @param erpOrderMap
     * @throws Exception
     */
    void transactionFrozenAmountAndDeliverGoodERP(SysOrder sysOrder, OrderRequestVo orderRequestVo, Map<String, Object> erpOrderMap, String username) throws Exception;

    /**
     * 冻结金额发货核心事务方法GoodCang
     *
     * @param sysOrder
     * @param orderRequestVo
     * @param goodCangOrder
     */
    void transactionFrozenAmountAndDeliverGoodGoodCang(SysOrder sysOrder, OrderRequestVo orderRequestVo, GoodCangOrder goodCangOrder, String username) throws Exception;

    /**
     * 回传平台发货数据
     *
     * @param sysOrder
     */
    void deliverInfoCallBack(SysOrder sysOrder);

    /**
     * 构造eBay平台订单发货数据
     *
     * @param sysOrder
     */
    List<EbayOrder> constructEbayOrderDeliverInfo(SysOrder sysOrder);

    /**
     * 构造ebay订单发货数据
     *
     * @param deliveredPackage 发货包裹
     * @return
     */
    List<EbayOrder> constructEbayOrderDeliverInfoNew(SysOrderPackage deliveredPackage);

    /**
     * 回传eBay订单发货信息到eBay平台
     *
     * @param list
     */
    void sendEbayOrderDeliverInfo(List<EbayOrder> list, SysOrder sysOrder);

    /**
     * 回传ebay平台订单发货信息
     *
     * @param list             需要回传的ebay订单列表
     * @param sysOrder         系统订单
     * @param deliveredPackage 已发货的包裹
     */
    void sendEbayOrderDeliverInfoNew(List<EbayOrder> list, SysOrderNew sysOrder, SysOrderPackage deliveredPackage);

    /**
     * 回传Amazon订单发货信息到Amazon平台
     *
     * @param sysOrder
     */
    List<AmazonOrderDetail> sendAmazonOrderDeliverInfo(SysOrder sysOrder);

    /**
     * 回传Amazon订单发货信息到Amazon平台
     *
     * @param sysOrder         系统订单
     * @param deliveredPackage 已发货的包裹
     * @return
     */
    List<AmazonOrderDetail> sendAmazonOrderDeliverInfoNew(SysOrderNew sysOrder, SysOrderPackage deliveredPackage);

    /**
     * 推送出库记录信息
     *
     * @param orderTrackId
     */
    void pushDeliverInfoToWareHouse(String orderTrackId);

    /**
     * 回传Aliexpress订单发货信息到Aliexpress平台
     *
     * @param sysOrder
     */
    void sendAliexpressDeliverInfo(SysOrder sysOrder);

    /**
     * 回传Aliexpress订单发货信息到Aliexpress平台
     *
     * @param sysOrder         发货包裹订单数据
     * @param deliveredPackage 已发货包裹
     */
    void sendAliexpressDeliverInfoNew(SysOrderNew sysOrder, SysOrderPackage deliveredPackage);

    /**
     * 推送商品销售统计
     *
     * @param sysOrder
     */
    void pushCommoditySalesRecord(SysOrder sysOrder);

    /**
     * 推送商品销售统计
     *
     * @param deliveredPackage {@link SysOrderPackage} 已发货的包裹
     */
    void pushCommoditySalesRecord(SysOrderPackage deliveredPackage);

    /**
     * 校验平台订单能否继续发货
     *
     * @param sysOrderId
     */
    Integer canBeDeliveredPlatformOrder(String sysOrderId);

    /**
     * 售后订单完成售后标记对应系统订单已完成状态
     *
     * @param sysIDList
     */
    void cmsMarkSYSCompleted(List<String> sysIDList);

    /**
     * 获取系统订单
     *
     * @param sysOrderId
     * @return
     */
    SysOrderNew getSysOrderNew(String sysOrderId);

    /**
     * 计算预估物流费
     *
     * @param sysOrderPackageDTO {@link SysOrderPackageDTO}
     * @param platformType       平台 1(eBay) 2(Amazon) 3(Wish) 4(AliExpress)
     * @param warehouseId        仓库ID
     * @param countryCode        国家二字码
     * @param postCode           邮编
     * @param searchType         搜索条件 (1 价格最低  2 综合排序   3 物流速度最快)
     * @param logisticsCode      物流方式编码
     * @param storeId      店铺ID
     * @return {@link CalculateLogisticsResultVO}
     */
    CalculateLogisticsResultVO calculateEstimateFreight(SysOrderPackageDTO sysOrderPackageDTO,
                                                        String platformType, String warehouseId, String countryCode,
                                                        String postCode, Integer searchType, String logisticsCode,String city, Integer storeId, Integer handOrder);

    /**
     * 获取合适的物流方式
     *
     * @param searchLogisticsListDTO {@link SearchLogisticsListDTO}
     * @return {@link List<LogisticsDetailVo>}
     */
    List<LogisticsDetailVo> getSuitLogisticsByType(SearchLogisticsListDTO searchLogisticsListDTO);

    /**
     * 订单发货-新接口
     *
     * @param sysOrderId 系统订单ID
     * @throws Exception
     */
    void deliverGoodSingleNew(String sysOrderId,boolean isAutoDeliveryPackage) throws Exception;

    /**
     * 包裹发货
     *
     * @param deliveryPackageDTO
     * @param orderRequestVoList
     * @throws Exception
     */
    void deliveryPackage(DeliveryPackageDTO deliveryPackageDTO, List<OrderRequestVo> orderRequestVoList,boolean isAutoDeliveryPackage) throws Exception;

    /**
     * 计算预估物流费
     *
     * @param logisticsCostVo   {@link LogisticsCostVo}
     * @param logisticsResultVO {@link CalculateLogisticsResultVO}
     * @param calculateType     {@link LogisticsCostEnum}
     * @return {@link CalculateLogisticsResultVO}
     */
    CalculateLogisticsResultVO calculateLogisticsFee(LogisticsCostVo logisticsCostVo,
                                                     CalculateLogisticsResultVO logisticsResultVO,
                                                     LogisticsCostEnum calculateType);
}
