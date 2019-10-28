package com.rondaful.cloud.order.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.entity.PlatformAccount;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.SysOrderDetail;
import com.rondaful.cloud.order.entity.orderRule.OrderRuleWithBLOBs;
import com.rondaful.cloud.order.entity.supplier.FreightTrial;
import com.rondaful.cloud.order.entity.supplier.LogisticsDTO;
import com.rondaful.cloud.order.entity.supplier.OrderInvDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDetailDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MappingOrderRuleUtil {

    private static final Logger logger = LoggerFactory.getLogger(MappingOrderRuleUtil.class);

    public static String RULE_OBJECT = "ruleObject";
    public static String WAREHOUSE_TYPE = "warehouseType";
    public static String WAREHOUSE_NAME = "warehouseName";
    public static String WAREHOUSE_CODE = "warehouseCode";
    public static String WAREHOUSE_ID = "warehouseId";
    public static String CARRIER_CODE = "carrierCode";
    public static String CARRIER_NAME = "carrierName";
    public static String CODE = "code";
    public static String SHORT_NAME = "shortName";
    public static String AMAZON_CARRIER = "amazonCarrier";
    public static String AMAZON_CODE = "amazonCode";
    public static String EBAY_CARRIER = "ebayCarrier";

    /**
     * 谷仓-调用邮寄方式：传品连sku格式
     *
     * @param order
     * @return
     */
    public static List<String> getGcSKUList(SysOrder order) {
        List<String> gclist = new ArrayList<>();
        List<SysOrderDetail> sysOrderDetails = order.getSysOrderDetails();
        for (SysOrderDetail s : sysOrderDetails) {
            gclist.add(s.getSku() + ":" + s.getSkuQuantity());
        }
        return gclist;
    }

    /**
     * 谷仓-调用邮寄方式：传品连sku格式
     *
     * @param sysOrderPackageDTO {@link SysOrderPackageDTO}
     * @return {@link List<String>}
     */
    private static List<String> getGcSKUList(SysOrderPackageDTO sysOrderPackageDTO) {
        List<String> gclist = new ArrayList<>();
        List<SysOrderPackageDetailDTO> sysOrderPackageDetailDTOList = sysOrderPackageDTO.getSysOrderPackageDetailList();
        for (SysOrderPackageDetailDTO sysOrderPackageDetailDTO : sysOrderPackageDetailDTOList) {
            gclist.add(sysOrderPackageDetailDTO.getSku() + ":" + sysOrderPackageDetailDTO.getSkuQuantity());
        }
        return gclist;
    }

    /**
     * 调用邮寄方式：传品连sku
     *
     * @param sysOrderPackageDTO {@link SysOrderPackageDTO}
     * @return {@link List<Map<String, Object>>}
     */
    private static List<Map<String, Object>> getSkuList(SysOrderPackageDTO sysOrderPackageDTO) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<SysOrderPackageDetailDTO> sysOrderPackageDetailDTOList = sysOrderPackageDTO.getSysOrderPackageDetailList();
        for (SysOrderPackageDetailDTO sysOrderPackageDetailDTO : sysOrderPackageDetailDTOList) {
            Map<String, Object> map = new HashMap<>();
            map.put("sku", sysOrderPackageDetailDTO.getSku());
            map.put("num", sysOrderPackageDetailDTO.getSkuQuantity());
            list.add(map);
        }
        return list;
    }

    /**
     * erp-调用邮寄方式：传供应商sku格式
     *
     * @param order
     * @return
     */
    public static List<Map<String, Object>> getSkuList(SysOrder order) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<SysOrderDetail> sysOrderDetails = order.getSysOrderDetails();
        for (SysOrderDetail s : sysOrderDetails) {
            Map<String, Object> map = new HashMap<>();
            map.put("sku", s.getSupplierSku());
            map.put("num", s.getSkuQuantity());
            list.add(map);
        }
        return list;
    }

    /**
     * 根据仓库类型返回FreightTrial对象
     *
     * @param warehouseType      {@link Constants.Warehouse}
     * @param sysOrderPackageDTO {@link SysOrderPackageDTO}
     * @param platform           平台
     * @param warehouseId        仓库ID
     * @return
     */
    public static FreightTrial getFreightTrialObjectNew(String warehouseType, SysOrderPackageDTO sysOrderPackageDTO,
                                                        String platform, Integer warehouseId) {
        FreightTrial freightTrial = new FreightTrial();
        freightTrial.setCountryCode(sysOrderPackageDTO.getShipToCountry());
        freightTrial.setSkuList(getSkuList(sysOrderPackageDTO));//传入品连的sku
        freightTrial.setPlatformType(platformTypeConversion(platform));
        freightTrial.setPostCode(sysOrderPackageDTO.getShipToPostalCode());
        freightTrial.setWarehouseId(warehouseId);
        return freightTrial;
    }

    /**
     * Conversion platform
     *
     * @param platform
     * @return
     */
    public static String platformTypeConversion(String platform) {
        if ("eBay".equalsIgnoreCase(platform)) {
            return "1";
        }
        if ("Amazon".equalsIgnoreCase(platform)) {
            return "2";
        }
        if ("Wish".equalsIgnoreCase(platform)) {
            return "3";
        }
        if ("AliExpress".equalsIgnoreCase(platform)) {
            return "4";
        } else {
            return "";
        }
    }


    /**
     * 设置订单order对象的规则的发货仓库和邮寄方式属性
     *
     * @param order
     * @param warehouseCode 仓库CODE
     * @param warehouseName 订单发货仓库名称
     * @param carrierCode   物流商CODE
     * @param carrierName   物流商名称
     * @param shortName     邮寄方式名称
     * @param code          邮寄方式CODE
     */
    public static void setOrderObject(SysOrder order, String warehouseCode, String warehouseName, String carrierCode, String carrierName, String shortName, String code) {
        order.setDeliveryWarehouseCode(warehouseCode);
        order.setDeliveryWarehouse(warehouseName);
        order.setShippingCarrierUsedCode(carrierCode);
        order.setShippingCarrierUsed(carrierName);
        order.setDeliveryMethod(shortName);
        order.setDeliveryMethodCode(code);
    }

    /**
     * 设置Amazon Or Ebay 的order对象
     *
     * @param platform
     * @param order
     * @param maps
     */
    public static void setAmazonOrEbayOrderObject(String platform, SysOrder order, Map<String, String> maps) {
        if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.AMAZON.getPlatform())) {
            order.setAmazonCarrierName(maps.get("amazonCarrier"));
            order.setAmazonShippingMethod(maps.get("amazonCode"));
        } else if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.E_BAU.getPlatform())) {
            order.setEbayCarrierName(maps.get("ebayCarrier"));
        } else {
            order.setAmazonCarrierName(maps.get(""));
            order.setAmazonShippingMethod(maps.get(""));
            order.setEbayCarrierName(maps.get(""));
        }
    }


    /**
     * 匹配邮寄方式
     *
     * @param sysOrderPackageDTO {@link SysOrderPackageDTO}
     * @param platform           平台
     * @param logisticsList      订单skus匹配成功后返回的仓库列表
     * @param warehouseRuleList  {@link List<OrderRuleWithBLOBs>}仓库规则列表(卖家平台/管理后台)
     * @return {@link Map<String, String>}
     */
    public static Map<String, String> mailMatchingMethods(SysOrderPackageDTO sysOrderPackageDTO, String platform,
                                                          List<LogisticsDTO> logisticsList,
                                                          List<OrderRuleWithBLOBs> warehouseRuleList,
                                                          String mappedWarehouseId) {
        String sysOrderId = sysOrderPackageDTO.getSysOrderId();
        String packageNumber = sysOrderPackageDTO.getOrderTrackId();
        Map<String, String> map = new HashMap<>();
        if (CollectionUtils.isEmpty(warehouseRuleList)) {
            logger.info("订单{}, 包裹{}, 获取系统平台的===邮寄规则列表为空！", sysOrderId, packageNumber);
            return null;
        }
        look:
        for (OrderRuleWithBLOBs orderRule : warehouseRuleList) {

            String idListStr = orderRule.getDeliveryWarehouseIdList();
            List<String> deliveryWarehouseIdList = JSONArray.parseArray(idListStr, String.class);
            boolean isContinue = false;
            for (String deliveryWarehouseId : deliveryWarehouseIdList) {
                if (deliveryWarehouseId.equals(mappedWarehouseId)) {
                    isContinue = true;
                    break;
                }
            }

            if (!isContinue) {
                logger.info("订单{}, 包裹{}, 与该规则{} 的仓库不匹配，跳过", sysOrderId, packageNumber, orderRule.getRuleName());
                continue;
            }

            String mailTypeCode = orderRule.getMailTypeCode();
            for (LogisticsDTO logistics : logisticsList) {
                String logisticsCode = logistics.getCode();
                if (Objects.equals(mailTypeCode, logisticsCode)) {
                    logger.info("订单{}, 包裹{}, 邮寄方式=======匹配成功!!!规则邮寄方式编码是{}, 匹配到的邮寄编码是{}",
                            sysOrderId, packageNumber, mailTypeCode, logisticsCode);
                    //匹配其他条件
                    boolean allCheckResult = matchingOthersRuleMethods(sysOrderPackageDTO, orderRule, platform);
                    if (allCheckResult) {
                        //匹配成功的邮寄规则:优先级已排序
                        map.put(RULE_OBJECT, orderRule.toString());
                        //物流商代码
                        map.put(CARRIER_CODE, logistics.getCarrierCode());
                        //物流商名称
                        map.put(CARRIER_NAME, logistics.getCarrierName());
                        //物流方式代码
                        map.put(CODE, logistics.getCode());
                        //物流方式简称
                        map.put(SHORT_NAME, logistics.getShortName());
                        //amazon物流商代码
                        map.put(AMAZON_CARRIER, logistics.getAmazonCarrier());
                        //amazon物流方式
                        map.put(AMAZON_CODE, logistics.getAmazonCode());
                        //ebay物流商代码
                        map.put(EBAY_CARRIER, logistics.getEbayCarrier());
                        logger.info("订单{}, 包裹{}, 匹配物流方式成功，获取到的物流方式代码:{}", sysOrderId,
                                packageNumber, map.get("code"));
                        break look;
                    }
                }
            }
        }
        return map;
    }

    /**
     * 匹配邮寄方式
     *
     * @param order             {@link SysOrder}
     * @param platform          平台
     * @param logisticsList     订单skus匹配成功后返回的仓库列表
     * @param warehouseRuleList {@link List<OrderRuleWithBLOBs>}仓库规则列表(卖家平台/管理后台)
     * @return {@link Map<String, String>}
     */
    public static Map<String, String> mailMatchingMethodsNew(SysOrder order, String platform,
                                                             List<LogisticsDTO> logisticsList,
                                                             List<OrderRuleWithBLOBs> warehouseRuleList) {
        String sysOrderId = order.getSysOrderId();
        Map<String, String> map = new HashMap<>();
        if (CollectionUtils.isEmpty(warehouseRuleList)) {
            logger.info("订单{}获取系统平台的===邮寄规则列表为空！", sysOrderId);
            return null;
        }
        look:

        for (OrderRuleWithBLOBs orderRule : warehouseRuleList) {
            String mailTypeCode = orderRule.getMailTypeCode();
            for (LogisticsDTO logistics : logisticsList) {
                String logisticsCode = logistics.getCode();
                if (Objects.equals(mailTypeCode, logisticsCode)) {
                    logger.info("订单{}邮寄方式=======匹配成功!!!规则邮寄方式编码是{}, 匹配到的邮寄编码是{}",
                            sysOrderId, mailTypeCode, logisticsCode);
                    //匹配其他条件
                    boolean allCheckResult = matchingOthersRuleMethods(order, orderRule, platform);
                    if (allCheckResult) {
                        //匹配成功的邮寄规则:优先级已排序
                        map.put(RULE_OBJECT, orderRule.toString());
                        //物流商代码
                        map.put(CARRIER_CODE, logistics.getCarrierCode());
                        //物流商名称
                        map.put(CARRIER_NAME, logistics.getCarrierName());
                        //物流方式代码
                        map.put(CODE, logistics.getCode());
                        //物流方式简称
                        map.put(SHORT_NAME, logistics.getShortName());
                        //amazon物流商代码
                        map.put(AMAZON_CARRIER, logistics.getAmazonCarrier());
                        //amazon物流方式
                        map.put(AMAZON_CODE, logistics.getAmazonCode());
                        //ebay物流商代码
                        map.put(EBAY_CARRIER, logistics.getEbayCarrier());
                        logger.info("订单{}匹配物流方式成功，获取到的物流方式代码:{}", sysOrderId, map.get("code"));
                        break look;
                    }
                }
            }
        }
        return map;
    }

    /**
     * @param order                    {@link SysOrder}
     * @param platform                 平台
     * @param warehouseInventoriesList 订单skus匹配成功后返回的仓库列表
     * @param warehouseRuleList        {@link List<OrderRuleWithBLOBs>}仓库规则列表(卖家平台/管理后台)
     * @return {@link Map<String, String>}
     */
    public static Map<String, String> warehouseMatchingMethodsNew(SysOrder order, String platform,
                                                                  List<OrderInvDTO> warehouseInventoriesList,
                                                                  List<OrderRuleWithBLOBs> warehouseRuleList) {
        Map<String, String> map = new HashMap<>();
        String sysOrderId = order.getSysOrderId();
        if (CollectionUtils.isEmpty(warehouseRuleList)) {
            logger.info("订单{}获取系统平台的===仓库规则列表为空！", sysOrderId);
            return null;
        }
        look:
        for (OrderRuleWithBLOBs warehouseRule : warehouseRuleList) {
            Integer deliveryWarehouseId = warehouseRule.getDeliveryWarehouseId();

            for (OrderInvDTO orderInvDTO : warehouseInventoriesList) {
                Integer warehouseId = orderInvDTO.getWarehouseId();

                if (Objects.equals(deliveryWarehouseId, warehouseId)) {
                    logger.info("订单{}发货仓库======匹配成功!!!规则仓库ID是：deliveryWarehouseId={},有货的仓库ID是：warehouseId={}",
                            sysOrderId, deliveryWarehouseId, warehouseId);
                    //匹配其他条件
                    boolean allCheckResult = matchingOthersRuleMethods(order, warehouseRule, platform);
                    logger.info("订单{}仓库规则其他条件与平台订单条件匹配的结果为====={}", sysOrderId, allCheckResult);
                    if (allCheckResult) {
                        //匹配成功的仓库规则:优先级已排序
                        map.put(RULE_OBJECT, warehouseRule.toString());
                        //仓库类型
                        map.put(WAREHOUSE_TYPE, orderInvDTO.getServiceCode());
                        //仓库名称
                        map.put(WAREHOUSE_NAME, orderInvDTO.getWarehouseName());
                        //仓库码
                        map.put(WAREHOUSE_CODE, orderInvDTO.getWarehouseCode());
                        // 仓库ID
                        map.put(WAREHOUSE_ID, String.valueOf(warehouseId));
                        logger.info("订单{}匹配成功的规则信息======》{}", sysOrderId, warehouseRule.getRuleName());
                        logger.info("订单{}匹配成功后，获取到的仓库信息======》仓库名称:{}", sysOrderId, map.get("warehouseName"));
                        break look;
                    }
                }
            }
        }
        return map;
    }

    public static Map<String, String> warehouseMatchingMethods(SysOrderPackageDTO sysOrderPackageDTO, String platform,
                                                               List<OrderInvDTO> warehouseInventoriesList,
                                                               List<OrderRuleWithBLOBs> warehouseRuleList) {
        Map<String, String> map = new HashMap<>();
        String sysOrderId = sysOrderPackageDTO.getSysOrderId();
        String packageNumber = sysOrderPackageDTO.getOrderTrackId();
        if (CollectionUtils.isEmpty(warehouseRuleList)) {
            logger.info("订单{}, 包裹{}, 获取系统平台的===仓库规则列表为空！", sysOrderId, packageNumber);
            return null;
        }
        look:
        for (OrderRuleWithBLOBs warehouseRule : warehouseRuleList) {
            Integer deliveryWarehouseId = warehouseRule.getDeliveryWarehouseId();

            for (OrderInvDTO orderInvDTO : warehouseInventoriesList) {
                Integer warehouseId = orderInvDTO.getWarehouseId();

                if (Objects.equals(deliveryWarehouseId, warehouseId)) {
                    logger.info("订单{}, 包裹{}, 发货仓库======匹配成功!!!规则仓库ID是：deliveryWarehouseId={},有货的仓库ID是：warehouseId={}",
                            sysOrderId, packageNumber, deliveryWarehouseId, warehouseId);
                    //匹配其他条件
                    boolean allCheckResult = matchingOthersRuleMethods(sysOrderPackageDTO, warehouseRule, platform);
                    logger.info("订单{}仓库规则其他条件与平台订单条件匹配的结果为====={}", sysOrderId, allCheckResult);
                    if (allCheckResult) {
                        //匹配成功的仓库规则:优先级已排序
                        map.put(RULE_OBJECT, warehouseRule.toString());
                        //仓库类型
                        map.put(WAREHOUSE_TYPE, orderInvDTO.getServiceCode());
                        //仓库名称
                        map.put(WAREHOUSE_NAME, orderInvDTO.getWarehouseName());
                        //仓库码
                        map.put(WAREHOUSE_CODE, orderInvDTO.getWarehouseCode());
                        // 仓库ID
                        map.put(WAREHOUSE_ID, String.valueOf(warehouseId));
                        logger.info("订单{}, 包裹{}, 匹配成功的规则信息: {}", sysOrderId, packageNumber,
                                warehouseRule.getRuleName());
                        logger.info("订单{}, 包裹{}, 匹配成功后，获取到的仓库信息仓库名称:{}", sysOrderId,
                                packageNumber, map.get("warehouseName"));
                        break look;
                    }
                }
            }
        }
        return map;
    }

    /**
     * 根据匹配成功的仓库，继续校验系统订单是否满足规则的其他条件
     *
     * @param order 系统订单
     * @param rule  匹配成功的订单规则
     * @return
     */
    public static boolean matchingOthersRuleMethods(SysOrder order, OrderRuleWithBLOBs rule, String platform) {
        logger.info("======开始校验【其他条件】是否满足平台订单=======");
        if (order == null || rule == null) {
            return false;
        }
        jsonStringToObject(rule);
        Integer accountId = order.getEmpowerId();//授权id
        String receiveGoodsCountry = order.getShipToCountry();//收货国家
        String receiveGoodsZipCode = order.getShipToPostalCode();//收货邮编
        List<String> skus = order.getSkus();//订单中的转入成功的sku列表
        BigDecimal price = order.getTotal();//订单总售价:单位 USD
        BigDecimal weight = order.getTotalWeight();//订单总重量:单位 g
        BigDecimal volume = order.getTotalBulk();//订单总体积:单位 cm3
        logger.info("其他条件校验时，订单中需要的属性======》" + "授权id:" + accountId + "/收货国家:" + receiveGoodsCountry + "/收货邮编:" + receiveGoodsZipCode + "/sku列表:" + skus + "/订单总售价:" + price + "/订单总重量:" + weight + "/订单总体积:" + volume);
        boolean commonCheckResult = checkPublic(rule, platform, accountId, receiveGoodsCountry, receiveGoodsZipCode, skus, price, weight, volume);
        if (commonCheckResult) {
            return true;
        }
        return false;
    }

    /**
     * 根据匹配成功的仓库，继续校验包裹是否满足规则的其他条件
     *
     * @param sysOrderPackageDTO {@link SysOrderPackageDTO}
     * @param rule               {@link OrderRuleWithBLOBs}
     * @param platform           platform
     * @return boolean
     */
    private static boolean matchingOthersRuleMethods(SysOrderPackageDTO sysOrderPackageDTO, OrderRuleWithBLOBs rule, String platform) {
        logger.info("======开始校验【其他条件】是否满足平台订单=======");
        if (sysOrderPackageDTO == null || rule == null) {
            return false;
        }
        jsonStringToObject(rule);
        Integer accountId = sysOrderPackageDTO.getEmpowerId();//授权id
        String receiveGoodsCountry = sysOrderPackageDTO.getShipToCountry();//收货国家
        String receiveGoodsZipCode = sysOrderPackageDTO.getShipToPostalCode();//收货邮编
        List<String> skus = sysOrderPackageDTO.getSkus();//订单中的转入成功的sku列表
        BigDecimal price = sysOrderPackageDTO.getTotal();//订单总售价:单位 USD
        BigDecimal weight = sysOrderPackageDTO.getTotalWeight();//订单总重量:单位 g
        BigDecimal volume = sysOrderPackageDTO.getTotalBulk();//订单总体积:单位 cm3
        logger.info("其他条件校验时，订单中需要的属性======》" + "授权id:" + accountId + "/收货国家:" + receiveGoodsCountry + "/收货邮编:" + receiveGoodsZipCode + "/sku列表:" + skus + "/订单总售价:" + price + "/订单总重量:" + weight + "/订单总体积:" + volume);
        boolean commonCheckResult = checkPublic(rule, platform, accountId, receiveGoodsCountry, receiveGoodsZipCode, skus, price, weight, volume);
        if (commonCheckResult) {
            return true;
        }
        return false;
    }

    /**
     * 将订单规则中的 json字符串 转换成代表条件的list对象
     *
     * @param rule 订单规则对象
     */
    public static void jsonStringToObject(OrderRuleWithBLOBs rule) {
        if (StringUtils.isNotBlank(rule.getDeliveryWarehouseCodeList())) {  //发货仓库code列表
            rule.setDeliveryWarehouseIds(JSONObject.parseArray(rule.getDeliveryWarehouseCodeList(), String.class));
        }
        if (StringUtils.isNotBlank(rule.getSellerAccountList())) { //订单来源平台账户列表
            rule.setPlatformAccounts(JSONObject.parseArray(rule.getSellerAccountList(), PlatformAccount.class));
        }
        if (StringUtils.isNotBlank(rule.getReceiveGoodsCountryList())) { //收货国家列表
            rule.setReceiveGoodsCountrys(JSONObject.parseArray(rule.getReceiveGoodsCountryList(), String.class));
        }
        if (StringUtils.isNotBlank(rule.getReceiveGoodsZipCodeList())) { //收货邮编列表
            rule.setReceiveGoodsZipCodes(JSONObject.parseArray(rule.getReceiveGoodsZipCodeList(), String.class));
        }
        if (StringUtils.isNotBlank(rule.getPlSkuList())) { //品连 sku 列表
            rule.setPlSkus(JSONObject.parseArray(rule.getPlSkuList(), String.class));
        }
    }

    /**
     * 对发货仓库和邮寄方式的公共条件进行检查
     *
     * @param rule                规则对象
     * @param platform            平台
     * @param accountId           账户id
     * @param receiveGoodsCountry 收货国家
     * @param receiveGoodsZipCode 收货邮编
     * @param skus                品连sku
     * @param price               订单总价
     * @param weight              订单总重量
     * @param volume              订单总体积
     * @return 是否检查成功[ true 成功  false 失败]
     */
    public static boolean checkPublic(OrderRuleWithBLOBs rule, String platform, Integer accountId, String receiveGoodsCountry,
                                      String receiveGoodsZipCode, List<String> skus, BigDecimal price, BigDecimal weight,
                                      BigDecimal volume) {
        boolean checkAccount = checkAccount(rule.getPlatformAccounts(), platform, String.valueOf(accountId));
        logger.info("来源平台账号的校验结果是：{}", checkAccount);
        if (!checkAccount) {
            return false;
        }

        boolean checkCountrys = checkStringList(rule.getReceiveGoodsCountrys(), receiveGoodsCountry);
        logger.info("国家的校验结果是：{}", checkCountrys);
        if (!checkCountrys) {
            return false;
        }

        boolean checkZipCodes = checkStringList(rule.getReceiveGoodsZipCodes(), receiveGoodsZipCode);
        logger.info("邮编的校验结果是：{}", checkZipCodes);
        if (!checkZipCodes) {
            return false;
        }

        boolean checkSkus = checkStringListToList(rule.getPlSkus(), skus);
        logger.info("sku的校验结果是：{}", checkSkus);
        if (!checkSkus) {
            return false;
        }

        boolean checkPrice = checkBigDecimal(rule.getPriceMin(), rule.getPriceMax(), price);
        logger.info("总价的校验结果是：{}", checkPrice);
        if (!checkPrice) {
            return false;
        }

        boolean checkWeight = checkBigDecimal(rule.getWeightMin(), rule.getWeightMax(), weight);
        logger.info("总重量的校验结果是：{}", checkWeight);
        if (!checkWeight) {
            return false;
        }

        boolean checkVolume = checkBigDecimal(rule.getVolumeMin(), rule.getVolumeMax(), volume);
        logger.info("总体积的校验结果是：{}", checkVolume);
        logger.info("最终的校验结果是：{}", checkVolume);
        return checkVolume;
    }

    /**
     * 检查账户是否匹配
     *
     * @param platformAccounts 平台账户信息分装对象列表
     * @param platform         平台
     * @param accountId        账户id
     * @return 是否检查成功[ true 成功  false 失败]
     */
    public static boolean checkAccount(List<PlatformAccount> platformAccounts, String platform, String accountId) {
        if (StringUtils.isBlank(accountId)) {
            logger.info("订单规则匹配======传入的参数【accountId】为空!");
            return false;
        }

        if (platformAccounts != null && platformAccounts.size() > 0) {
            if (StringUtils.isBlank(platform)) {
                logger.info("订单规则匹配======传入的参数【platform】为空!");
                return false;
            }
            for (PlatformAccount platformAccount : platformAccounts) {
                String judgePlatform = platformAccount.getPlatform();
                if (!platform.equalsIgnoreCase(platformAccount.getPlatform())) {
                    logger.info("订单规则匹配======传入的参数【platform={}】 不符合该账号条件 judgePlatform={}!, 跳过",
                            platform, judgePlatform);
                    continue;
                }

                List<String> accounts = platformAccount.getAccounts();
                if (CollectionUtils.isEmpty(accounts)) {
                    logger.info("judgePlatform={} 没有勾选账号, 跳过", judgePlatform);
                    continue;
                }

                for (String id : platformAccount.getAccounts()) {
                    if (id.equalsIgnoreCase(accountId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 检查字符串是否存在于 字符串列表中
     *
     * @param strs 字符串列表
     * @param str  字符串
     * @return 是否检查成功[ true 成功  false 失败]
     */
    public static boolean checkStringList(List<String> strs, String str) {
        if (strs != null && strs.size() > 0) {
            if (StringUtils.isBlank(str))
                return false;
            for (String country : strs) {
                if (country.equalsIgnoreCase(str))
                    return true;
            }
            return false;
        }
        return true;
    }

    /**
     * 判断前集合是否包含后集合
     *
     * @param strs list1 规则
     * @param skus list2 订单
     * @return 是否检查成功[ true 成功  false 失败]
     */
    public static boolean checkStringListToList(List<String> strs, List<String> skus) {
        if (skus == null || skus.size() == 0)
            return true;
        if (strs == null || strs.size() == 0)
            return true;
        for (String sku : skus) {
            if (!strs.contains(sku))
                return false;
        }
        return true;
    }

    /**
     * 检查BigDecimal 类型是否在区间内是否在区间范围内
     *
     * @param priceMin 价格下限 (0#1.00,#号前 0比较时代等号，1不带等号)
     * @param priceMax 价格上限 (0#1.00,#号前 0比较时代等号，1不带等号)
     * @param price    价格
     * @return 是否检查成功[ true 成功  false 失败]
     */
    public static boolean checkBigDecimal(String priceMin, String priceMax, BigDecimal price) {
        if (StringUtils.isNotBlank(priceMin)) {
            if (price == null)
                return false;
            String[] split = priceMin.split("#");
            if (split.length != 2)
                return false;
            int i = Integer.valueOf(split[0]);
            BigDecimal bigDecimal = new BigDecimal(split[1]);
            if (i == 0) {
                if (!(price.compareTo(bigDecimal) >= 0))
                    return false;
            } else if (i == 1) {
                if (!(price.compareTo(bigDecimal) > 0))
                    return false;
            } else
                return false;
        }
        if (StringUtils.isNotBlank(priceMax)) {
            if (price == null)
                return false;
            String[] split = priceMax.split("#");
            if (split.length != 2)
                return false;
            int i = Integer.parseInt(split[0]);
            BigDecimal bigDecimal = new BigDecimal(split[1]);
            if (i == 0) {
                if (!(price.compareTo(bigDecimal) <= 0))
                    return false;
            } else if (i == 1) {
                if (!(price.compareTo(bigDecimal) < 0))
                    return false;
            } else
                return false;
        }
        return true;
    }
}



