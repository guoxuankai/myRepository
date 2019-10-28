package com.rondaful.cloud.transorder.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.model.vo.freight.LogisticsCostVo;
import com.rondaful.cloud.common.model.vo.freight.SkuGroupVo;
import com.rondaful.cloud.common.model.vo.freight.SupplierGroupVo;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.transorder.constant.Constants;
import com.rondaful.cloud.transorder.entity.system.*;
import com.rondaful.cloud.transorder.remote.RemoteSupplierService;
import com.rondaful.cloud.transorder.service.CalculateFeeService;
import com.rondaful.cloud.transorder.utils.FastJsonUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author guoxuankai
 * @date 2019/10/8 17:33
 */
@Service
public class CalculateFeeServiceImpl implements CalculateFeeService {


    private static final Logger logger = LoggerFactory.getLogger(CalculateFeeServiceImpl.class);

    @Autowired
    private RemoteSupplierService remoteSupplierService;

    @Override
    public void calculateFee(SysOrderDTO sysOrderDTO) {
        SysOrderPackageDTO sysOrderPackageDTO = sysOrderDTO.getPackages().get(0);
        if (sysOrderPackageDTO.getDeliveryWarehouseId() == null || sysOrderPackageDTO.getDeliveryWarehouseId() == -1 || sysOrderPackageDTO.getDeliveryMethodCode() == null) {
            logger.info("平台订单{}不存在仓库id和邮寄方式，放弃运费计算", sysOrderDTO.getSourceOrderId());
            return;
        }
        LogisticsCostVo logisticsCostVo = assembleGetEstimateFreightParam(sysOrderDTO);
        LogisticsCostVo logisticsCostVoResult = getEstimateFreight(logisticsCostVo);
        setPackageInfo(logisticsCostVoResult, sysOrderDTO);

    }


    private LogisticsCostVo assembleGetEstimateFreightParam(SysOrderDTO sysOrderDTO) {

        SysOrderReceiveAddress sysOrderReceiveAddress = sysOrderDTO.getSysOrderReceiveAddress();
        SysOrderPackageDTO sysOrderPackageDTO = sysOrderDTO.getPackages().get(0);

        Integer searchType = getByLogisticsStrategy(sysOrderPackageDTO.getLogisticsStrategy());
        String deliveryMethodCode = sysOrderPackageDTO.getDeliveryMethodCode();

//        if (null != searchType && null != deliveryMethodCode) {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "物流方式code和搜索条件不能同时存在");
//        }
        List<SkuGroupVo> skuGroupVoList = new ArrayList<>();
//        for (SysOrderPackageDetail sysOrderPackageDetail : sysOrderPackageDTO.getSysOrderPackageDetails()) {
//            SkuGroupVo skuGroupVo = new SkuGroupVo(Long.valueOf(sysOrderPackageDetail.getSupplierId()),
//                    sysOrderPackageDetail.getSku(), sysOrderPackageDetail.getSkuQuantity(),
//                    BigDecimal.ZERO, sysOrderPackageDetail.getFreeFreight());
//            skuGroupVoList.add(skuGroupVo);
//        }
        for (SysOrderDetail detail : sysOrderDTO.getSysOrderDetails()) {
            SkuGroupVo skuGroupVo = new SkuGroupVo(detail.getSupplierId(),
                    detail.getSku(), detail.getSkuQuantity(),
                    BigDecimal.ZERO, detail.getFreeFreight()?1:0);
            skuGroupVoList.add(skuGroupVo);
        }
        LogisticsCostVo logisticsCostVo = getLogisticsBySkuGroup(skuGroupVoList);
        logisticsCostVo.setPlatformType(String.valueOf(sysOrderDTO.getPlatformType()));
        logisticsCostVo.setWarehouseId(String.valueOf(sysOrderPackageDTO.getDeliveryWarehouseId()));
        logisticsCostVo.setCountryCode(sysOrderReceiveAddress.getShipToCountry());
        logisticsCostVo.setPostCode(sysOrderReceiveAddress.getShipToPostalCode());
        logisticsCostVo.setSearchType(searchType);
        logisticsCostVo.setLogisticsCode(deliveryMethodCode);
        logisticsCostVo.setCity(sysOrderReceiveAddress.getShipToCity());
        logisticsCostVo.setStoreId(sysOrderDTO.getPlatformShopId());
        logisticsCostVo.setHandOrder(sysOrderDTO.getHandOrder());
        return logisticsCostVo;
    }

    /**
     * 根据Sku信息,获取物流计算对象
     *
     * @param list
     * @return
     */
    public static LogisticsCostVo getLogisticsBySkuGroup(List<SkuGroupVo> list) {

        SupplierGroupVo supplier = new SupplierGroupVo();
        supplier.setItems(list);

        List<SupplierGroupVo> suppliers = new ArrayList<>();
        suppliers.add(supplier);

        LogisticsCostVo entity = new LogisticsCostVo();
        entity.setSellers(suppliers);
        entity.setSupplier(getSupplierGroup(list));
        return entity;
    }

    /**
     * 根据供应商ID对sku进行分组
     *
     * @param list 分组对象
     * @return 供应商分组
     */
    public static List<SupplierGroupVo> getSupplierGroup(List<SkuGroupVo> list) {
        Map<Long, List<SkuGroupVo>> map = getSkuGroupMap(list);
        List<SupplierGroupVo> suppliers = new ArrayList<>();
        for (Long key : map.keySet()) {
            SupplierGroupVo groupVo = new SupplierGroupVo();
            groupVo.setSupplierId(key);
            groupVo.setItems(map.get(key));
            suppliers.add(groupVo);
        }
        return suppliers;
    }

    /**
     * 根据供应商ID对sku进行分组
     *
     * @param list 分组对象
     * @return map key 为供应商ID,list分组后的对象
     */
    public static Map<Long, List<SkuGroupVo>> getSkuGroupMap(List<SkuGroupVo> list) {
        Map<Long, List<SkuGroupVo>> map = new HashMap<>();
        for (SkuGroupVo item : list) {
            if (!map.containsKey(item.getSupplierId())) {
                map.put(item.getSupplierId(), new ArrayList<>());
            }
            map.get(item.getSupplierId()).add(item);
        }
        return map;
    }

    private Integer getByLogisticsStrategy(String logisticsStrategy) {
        if ("cheapest".equals(logisticsStrategy)) {
            return 1;
        } else if ("integrated_optimal".equals(logisticsStrategy)) {
            return 2;
        } else if ("fastest".equals(logisticsStrategy)) {
            return 3;
        }
        return  null;
//        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "物流类型没有找到");
    }

    public LogisticsCostVo getEstimateFreight(LogisticsCostVo logisticsCostVo) {
        logger.info("获取预估物流费入参{}",FastJsonUtils.toJsonString(logisticsCostVo));
        String result = remoteSupplierService.queryFreightByLogisticsCode(logisticsCostVo);
        String data = Utils.returnRemoteResultDataString(result, "获取预估物流费时，调用供应商服务异常");
        if (StringUtils.isBlank(data)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "获取到的预估物流费为空");
        }
        LogisticsCostVo logisticsCostVoResult = JSONObject.parseObject(data, LogisticsCostVo.class);
        return logisticsCostVoResult;

    }


    private void setPackageInfo(LogisticsCostVo logisticsCostVo, SysOrderDTO sysOrderDTO) {


        SysOrderPackageDTO sysOrderPackageDTO = sysOrderDTO.getPackages().get(0);

        List<SysOrderPackageDetail> sysOrderPackageDetails = sysOrderPackageDTO.getSysOrderPackageDetails();

        BigDecimal estimateShipCost = BigDecimal.ZERO;

        for (SysOrderPackageDetail sysOrderPackageDetail : sysOrderPackageDetails) {
            String sku = sysOrderPackageDetail.getSku();

            for (SupplierGroupVo supplier : logisticsCostVo.getSupplier()) {
                List<SkuGroupVo> items = supplier.getItems();
                for (SkuGroupVo item : items) {
                    if (sku.equals(item.getSku())) {
                        BigDecimal upwardCost = item.getSkuPlCost();
                        upwardCost = upwardCost.add(upwardCost.multiply(new BigDecimal(Constants.ADDITIONAL_FREIGHT_RATE)));
                        upwardCost = calculateMoney(upwardCost, false);
                        sysOrderPackageDetail.setSupplierShipFee(upwardCost);
                        break;
                    }
                }
            }

            for (SupplierGroupVo seller : logisticsCostVo.getSellers()) {
                List<SkuGroupVo> items = seller.getItems();
                for (SkuGroupVo item : items) {
                    if (sku.equals(item.getSku())) {
                        BigDecimal upwardCost = item.getSkuPlCost();
                        upwardCost = upwardCost.add(upwardCost.multiply(new BigDecimal(Constants.ADDITIONAL_FREIGHT_RATE)));
                        upwardCost = calculateMoney(upwardCost, false);
                        sysOrderPackageDetail.setSellerShipFee(upwardCost);
                        if (item.getFreeFreight().equals(Constants.SysOrder.NOT_FREE_FREIGHT)) {
                            estimateShipCost = estimateShipCost.add(upwardCost.multiply(new BigDecimal(item.getSkuNumber())));
                        }
                        break;
                    }
                }
            }

            sysOrderPackageDTO.setCalculateFeeInfo(FastJsonUtils.toJsonString(logisticsCostVo));
            sysOrderPackageDTO.setEstimateShipCost(estimateShipCost);
            sysOrderDTO.setEstimateShipCost(estimateShipCost);
            sysOrderDTO.setTotal(sysOrderDTO.getOrderAmount().add(estimateShipCost));


        }

    }


    /**
     * 订单金额计算
     *
     * @param money                参与计算的金额
     * @param isPlPayToOtherOrShow 品连向外付钱or作展示用：true（直接截取小数点后两位）；品连向外收钱的：false（超过小数点后两位，第三位不为0向前进1）
     * @return
     */
    public static BigDecimal calculateMoney(BigDecimal money, Boolean isPlPayToOtherOrShow) {
        if (null == money) {
            return null;
        }

        if (money.compareTo(new BigDecimal("0")) == 0) {
            return money.setScale(2, BigDecimal.ROUND_DOWN);
        }
        BigDecimal OneHundred = new BigDecimal(100);
        String[] moneyStrArray = org.apache.commons.lang3.StringUtils.split(money.toString(), ".");
        if (moneyStrArray.length < 2) {
            // 只有个位数
            return money.multiply(OneHundred).divide(OneHundred, 2, BigDecimal.ROUND_HALF_UP);
        }

        // 小数位
        String decimalPlaceStr = moneyStrArray[1];
        if (decimalPlaceStr.length() == 1) {
            // 只有十分位
            return money.multiply(OneHundred).divide(OneHundred, 2, BigDecimal.ROUND_HALF_UP);
        }

        if (decimalPlaceStr.length() == 2) {
            return money;
        }

//        // 截取百分位之后的数据
//        String afterPercentile = decimalPlaceStr.substring(1,decimalPlaceStr.length());
//        if (new BigDecimal(afterPercentile).compareTo(new BigDecimal("0")) == 0) {
//            // 百分位之后都是0
//            return money.multiply(OneHundred).divide(OneHundred, 2, BigDecimal.ROUND_HALF_UP);
//        }

        if (isPlPayToOtherOrShow) {  //向外付钱或者作展示用
            return money.setScale(2, BigDecimal.ROUND_DOWN);  //小数点后超过两位的直接截取两位
        } else {  //向外收钱
            return money.setScale(2, BigDecimal.ROUND_UP);  //超过2位 小数点后第三位不为0的进1
        }
    }


}
