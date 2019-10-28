package com.rondaful.cloud.order.service.impl;


import com.github.pagehelper.PageInfo;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.common.utils.ExcelUtil;
import com.rondaful.cloud.order.entity.aliexpress.AliexpressOrderChild;
import com.rondaful.cloud.order.entity.orderRule.SKUMapMailRuleDTO;
import com.rondaful.cloud.order.entity.orderRule.SellerSkuMap;
import com.rondaful.cloud.order.enums.SkuEnmus;
import com.rondaful.cloud.order.mapper.SellerSkuMapMapper;
import com.rondaful.cloud.order.remote.RemoteCommodityService;
import com.rondaful.cloud.order.remote.RemoteSellerService;
import com.rondaful.cloud.order.remote.RemoteUserService;
import com.rondaful.cloud.order.service.IOrderRuleService;
import com.rondaful.cloud.order.service.ISellerSkuMapService;
import com.rondaful.cloud.order.service.ISystemOrderCommonService;
import com.rondaful.cloud.order.service.TriggerConverSYSService;
import com.rondaful.cloud.order.utils.ThreadPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class SellerSkuMapServiceImpl extends BaseServiceImpl<SellerSkuMap> implements ISellerSkuMapService {
    private final SellerSkuMapMapper sellerSkuMapMapper;
    private final RemoteCommodityService remoteCommodityService;
    private final IOrderRuleService orderRuleService;
    private final ISystemOrderCommonService systemOrderCommonService;
    private final RemoteSellerService remoteSellerService;
    private final RemoteUserService remoteUserService;
    @Autowired
    private ISellerSkuMapService sellerSkuMapService;
    @Autowired
    private TriggerConverSYSService triggerConverSYSService;

    private static final Logger _log = LoggerFactory.getLogger(SellerSkuMapServiceImpl.class);

    @Autowired
    public SellerSkuMapServiceImpl(RemoteCommodityService remoteCommodityService,
                                   SellerSkuMapMapper sellerSkuMapMapper,
                                   IOrderRuleService orderRuleService,
                                   RemoteSellerService remoteSellerService,
                                   ISystemOrderCommonService systemOrderCommonService,
                                   RemoteUserService remoteUserService) {
        this.remoteCommodityService = remoteCommodityService;
        this.sellerSkuMapMapper = sellerSkuMapMapper;
        this.orderRuleService = orderRuleService;
        this.remoteSellerService = remoteSellerService;
        this.systemOrderCommonService = systemOrderCommonService;
        this.remoteUserService = remoteUserService;
    }

    @Override
    public HashMap<String, List<SellerSkuMap>> inserts(List<SellerSkuMap> maps) {
        Map<String, List<SellerSkuMap>> resulte1 = this.checkSku(maps, SkuEnmus.skuType.PLATFORM);
        List<SellerSkuMap> error_list1 = resulte1.get("ERROR_LIST");
        List<SellerSkuMap> right_list1 = resulte1.get("RIGHT_LIST");
        List<AliexpressOrderChild> aliexpressCall = new ArrayList<>();
        if (right_list1 != null && right_list1.size() > 0) {
            List<SKUMapMailRuleDTO> skuMapList = new ArrayList<SKUMapMailRuleDTO>();
            Set<String> keyList = new HashSet<String>();
            StringBuilder sb;
            for (SellerSkuMap m : right_list1) {
                try {
                    sellerSkuMapMapper.insert(m);
                    
                    switch (m.getPlatform()) {
                        case "aliexpress":
                            AliexpressOrderChild skuMap = new AliexpressOrderChild();
                            skuMap.setSkuCode(m.getPlatformSku());
                            skuMap.setPlSkuCode(m.getPlSku());
                            aliexpressCall.add(skuMap);
                            break;
                        default:
                            _log.info("暂时不考虑其他平台回调绑定");
                    }
                } catch (Exception e) {
                    _log.error("插入平台sku绑定异常", e);
                }

                sb = new StringBuilder();
                sb.append(m.getPlatform()).append("==").append(m.getAuthorizationId());
                keyList.add(sb.toString());
            }

            //根据平台和店铺ID分组，批量转单
            SKUMapMailRuleDTO mailRuleDTO = null;
            Map<String, String> skuRelationMap = null;
            for (String key : keyList) {
                mailRuleDTO = new SKUMapMailRuleDTO();
                skuRelationMap = new HashMap<String, String>();
                for (SellerSkuMap map2 : right_list1) {
                    sb = new StringBuilder();
                    sb.append(map2.getPlatform()).append("==").append(map2.getAuthorizationId());
                    if (key.equals(sb.toString())) {
                        skuRelationMap.put(map2.getPlatformSku(), map2.getPlSku());
                    }
                }

                mailRuleDTO.setPlatform(key.split("==")[0]);
                mailRuleDTO.setEmpowerID(Integer.parseInt(key.split("==")[1]));
                mailRuleDTO.setSkuRelationMap(skuRelationMap);
                skuMapList.add(mailRuleDTO);
            }

            if (skuMapList.size() > 0) {
            	new Thread() {
        			public void run() {
						try {
							triggerConverSYSService.triggerSKUMapAndMailRuleMate(skuMapList);
						} catch (ParseException e) {
							 _log.error("新增平台sku绑定，调用转单接口异常", e);
						}
        			}
        		}.start();
            }

        }
        HashMap<String, List<SellerSkuMap>> resulte = new HashMap<>();
        resulte.put(SkuEnmus.skuType.PLATFORM.getKey(), error_list1);
        return resulte;
    }

    @Override
    public List<SellerSkuMap> insertsNotError(List<SellerSkuMap> maps) {
        Map<String, List<SellerSkuMap>> resulte1 = this.checkSku(maps, SkuEnmus.skuType.PLATFORM);
        List<SellerSkuMap> error_list1 = resulte1.get("ERROR_LIST");
        if (error_list1 != null && error_list1.size() > 0)
            return error_list1;

        List<AliexpressOrderChild> aliexpressCall = new ArrayList<>();
        if (maps != null && maps.size() > 0) {
        	List<SKUMapMailRuleDTO> skuMapList = new ArrayList<SKUMapMailRuleDTO>();
            Set<String> keyList = new HashSet<String>();
            StringBuilder sb = null;
            for (SellerSkuMap m : maps) {
                try {
                    sellerSkuMapMapper.insert(m);
                    switch (m.getPlatform()) {
                        case "aliexpress":
                            AliexpressOrderChild skuMap = new AliexpressOrderChild();
                            skuMap.setSkuCode(m.getPlatformSku());
                            skuMap.setPlSkuCode(m.getPlSku());
                            aliexpressCall.add(skuMap);
                            break;
                        default:
                            _log.info("暂时不考虑其他平台回调绑定(不可错误的那个)");
                    }
                } catch (Exception e) {
                    _log.error("批量插入时可能从数据库中出来的错误(不可错误的那个)", e);
                }
                
                sb = new StringBuilder();
                sb.append(m.getPlatform()).append("==").append(m.getAuthorizationId());
                keyList.add(sb.toString());
            }
            
            //根据平台和店铺ID分组，批量转单
            SKUMapMailRuleDTO mailRuleDTO = null;
            Map<String, String> skuRelationMap = null;
            for (String key : keyList) {
                mailRuleDTO = new SKUMapMailRuleDTO();
                skuRelationMap = new HashMap<String, String>();
                for (SellerSkuMap map2 : maps) {
                    sb = new StringBuilder();
                    sb.append(map2.getPlatform()).append("==").append(map2.getAuthorizationId());
                    if (key.equals(sb.toString())) {
                        skuRelationMap.put(map2.getPlatformSku(), map2.getPlSku());
                    }
                }

                mailRuleDTO.setPlatform(key.split("==")[0]);
                mailRuleDTO.setEmpowerID(Integer.parseInt(key.split("==")[1]));
                mailRuleDTO.setSkuRelationMap(skuRelationMap);
                skuMapList.add(mailRuleDTO);
            }

            if (skuMapList.size() > 0) {
            	new Thread() {
        			public void run() {
						try {
							triggerConverSYSService.triggerSKUMapAndMailRuleMate(skuMapList);
						} catch (ParseException e) {
							 _log.error("新增平台sku绑定，调用转单接口异常", e);
						}
        			}
        		}.start();
            }
        }
        return error_list1;
    }


    private HashMap<String, List<SellerSkuMap>> checkSku(List<SellerSkuMap> maps, SkuEnmus.skuType skuType) {
        SellerSkuMap map;
        SellerSkuMap skuMap = new SellerSkuMap();
        ArrayList<SellerSkuMap> errorList = new ArrayList<>();
        ArrayList<SellerSkuMap> resultList = new ArrayList<>();
        for (SellerSkuMap m : maps) {
            skuMap.setPlatform(m.getPlatform());
            skuMap.setAuthorizationId(m.getAuthorizationId());
            if (skuType.getKey().equalsIgnoreCase(SkuEnmus.skuType.PLATFORM.getKey())) {
                skuMap.setPlatformSku(m.getPlatformSku());
                map = selectByEntry(skuMap);
                if (map != null) {
                    errorList.add(m);
                } else {
                    resultList.add(m);
                }
            } else {
                skuMap.setPlSku(m.getPlSku());
                map = selectByEntry(skuMap);
                if (map != null) {
                    errorList.add(m);
                } else {
                    resultList.add(m);
                }
            }
        }
        HashMap<String, List<SellerSkuMap>> resulte = new HashMap<>();
        resulte.put("ERROR_LIST", errorList);
        resulte.put("RIGHT_LIST", resultList);
        return resulte;
    }


    @Override
    public int updateByPrimaryKeySelective(SellerSkuMap sellerSkuMap) {
        SellerSkuMap skuMap = new SellerSkuMap();
        skuMap.setPlatform(sellerSkuMap.getPlatform());
        skuMap.setAuthorizationId(sellerSkuMap.getAuthorizationId());
        skuMap.setPlatformSku(sellerSkuMap.getPlatformSku());
        SellerSkuMap map = selectByEntry(skuMap);
        if (map != null && !map.getId().equals(sellerSkuMap.getId())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400.getCode(), sellerSkuMap.getPlatformSku() + " 平台SKU已存在！");
        }
        switch (sellerSkuMap.getPlatform()) {
            case "aliexpress":
                //List<String> unList = new ArrayList<>();
                //unList.add(map.getPlatformSku());
                List<AliexpressOrderChild> childList = new ArrayList<>(1);
                AliexpressOrderChild child = new AliexpressOrderChild();
                child.setSkuCode(sellerSkuMap.getPlatformSku());
                child.setPlSkuCode(sellerSkuMap.getPlSku());
                childList.add(child);
                break;
            default:
                _log.info("暂时不考虑其他平台回调绑定");
        }
        int count = super.updateByPrimaryKeySelective(sellerSkuMap);
        //调用转单
        ThreadPoolUtil.getInstance().execute(new Runnable() {  //异步进行转单
            @Override
            public void run() {
                try {
                    if (sellerSkuMap.getStatus() != null && sellerSkuMap.getStatus().intValue() != 2) {
                        Map<String, String> skuRelationMap = new HashMap<String, String>();
                        skuRelationMap.put(sellerSkuMap.getPlatformSku(), sellerSkuMap.getPlSku());
                        SKUMapMailRuleDTO mailRuleDTO = new SKUMapMailRuleDTO();
                        mailRuleDTO.setPlatform(sellerSkuMap.getPlatform());
                        mailRuleDTO.setEmpowerID(Integer.parseInt(sellerSkuMap.getAuthorizationId()));
                        mailRuleDTO.setSkuRelationMap(skuRelationMap);
                        List<SKUMapMailRuleDTO> skuMapList = new ArrayList<SKUMapMailRuleDTO>();
                        skuMapList.add(mailRuleDTO);
                        triggerConverSYSService.triggerSKUMapAndMailRuleMate(skuMapList);
                    }
                } catch (ParseException e) {
                    _log.error("批量转单异常", e);
                }
            }
        });
        return count;
    }

    /**
     * 平台SKU查询品连SKU
     *
     * @param platform
     * @param authorizationId
     * @param platformSku
     * @param sellerId
     * @return
     */
    @Override
    public SellerSkuMap getSellerSkuMapByPlatformSku(String platform, String authorizationId, String platformSku, String sellerId) {
        SellerSkuMap m = new SellerSkuMap();
        m.setPlatform(platform);
        m.setAuthorizationId(authorizationId);
        m.setPlatformSku(platformSku);
        m.setStatus(1);
        return this.selectByEntry(m);
    }

//    /**
//     * 根据授权ID和平台SKU查询品连SKU
//     *
//     * @param platform        卖家所属平台名称[amazon, eBay, wish, aliexpress]
//     * @param authorizationId 授权id
//     * @param platformSku     卖家平台商品sku
//     * @return
//     */
//    @Override
//    public String queryPlSku(String platform, String authorizationId, String platformSku, String sellerId) {
//        if (StringUtils.isBlank(platform) || StringUtils.isBlank(authorizationId) || StringUtils.isBlank(platformSku)) {
//            _log.error("转单日志 sku匹配传入参数不全 platform:　" + platform + "　authorizationId：" + authorizationId + "　platformSku：" + platformSku);
//            return null;
//        }
//        SellerSkuMap m = this.getSellerSkuMapByPlatformSku(platform, authorizationId, platformSku, sellerId);//TODO 查询本地映射表中映射数据
//        if (m == null || StringUtils.isBlank(m.getPlSku())) {//TODO 用品连SKU作为接口参数去查数据，解决卖家直接使用品连SKU去第三方平台登录的场景
//            m = new SellerSkuMap();
//            try {
//                SkuMapRule skuMapRule = skuMapRuleService.selectBySellerId(new SkuMapRule() {{
//                    setSellerId(sellerId);
//                }});
//                if (skuMapRule == null || skuMapRule.getStatus() == 1)
//                    return null;
//                String skuModel = this.createSkuModel(skuMapRule, platformSku);
//                if (StringUtils.isBlank(skuModel))
//                    return null;
//                String result = remoteCommodityService.getCommoditySpecBySku(skuModel);
//                String sku = JSONObject.parseObject(result).getJSONObject("data").getString("systemSku");
//                if (StringUtils.isNotBlank(sku))
//                    m.setPlSku(sku);
//            } catch (Exception e) {
//                _log.error(platform + "平台 转单日志 调用商品服务远程匹配sku异常 对应平台sku" + platformSku, e);
//                m.setPlSku(null);
//            }
//        }
//        return m.getPlSku();
//    }

//    /**
//     * 根据规则生成一个可能的sku
//     *
//     * @param skuMapRule  规则
//     * @param platformSku 平台sku
//     * @return 品连sku
//     */
//    private String createSkuModel(SkuMapRule skuMapRule, String platformSku) {
//        try {
//            String sku;
//            if (skuMapRule.getRuleType().equalsIgnoreCase(SkuEnmus.ruleType.splitByNum.getType())) {
//                if (StringUtils.isBlank(skuMapRule.getRule1()) || StringUtils.isBlank(skuMapRule.getRule2()))
//                    return null;
//                return platformSku.substring(Integer.valueOf(skuMapRule.getRule1()) - 1, Integer.valueOf(skuMapRule.getRule1()) + Integer.valueOf(skuMapRule.getRule2()) - 1);
//
//            } else if (skuMapRule.getRuleType().equalsIgnoreCase(SkuEnmus.ruleType.spliteByChar.getType())) {
//                if (StringUtils.isBlank(skuMapRule.getRule1()) && StringUtils.isBlank(skuMapRule.getRule2()))
//                    return null;
//                if (StringUtils.isNotBlank(skuMapRule.getRule1())) {
//                    int i = platformSku.indexOf(skuMapRule.getRule1());
//                    if (i == -1)
//                        return null;
//                    sku = platformSku.substring(i + 1);
//                } else {
//                    sku = platformSku;
//                }
//                if (StringUtils.isNotBlank(skuMapRule.getRule2()) && StringUtils.isNotBlank(sku)) {
//                    int i = sku.lastIndexOf(skuMapRule.getRule2());
//                    if (i <= 0)
//                        return null;
//                    return sku.substring(0, i);
//                }
//                return sku;
//            }
//        } catch (Exception e) {
//            _log.error("sku映射规则ID为：" + skuMapRule.getId() + " 映射sku : " + platformSku + "异常", e);
//            return null;
//        }
//        return null;
//    }

//    private StringBuilder getOrderIds(List<SysOrder> orders) {
//        StringBuilder sb = new StringBuilder();
//        if (orders != null && orders.size() > 0) {
//            orders.forEach(o -> sb.append(o.getSourceOrderId()).append(","));
//        }
//        return sb;
//    }
//
//    private StringBuilder getOrderSku(Set<String> skus) {
//        StringBuilder sb = new StringBuilder();
//        if (skus != null && skus.size() > 0) {
//            skus.forEach(sku -> sb.append(sku).append(","));
//        }
//        return sb;
//    }

//    /**
//     * 设置订单列表的基础参数
//     *
//     * @param order             订单
//     * @param commodityBaseList 商品列表
//     */
//    private void setOrderBaseData(SysOrder order, List<CommodityBase> commodityBaseList) {
//        order.setItemNum(0L);
//        order.setTotal(BigDecimal.valueOf(0));//订单总售价:预估物流费+商品金额(未加预估物流费)
//        order.setOrderAmount(BigDecimal.valueOf(0));//系统订单总价
//        order.setTotalBulk(BigDecimal.valueOf(0));
//        order.setTotalWeight(BigDecimal.valueOf(0));
//        order.setEstimateShipCost(BigDecimal.valueOf(0));//预估物流费
//        order.setProductCost(BigDecimal.valueOf(0));//产品成本
//        for (SysOrderDetail detail : order.getSysOrderInvoiceExportSkuDetailList()) {
//            try {
//                this.setDetail(detail, commodityBaseList);  //TODO sku存在判断
//
//                if (StringUtils.isNotBlank(detail.getSku())) {
//                    order.setItemNum(order.getItemNum() + detail.getSkuQuantity());
//                    order.setOrderAmount(order.getOrderAmount().add(this.totalBigDecimal(detail.getSkuQuantity(), detail.getItemPrice())));
//                    order.setTotalBulk(order.getTotalBulk().add(detail.getBulk().multiply(BigDecimal.valueOf(detail.getSkuQuantity()))));
//                    order.setTotalWeight(order.getTotalWeight().add(detail.getWeight().multiply(BigDecimal.valueOf(detail.getSkuQuantity()))));   //todo 重量这块还有单位处理
//                }
//            } catch (Exception e) {
//                _log.error("转单日志 设置订单项商品参数异常,平台id： " + order.getSourceOrderId(), e);
//            }
//        }
//        //TODO
//        order.setTotal(order.getOrderAmount());
//    }

//    /**
//     * 设置订单列表的订单映射结果
//     *
//     * @param platform 平台
//     * @param orders   订单列表
//     * @param plSKUSet 用来返回
//     */
//    private void orderMap(String platform, List<SysOrder> orders, HashSet<String> plSKUSet) {
//        boolean successFlag;
//        boolean failFlag;
//        List<String> skus;
//        Empower empower;
//        String oneEmpowByAccount;
//        String dataString;
//        String plSku;
//        for (SysOrder order : orders) {
//            empower = null;
//            successFlag = false;
//            failFlag = false;
//            skus = new ArrayList<>();
//            try {
//                if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.AMAZON.getPlatform())) {
//                    oneEmpowByAccount = remoteSellerService.findOneEmpowByAccount(this.getPlatform(platform),
//                            order.getPlatformSellerId(), order.getSite(), null);
//                    dataString = Utils.returnRemoteResultDataString(oneEmpowByAccount, "转单日志 卖家服务异常");
//                    empower = JSONObject.parseObject(dataString, Empower.class);
//                } else if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.E_BAU.getPlatform())) {
//                    oneEmpowByAccount = remoteSellerService.findOneEmpowByAccount(this.getPlatform(platform),
//                            order.getPlatformSellerAccount(), order.getSite(), null);
//                    dataString = Utils.returnRemoteResultDataString(oneEmpowByAccount, "转单日志 卖家服务异常");
//                    empower = JSONObject.parseObject(dataString, Empower.class);
//                } else if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.ALIEXPRESS.getPlatform())) {
//                    if (order.getEmpowerId() != null && order.getSellerPlId() != null && StringUtils.isNotBlank(order.getSellerPlAccount())) {
//                        empower = new Empower() {{
//                            setEmpowerid(order.getEmpowerId());
//                            setPinlianid(order.getSellerPlId());
//                            setPinlianaccount(order.getSellerPlAccount());
//                        }};
//                    } else {
//                        _log.error("速卖通转单授权信息传入不全");
//                    }
//                }
//                if (empower == null) {
//                    order.setConverSysStatus(OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue());
//                    order.getSysOrderInvoiceExportSkuDetailList().forEach(x -> x.setConverSysDetailStatus(OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue()));
//                    continue;
//                }
//                Integer rentstatus = empower.getRentstatus();
//                order.setShopType(rentstatus == 0 ? "PERSONAL" : "RENT");
//            } catch (Exception e) {
//                _log.error(platform + " 转单日志 查询卖家服务授权信息异常,平台订单id ：" + order.getSourceOrderId(), e);
//                order.setConverSysStatus(OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue());
//                order.getSysOrderInvoiceExportSkuDetailList().forEach(x -> x.setConverSysDetailStatus(OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue()));
//                continue;
//            }
//            for (SysOrderDetail detail : order.getSysOrderInvoiceExportSkuDetailList()) {
//                try {
//                    plSku = this.queryPlSku(platform, empower.getEmpowerid().toString(), detail.getPlatformSKU(), String.valueOf(empower.getPinlianid()));//TODO
//                    if (StringUtils.isNotBlank(plSku)) {
//                        plSKUSet.add(plSku);
//                        detail.setSku(plSku);
//                        successFlag = true;
//                        detail.setConverSysDetailStatus(OrderHandleEnum.ConverSysStatus.CONVER_SUCCESS.getValue());
//                        skus.add(plSku);
//                        order.setSellerPlAccount(empower.getPinlianaccount());
//                        order.setSellerPlId(empower.getPinlianid());
//                        order.setEmpowerId(empower.getEmpowerid());
//                    } else {
//                        failFlag = true;
//                        detail.setConverSysDetailStatus(OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue());
//                        _log.info(platform + " 转单日志 没有匹配到sku,平台订单id ：" + order.getSourceOrderId() + "平台sku：" + detail.getPlatformSKU());
//                    }
//                } catch (Exception e) {
//                    _log.error(platform + " 转单日志 设置sku异常,平台订单id ：" + order.getSourceOrderId() + "平台sku：" + detail.getPlatformSKU(), e);
//                    failFlag = true;
//                    detail.setConverSysDetailStatus(OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue());
//                }
//            }
//            if (successFlag && failFlag)
//                order.setConverSysStatus(OrderHandleEnum.ConverSysStatus.CONVER_PORTION_SUCCESS.getValue());
//            else if (successFlag)
//                order.setConverSysStatus(OrderHandleEnum.ConverSysStatus.CONVER_SUCCESS.getValue());
//            else if (failFlag)
//                order.setConverSysStatus(OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue());
//            order.setSkus(skus);
//            String supplyChainStr = remoteUserService.getSupplyChainByUserId("1", new ArrayList<Integer>() {{
//                this.add(order.getSellerPlId());
//            }});
//            String resultString = Utils.returnRemoteResultDataString(supplyChainStr, "用户服务异常");
//            if (StringUtils.isBlank(resultString)) continue;
//            JSONArray jsonArray = JSONObject.parseArray(resultString);
//            if (CollectionUtils.isEmpty(jsonArray)) continue;
//            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
//            if (StringUtils.isBlank(jsonObject.getString("supplyId")) || StringUtils.isEmpty(jsonObject.getString("supplyChainCompanyName")))
//                continue;
//            order.setSupplyChainCompanyId(Integer.valueOf(jsonObject.getString("supplyId")));
//            order.setSupplyChainCompanyName(jsonObject.getString("supplyChainCompanyName"));
//        }
//    }

    /**
     * 调用授权服务是转换平台格式
     *
     * @param platform 平台字符串格式
     * @return 平台Integer格式
     */
    @Override
    public Integer getPlatform(String platform) {
        if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.AMAZON.getPlatform()))
            return 2;
        if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.E_BAU.getPlatform()))
            return 1;
        if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.ALIEXPRESS.getPlatform()))
            return 3;
        return null;
    }

//    /**
//     * 将金额转换为人民币并计算总金额
//     *
//     * @param skuNum    商品数量
//     * @param itemPrice 商品单价：0.0000#CNY#1.00　　金额＃币种＃汇率
//     * @return 总金额
//     */
//    private BigDecimal totalBigDecimal(Integer skuNum, BigDecimal itemPrice) {
//        if (itemPrice == null || skuNum == null || skuNum == 0)
//            return BigDecimal.valueOf(0);
//        return itemPrice.multiply(BigDecimal.valueOf(skuNum));
//    }

//    /**
//     * 设置订单项参数
//     *
//     * @param detail            订单项对象
//     * @param commodityBaseList 商品列表
//     */
//    private void setDetail(SysOrderDetail detail, List<CommodityBase> commodityBaseList) {
//        if (StringUtils.isNotBlank(detail.getSku())) {
//            commodityBaseList.forEach(c -> {
//                if (c.getCommoditySpecList() != null && c.getCommoditySpecList().size() > 0) {
//                    c.getCommoditySpecList().forEach(s -> {
//                        if (s.getSystemSku().equalsIgnoreCase(detail.getSku())) {
//                            detail.setItemId(s.getId());
//                            detail.setItemPrice(s.getCommodityPriceUs() == null ? BigDecimal.valueOf(0) : s.getCommodityPriceUs());
//                            if (s.getPackingHeight() != null && s.getPackingLength() != null
//                                    && s.getPackingWidth() != null) {
//                                detail.setBulk(s.getPackingHeight().multiply(s.getPackingLength()).multiply(s.getPackingWidth()));
//                            } else {
//                                detail.setBulk(BigDecimal.valueOf(0));
//                            }
//                            detail.setWeight(s.getCommodityWeight() == null ? BigDecimal.valueOf(0) : s.getCommodityWeight());
//                            detail.setItemCost(s.getCommodityPriceUs() == null ? BigDecimal.valueOf(0) : s.getCommodityPriceUs());
//                            detail.setItemAttr(s.getCommoditySpec());
//                            detail.setItemUrl(s.getMasterPicture().split("\\|")[0]);//若主图有多个URL，只取第一个
//                            detail.setItemName(s.getCommodityNameCn());
//                            detail.setItemNameEn(s.getCommodityNameEn());
//                            detail.setSkuTitle(s.getCommodityNameCn());
//                            detail.setSupplierSkuTitle(s.getCommodityNameCn());
//                            detail.setSupplierId(c.getSupplierId());
//                            detail.setSupplierName(s.getSupplierName());
//                            detail.setSupplyChainCompanyId(s.getSupChainCompanyId());
//                            detail.setSupplyChainCompanyName(s.getSupChainCompanyName());
//                            detail.setFareTypeAmount(s.getFeePriceUs() == null ? (s.getFeeRate() == null ? null : "2#" + s.getFeeRate()) : "1#" + s.getFeePriceUs());
//                            detail.setSupplierSku(s.getSupplierSku());
//                            detail.setSupplierSkuPrice(s.getCommodityPriceUs() == null ? null : s.getCommodityPriceUs());
//                        }
//                    });
//                }
//            });
//        }
//    }

    @Override
    public SellerSkuMap selectByEntry(SellerSkuMap map) {
        return sellerSkuMapMapper.selectByEntry(map);
    }

    @Override
    public void discardMap(String plSku) {
        if (StringUtils.isBlank(plSku))
            return;
        SellerSkuMap sellerSkuMap = new SellerSkuMap();
        sellerSkuMap.setStatus(2);
        sellerSkuMap.setPlSku(plSku);
        sellerSkuMapMapper.updateStatusByPlSku(sellerSkuMap);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Page<SellerSkuMap> page(SellerSkuMap t) {
        List<SellerSkuMap> list = sellerSkuMapMapper.page(t);
        PageInfo<SellerSkuMap> pageInfo = new PageInfo(list);
        return new Page(pageInfo);
    }

    @Override
    public List<SellerSkuMap> findAll(SellerSkuMap model) {
        return sellerSkuMapMapper.page(model);
    }

    @Override
    public int deleteByPrimaryKey(Long primaryKey) {
        SellerSkuMap sellerSkuMap = this.sellerSkuMapMapper.selectByPrimaryKey(primaryKey);
        if (sellerSkuMap == null) {
            return 0;
        }
        switch (sellerSkuMap.getPlatform()) {
            case "aliexpress":
                List<String> unList = new ArrayList<>();
                unList.add(sellerSkuMap.getPlatformSku());
                break;
            default:
                _log.info("暂时不考虑其他平台回调绑定");
        }
        return this.sellerSkuMapMapper.deleteByPrimaryKey(primaryKey);
    }


    @Override
    public void deleteByPlSku(String plSku) {
        sellerSkuMapMapper.deleteByPlSku(plSku);
    }
}
