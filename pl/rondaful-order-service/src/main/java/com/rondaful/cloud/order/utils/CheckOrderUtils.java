package com.rondaful.cloud.order.utils;

import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.SysOrderDetail;
import com.rondaful.cloud.order.entity.commodity.CommodityBase;
import com.rondaful.cloud.order.entity.system.SysOrderNew;
import com.rondaful.cloud.order.entity.system.SysOrderPackage;
import com.rondaful.cloud.order.entity.system.SysOrderPackageDetail;
import com.rondaful.cloud.order.entity.system.SysOrderReceiveAddress;
import com.rondaful.cloud.order.enums.OrderCodeEnum;
import com.rondaful.cloud.order.enums.OrderSourceEnum;
import com.rondaful.cloud.order.enums.SkuBindEnum;
import com.rondaful.cloud.order.service.ISysOrderService;
import com.rondaful.cloud.order.service.ISystemOrderCommonService;
import com.rondaful.cloud.order.service.impl.SysOrderServiceImpl;
import com.rondaful.cloud.order.service.impl.SystemOrderCommonServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * 作者: wujiachuang
 * 时间: 2018-12-29 8:58
 * 包名: com.rondaful.cloud.order.utils
 * 描述:
 */
public class CheckOrderUtils {
    private final static Logger logger = LoggerFactory.getLogger(CheckOrderUtils.class);

    private static ISysOrderService sysOrderService = (SysOrderServiceImpl) ApplicationContextProvider.getBean("sysOrderServiceImpl");
    private static ISystemOrderCommonService systemOrderCommonService = (SystemOrderCommonServiceImpl) ApplicationContextProvider
            .getBean("systemOrderCommonServiceImpl");

    /**
     * 判断商品对该卖家是否可售并且是否含有已下架商品
     *
     * @param sellerPlId    卖家品连ID
     * @param systemSkuList 系统SKU集合
     * @return
     */
    public static boolean judgeSkusAbleSaleAndIsPutAway(Integer sellerPlId, List<String> systemSkuList) {
        if (sellerPlId == null || CollectionUtils.isEmpty(systemSkuList)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家品连ID、系统SKU集合不允许为空！");
        }
        int size=0;
        if (CollectionUtils.isNotEmpty(systemSkuList)) {
             size = systemSkuList.stream().collect(Collectors.groupingBy(String::toString)).keySet().size();
        }
        logger.info("调用商品服务查询是否可售、是否下架参数sellerId:{},skulist:{}", sellerPlId,FastJsonUtils.toJsonString(systemSkuList));
        List<CommodityBase> commodityBaseList = systemOrderCommonService.getCommodityBySkuList(sellerPlId, systemSkuList);
        logger.info("调用商品服务查询是否可售、是否下架返回的结果：{}", FastJsonUtils.toJsonString(commodityBaseList));
        int count = 0;
        for (CommodityBase commodityBase : commodityBaseList) {
            count += commodityBase.getCommoditySpecList().size();
        }
        if (size != count) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "含有非品连商品，查询不到数据");
        }
        return judgeSkuState(commodityBaseList);
    }

    /**
     * 判断商品是否可售或者是否已下架
     *
     * @param commodityBaseList {@link List<CommodityBase>}
     * @return boolean
     */
    public static boolean judgeSkuState(List<CommodityBase> commodityBaseList) {
        try {
            commodityBaseList.forEach(commodityBase -> {
                if (commodityBase.getCanSale().equals(Constants.CommodityBase.CANT_SALE)) {
                    throw new GlobalException(OrderCodeEnum.RETURN_CODE_300120);
                }
                commodityBase.getCommoditySpecList().forEach(commoditySpec -> {
                    if (commoditySpec.getState().equals(Constants.CommodityStatus.WAIT_PUTAWAY)) {
                        throw new GlobalException(OrderCodeEnum.RETURN_CODE_300121);
                    }
                });
            });
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /*手工新建订单校验*/
    public static String checkSysOrder(SysOrder sysOrder) {
        //订单项校验
        for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
            if (sysOrderDetail.getSku() == null || sysOrderDetail.getSku() == "") {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "产品SKU不能为空");
            }
            if (sysOrderDetail.getSkuQuantity() == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "产品数量不能为空");
            }
        }
//        //订单校验
        if (StringUtils.isBlank(sysOrder.getLogisticsStrategy())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "物流类型不能为空");
        }
        if (sysOrder.getPlatformShopId() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台店铺ID不能为空");
        }
        if (sysOrder.getOrderSource() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台不能为空");
        }
        if (sysOrder.getDeliveryMethodCode() == null || sysOrder.getDeliveryMethodCode() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "邮寄方式code不能为空");
        }
        if (sysOrder.getShipToName() == null || sysOrder.getShipToName() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "收货人姓名不能为空");
        }
        if (sysOrder.getShipToCountryName() == null || sysOrder.getShipToCountryName() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "国家不能为空");
        }
        if (sysOrder.getShipToCountry() == null || sysOrder.getShipToCountry() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "国家代码不能为空");
        }
        if (StringUtils.isBlank(sysOrder.getShipToState())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "省州不能为空");
        }
        if (sysOrder.getShipToCity() == null || sysOrder.getShipToCity() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "城市不能为空");
        }
        if (sysOrder.getShipToAddrStreet1() == null || sysOrder.getShipToAddrStreet1() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "地址1不能为空");
        }
        if (sysOrder.getShipToPhone() == null || sysOrder.getShipToPhone() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "联系电话不能为空");
        }
        if (sysOrder.getSellerPlId() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家品连ID不能为空");
        }
        if (sysOrder.getDeliverDeadline() == null || sysOrder.getDeliverDeadline() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "最迟发货时间不能为空");//当前时间+两天
        }
        if (sysOrder.getDeliveryWarehouseId() == null || sysOrder.getDeliveryWarehouseId() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "发货仓库ID不能为空");
        }
        else {
            return "success";
        }
    }

    /**
     * 订单包裹物流信息校验
     *
     * @param orderNew
     * @return
     */
    public static void checkPackageInfo(SysOrderNew orderNew) {
        List<SysOrderPackage> sysOrderPackageList = orderNew.getSysOrderPackageList();
        if (CollectionUtils.isEmpty(sysOrderPackageList)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "物流信息不能为空");
        } else {
            sysOrderPackageList.forEach(orderPackage -> {
                if (StringUtils.isBlank(orderPackage.getLogisticsStrategy())) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "物流类型不能为空");
                }
                if (orderPackage.getDeliveryWarehouseId() == null) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "仓库ID不能为空");
                }
                if (StringUtils.isBlank(orderPackage.getDeliveryMethodCode())) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "邮寄方式code不能为空");
                }
                if (orderNew.getOrderSource().equals(OrderSourceEnum.CONVER_FROM_AMAZON.getValue())) {
                    if (StringUtils.isBlank(orderPackage.getAmazonCarrierName())) {
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "映射后的Amazon物流商名称不能为空");
                    }
                    if (StringUtils.isBlank(orderPackage.getAmazonShippingMethod())) {
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "映射后的Amazon配送方式不能为空");
                    }
                }
                if (orderNew.getOrderSource().equals(OrderSourceEnum.CONVER_FROM_EBAY.getValue())) {
                    if (StringUtils.isBlank(orderPackage.getEbayCarrierName())) {
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "映射后的Ebay物流商名称不能为空");
                    }
                }
            });
        }
    }

    /**
     * 订单地址校验
     *
     * @param address
     * @return
     */
    public static void checkAddress(SysOrderReceiveAddress address) {
        if (address == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "收货地址不能为空");
        }
        if (address.getShipToName() == null || address.getShipToName() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "收货人姓名不能为空");
        }
        if (address.getShipToCountryName() == null || address.getShipToCountryName() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "国家不能为空");
        }
        if (address.getShipToCountry() == null || address.getShipToCountry() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "国家代码不能为空");
        }
        if (StringUtils.isBlank(address.getShipToState())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "省州不能为空");
        }
        if (StringUtils.isBlank(address.getShipToPhone())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "联系电话不能为空");
        }
        if (address.getShipToCity() == null || address.getShipToCity() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "城市不能为空");
        }
        if (address.getShipToAddrStreet1() == null || address.getShipToAddrStreet1() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "地址1不能为空");
        }
    }

//    /*手工新建订单校验*/
//    public static String checkSysOrder(SysOrderNew sysOrderNew) {
//        //订单项校验
//        for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
//            if (sysOrderDetail.getItemId() == null || sysOrderDetail.getItemId() == 0) {
//                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "产品ID不能为空");
//            }
//            if (sysOrderDetail.getItemUrl() == null || sysOrderDetail.getItemUrl() == "") {
//                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "产品图片不能为空");
//            }
//            if (sysOrderDetail.getItemName() == null || sysOrderDetail.getItemName() == "") {
//                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "产品标题不能为空");
//            }
//            if (sysOrderDetail.getSku() == null || sysOrderDetail.getSku() == "") {
//                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品连SKU不能为空");
//            }
//            if (sysOrderDetail.getItemAttr() == null || sysOrderDetail.getItemAttr() == "") {
//                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "产品属性不能为空");
//            }
//            if (sysOrderDetail.getItemPrice() == null) {
//                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "产品价格不能为空");
//            }
//            if (sysOrderDetail.getSkuQuantity() == null) {
//                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "产品数量不能为空");
//            }
//            if (sysOrderDetail.getSupplierSku() == null || sysOrderDetail.getSupplierSku() == "") {
//                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商SKU不能为空");
//            }
//            if (sysOrderDetail.getSupplierId() == null) {
//                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商ID不能为空");
//            }
//            if (sysOrderDetail.getSupplierName() == null || sysOrderDetail.getSupplierName() == "") {
//                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商名字不能为空");
//            }
//            if (sysOrderDetail.getFareTypeAmount().contains("null") || sysOrderDetail.getFareTypeAmount() == null || sysOrderDetail.getFareTypeAmount() == "") {
//                logger.error("订单校验异常：订单项ID：" + sysOrderDetail.getOrderLineItemId() + "商品ID：" + sysOrderDetail.getItemId() + "商品名：" + sysOrderDetail
//                        .getItemName() + "服务费类型为空");
//                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "数据异常");
//            }
//
//        }
//        //TODO 订单校验
//        if (StringUtils.isBlank(sysOrderNew.getPlatformSellerAccount())) {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台店铺名不能为空");
//        }
//        if (StringUtils.isBlank(sysOrderNew.getPlatformSellerId())) {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台店铺ID不能为空");
//        }
//        if (sysOrderNew.getOrderSource() == null) {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台不能为空");
//        }
//        if (sysOrderNew.getSellerPlId() == null) {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家品连ID不能为空");
//        }
//        if (StringUtils.isBlank(sysOrderNew.getSellerPlAccount())) {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家品连账号不能为空");
//        }
//        if (StringUtils.isBlank(sysOrderNew.getDeliverDeadline())) {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "最迟发货时间不能为空");//当前时间+两天
//        }
//
//
//        //TODO 订单收货地址校验
//        SysOrderReceiveAddress address = sysOrderNew.getSysOrderReceiveAddress();
//        if (address == null) {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "收货地址不能为空");
//        }
//        if (StringUtils.isBlank(address.getShipToName())) {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "收货人姓名不能为空");
//        }
//        if (StringUtils.isBlank(address.getShipToCountryName())) {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "国家不能为空");
//        }
//        if (StringUtils.isBlank(address.getShipToCountry())) {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "国家代码不能为空");
//        }
//        if (StringUtils.isBlank(address.getShipToCity())) {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "城市不能为空");
//        }
//        if (StringUtils.isBlank(address.getShipToAddrStreet1())) {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "地址1不能为空");
//        }
//
//        //TODO 订单包裹校验
//        sysOrderNew.getSysOrderPackageList().forEach(sysOrderPackage -> {  sysOrderPackage.gett
//            if (StringUtils.isBlank(sysOrderPackage.getDeliveryMethod())) {
//                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "邮寄方式不能为空");
//            }
//            if (StringUtils.isBlank(sysOrderPackage.getDeliveryMethodCode())) {
//                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "邮寄方式code不能为空");
//            }
//        });
//
//
////        if (sysOrder.getTotalBulk() == null) {
////            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "订单产品的总体积不能为空");
////        }
////        if (sysOrder.getTotalWeight() == null) {
////            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "订单产品的总重量不能为空");
////        }
//
//        if (sysOrder.getDeliveryWarehouseId() == null || sysOrder.getDeliveryWarehouseId() == "") {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "发货仓库ID不能为空");
//        }
//        if (sysOrder.getDeliveryWarehouse() == null || sysOrder.getDeliveryWarehouse() == "") {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "发货仓库不能为空");
//        }
//        if (sysOrder.getShippingCarrierUsedCode() == null || sysOrder.getShippingCarrierUsedCode() == "") {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "物流商CODE不能为空");
//        }
//        if (sysOrder.getShippingCarrierUsed() == null || sysOrder.getShippingCarrierUsed() == "") {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "物流商名称不能为空");
//        } else {
//            return "success";
//        }
//    }

    /*编辑订单校验*/
    public static String checkEditSysOrder(SysOrderNew orderNew, String area) {
        switch (area) {
         /*   case "1"://编辑收货地址
                checkAddress(sysOrder);
                return "success";
            case "2"://编辑物流信息
                checkLogistics(sysOrder);
                return "success";*/
            case "3"://编辑全部订单信息
                checkAddress(orderNew.getSysOrderReceiveAddress());
                checkPackageInfo(orderNew);
//                checkAllOrderInfo(sysOrder);
                return "success";
//            case "4"://编辑商品信息  TODO 后期需求有需求再添加
//                checkCommodityInfo(sysOrder);
            default:
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        }
    }

    public static void checkAllOrderInfo(SysOrder sysOrder) {
        //订单项校验
        checkCommodityInfo(sysOrder);
        //订单校验
        if (sysOrder.getOrderAmount() == null)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "系统商品总价(商品单价X数量)不能为空");
        if (StringUtils.isBlank(sysOrder.getPlatformSellerAccount())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台店铺名不能为空");
        }
        if (sysOrder.getPlatformShopId() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台店铺ID不能为空");
        }
        if (sysOrder.getOrderSource() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台不能为空");
        }
        if (sysOrder.getOrderSource() == 4) {   //EBAY来源的订单
            if (sysOrder.getEbayCarrierName() == null || sysOrder.getEbayCarrierName() == "") {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "映射后的ebay物流商名称不能为空");
            }
        }
        if (sysOrder.getOrderSource() == 5) {   //亚马逊来源的订单
            if (sysOrder.getAmazonCarrierName() == null || sysOrder.getAmazonCarrierName() == "") {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "映射后的亚马逊物流商名称不能为空");
            }
            if (sysOrder.getAmazonShippingMethod() == null || sysOrder.getAmazonShippingMethod() == "") {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "映射后的亚马逊配送方式不能为空");
            }
        }
        if (sysOrder.getDeliveryMethod() == null || sysOrder.getDeliveryMethod() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "邮寄方式不能为空");
        }
        if (sysOrder.getDeliveryMethodCode() == null || sysOrder.getDeliveryMethodCode() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "邮寄方式code不能为空");
        }
        if (sysOrder.getShipToName() == null || sysOrder.getShipToName() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "收货人姓名不能为空");
        }
        if (sysOrder.getShipToCountryName() == null || sysOrder.getShipToCountryName() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "国家不能为空");
        }
        if (sysOrder.getShipToCountry() == null || sysOrder.getShipToCountry() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "国家代码不能为空");
        }
        if (sysOrder.getShipToCity() == null || sysOrder.getShipToCity() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "城市不能为空");
        }
        if (sysOrder.getShipToAddrStreet1() == null || sysOrder.getShipToAddrStreet1() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "地址1不能为空");
        }
        if (sysOrder.getShipToPostalCode() == null || sysOrder.getShipToPostalCode() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "邮编不能为空");
        }
//        if (sysOrder.getShipToPhone() == null || sysOrder.getShipToPhone() == "") {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "联系电话不能为空");
//        }
        if (sysOrder.getTotalBulk() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "订单产品的总体积不能为空");
        }
        if (sysOrder.getTotalWeight() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "订单产品的总重量不能为空");
        }
        if (sysOrder.getSellerPlId() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家品连ID不能为空");
        }
        if (sysOrder.getSellerPlAccount() == null || sysOrder.getSellerPlAccount() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家品连账号不能为空");
        }
        if (sysOrder.getSupplyChainCompanyId() == null || StringUtils.isEmpty(sysOrder.getSupplyChainCompanyName())) {
            logger.error("异常：卖家品连ID:" + sysOrder.getSellerPlId() + ",卖家名字:" + sysOrder.getSellerPlAccount() + "供应链公司ID或名称为空");
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "数据异常");
        }
//        if (sysOrder.getDeliverDeadline() == null || sysOrder.getDeliverDeadline() == "") {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "最迟发货时间不能为空");//当前时间+两天
//        }

/*        if (sysOrder.getShippingServiceCost() == null ) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "订单运费不能为空"); //默认为0
        }*/
//        if (sysOrder.getDeliveryWarehouseCode() == null || sysOrder.getDeliveryWarehouseCode() == "") {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "发货仓库编码不能为空");
//        }
        if (sysOrder.getDeliveryWarehouseId() == null || sysOrder.getDeliveryWarehouseId() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "发货仓库ID不能为空");
        }
        if (sysOrder.getDeliveryWarehouse() == null || sysOrder.getDeliveryWarehouse() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "发货仓库不能为空");
        }
        if (sysOrder.getShippingCarrierUsedCode() == null || sysOrder.getShippingCarrierUsedCode() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "物流商CODE不能为空");
        }
//        if (sysOrder.getDeliveryWarehouseCode().startsWith("GC")) {
//            return "success";    //选择谷仓的不校验下面的物流商名称，直接返回校验成功success
//        }
        if (sysOrder.getShippingCarrierUsed() == null || sysOrder.getShippingCarrierUsed() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "物流商名称不能为空");
        }
    }

    public static void checkLogistics(SysOrder sysOrder) {
        checkCommodityInfo(sysOrder);
        if (sysOrder.getOrderSource() == 4) {   //EBAY来源的订单
            if (sysOrder.getEbayCarrierName() == null || sysOrder.getEbayCarrierName() == "") {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "映射后的ebay物流商名称不能为空");
            }
        }
        if (sysOrder.getOrderSource() == 5) {   //亚马逊来源的订单
            if (sysOrder.getAmazonCarrierName() == null || sysOrder.getAmazonCarrierName() == "") {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "映射后的亚马逊物流商名称不能为空");
            }
            if (sysOrder.getAmazonShippingMethod() == null || sysOrder.getAmazonShippingMethod() == "") {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "映射后的亚马逊配送方式不能为空");
            }
        }
        if (sysOrder.getDeliveryMethod() == null || sysOrder.getDeliveryMethod() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "邮寄方式不能为空");
        }
        if (sysOrder.getDeliveryMethodCode() == null || sysOrder.getDeliveryMethodCode() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "邮寄方式code不能为空");
        }
        if (sysOrder.getDeliveryWarehouseCode() == null || sysOrder.getDeliveryWarehouseCode() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "发货仓库编码不能为空");
        }
        if (sysOrder.getDeliveryWarehouse() == null || sysOrder.getDeliveryWarehouse() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "发货仓库不能为空");
        }
        if (sysOrder.getShippingCarrierUsedCode() == null || sysOrder.getShippingCarrierUsedCode() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "物流商CODE不能为空");
        }
        if (sysOrder.getShippingCarrierUsed() == null || sysOrder.getShippingCarrierUsed() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "物流商名称不能为空");
        }
    }

    public static void checkCommodityInfo(SysOrder sysOrder) {
        for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
            if (sysOrderDetail.getItemId() == null || sysOrderDetail.getItemId() == 0) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "产品ID不能为空");
            }
            if (sysOrderDetail.getItemUrl() == null || sysOrderDetail.getItemUrl() == "") {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "产品图片不能为空");
            }
            if (sysOrderDetail.getItemName() == null || sysOrderDetail.getItemName() == "") {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "产品标题不能为空");
            }
            if (sysOrderDetail.getSku() == null || sysOrderDetail.getSku() == "") {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "品连SKU不能为空");
            }
            if (sysOrderDetail.getItemAttr() == null || sysOrderDetail.getItemAttr() == "") {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "产品属性不能为空");
            }
            if (sysOrderDetail.getItemPrice() == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "产品价格不能为空");
            }
            if (sysOrderDetail.getSkuQuantity() == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "产品数量不能为空");
            }
            if (sysOrderDetail.getSupplierSku() == null || sysOrderDetail.getSupplierSku() == "") {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商SKU不能为空");
            }
            if (sysOrderDetail.getSupplierId() == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商ID不能为空");
            }
            if (sysOrderDetail.getSupplierName() == null || sysOrderDetail.getSupplierName() == "") {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商名字不能为空");
            }
            if (sysOrderDetail.getFareTypeAmount().contains("null") || sysOrderDetail.getFareTypeAmount() == null || sysOrderDetail.getFareTypeAmount() == "") {
                logger.error("异常：订单校验异常：订单项ID：" + sysOrderDetail.getOrderLineItemId() + "商品ID：" + sysOrderDetail.getItemId() + "商品名：" + sysOrderDetail
                        .getItemName() + "服务费类型为空");
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "数据异常");
            }

            if (sysOrderDetail.getSupplyChainCompanyId() == null || StringUtils.isEmpty(sysOrderDetail.getSupplyChainCompanyName())) {
                logger.error("异常：供应商ID:" + sysOrderDetail.getSupplierId() + ",供应商名字:" + sysOrderDetail.getSupplierName() + "供应链公司ID或名称为空");
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "数据异常");
            }

        }
    }

//    public static void checkAddress(SysOrder sysOrder) {
////        checkCommodityInfo(sysOrder);
//        if (sysOrder.getShipToName() == null || sysOrder.getShipToName() == "") {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "收货人姓名不能为空");
//        }
//        if (sysOrder.getShipToCountryName() == null || sysOrder.getShipToCountryName() == "") {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "国家不能为空");
//        }
//        if (sysOrder.getShipToCountry() == null || sysOrder.getShipToCountry() == "") {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "国家代码不能为空");
//        }
//        if (sysOrder.getShipToCity() == null || sysOrder.getShipToCity() == "") {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "城市不能为空");
//        }
//        if (sysOrder.getShipToAddrStreet1() == null || sysOrder.getShipToAddrStreet1() == "") {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "地址1不能为空");
//        }
//        if (sysOrder.getShipToPostalCode() == null || sysOrder.getShipToPostalCode() == "") {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "邮编不能为空");
//        }
//    }


    /*ERP推单至品连系统订单校验*/
    public static String checkErpSysOrder(SysOrder sysOrder) throws Exception {
        //订单项校验
        for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
            if (sysOrderDetail.getSourceOrderId() == null || sysOrderDetail.getSourceOrderId() == "") {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品来源订单ID不能为空");
            }
            if (sysOrderDetail.getSourceOrderLineItemId() == null || sysOrderDetail.getSourceOrderLineItemId() == "") {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品来源订单项ID不能为空");
            }
            if (sysOrderDetail.getSkuQuantity() == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "产品数量不能为空");
            }
            if (sysOrderDetail.getSupplierSku() == null || sysOrderDetail.getSupplierSku() == "") {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商SKU不能为空");
            }
        }
        //订单校验
        if (sysOrder.getSellerPlId() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家品连ID不能为空");
        }
        if (sysOrder.getSourceOrderId() == null || sysOrder.getSourceOrderId() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "来源平台订单号不能为空");
        }
        if (sysOrder.getOrderTime() == null || sysOrder.getOrderTime() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "平台下单时间不能为空");   //平台下单时间
        }
        if (sysOrder.getDeliverDeadline() == null || sysOrder.getDeliverDeadline() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "最迟发货时间不能为空");//
        }
        if (sysOrder.getShipToName() == null || sysOrder.getShipToName() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "收货人姓名不能为空");
        }
        if (sysOrder.getShipToPhone() == null || sysOrder.getShipToPhone() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "联系电话");
        }
        if (sysOrder.getShipToCountry() == null || sysOrder.getShipToCountry() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "国家代码不能为空");
        }
        if (sysOrder.getShipToCountryName() == null || sysOrder.getShipToCountryName() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "国家名称不能为空");
        }
        if (sysOrder.getShipToCity() == null || sysOrder.getShipToCity() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "城市不能为空");
        }
        if (sysOrder.getShipToAddrStreet1() == null || sysOrder.getShipToAddrStreet1() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "地址1不能为空");
        }
        if (sysOrder.getShipToEmail() == null || sysOrder.getShipToEmail() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "买家Email不能为空");
        }

        if (sysOrder.getShipToPostalCode() == null || sysOrder.getShipToPostalCode() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "邮编不能为空");
        }
        if (sysOrder.getDeliveryWarehouseCode() == null || sysOrder.getDeliveryWarehouseCode() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "仓库code不能为空");
        }
        if (sysOrder.getDeliveryWarehouse() == null || sysOrder.getDeliveryWarehouse() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "仓库名称不能为空");
        }
        if (sysOrder.getShippingCarrierUsedCode() == null || sysOrder.getShippingCarrierUsedCode() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "物流方式code不能为空");
        }
        if (sysOrder.getShippingCarrierUsed() == null || sysOrder.getShippingCarrierUsed() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "物流方式名称不能为空");
        }
        if (sysOrder.getShippingServiceCost() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "订单运费不能为空"); //平台抓取下来的运费
        }
        if (sysOrder.getCommoditiesAmount() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "订单货款不能为空");//平台抓下来的金额
        }
        if (sysOrder.getPlatformSellerAccount() == null || sysOrder.getPlatformSellerAccount() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家平台账号不能为空");
        }
        if (sysOrder.getBuyerCheckoutMessage() == null || sysOrder.getBuyerCheckoutMessage() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "买家留言不能为空");
        }
        if (sysOrder.getRemark() == null || sysOrder.getShipToCountryName() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "订单备注不能为空");
        } else {
            return "success";
        }
    }

    /**
     * 渠道创建系统订单必填项校验
     *
     * @param sysOrder
     * @return
     */
    public static void distributionCreateSysOrder(SysOrder sysOrder) {
        String sourceOrderId = sysOrder.getSourceOrderId();
        if (StringUtils.isBlank(sourceOrderId)) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300100);
        }

        if (!sourceOrderId.startsWith(Constants.DistributionType.DistributionType_QT)) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300101);
        }

        List<SysOrder> sysOrderList = sysOrderService.findSysOrderBySourceOrderId(sourceOrderId);
        if (!CollectionUtils.isEmpty(sysOrderList)) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300102);
        }

        if (null == sysOrder.getPlatformShopId() || sysOrder.getPlatformShopId()<=0) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300103);
        }
        if (StringUtils.isBlank(sysOrder.getBuyerName())) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300104);
        }
        if (StringUtils.isBlank(sysOrder.getShipToName())) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300105);
        }
        if (StringUtils.isBlank(sysOrder.getShipToCountry())) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300106);
        }
        if (StringUtils.isBlank(sysOrder.getShipToState())) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300107);
        }
        if (StringUtils.isBlank(sysOrder.getShipToCity())) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300108);
        }
        if (StringUtils.isBlank(sysOrder.getShipToAddrStreet1())) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300109);
        }
        if (StringUtils.isBlank(sysOrder.getShipToPostalCode())) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300110);
        }
        if (StringUtils.isBlank(sysOrder.getShipToPhone())) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300111);
        }

        if (sysOrder.getAppointDeliveryWay() == null) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300112);
        }

        if (sysOrder.getAppointDeliveryWay() == 1) {
            if (StringUtils.isBlank(sysOrder.getDeliveryWarehouseCode()) || StringUtils.isBlank(sysOrder.getDeliveryMethodCode())) {
                throw new GlobalException(OrderCodeEnum.RETURN_CODE_300113);
            }
        }

        List<SysOrderDetail> detailList = sysOrder.getSysOrderDetails();
        if (CollectionUtils.isEmpty(sysOrder.getSysOrderDetails())) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300114);
        }
        for (SysOrderDetail detail : detailList) {
            if (StringUtils.isBlank(detail.getSku())) {
                throw new GlobalException(OrderCodeEnum.RETURN_CODE_300115);
            }
            if (detail.getSkuQuantity() == null || detail.getSkuQuantity() == 0) {
                throw new GlobalException(OrderCodeEnum.RETURN_CODE_300116);
            }
        }
    }

    public static void validateSysOrderDataForDeliverGood(SysOrderNew sysOrderNew, SysOrderPackage sysOrderPackage,
                                                          SysOrderReceiveAddress sysOrderReceiveAddress) {
        byte orderSource = sysOrderNew.getOrderSource();
        if (orderSource == OrderSourceEnum.CONVER_FROM_EBAY.getValue()) {
            if (StringUtils.isBlank(sysOrderPackage.getEbayCarrierName())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "物流商名称不能为空");
            }
        }

        if (orderSource == OrderSourceEnum.CONVER_FROM_AMAZON.getValue()) {
            if (StringUtils.isBlank(sysOrderPackage.getAmazonCarrierName()) || StringUtils.isBlank(sysOrderPackage.getAmazonShippingMethod())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "物流商名称和配送方式不能为空");
            }
        }

        if (StringUtils.isEmpty(sysOrderNew.getOrderTime())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "下单时间不能为空");
        }

        if (sysOrderNew.getTotal() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "订单总售价:预估物流费+商品金额不能为空");
        }

        //consignee收货人
        if (StringUtils.isBlank(sysOrderReceiveAddress.getShipToName())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "收货人姓名不能为空");
        }

        //consignee收货人电话
        if (StringUtils.isBlank(sysOrderReceiveAddress.getShipToPhone())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "收货人电话不能为空");
        }

        //country_code国家简称
        if (StringUtils.isBlank(sysOrderReceiveAddress.getShipToCountry())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "国家不能为空");
        }

        //city城市
        if (StringUtils.isBlank(sysOrderReceiveAddress.getShipToCity())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "城市不能为空");
        }

        //province省州
        Integer warehouseId = sysOrderPackage.getDeliveryWarehouseId();
        if (null == warehouseId || -1 == warehouseId) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "仓库不能为空");
        }

        boolean isGoodCangOrder = systemOrderCommonService.isGoodCangWarehouse(String.valueOf(warehouseId));
        if (isGoodCangOrder && sysOrderReceiveAddress.getShipToCountry().equalsIgnoreCase("US")) {
            if (StringUtils.isBlank(sysOrderReceiveAddress.getShipToState())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "省州不能为空");
            }
        }
        //address地址
        if (StringUtils.isBlank(sysOrderReceiveAddress.getShipToAddrStreet1())
                && StringUtils.isBlank(sysOrderReceiveAddress.getShipToAddrStreet2())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "地址1和地址2不能同时为空");
        }
        //zipcode邮编
        if (StringUtils.isBlank(sysOrderReceiveAddress.getShipToPostalCode())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "邮编不能为空");
        }

        if (sysOrderNew.getPlatformShopId() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家平台店铺ID不能为空");
        }
        if (sysOrderNew.getSellerPlId() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家品连ID不能为空");
        }
        //supply_chain_company_id
        //supply_chain_company_name
        if (sysOrderNew.getSupplyChainCompanyId() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家供应链ID不能为空");
        }
        if (StringUtils.isBlank(sysOrderNew.getSupplyChainCompanyName())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家供应链名称不能为空");
        }

        if (StringUtils.isBlank(sysOrderPackage.getOrderTrackId())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "ERP订单跟踪号不能为空");
        }
        //related_order_id  关联订单ID,取来源订单ID,取系统订单ID
        //is_wish_express  Wish订单是否海外仓（0-是 1-否）
        //currency_code   ("CNY");//货币简写
        //channel_shipping_free   渠道运费,取系统预估物流费
        //channel_shipping_discount渠道折扣运费,取系统预估物流费
        //channel_cost  (0.00);//渠道手续费
        //requires_delivery_confirmation Wish订单是否妥投 1-是 0-否
        //site_code站点简称
//        if (orderSource == (byte) 5) {
        if (orderSource == OrderSourceEnum.CONVER_FROM_AMAZON.getValue()) {
            if (StringUtils.isBlank(sysOrderNew.getMarketplaceId())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "站点简称不能为空");
            }
        }
        //goods_amount商品总额:商品单价x数量再取和
        if (sysOrderNew.getOrderAmount() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品总额不能为空");
        }
        //订单发货仓库编码
        if (StringUtils.isBlank(sysOrderPackage.getDeliveryWarehouseCode())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "发货仓库编码不能为空");
        }
        //订单发货仓库ID
        if (null == sysOrderPackage.getDeliveryWarehouseId()) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "发货仓库ID不能为空");
        }
        //邮寄方式编码
        if (StringUtils.isBlank(sysOrderPackage.getDeliveryMethodCode())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "邮寄方式编码不能为空");
        }
        //物流商CODE
        if (StringUtils.isBlank(sysOrderPackage.getShippingCarrierUsedCode())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "物流商CODE不能为空");
        }
        //物流商名称
        if (StringUtils.isBlank(sysOrderNew.getShopType())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "店铺类型不能为空");
        }

        List<SysOrderPackageDetail> sysOrderPackageDetailList = sysOrderPackage.getSysOrderPackageDetailList();

        if (CollectionUtils.isEmpty(sysOrderPackageDetailList)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "包裹" + sysOrderPackage.getOrderTrackId() + "无商品");
        }
        for (SysOrderPackageDetail detail : sysOrderPackageDetailList) {
            if (!detail.getBindStatus().equals(SkuBindEnum.BIND.getValue())){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "sku未绑定");
            }
            //fareTypeAmount服务费不能为null
            if (StringUtils.isBlank(detail.getFareTypeAmount())
                    || detail.getFareTypeAmount().contains("null")
                    || detail.getFareTypeAmount().contains("NULL")) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "服务费不能为空");
            }
            //supplier_id  推送出库记录使用
            if (detail.getSupplierId() == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商ID不能为空");
            }
            //supply_chain_company_id
            //supply_chain_company_name
            if (detail.getSupplyChainCompanyId() == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商所属供应链ID不能为空");
            }
            if (StringUtils.isBlank(detail.getSupplyChainCompanyName())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商所属供应链名称不能为空");
            }
            //sku_quantity
            if (detail.getSkuQuantity() == null || detail.getSkuQuantity() == 0) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "购买此SKU数量不能为空");
            }
            //chanel_currency_code渠道价格货币简写
            //验证邮寄方式是否支持使用
            if (detail.getWeight() == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "此SKU重量不能为空");
            }
        }
    }

    /**
     * 发货时校验必填项
     *
     * @param sysOrder
     */
    public static void validateSysOrderDataForDeliverGood(SysOrderNew sysOrder) {
        Byte orderSource = sysOrder.getOrderSource();
        //获取订单收货地址信息
        SysOrderReceiveAddress receiveAddress = sysOrder.getSysOrderReceiveAddress();

        //获取订单包裹信息
        List<SysOrderPackage> sysOrderPackages = sysOrder.getSysOrderPackageList();
        if (sysOrderPackages == null || sysOrderPackages.size() <= 0) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "订单中包裹信息为空。。。");
        }

        sysOrderPackages.forEach(sysOrderPackage -> {
            if (orderSource == OrderSourceEnum.CONVER_FROM_EBAY.getValue()) {
                if (StringUtils.isBlank(sysOrderPackage.getEbayCarrierName())) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "物流商名称不能为空");
                }
            }
            if (orderSource == OrderSourceEnum.CONVER_FROM_AMAZON.getValue()) {
                if (StringUtils.isBlank(sysOrderPackage.getAmazonCarrierName()) || StringUtils.isBlank(sysOrderPackage.getAmazonShippingMethod())) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "物流商名称和配送方式不能为空");
                }
            }

            if (systemOrderCommonService.isGoodCangWarehouse(sysOrderPackage.getDeliveryWarehouseId().toString()) && receiveAddress.getShipToCountry().equalsIgnoreCase("US")) {
                if (StringUtils.isBlank(receiveAddress.getShipToState())) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "省州不能为空");
                }
            }

            if (StringUtils.isBlank(sysOrderPackage.getOrderTrackId())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "ERP订单跟踪号不能为空");
            }

            //订单发货仓库编码
            if (StringUtils.isBlank(sysOrderPackage.getDeliveryWarehouseCode())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "发货仓库编码不能为空");
            }
            //订单发货仓库ID
            if (ObjectUtils.isEmpty(sysOrderPackage.getDeliveryWarehouseId())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "发货仓库ID不能为空");
            }
            //邮寄方式编码
            if (StringUtils.isBlank(sysOrderPackage.getDeliveryMethodCode())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "邮寄方式编码不能为空");
            }
            //物流商CODE
            if (StringUtils.isBlank(sysOrderPackage.getShippingCarrierUsedCode())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "物流商CODE不能为空");
            }

        });

        if (sysOrder.getOrderTime() == null || sysOrder.getOrderTime() == "") {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "下单时间不能为空");
        }
        if (sysOrder.getTotal() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "订单总售价:预估物流费+商品金额不能为空");
        }

        if (StringUtils.isBlank(receiveAddress.getShipToName())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "收货人姓名不能为空");
        }
        //country_code国家简称
        if (StringUtils.isBlank(receiveAddress.getShipToCountry())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "国家不能为空");
        }
        //city城市
        if (StringUtils.isBlank(receiveAddress.getShipToCity())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "城市不能为空");
        }

        //address地址
        if (StringUtils.isBlank(receiveAddress.getShipToAddrStreet1()) && StringUtils.isBlank(receiveAddress.getShipToAddrStreet2())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "地址1和地址2不能同时为空");
        }
        //zipcode邮编
        if (StringUtils.isBlank(receiveAddress.getShipToPostalCode())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "邮编不能为空");
        }

        if (sysOrder.getPlatformShopId() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家平台店铺ID不能为空");
        }
        if (sysOrder.getSellerPlId() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家品连ID不能为空");
        }

        if (sysOrder.getSupplyChainCompanyId() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家供应链ID不能为空");
        }
        if (StringUtils.isBlank(sysOrder.getSupplyChainCompanyName())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "卖家供应链名称不能为空");
        }

        //TODO   不包邮的订单才校验预估物流费是否为0
        if (sysOrder.getFreeFreightType().equals(Constants.SysOrder.NOT_FREE_FREIGHT)) {
            if (sysOrder.getEstimateShipCost() == null || sysOrder.getEstimateShipCost().compareTo(new BigDecimal(0.00)) == 0) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "系统预估物流费不能为空或者为0；如果为0，请联系客服处理");
            }
        }

        if (sysOrder.getOrderSource() == OrderSourceEnum.CONVER_FROM_AMAZON.getValue()) {
            if (StringUtils.isBlank(sysOrder.getMarketplaceId())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "站点简称不能为空");
            }
        }
        //goods_amount商品总额:商品单价x数量再取和
        if (sysOrder.getOrderAmount() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "商品总额不能为空");
        }

        if (StringUtils.isBlank(sysOrder.getShopType())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "店铺类型不能为空");
        }

        List<SysOrderDetail> detailList = sysOrder.getSysOrderDetails();
        if (CollectionUtils.isEmpty(detailList)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "系统订单项不能为空");
        }
        for (SysOrderDetail detail : detailList) {
            //channel_item_id
            if (StringUtils.isBlank(detail.getOrderLineItemId())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统订单项ID不能为空");
            }
            //channel_sku
            if (StringUtils.isBlank(detail.getSupplierSku())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商SKU不能为空");
            }
            //channel_sku_title
            if (StringUtils.isBlank(detail.getSupplierSkuTitle())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商SKU标题不能为空");
            }
            //fareTypeAmount服务费不能为null
            if (StringUtils.isBlank(detail.getFareTypeAmount())
                    || detail.getFareTypeAmount().contains("null")
                    || detail.getFareTypeAmount().contains("NULL")) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "服务费不能为空");
            }
            //supplier_id  推送出库记录使用
            if (detail.getSupplierId() == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商ID不能为空");
            }
            //supply_chain_company_id
            //supply_chain_company_name
            if (detail.getSupplyChainCompanyId() == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商所属供应链ID不能为空");
            }
            if (StringUtils.isBlank(detail.getSupplyChainCompanyName())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商所属供应链名称不能为空");
            }
            //channel_sku_price
            if (detail.getSupplierSkuPrice() == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应商SKU价格不能为空");
            }
            //sku_quantity
            if (detail.getSkuQuantity() == null || detail.getSkuQuantity() == 0) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "购买此SKU数量不能为空");
            }
            //chanel_currency_code渠道价格货币简写
            //验证邮寄方式是否支持使用
            if (detail.getWeight() == null) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "此SKU重量不能为空");
            }
        }
    }
}