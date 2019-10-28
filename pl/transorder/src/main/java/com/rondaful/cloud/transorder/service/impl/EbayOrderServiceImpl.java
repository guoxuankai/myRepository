package com.rondaful.cloud.transorder.service.impl;

import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.transorder.entity.ebay.EbayOrder;
import com.rondaful.cloud.transorder.entity.ebay.EbayOrderDetail;
import com.rondaful.cloud.transorder.entity.ebay.EbayOrderStatus;
import com.rondaful.cloud.transorder.entity.system.SysOrderDTO;
import com.rondaful.cloud.transorder.entity.system.SysOrderDetail;
import com.rondaful.cloud.transorder.entity.system.SysOrderReceiveAddress;
import com.rondaful.cloud.transorder.enums.OrderSourceEnum;
import com.rondaful.cloud.transorder.mapper.EbayOrderMapper;
import com.rondaful.cloud.transorder.service.EbayOrderService;
import com.rondaful.cloud.transorder.utils.OrderUtils;
import com.rondaful.cloud.transorder.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guoxuankai
 * @date 2019/9/21 9:34
 */
@Service
public class EbayOrderServiceImpl extends BaseServiceImpl<EbayOrder> implements EbayOrderService {

    @Autowired
    private EbayOrderMapper ebayOrderMapper;


    @Override
    public List<SysOrderDTO> assembleData(List<String> orderIds) {

        List<EbayOrder> ebayOrders = ebayOrderMapper.getsByOrderIds(orderIds);
        List<SysOrderDTO> sysOrderDTOList = new ArrayList<>();
        for (EbayOrder ebayOrder : ebayOrders) {
            SysOrderDTO sysOrder = new SysOrderDTO();
            sysOrder.setSysOrderId(OrderUtils.getPLOrderNumber());
            sysOrder.setSourceOrderId(ebayOrder.getOrderId());
            sysOrder.setOrderSource(Byte.valueOf(OrderSourceEnum.CONVER_FROM_EBAY.getValue()));
            sysOrder.setPlatformTotalPrice(OrderUtils.stringToBigDecimal(ebayOrder.getTotal()));
            sysOrder.setCreatedTime(Utils.strToDateLong(ebayOrder.getCreatedTime()));
            sysOrder.setOrderTime(ebayOrder.getCreatedTime());
            sysOrder.setPlatformSellerId(ebayOrder.getSellerUserId());
            sysOrder.setRecordNumber(ebayOrder.getRecordNumber());
            sysOrder.setBuyerUserId(ebayOrder.getBuyerUserId());


            // ??订单货款？？
            sysOrder.setCommoditiesAmount(OrderUtils.stringToBigDecimal(ebayOrder.getTotal()));

            // 买家email
            // 发货时间
            sysOrder.setShippingServiceCost(OrderUtils.stringToBigDecimal(ebayOrder.getShippingServiceCost()));
            // 付款金额
            // 全球配送订单发货的唯一编号
            // 订单最近修改时间
            // 卖家email
            sysOrder.setBuyerCheckoutMessage(ebayOrder.getBuyerCheckoutMessage());

            SysOrderReceiveAddress sysOrderReceiveAddress = new SysOrderReceiveAddress();
            sysOrderReceiveAddress.setShipToName(ebayOrder.getName());
            sysOrderReceiveAddress.setShipToPhone(ebayOrder.getPhone());
            sysOrderReceiveAddress.setShipToCountry(ebayOrder.getCountry());
            sysOrderReceiveAddress.setShipToCountryName(ebayOrder.getCountryName());
            sysOrderReceiveAddress.setShipToState(ebayOrder.getStateOrProvince());
            sysOrderReceiveAddress.setShipToCity(ebayOrder.getCityName());
            sysOrderReceiveAddress.setShipToAddrStreet1(ebayOrder.getStreet1());
            sysOrderReceiveAddress.setShipToAddrStreet2(ebayOrder.getStreet2());
            sysOrderReceiveAddress.setShipToPostalCode(ebayOrder.getPostalCode());
            sysOrderReceiveAddress.setSysOrderId(sysOrder.getSysOrderId());
            sysOrder.setSysOrderReceiveAddress(sysOrderReceiveAddress);


            List<EbayOrderDetail> childs = ebayOrder.getChilds();


            boolean flag = true;
            long deliverDeadline = 0;
            String deliverDeadlineStr = null;

            for (EbayOrderDetail child : childs) {
                SysOrderDetail detail = new SysOrderDetail();
                detail.setSourceOrderId(child.getOrderId());
                detail.setSourceOrderLineItemId(child.getOrderLineItemId());
                // 平台交易号
                // 平台sku价格
                detail.setSkuQuantity(child.getQuantityPurchased());
                // 获取商品信息时设置
//                detail.setItemId(Long.valueOf(child.getItemId()));

                String handleByTime = child.getHandleByTime();
                detail.setDeliverDeadline(handleByTime);

                long longDate = Long.valueOf(handleByTime.replaceAll("[-\\s:]", ""));

                if (flag) {
                    deliverDeadline = longDate;
                    deliverDeadlineStr = handleByTime;
                    flag = false;
                } else {
                    if (longDate < deliverDeadline) {
                        deliverDeadline = longDate;
                        deliverDeadlineStr = handleByTime;
                    }
                }

                // 商品名称==商品标题？？？
                detail.setItemName(child.getItemTitle());
                detail.setSourceSku(StringUtils.isNotBlank(child.getVariationSku()) ? child.getVariationSku() : child.getSku());
                detail.setSkuTitle(child.getVariationTitle());
                // 销售记录号？？
//                detail.setRecordNumber(child.getRecordNumber());
                sysOrder.getSysOrderDetails().add(detail);
            }

            sysOrder.setDeliverDeadline(deliverDeadlineStr == null ? "" : deliverDeadlineStr);

            EbayOrderStatus orderStatus = ebayOrder.getOrderStatus();

            // 获取授权信息
//            String empowResult = this.remoteSellerService.findOneEmpowByAccount(1, null, null, orderStatus.getEmpowerId().toString());
//            String empowerData = com.rondaful.cloud.common.utils.Utils.returnRemoteResultDataString(empowResult, "获取店铺授权信息时,调用卖家服务异常");
//            Empower empower = JSONObject.parseObject(empowerData, Empower.class);
//            sysOrder.setShopType(String.valueOf(empower.getRentstatus()));
            sysOrder.setSellerPlAccount(orderStatus.getSellerPlAccount());
            sysOrder.setSellerPlId(orderStatus.getSellerPlId());
            sysOrder.setEmpowerId(String.valueOf(orderStatus.getEmpowerId()));
            sysOrder.setPlatformShopId(orderStatus.getEmpowerId());
            sysOrder.setPlatformName("eBay");
            sysOrder.setPlatformType(1);

            sysOrderDTOList.add(sysOrder);

        }
        return sysOrderDTOList;
    }
}
