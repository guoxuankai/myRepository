package com.rondaful.cloud.order.service;

import com.rondaful.cloud.common.model.vo.freight.LogisticsCostVo;
import com.rondaful.cloud.common.model.vo.freight.LogisticsDetailVo;
import com.rondaful.cloud.common.model.vo.freight.SearchLogisticsListDTO;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.entity.SupplyChainCompany;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.commodity.CommodityBase;
import com.rondaful.cloud.order.entity.finance.OrderRequestVo;
import com.rondaful.cloud.order.entity.supplier.LogisticsDTO;
import com.rondaful.cloud.order.entity.supplier.OrderInvDTO;
import com.rondaful.cloud.order.entity.supplier.WarehouseDTO;
import com.rondaful.cloud.order.entity.supplier.WarehouseInventory;
import com.rondaful.cloud.order.entity.supplier.WarehouseSync;
import com.rondaful.cloud.order.entity.system.OrderProfitCalculation;
import com.rondaful.cloud.order.entity.system.SysOrderPackage;
import com.rondaful.cloud.order.entity.user.ThirdAppDTO;
import com.rondaful.cloud.order.model.dto.remoteUser.GetSupplyChainByUserIdDTO;
import com.rondaful.cloud.order.model.dto.remoteUser.UserXieRequest;
import com.rondaful.cloud.order.model.dto.remoteseller.GetByplatformSkuAndSiteVO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderReceiveAddressDTO;
import com.rondaful.cloud.order.seller.Empower;
import org.json.JSONException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ISystemOrderCommonService {

//    /**
//     * 查询可用库存接口
//     * @param list
//     * @return
//     * @throws Exception
//     */
//    Map<String, Object> getWareHousetInventory(List<WarehouseInventory> list) throws Exception;

//    /**
//     * 根据系统订单SKU计算改订单的预估物流费
//     * @param sysOrder
//     * @return
//     */
//    String calculateErpTrialBySKUS(SysOrder sysOrder) throws Exception;

    /**
     * 计算预估物流费
     *
     * @param sysOrder
     * @return
     * @throws Exception
     */
    String calculateLogisticFeeBySKUS(SysOrder sysOrder) throws Exception;

    /**
     * 计算包裹预估物流费
     *
     * @param sysOrderPackageDTO        {@link SysOrderPackageDTO}
     * @param sysOrderReceiveAddressDTO {@link SysOrderReceiveAddressDTO}
     * @return BigDecimal
     * @throws Exception
     */
    BigDecimal
    calculatePackageLogisticFeeBySKUS(SysOrderPackageDTO sysOrderPackageDTO,
                                                 SysOrderReceiveAddressDTO sysOrderReceiveAddressDTO,
                                                 Integer orderSource, Integer storeId, Integer handOrder) throws Exception;

    /**
     * 判断库存是否足够
     *
     * @param sysOrder
     */
    void judgeWareHouseIsEnough(SysOrder sysOrder, String username, boolean isFromDeliver) throws JSONException, IOException;

    /**
     * 判断库存是否足够
     *
     * @param sysOrderPackage {@link SysOrderPackage}
     * @param username        用户名， 操作日志用
     * @param isFromDeliver   是否来自发货 true，则需要设置
     * @throws JSONException
     * @throws IOException
     */
    void judgeWareHouseIsEnough(SysOrderPackage sysOrderPackage, String username, String sellerPlAccount,
                                boolean isFromDeliver) throws JSONException, IOException;

    /**
     * 判断邮寄方式是否支持
     *
     * @param sysOrder
     */
    Boolean judgeSupportDeliverMethod(SysOrder sysOrder) throws Exception;

    /**
     * 发货异常更新订单数据
     *
     * @param sysOrder
     * @param msg
     * @param orderDeliveryStatus
     * @param payStatus
     */
    void updateDeliverExceptionInfo(SysOrder sysOrder, String msg, Byte orderDeliveryStatus, Byte payStatus, String operator);

    /**
     * 根据店铺账号和平台，获取信息
     *
     * @param platformSellerAccount 平台卖家账号
     * @param platform              平台
     * @return {@link Empower}
     */
    Empower queryAuthorizationFromSellerBySellerAccount(String platformSellerAccount, Integer platform);

    /**
     * 查询店铺类型
     *
     * @param sysOrder
     * @return
     */
    Empower queryAuthorizationFromSeller(SysOrder sysOrder);

    /**
     * 根据店铺ID查询授权
     *
     * @param empowerID
     * @return
     */
    Empower queryAuthorizationByShopID(Integer empowerID);

    /*    *//**
     * 取消仓库订单
     *
     * @param sysOrder
     *//*
    void cancelWareHouseOrder(SysOrder sysOrder, String cancelReason) throws Exception;*/

    /* *//**
     * 根据仓库code查询仓库服务商数据
     *
     * @param set
     * @return
     *//*
    Map<String, AuthorizeDTO> getGCAuthorizeByCompanyCode(Set<String> set);
*/

    /**
     * 根据仓库ID查询仓库服务商数据
     *
     * @param warehouseIds
     * @return
     */
    Map<String, WarehouseDTO> getGCAuthorizeByWarehouseId(List<String> warehouseIds);


    /**
     * 预估物流费存在时设置毛利和利润率
     *
     * @param sysOrder
     */
    void setGrossMarginAndProfitMargin(SysOrder sysOrder);

    /**
     * 设置毛利和利润率和订单总金额
     *
     * @param orderProfitCalculation
     */
    void setGrossMarginAndProfitMarginAndTotal(OrderProfitCalculation orderProfitCalculation);


    String test() throws Exception;

    void deliverAmazonSYSOrder(String sysOrderId);

    /**
     * 根据仓库编码获取仓库信息
     *
     * @param warehouseCode 仓库编码
     * @return {@link WarehouseSync}
     */
    WarehouseDTO getWarehouseInfoByCode(String warehouseCode);

    /**
     * 根据仓库ID获取仓库信息
     *
     * @param warehouseId 仓库ID
     * @return {@link WarehouseDTO}
     */
    WarehouseDTO getWarehouseInfo(String warehouseId);

    /**
     * 根据物流方式code和仓库code获取物流方式信息
     *
     * @param logisticsCode 物流方式code
     * @param warehouseId   仓库Id
     * @return {@link LogisticsDTO}
     */
    LogisticsDTO queryLogisticsByCode(String logisticsCode, Integer warehouseId);

    /**
     * 根据sku列表获取匹配的仓库
     *
     * @param skuList sku列表
     * @return {@link List<WarehouseInventory>}
     * @throws Exception 异常
     */
    List<OrderInvDTO> getMappingWarehouseBySkuList(List<String> skuList) ;

    /**
     * 根据用户ID获取对应的供应链信息
     *
     * @param userId       用户ID
     * @param platformType 平台类型
     * @return {@link GetSupplyChainByUserIdDTO}
     */
    GetSupplyChainByUserIdDTO getSupplyChinByUserId(Integer userId, Integer platformType);

    public GetSupplyChainByUserIdDTO getSupplyChinByUserIdOrUsername(Integer userId, String userName, Integer platformType);

    /**
     * 根据appKey 获取第三方应用验证信息
     *
     * @param appKey appkey
     * @return {@link ThirdAppDTO}
     */
    ThirdAppDTO getByAppKey(String appKey);

    /**
     * 包裹匹配订单物流方式
     *
     * @param warehouseSourceType {@link Constants.Warehouse}
     * @param sysOrderPackageDTO  {@link SysOrderPackageDTO}
     * @param platform            平台
     * @param deliveryWarehouseId 仓库编码
     * @return {@link List<LogisticsDTO>}
     */
    List<LogisticsDTO> matchOrderLogistics(String warehouseSourceType, SysOrderPackageDTO sysOrderPackageDTO,
                                           String platform, Integer deliveryWarehouseId);

    /**
     * 判断是否谷仓仓库
     *
     * @param warehouseId 仓库ID
     * @return boolean
     */
    boolean isGoodCangWarehouse(String warehouseId);

    /**
     * 获取ERP仓库ID列表
     *
     * @return {@link List<Integer>}
     */
    List<Integer> getErpWarehouseIdList();

    /**
     * 获取WMS仓库ID列表
     *
     * @return {@link List<Integer>}
     */
    List<Integer> getWmsWarehouseIdList();

    /**
     * 获取谷仓仓库ID列表
     *
     * @return {@link List<Integer>}
     */
    List<Integer> getGoodCangWarehouseIdList();

    /**
     * 根据仓库服务CODE获取仓库ID列表
     *
     * @return {@link List<Integer>}
     */
    public List<Integer> getsWarehouseIdList(String firmCode);

    /**
     * 根据卖家ID获取子账号列表
     *
     * @param sellerId 卖家ID
     * @return {@link List<UserXieRequest>}
     */
    List<UserXieRequest> getChildAccountFromSeller(Integer sellerId);

    /**
     * 获取一个授权账号
     *
     * @param platform  平台
     * @param account   账号
     * @param webName   站点名称
     * @param empowerId empowerId
     * @return {@link Empower}
     */
    Empower findOneEmpowerByAccount(Integer platform, String account, String webName, String empowerId);

    /**
     * 根据sellerPlId 和 品连sku列表查询品连sku信息
     *
     * @param sellerPlId sellerPlId
     * @param plSkuList  品连sku列表
     * @return {@link List<CommodityBase>}
     */
    List<CommodityBase> getCommodityBySkuList(Integer sellerPlId, List<String> plSkuList);

    /**
     * 获取预估物流费
     *
     * @param logisticsCostVo {@link LogisticsCostVo}
     * @return {@link LogisticsCostVo}
     */
    LogisticsCostVo getEstimateFreight(LogisticsCostVo logisticsCostVo);

    /**
     * 财务冻结
     *
     * @param orderRequestVo {@link OrderRequestVo}
     * @return {@link Map<String, String>}
     */
    Map<String, String> financeGenerate(OrderRequestVo orderRequestVo);

    /**
     * 获取合适的物流方式
     *
     * @param searchLogisticsListDTO {@link SearchLogisticsListDTO}
     * @return {@link List<LogisticsDetailVo>}
     */
    List<LogisticsDetailVo> getSuitLogisticsByType(SearchLogisticsListDTO searchLogisticsListDTO);

    /**
     * 更新包裹发货
     *
     * @param orderTrackId              包裹号
     * @param warehouseShipExceptionMsg
     * @param packageStatus
     * @param modifier
     */
    void updatePackageDeliverException(String orderTrackId, String warehouseShipExceptionMsg, String packageStatus,
                                       String modifier);

    /**
     * 获取供应链公司信息
     *
     * @param platformType {@link Constants.System}
     * @param userIds      卖家用户ID
     * @return {@link List<SupplyChainCompany>}
     */
    List<SupplyChainCompany> getSupplyChainByUserId(String platformType, List<Integer> userIds);


    /**
     * @param platformSkuList 平台sku list
     * @param site            目前只有亚马逊需要站点信息
     * @param type            亚马逊:AMAZON,速卖通:ALIEXPRESS,EBAY
     * @param empowerId       授权店铺ID
     * @return {@link List<GetByplatformSkuAndSiteVO>}
     */
    List<GetByplatformSkuAndSiteVO> getByplatformSkuAndSite(List<String> platformSkuList, String site,
                                                            String type, Integer empowerId);


    /**
     * 判断订单利润阈值
     * @param sellerId
     * @param storeId
     * @param grossMargin
     * @return
     */
    Boolean getThreshold(Integer sellerId,Integer storeId, BigDecimal grossMargin);
}
