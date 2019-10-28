package com.rondaful.cloud.transorder.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.transorder.entity.ConvertOrderVO;
import com.rondaful.cloud.transorder.entity.SupplyChainCompany;
import com.rondaful.cloud.transorder.entity.commodity.CommodityBase;
import com.rondaful.cloud.transorder.entity.commodity.SkuInventoryVo;
import com.rondaful.cloud.transorder.entity.dto.GetCommodityBySkuListDTO;
import com.rondaful.cloud.transorder.entity.system.*;
import com.rondaful.cloud.transorder.entity.vo.QuerySkuMapForOrderVo;
import com.rondaful.cloud.transorder.entity.vo.SkuMapBind;
import com.rondaful.cloud.transorder.enums.LogisticsStrategyCovertToLogisticsLogisticsType;
import com.rondaful.cloud.transorder.mapper.*;
import com.rondaful.cloud.transorder.remote.RemoteCommodityService;
import com.rondaful.cloud.transorder.remote.RemoteUserService;
import com.rondaful.cloud.transorder.service.CalculateFeeService;
import com.rondaful.cloud.transorder.service.MatchingLogisticsRuleService;
import com.rondaful.cloud.transorder.service.TransferContext;
import com.rondaful.cloud.transorder.service.TransferService;
import com.rondaful.cloud.transorder.utils.OrderUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransferServiceImpl implements TransferService {

    private static final Logger logger = LoggerFactory.getLogger(TransferServiceImpl.class);

    @Autowired
    private RemoteCommodityService remoteCommodityService;

    @Autowired
    private MatchingLogisticsRuleService matchingLogisticsRuleService;

    @Autowired
    private CalculateFeeService calculateFeeService;

    @Autowired
    private SysOrderMapper sysOrderMapper;

    @Autowired
    private SysOrderDetailMapper sysOrderDetailMapper;

    @Autowired
    private SysOrderReceiveAddressMapper sysOrderReceiveAddressMapper;

    @Autowired
    private SysOrderPackageMapper sysOrderPackageMapper;

    @Autowired
    private SysOrderPackageDetailMapper sysOrderPackageDetailMapper;
    @Autowired
    private RemoteUserService remoteUserService;


    @Override
    public List<ConvertOrderVO> transfer(List<String> orderIdList, TransferContext context) {
        List<ConvertOrderVO> convertOrderVOList = new ArrayList<>();
        List<SysOrderDTO> sysOrders = context.assembleData(orderIdList);
        if (CollectionUtils.isEmpty(sysOrders)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询不到存在的订单");
        }
        for (SysOrderDTO sysOrder : sysOrders) {
            sysOrder.setHandOrder(0);
            try {
                skuMap(sysOrder);
                setCommodityData(sysOrder);
                calculateFeeService.calculateFee(sysOrder);
                setPackageData(sysOrder);
                sysOrder.setConvertible((byte) 1);
                sysOrder.setConverSysStatus((byte) 1);
                sysOrder.setOrderDeliveryStatus((byte) 1);
                sysOrder.setIsAfterSaleOrder((byte) 0);
                updateData(sysOrder, context);
            } catch (GlobalException e) {
                sysOrder.setConvertible((byte) 2);
                sysOrder.setFailureReason(e.getMessage());
                logger.error("平台订单{}转单异常:", sysOrder.getSourceOrderId(), e);
            } catch (Exception e) {
                sysOrder.setConvertible((byte) 2);
                sysOrder.setFailureReason("系统异常");
                logger.error("平台订单{}转单异常:", sysOrder.getSourceOrderId(), e);
            }
            ConvertOrderVO convertOrderVO = new ConvertOrderVO(sysOrder.getSourceOrderId(), sysOrder.getConvertible(), sysOrder.getFailureReason());
            convertOrderVOList.add(convertOrderVO);

        }

        return convertOrderVOList;
    }


    private void updateData(SysOrderDTO sysOrderDTO, TransferContext context) {
        insertSystemData(sysOrderDTO);
        context.updateConverStatus(sysOrderDTO.getSourceOrderId(), sysOrderDTO.getConvertible(), sysOrderDTO.getFailureReason());
    }

    private void insertSystemData(SysOrderDTO sysOrderDTO) {
        SysOrder sysOrder = new SysOrder();
        SysOrderPackage sysOrderPackage = new SysOrderPackage();
        SysOrderPackageDTO sysOrderPackageDTO = sysOrderDTO.getPackages().get(0);
        BeanUtils.copyProperties(sysOrderDTO, sysOrder);
        BeanUtils.copyProperties(sysOrderPackageDTO, sysOrderPackage);
        sysOrderMapper.insertSelective(sysOrder);
//        sysOrderDetailMapper.insertBatch(sysOrderDTO.getSysOrderDetails());
        List<SysOrderDetail> sysOrderDetails = sysOrderDTO.getSysOrderDetails();
        for (SysOrderDetail sysOrderDetail : sysOrderDetails) {
            sysOrderDetailMapper.insertSelective(sysOrderDetail);
        }
        sysOrderReceiveAddressMapper.insertSelective(sysOrderDTO.getSysOrderReceiveAddress());
        sysOrderPackageMapper.insertSelective(sysOrderPackage);
//        sysOrderPackageDetailMapper.insertBatch(sysOrderPackageDTO.getSysOrderPackageDetails());
        List<SysOrderPackageDetail> sysOrderPackageDetails = sysOrderPackageDTO.getSysOrderPackageDetails();
        for (SysOrderPackageDetail sysOrderPackageDetail : sysOrderPackageDetails) {
            sysOrderPackageDetailMapper.insertSelective(sysOrderPackageDetail);
        }

    }


    /**
     * 判断商品是否可售、下架，设置商品数据
     *
     * @param sysOrder
     * @return
     */
    private void setCommodityData(SysOrderDTO sysOrder) {

        List<CommodityBase> commodityBases = getCommodityBySystemSku(sysOrder);

        for (CommodityBase commodityBase : commodityBases) {
            if (commodityBase.getCommoditySpecList().get(0).getState() != 3 || commodityBase.getCanSale() != 1) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "该订单存在不可售或者未上架商品");
            }
        }

        // 匹配订单规则
        matchingLogisticsRuleService.matchingLogisticsRule(sysOrder);

        List<SysOrderDetail> sysOrderDetails = sysOrder.getSysOrderDetails();
        Map<String, SysOrderDetail> sysOrderDetailMap = sysOrderDetails.stream().collect(Collectors.toMap(SysOrderDetail::getSku, v -> v));
        SysOrderPackageDTO packageDTO = sysOrder.getPackages().get(0);

        BigDecimal amount = new BigDecimal(0);

        for (CommodityBase commodityBase : commodityBases) {

            commodityBase.getCommoditySpecList().forEach(commoditySpec -> {

                SysOrderDetail detail = sysOrderDetailMap.get(commoditySpec.getSystemSku());
                if (detail != null) {
                    detail.setItemId(commoditySpec.getId());
                    //TODO 修改为分仓定价
                    detail.setItemPrice(commoditySpec.getCommodityPriceUs() == null ? BigDecimal.valueOf(0) : commoditySpec.getCommodityPriceUs());
                    List<SkuInventoryVo> inventoryList = commoditySpec.getInventoryList();

                    if (packageDTO != null && packageDTO.getDeliveryWarehouseId() != null) {
                        inventoryList = inventoryList.stream().filter(inventory -> String.valueOf(inventory.getWarehouseId()).equals(String.valueOf(packageDTO.getDeliveryWarehouseId()))).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(inventoryList)) {
                            BigDecimal price = new BigDecimal(inventoryList.get(0).getWarehousePrice());
                            detail.setItemPrice(price);
                        }

                    }

                    if (commoditySpec.getPackingHeight() != null && commoditySpec.getPackingLength() != null
                            && commoditySpec.getPackingWidth() != null) {
                        detail.setBulk(commoditySpec.getPackingHeight().multiply(commoditySpec.getPackingLength()).multiply(commoditySpec.getPackingWidth()));
                    } else {
                        detail.setBulk(BigDecimal.valueOf(0));
                    }
                    detail.setWeight(commoditySpec.getCommodityWeight() == null ? BigDecimal.valueOf(0) : commoditySpec.getCommodityWeight());
                    detail.setItemCost(commoditySpec.getCommodityPriceUs() == null ? BigDecimal.valueOf(0) : commoditySpec.getCommodityPriceUs());
                    detail.setItemAttr(commoditySpec.getCommoditySpec());
                    // 若主图有多个URL，只取第一个
                    detail.setItemUrl(StringUtils.split(commoditySpec.getMasterPicture(), "|")[0]);
                    detail.setItemName(commoditySpec.getCommodityNameCn());
                    detail.setItemNameEn(commoditySpec.getCommodityNameEn());
                    detail.setSkuTitle(commoditySpec.getCommodityNameCn());
                    detail.setSupplierSkuTitle(commoditySpec.getCommodityNameCn());
                    detail.setSupplierId(commodityBase.getSupplierId());
                    detail.setSupplierName(commoditySpec.getSupplierName());
                    detail.setSupplyChainCompanyId(commoditySpec.getSupChainCompanyId());
                    detail.setSupplyChainCompanyName(commoditySpec.getSupChainCompanyName());
                    detail.setFareTypeAmount(commoditySpec.getFeePriceUs() == null ? (commoditySpec.getFeeRate() == null ? null : "2#" + commoditySpec.getFeeRate()) : "1#" + commoditySpec.getFeePriceUs());
                    detail.setSupplierSku(commoditySpec.getSupplierSku());
                    detail.setSupplierSkuPrice(commoditySpec.getCommodityPriceUs() == null ? null : commoditySpec.getCommodityPriceUs());
                    detail.setFreeFreight(commoditySpec.getFreeFreight() == 1 ? true : false);

                    // 商品成本总价
                    amount.add(detail.getItemCost().multiply(BigDecimal.valueOf(detail.getSkuQuantity())));

                }


            });

        }
        sysOrder.setOrderAmount(amount);


    }

    private List<CommodityBase> getCommodityBySystemSku(SysOrderDTO sysOrder) {
        GetCommodityBySkuListDTO getCommodityBySkuListDTO = new GetCommodityBySkuListDTO();
        getCommodityBySkuListDTO.setSellerId(sysOrder.getSellerPlId());
        getCommodityBySkuListDTO.setSystemSkuList(sysOrder.getSkus());
        String result = remoteCommodityService.getCommodityBySkuList(getCommodityBySkuListDTO);
        String data = Utils.returnRemoteResultDataString(result, "根据品连sku查询商品信息时，调用商品服务异常");
        List<CommodityBase> commodityBases = JSONObject.parseArray(data, CommodityBase.class);
        if (CollectionUtils.isEmpty(commodityBases)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "根据品连sku查询订单当中的商品信息为空");
        }
        return commodityBases;
    }

    /**
     * 查询sku映射，组装数据
     *
     * @param sysOrder
     */
    private void skuMap(SysOrderDTO sysOrder) {

        List<SysOrderDetail> sysOrderDetails = sysOrder.getSysOrderDetails();
        List<QuerySkuMapForOrderVo> querySkuMapForOrderVos = new ArrayList();

        for (SysOrderDetail sysOrderDetail : sysOrderDetails) {
            QuerySkuMapForOrderVo querySkuMapForOrderVo = new QuerySkuMapForOrderVo();
            querySkuMapForOrderVo.setSourceOrderId(sysOrder.getSourceOrderId());
            querySkuMapForOrderVo.setSourceOrderLineItemId(sysOrderDetail.getSourceOrderLineItemId());
            querySkuMapForOrderVo.setPlatform(sysOrder.getPlatformName());
            querySkuMapForOrderVo.setPlatformSku(sysOrderDetail.getSourceSku());
            querySkuMapForOrderVo.setSellerId(String.valueOf(sysOrder.getSellerPlId()));
            querySkuMapForOrderVo.setAuthorizationId(sysOrder.getEmpowerId());
            querySkuMapForOrderVos.add(querySkuMapForOrderVo);
        }

        String skuMapResult = remoteCommodityService.getSkuMapForOrder(querySkuMapForOrderVos);
        String skuMapData = Utils.returnRemoteResultDataString(skuMapResult, "查询sku映射时，调用商品服务异常");
        List<QuerySkuMapForOrderVo> mapForOrderVos = JSONObject.parseArray(skuMapData, QuerySkuMapForOrderVo.class);
        Map<String, List<QuerySkuMapForOrderVo>> collect = mapForOrderVos.stream().collect(Collectors.groupingBy(vo -> vo.getSourceOrderLineItemId()));

        long count = mapForOrderVos.stream().filter(vo -> CollectionUtils.isNotEmpty(vo.getSkuBinds())).count();

        if (count < 1) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "该订单下所有商品都不存在sku映射");
        } else {
            for (SysOrderDetail sysOrderDetail : sysOrderDetails) {
                List<SkuMapBind> skuBinds = collect.get(sysOrderDetail.getSourceOrderLineItemId()).get(0).getSkuBinds();
                if (CollectionUtils.isEmpty(skuBinds)) {
                    sysOrderDetail.setBindStatus("unbind");
                } else {
                    sysOrderDetail.setBindStatus("bind");
                    String systemSku = skuBinds.get(0).getSystemSku();
                    sysOrderDetail.setSku(systemSku);
                    sysOrder.getSkus().add(systemSku);
                    BigDecimal skuNum = new BigDecimal(skuBinds.get(0).getSkuNum()).multiply(new BigDecimal(sysOrderDetail.getSkuQuantity()));
                    sysOrderDetail.setSkuQuantity(Integer.valueOf(String.valueOf(skuNum)));
                }
            }
        }
    }

    private void setPackageData(SysOrderDTO sysOrder) {

        SysOrderPackageDTO sysOrderPackageDTO = sysOrder.getPackages().get(0);

        sysOrderPackageDTO.setSysOrderId(sysOrder.getSysOrderId());
        sysOrderPackageDTO.setOrderTrackId(OrderUtils.getPLTrackNumber());
        sysOrderPackageDTO.setSourceOrderId(sysOrder.getSourceOrderId());


        List<SysOrderPackageDetail> sysOrderPackageDetails = sysOrderPackageDTO.getSysOrderPackageDetails();
        List<SysOrderDetail> sysOrderDetails = sysOrder.getSysOrderDetails();

        for (SysOrderDetail sysOrderDetail : sysOrderDetails) {

            sysOrderDetail.setOrderLineItemId(OrderUtils.getPLOrderItemNumber());
            sysOrderDetail.setSysOrderId(sysOrder.getSysOrderId());

            SysOrderPackageDetail sysOrderPackageDetail = new SysOrderPackageDetail();
            sysOrderPackageDetail.setSku(sysOrderDetail.getSku());
            sysOrderPackageDetail.setSkuQuantity(sysOrderDetail.getSkuQuantity());
            sysOrderPackageDetail.setSkuCost(sysOrderDetail.getItemCost());
            sysOrderPackageDetail.setSkuUrl(sysOrderDetail.getItemUrl());
            sysOrderPackageDetail.setSkuName(sysOrderDetail.getItemName());
            sysOrderPackageDetail.setSkuNameEn(sysOrderDetail.getItemNameEn());
            sysOrderPackageDetail.setSkuAttr(sysOrderDetail.getItemAttr());
            sysOrderPackageDetail.setSourceSku(sysOrderDetail.getSourceSku());
            sysOrderPackageDetail.setWeight(sysOrderDetail.getWeight());
            sysOrderPackageDetail.setBulk(sysOrderDetail.getBulk());
            sysOrderPackageDetail.setFreeFreight(sysOrderDetail.getFreeFreight() == true ? 1 : 0);
            sysOrderPackageDetail.setSupplierId(sysOrderDetail.getSupplierId().intValue());
            sysOrderPackageDetail.setSupplierName(sysOrderDetail.getSupplierName());
            sysOrderPackageDetail.setBindStatus(sysOrderDetail.getBindStatus());
            sysOrderPackageDetail.setOrderTrackId(sysOrderPackageDTO.getOrderTrackId());
            sysOrderPackageDetail.setSourceOrderLineItemId(sysOrderDetail.getSourceOrderLineItemId());

            sysOrderPackageDetails.add(sysOrderPackageDetail);

        }


        sysOrderPackageDTO.setSysOrderPackageDetails(sysOrderPackageDetails);

        if (StringUtils.isBlank(sysOrderPackageDTO.getLogisticsStrategy())) {
            sysOrderPackageDTO.setLogisticsStrategy(LogisticsStrategyCovertToLogisticsLogisticsType.INTEGRATED_OPTIMAL.getLogisticsStrategy());
        }


        setSupplyChainInfo(sysOrder);


        //系统折扣运费？？？
        // 支付利息????
        // 毛利？？
        // 毛利润？？？

        sysOrder.setCreateBy("SYSTEM2");
        sysOrder.setUpdateBy("SYSTEM2");


    }

    private void setSupplyChainInfo(SysOrderDTO sysOrder) {
        List<Integer> list = new ArrayList<>();
        list.add(sysOrder.getSellerPlId());
        String result = remoteUserService.getSupplyChainByUserId("1", list);
        String data = Utils.returnRemoteResultDataString(result, "查询供应商信息时，调用用户服务异常");
        List<SupplyChainCompany> supplyChainCompanies = JSONObject.parseArray(data, SupplyChainCompany.class);
        if (CollectionUtils.isEmpty(supplyChainCompanies)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "供应商信息为空");
        }
        String supplyChainCompanyName = supplyChainCompanies.get(0).getSupplyChainCompanyName();
        String supplyId = supplyChainCompanies.get(0).getSupplyId();
        if (StringUtils.isBlank(supplyChainCompanyName) || StringUtils.isBlank(supplyId)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "供应链公司名称或者id为空");
        }
        sysOrder.setSupplyChainCompanyName(supplyChainCompanyName);
        sysOrder.setSupplyChainCompanyId(Integer.valueOf(supplyId));

    }


}


