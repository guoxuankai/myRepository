package com.rondaful.cloud.transorder.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.model.vo.freight.LogisticsDetailVo;
import com.rondaful.cloud.common.model.vo.freight.SearchLogisticsListDTO;
import com.rondaful.cloud.common.model.vo.freight.SearchLogisticsListSku;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.transorder.entity.*;
import com.rondaful.cloud.transorder.entity.commodity.CommodityBase;
import com.rondaful.cloud.transorder.entity.commodity.CommoditySpec;
import com.rondaful.cloud.transorder.entity.dto.GetCommodityBySkuListDTO;
import com.rondaful.cloud.transorder.entity.supplier.OrderInvDTO;
import com.rondaful.cloud.transorder.entity.supplier.WarehouseDTO;
import com.rondaful.cloud.transorder.entity.system.SysOrderDTO;
import com.rondaful.cloud.transorder.entity.system.SysOrderDetail;
import com.rondaful.cloud.transorder.entity.system.SysOrderReceiveAddress;
import com.rondaful.cloud.transorder.enums.LogisticsStrategyCovertToLogisticsLogisticsType;
import com.rondaful.cloud.transorder.enums.OrderHandleLogEnum;
import com.rondaful.cloud.transorder.mapper.OrderRuleMailMapper;
import com.rondaful.cloud.transorder.mapper.OrderRuleWarehouseMapper;
import com.rondaful.cloud.transorder.remote.RemoteCommodityService;
import com.rondaful.cloud.transorder.remote.RemoteSellerService;
import com.rondaful.cloud.transorder.remote.RemoteSupplierService;
import com.rondaful.cloud.transorder.service.MatchingLogisticsRuleService;
import com.rondaful.cloud.transorder.utils.FastJsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author guoxuankai
 * @date 2019/9/29 15:03
 */
@Service
public class MatchingLogisticsRuleServiceImpl implements MatchingLogisticsRuleService {


    private static final Logger logger = LoggerFactory.getLogger(MatchingLogisticsRuleServiceImpl.class);

    @Autowired
    private RemoteCommodityService remoteCommodityService;
    @Autowired
    private RemoteSellerService remoteSellerService;
    @Autowired
    private RemoteSupplierService remoteSupplierService;
    @Autowired
    OrderRuleWarehouseMapper orderRuleWarehouseMapper;
    @Autowired
    private OrderRuleMailMapper orderRuleMailMapper;




    @Override
    public void matchingLogisticsRule(SysOrderDTO sysOrder) {


        List<SysOrderDetail> sysOrderDetails = sysOrder.getSysOrderDetails();

        for (SysOrderDetail sysOrderDetail : sysOrderDetails) {
            if ("unbind".equals(sysOrderDetail.getBindStatus())) {
                logger.info("平台订单{}存在未关联sku商品,放弃匹配规则", sysOrder.getSourceOrderId());
                return;
            }
        }

        List<String> plSkuList = sysOrder.getSkus();
        String orderInvResult = remoteSupplierService.getsInvBySku(JSONArray.toJSONString(plSkuList));
        String orderInvData = Utils.returnRemoteResultDataString(orderInvResult, "根据sku集合查询仓库信息时，调用供应商服务异常");
        List<OrderInvDTO> orderInvDTOS = JSONObject.parseArray(orderInvData, OrderInvDTO.class);
        if (CollectionUtils.isEmpty(orderInvDTOS)) {
            logger.info("平台订单{}查不到任何可用仓库,放弃匹配规则", sysOrder.getSourceOrderId());
            return;
        }

        List<Integer> warehouseIds = orderInvDTOS.stream()
                .map(e -> e.getWarehouseId())
                .collect(Collectors.toList());

        List<SearchLogisticsListSku> searchPlSkuList = new LinkedList<>();
        sysOrderDetails.forEach(e -> {
            SearchLogisticsListSku searchLogisticsListSku = new SearchLogisticsListSku();
//            searchLogisticsListSku.setSku("A-1-0064B623-475729");
            searchLogisticsListSku.setSku(e.getSku());
            searchLogisticsListSku.setSkuNumber(e.getSkuQuantity());
            searchPlSkuList.add(searchLogisticsListSku);
        });

        // TODO 匹配刊登SKU物流规则START--------------------------------------------------------------------------------

        List<String> platformSkuList = sysOrderDetails.stream().map(e -> new String(e.getSourceSku())).collect(Collectors.toList());
        GetByplatformSkuAndSiteDTO dto = new GetByplatformSkuAndSiteDTO();
        dto.setEmpowerId(Integer.valueOf(sysOrder.getEmpowerId()));
        dto.setType(sysOrder.getPlatformName().toUpperCase());
        dto.setPlatformSku(platformSkuList);
        String getByplatformSkuAndSiteResult = remoteSellerService.getByplatformSkuAndSite(dto);
        String getByplatformSkuAndSiteData = Utils.returnRemoteResultDataString(getByplatformSkuAndSiteResult, "查询刊登sku的仓库信息时,调用卖家服务异常");
        List<GetResultPublishListingVO> getResultPublishListingVOS = JSON.parseArray(getByplatformSkuAndSiteData, GetResultPublishListingVO.class);


        if (CollectionUtils.isNotEmpty(getResultPublishListingVOS)) {

            GetResultPublishListingVO getResultPublishListingVO = getResultPublishListingVOS.get(0);
            Integer logisticsType = getResultPublishListingVO.getLogisticsType();
            Integer warehouseId = getResultPublishListingVO.getWarehouseId();

            List<LogisticsDetailVo> logisticsDetailVos = getLogisticsDetailVos(sysOrder, searchPlSkuList, warehouseId, logisticsType);

            //仓库信息、物流信息均不为空时候才匹配，否则继续后面的物流规则匹配
            if (CollectionUtils.isNotEmpty(logisticsDetailVos) && warehouseIds.contains(warehouseId)) {

                LogisticsDetailVo logisticsDetailVo = logisticsDetailVos.get(0);

                WarehouseDTO warehouseDTO = getWarehouseInfo(warehouseId);

                sysOrder.getPackages().forEach(orderPackageDTO -> {
                    orderPackageDTO.setDeliveryWarehouseId(getResultPublishListingVO.getWarehouseId());
                    orderPackageDTO.setDeliveryMethod(logisticsDetailVo.getLogisticsName());
                    orderPackageDTO.setShippingCarrierUsed(logisticsDetailVo.getCarrierName());
                    orderPackageDTO.setShippingCarrierUsedCode(logisticsDetailVo.getCarrierCode());
                    orderPackageDTO.setAmazonCarrierName(logisticsDetailVo.getAmazonCarrier());
                    orderPackageDTO.setAmazonShippingMethod(logisticsDetailVo.getAmazonCode());
                    orderPackageDTO.setEbayCarrierName(logisticsDetailVo.getEbayCarrier());
                    orderPackageDTO.setDeliveryMethodCode(logisticsDetailVo.getLogisticsCode());
                    orderPackageDTO.setLogisticsStrategy(LogisticsStrategyCovertToLogisticsLogisticsType.getLogisticsStrategyByLogisticsType(logisticsType));
                    orderPackageDTO.setDeliveryWarehouseCode(warehouseDTO.getWarehouseCode());
                    orderPackageDTO.setDeliveryWarehouse(warehouseDTO.getWarehouseName());
                    orderPackageDTO.setMappingOrderRuleLog(OrderHandleLogEnum.Content.MAPPING_PUBLISH_RULE_SUCCESS.mappingPublishRuleSuccess());

                });

                return;
            }
        }

        sysOrder.getPackages().get(0).setLogisticsStrategy(null);

        logger.info("平台订单{}匹配不到刊登SKU物流规则", sysOrder.getSourceOrderId());

        // TODO 匹配仓库规则START---------------------------------------------------------------------------------------

        setTotalWeightAndBulk(sysOrder);

        OrderRuleWithBLOBs orderRuleWithBLOBs = disposeRule(sysOrder, warehouseIds, null, 1);

        if (orderRuleWithBLOBs == null) {
            logger.info("平台订单{}匹配不到可用的仓库规则", sysOrder.getSourceOrderId());
            return;
        }

        Integer deliveryWarehouseId = orderRuleWithBLOBs.getDeliveryWarehouseId();
        WarehouseDTO warehouseInfo = getWarehouseInfo(deliveryWarehouseId);
        sysOrder.getPackages().stream().forEach(p -> {
            p.setDeliveryWarehouse(warehouseInfo.getWarehouseName());
            p.setDeliveryWarehouseId(warehouseInfo.getWarehouseId());
            p.setDeliveryWarehouseCode(warehouseInfo.getWarehouseCode());
        });

        // TODO 匹配物流方式规则START-----------------------------------------------------------------------------------
        int logisticsType = LogisticsStrategyCovertToLogisticsLogisticsType.INTEGRATED_OPTIMAL.getLogisticsType();
        Integer WarehouseId = sysOrder.getPackages().get(0).getDeliveryWarehouseId();
        List<LogisticsDetailVo> logisticsDetailVos = getLogisticsDetailVos(sysOrder, searchPlSkuList, WarehouseId, logisticsType);
        if (CollectionUtils.isEmpty(logisticsDetailVos)) {
            logger.info("平台订单{}没有匹配到物流规则", sysOrder.getSourceOrderId());
            return;
        }
        orderRuleWithBLOBs = disposeRule(sysOrder, null, logisticsDetailVos, 2);
        if (orderRuleWithBLOBs == null) {
            logger.info("平台订单{}匹配不到可用的物流规则", sysOrder.getSourceOrderId());
            return;
        }
        setDeliveryLogisticsInfo(sysOrder, orderRuleWithBLOBs, logisticsDetailVos);


    }

    private void setTotalWeightAndBulk(SysOrderDTO order) {
        Map<String, List<SysOrderDetail>> map = order.getSysOrderDetails().stream().collect(Collectors.groupingBy(e -> e.getSku()));
        BigDecimal totalWeight = BigDecimal.ZERO;//总重量
        BigDecimal totalBulk = BigDecimal.ZERO;//总体积
        if (MapUtils.isNotEmpty(map)) {

            GetCommodityBySkuListDTO getCommodityBySkuListDTO = new GetCommodityBySkuListDTO();
            getCommodityBySkuListDTO.setSellerId(order.getSellerPlId());
            getCommodityBySkuListDTO.setSystemSkuList(order.getSkus());
            String result = remoteCommodityService.getCommodityBySkuList(getCommodityBySkuListDTO);
            String data = Utils.returnRemoteResultDataString(result, "根据sku查询对应商品信息时，调用商品服务异常");
            List<CommodityBase> commodityBases = JSONObject.parseArray(data, CommodityBase.class);

            for (CommodityBase commodityBase : commodityBases) {
                for (CommoditySpec commoditySpec : commodityBase.getCommoditySpecList()) {
                    if (commoditySpec.getPackingHeight() != null && commoditySpec.getPackingLength() != null
                            && commoditySpec.getPackingWidth() != null) {
                        totalBulk = totalBulk.add(commoditySpec.getPackingHeight().multiply(commoditySpec.getPackingLength()).multiply(commoditySpec.getPackingWidth().multiply(new BigDecimal(map.get(commoditySpec.getSystemSku()).get(0).getSkuQuantity()))));
                    }
                    totalWeight = totalWeight.add(commoditySpec.getPackingWeight() == null ? BigDecimal.valueOf(0) : commoditySpec.getPackingWeight().multiply(new BigDecimal(map.get(commoditySpec.getSystemSku()).get(0).getSkuQuantity())));
                }
            }
        }
        order.setTotalBulk(totalBulk);
        order.setTotalWeight(totalWeight);
    }


    /**
     * @param orderDTO
     * @param type     规则类型：1仓库规则，2物流规则
     * @return
     */
    private OrderRuleWithBLOBs disposeRule(SysOrderDTO orderDTO, List<Integer> warehouseIds, List<LogisticsDetailVo> logisticsDetailVos, Integer type) {

        OrderRule orderRule = new OrderRule() {{
            setSellerId(String.valueOf(orderDTO.getSellerPlId()));
            setStatus(1);
            setSellerAccount(orderDTO.getSellerPlAccount());
            setPlatformMark("S");
        }};
        List<OrderRuleWithBLOBs> orderRuleList;
        if (type == 1) {
            orderRuleList = orderRuleWarehouseMapper.findAll(orderRule);
        } else {
            orderRuleList = orderRuleMailMapper.findAll(orderRule);
        }


        List<OrderRuleWithBLOBs> ruleList;

        if (type == 1) {
            ruleList = orderRuleList.stream().filter(rule -> warehouseIds.contains(rule.getDeliveryWarehouseId())).collect(Collectors.toList());
        } else {
            ruleList = orderRuleList.stream().filter(rule -> rule.getDeliveryWarehouseIdList().contains(String.valueOf(orderDTO.getPackages().get(0).getDeliveryWarehouseId()))).collect(Collectors.toList());
        }

        OrderRuleWithBLOBs orderRuleWithBLOBs = filterRule(orderDTO, ruleList, logisticsDetailVos);

        if (orderRuleWithBLOBs == null) {
            orderRule.setSellerId(null);
            orderRule.setSellerAccount(null);
            orderRule.setPlatformMark("G");
            if (type == 1) {
                orderRuleList = orderRuleWarehouseMapper.findAll(orderRule);
                ruleList = orderRuleList.stream().filter(rule -> warehouseIds.contains(rule.getDeliveryWarehouseId())).collect(Collectors.toList());
            } else {
                orderRuleList = orderRuleMailMapper.findAll(orderRule);
                ruleList = orderRuleList.stream().filter(rule -> rule.getDeliveryWarehouseIdList().contains(String.valueOf(orderDTO.getPackages().get(0).getDeliveryWarehouseId()))).collect(Collectors.toList());
            }
            orderRuleWithBLOBs = filterRule(orderDTO, ruleList, logisticsDetailVos);
        }

        return orderRuleWithBLOBs;
    }


    private OrderRuleWithBLOBs filterRule(SysOrderDTO orderDTO, List<OrderRuleWithBLOBs> orderRuleList, List<LogisticsDetailVo> logisticsDetailVos) {


        SysOrderReceiveAddress sysOrderReceiveAddress = orderDTO.getSysOrderReceiveAddress();
        String shipToCountry = sysOrderReceiveAddress.getShipToCountry();//国家代码
        String shipToPostalCode = sysOrderReceiveAddress.getShipToPostalCode();//邮编
        // ???
        BigDecimal TotalPrice = orderDTO.getCommoditiesAmount(); //平台总售价

        List<String> skus = orderDTO.getSkus();  //转入成功的SKU集合

        Map<String, List<LogisticsDetailVo>> logisticsMap = null;
        if (CollectionUtils.isNotEmpty(logisticsDetailVos)) {
            logisticsMap = logisticsDetailVos.stream().collect(Collectors.groupingBy(vo -> vo.getLogisticsCode()));
        }

        for (OrderRuleWithBLOBs rule : orderRuleList) {

            if (logisticsMap != null) {
                if (!logisticsMap.containsKey(rule.getMailTypeCode())) {
                    continue;
                }
            }

            jsonStringToObject(rule);
            if (checkSourceAndShop(orderDTO, rule))
                if (checkCountryCode(shipToCountry, rule))
                    if (checkShipToPostalCode(shipToPostalCode, rule))
                        if (checkPlSku(rule, skus))
                            if (checkBigDecimal(rule.getPriceMin(), rule.getPriceMax(), TotalPrice))
                                if (checkBigDecimal(rule.getWeightMin(), rule.getWeightMax(), orderDTO.getTotalWeight()))
                                    if (checkBigDecimal(rule.getVolumeMin(), rule.getVolumeMax(), orderDTO.getTotalBulk())) {
                                        return rule;
                                    }
        }

        return null;
    }


    /**
     * 将订单规则中的 json字符串 转换成代表条件的list对象
     *
     * @param rule 订单规则对象
     */
    public void jsonStringToObject(OrderRuleWithBLOBs rule) {
        if (StringUtils.isNotBlank(rule.getDeliveryWarehouseIdList())) {
            rule.setDeliveryWarehouseIds(JSONObject.parseArray(rule.getDeliveryWarehouseIdList(), String.class));
        }
        if (StringUtils.isNotBlank(rule.getDeliveryWarehouseCodeList())) {
            rule.setDeliveryWarehouseIds(JSONObject.parseArray(rule.getDeliveryWarehouseCodeList(), String.class));
        }
        if (StringUtils.isNotBlank(rule.getSellerAccountList())) {
            rule.setPlatformAccounts(JSONObject.parseArray(rule.getSellerAccountList(), PlatformAccount.class));
        }
        if (StringUtils.isNotBlank(rule.getReceiveGoodsCountryList())) {
            rule.setReceiveGoodsCountrys(JSONObject.parseArray(rule.getReceiveGoodsCountryList(), String.class));
        }
        if (StringUtils.isNotBlank(rule.getReceiveGoodsZipCodeList())) {
            rule.setReceiveGoodsZipCodes(JSONObject.parseArray(rule.getReceiveGoodsZipCodeList(), String.class));
        }
        if (StringUtils.isNotBlank(rule.getPlSkuList())) {
            rule.setPlSkus(JSONObject.parseArray(rule.getPlSkuList(), String.class));
        }
    }


    private boolean checkSourceAndShop(SysOrderDTO orderDTO, OrderRuleWithBLOBs rule) {
        String sellerAccountList = rule.getSellerAccountList();
        if (StringUtils.isNotBlank(sellerAccountList)) {
            List<MappingShopDTO> list = JSONObject.parseArray(sellerAccountList, MappingShopDTO.class);
            boolean flag = false;
            for (MappingShopDTO dto : list) {
                List<String> accounts = dto.getAccounts();
                if (CollectionUtils.isNotEmpty(accounts)) {
                    flag = true;
                }
            }
            if (!flag) {
                return true;
            }
            for (MappingShopDTO dto : list) {
                if (dto.getPlatform().equalsIgnoreCase(orderDTO.getPlatformName())) {
                    if (dto.getAccounts().toString().contains(String.valueOf(orderDTO.getPlatformShopId()))) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return true;
        }
    }

    private boolean checkCountryCode(String shipToCountry, OrderRuleWithBLOBs rule) {
        List<String> receiveGoodsCountrys = rule.getReceiveGoodsCountrys();
        if (CollectionUtils.isNotEmpty(receiveGoodsCountrys)) {
            if (!receiveGoodsCountrys.contains(shipToCountry)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkShipToPostalCode(String shipToPostalCode, OrderRuleWithBLOBs rule) {
        List<String> receiveGoodsZipCodes = rule.getReceiveGoodsZipCodes();
        if (CollectionUtils.isNotEmpty(receiveGoodsZipCodes)) {
            if (!receiveGoodsZipCodes.contains(shipToPostalCode)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkPlSku(OrderRuleWithBLOBs rule, List<String> skus) {
        List<String> plSkus = rule.getPlSkus();
        if (CollectionUtils.isNotEmpty(plSkus)) {
            for (String sku : skus) {
                if (!plSkus.contains(sku)) {
                    return false;
                }
            }
        }
        return true;
    }

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

    public WarehouseDTO getWarehouseInfo(Integer warehouseId) {
        String result = remoteSupplierService.getWarehouseById(warehouseId);
        String data = Utils.returnRemoteResultDataString(result, "根据仓库id获取仓库信息时，调用供应商服务异常");
        WarehouseDTO warehouseDTO = JSONObject.parseObject(data, WarehouseDTO.class);
        if (warehouseDTO == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "根据仓库id获取仓库信息为空");
        }
        return warehouseDTO;
    }

    private List<LogisticsDetailVo> getLogisticsDetailVos(SysOrderDTO order, List<SearchLogisticsListSku> plSkuList, Integer deliveryWarehouseId, Integer logisticsType) {
        SearchLogisticsListDTO searchLogisticsListDTO = new SearchLogisticsListDTO();
        searchLogisticsListDTO.setCountryCode(order.getSysOrderReceiveAddress().getShipToCountry());
        searchLogisticsListDTO.setPlatformType(String.valueOf(order.getPlatformType()));
        searchLogisticsListDTO.setSearchLogisticsListSkuList(plSkuList);
        searchLogisticsListDTO.setPostCode(order.getSysOrderReceiveAddress().getShipToPostalCode());
        searchLogisticsListDTO.setWarehouseId(String.valueOf(deliveryWarehouseId));
        searchLogisticsListDTO.setSearchType(logisticsType);
        searchLogisticsListDTO.setStoreId(order.getPlatformShopId());
        searchLogisticsListDTO.setHandOrder(order.getHandOrder());
        logger.info("获得合适的物流方式入参{}：", FastJsonUtils.toJsonString(searchLogisticsListDTO));
        String result = remoteSupplierService.getSuitLogisticsByType(searchLogisticsListDTO);
        logger.info("获得合适的物流方式结果{}：", FastJsonUtils.toJsonString(searchLogisticsListDTO));
        String data = Utils.returnRemoteResultDataString(result, "获取合适的物流方式时，调用供应商服务失败");
        List<LogisticsDetailVo> logisticsDetailVos = JSON.parseArray(data, LogisticsDetailVo.class);
        return logisticsDetailVos;
    }

    private void setDeliveryLogisticsInfo(SysOrderDTO order, OrderRuleWithBLOBs logisticsRule, List<LogisticsDetailVo> logisticsDetailVos) {

        Map<String, List<LogisticsDetailVo>> logisticsMap = logisticsDetailVos.stream().collect(Collectors.groupingBy(vo -> vo.getLogisticsCode()));

        // TODO 设置匹配物流规则成功的日志
        String mappingOrderRuleLog = order.getPackages().get(0).getMappingOrderRuleLog();//前面匹配仓库规则的日志
        order.getPackages().get(0).setMappingOrderRuleLog(mappingOrderRuleLog + "#" + OrderHandleLogEnum.Content.MAPPING_LOGISTICS_RULE_SUCCESS.mappingLogisticsRuleSuccess(logisticsRule.getRuleName()));

        for (LogisticsDetailVo logisticsVo : logisticsMap.get(logisticsRule.getMailTypeCode())) {
            order.getPackages().stream().forEach(p -> {
                p.setDeliveryMethod(logisticsVo.getLogisticsName());
                p.setShippingCarrierUsed(logisticsVo.getCarrierName());
                p.setShippingCarrierUsedCode(logisticsVo.getCarrierCode());
                p.setAmazonCarrierName(logisticsVo.getAmazonCarrier());
                p.setAmazonShippingMethod(logisticsVo.getAmazonCode());
                p.setEbayCarrierName(logisticsVo.getEbayCarrier());
                p.setDeliveryMethodCode(logisticsVo.getLogisticsCode());
            });
            break;
        }
    }


}
