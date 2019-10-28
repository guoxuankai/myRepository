package com.rondaful.cloud.order.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.enums.ERPOrderEnum;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.HttpUtil;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.entity.SysOrderDetail;
import com.rondaful.cloud.order.entity.erpentity.ERPOrderDetail;
import com.rondaful.cloud.order.entity.erpentity.WareHouseDeliverCallBack;
import com.rondaful.cloud.order.entity.supplier.WarehouseDTO;
import com.rondaful.cloud.order.entity.system.SysOrderNew;
import com.rondaful.cloud.order.entity.system.SysOrderPackage;
import com.rondaful.cloud.order.entity.system.SysOrderPackageDetail;
import com.rondaful.cloud.order.entity.system.SysOrderReceiveAddress;
import com.rondaful.cloud.order.entity.wmsdto.WareHouseWmsCallBack;
import com.rondaful.cloud.order.enums.OrderSourceCovertToGoodCandPlatformEnum;
import com.rondaful.cloud.order.enums.WmsEnum;
import com.rondaful.cloud.order.mapper.SysOrderPackageMapper;
import com.rondaful.cloud.order.model.dto.remoteErp.GetOrderSpeedInfoVO;
import com.rondaful.cloud.order.model.dto.wms.WmsOrderDTO;
import com.rondaful.cloud.order.model.dto.wms.WmsOrderDetailDTO;
import com.rondaful.cloud.order.model.dto.wms.WmsRecipientsDTO;
import com.rondaful.cloud.order.model.dto.wms.WmsSenderDTO;
import com.rondaful.cloud.order.service.IGoodCangService;
import com.rondaful.cloud.order.service.ISystemOrderCommonService;
import com.rondaful.cloud.order.service.ISystemOrderService;
import com.rondaful.cloud.order.service.IWmsService;
import com.rondaful.cloud.order.utils.ErpHttpUtils;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author Blade
 * @date 2019-08-09 10:27:11
 **/
@Service
public class WmsServiceImpl implements IWmsService {

    @Autowired
    private ISystemOrderCommonService systemOrderCommonService;

    @Autowired
    private SysOrderPackageMapper sysOrderPackageMapper;

    @Autowired
    private IGoodCangService iGoodCangService;

    @Autowired
    private ISystemOrderService iSystemOrderService;

    @Value("${brandslink.wms.url}")
    private String wmsUrl;

    private final static Logger _log = LoggerFactory.getLogger(SystemOrderServiceImpl.class);

    /**
     * wms发货
     * @param wmsOrderDTO
     * @param warehouseId
     */
    @Override
    public void createWmsOrder(WmsOrderDTO wmsOrderDTO, String warehouseId) {
        String url = wmsUrl + Constants.WmsSystem.CREATE_ORDER_URL;


        WarehouseDTO warehouseDTO = systemOrderCommonService.getWarehouseInfo(warehouseId);
        String resp = null;
        try {
            URIBuilder uri=new URIBuilder(url);
            uri.addParameter("customerAppId",warehouseDTO.getAppKey());
            uri.addParameter("sign",warehouseDTO.getAppToken());
            _log.debug("WMS包裹{} 发货,内容是: {}", wmsOrderDTO.getPackageNum(), FastJsonUtils.toJsonString(wmsOrderDTO));
            _log.debug("WMS包裹发货,地址是: {}", uri.toString());
            resp= HttpUtil.wmsPost(uri.toString(),wmsOrderDTO);
        } catch (Exception e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "调用wms服务异常");
        }
        JSONObject respJson = JSONObject.parseObject(resp);
        _log.debug("WMS包裹发货返回: {}", respJson);
        if (!respJson.getBoolean("success")){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "WMS返回->"+respJson.getString("msg"));
        }
    }

    /**
     * 组装wms订单数据
     * @param sysOrderNew
     * @param sysOrderPackage
     * @param sysOrderDetailList
     * @return
     */
    @Override
    public WmsOrderDTO assembleWmsOrderDate(SysOrderNew sysOrderNew,
                                             SysOrderPackage sysOrderPackage,
                                             List<SysOrderDetail> sysOrderDetailList) {
        WmsOrderDTO wmsOrderDTO = new WmsOrderDTO();
        List<WmsOrderDetailDTO> wmsOrderDetailDTOList = new ArrayList<>();
        wmsOrderDTO.setOrderNum(sysOrderNew.getSourceOrderId());
        wmsOrderDTO.setPackageNum(sysOrderPackage.getOrderTrackId());
        wmsOrderDTO.setReceiverCode(sysOrderPackage.getShippingCarrierUsedCode());
        wmsOrderDTO.setReceiver(sysOrderPackage.getShippingCarrierUsed());
        wmsOrderDTO.setMailingMethodCode(sysOrderPackage.getDeliveryMethodCode());
        wmsOrderDTO.setMailingMethod(sysOrderPackage.getDeliveryMethod());
        wmsOrderDTO.setBuyer(sysOrderNew.getBuyerName());
        wmsOrderDTO.setLatestDeliveryTime(sysOrderNew.getDeliverDeadline());
        //orderDetailsList	是	List	包裹详情,可传多个
        List<SysOrderPackageDetail> sysOrderPackageDetailList = sysOrderPackage.getSysOrderPackageDetailList();
        for (SysOrderPackageDetail sysOrderPackageDetail : sysOrderPackageDetailList) {
            WmsOrderDetailDTO wmsOrderDetailDTO = new WmsOrderDetailDTO();
            SysOrderDetail sysOrderDetail = null;

            String plSku = sysOrderPackageDetail.getSku();
            for (SysOrderDetail orderDetail : sysOrderDetailList) {
                if (Objects.equals(plSku, orderDetail.getSku())) {
                    sysOrderDetail = orderDetail;
                    break;
                }
            }

            if (null == sysOrderDetail) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "包裹商品信息错误");
            }
            wmsOrderDetailDTO.setSku(sysOrderPackageDetail.getSku());
            wmsOrderDetailDTO.setNumber(sysOrderPackageDetail.getSkuQuantity());
            wmsOrderDetailDTO.setsName(sysOrderPackageDetail.getSkuName());
            wmsOrderDetailDTO.setsEnglishName(sysOrderPackageDetail.getSkuNameEn());
            wmsOrderDetailDTO.setSupplier(sysOrderPackageDetail.getSupplierId().toString());
            wmsOrderDetailDTOList.add(wmsOrderDetailDTO);
        }
        wmsOrderDTO.setOrderDetailsList(wmsOrderDetailDTOList);

        wmsOrderDTO.setOrderType("1");
        wmsOrderDTO.setPaymentTime(sysOrderNew.getPayTime());

        // 收货地址
        WmsRecipientsDTO wmsRecipientsDTO = new WmsRecipientsDTO();
        SysOrderReceiveAddress sysOrderReceiveAddress = sysOrderNew.getSysOrderReceiveAddress();
        wmsRecipientsDTO.setCountryCode(sysOrderReceiveAddress.getShipToCountry());
        wmsRecipientsDTO.setCountryName(sysOrderReceiveAddress.getShipToCountryName());
        wmsRecipientsDTO.setState(sysOrderReceiveAddress.getShipToState());
        wmsRecipientsDTO.setCity(sysOrderReceiveAddress.getShipToCity());
        wmsRecipientsDTO.setAddressOne(sysOrderReceiveAddress.getShipToAddrStreet1());
        wmsRecipientsDTO.setAddressTwo(sysOrderReceiveAddress.getShipToAddrStreet2());
        wmsRecipientsDTO.setMobilePhone(sysOrderReceiveAddress.getShipToPhone());
        wmsRecipientsDTO.setSur(sysOrderReceiveAddress.getShipToName());
        wmsRecipientsDTO.setName(sysOrderReceiveAddress.getShipToName());
        wmsRecipientsDTO.setEmail(sysOrderReceiveAddress.getShipToEmail());
        wmsRecipientsDTO.setPostCode(sysOrderReceiveAddress.getShipToPostalCode());
        wmsOrderDTO.setRecipients(wmsRecipientsDTO);
        wmsOrderDTO.setSalesChannels(OrderSourceCovertToGoodCandPlatformEnum.getGoodCangPlatformCode(sysOrderNew.getOrderSource()));

        //sender	否	Object	寄件人信息存在时，只能为一个	100
        WmsSenderDTO wmsSenderDTO = new WmsSenderDTO();
        wmsOrderDTO.setSender(wmsSenderDTO);

        wmsOrderDTO.setShopName(sysOrderNew.getPlatformSellerAccount());
        wmsOrderDTO.setTotalMoney(sysOrderNew.getTotal());
        wmsOrderDTO.setWarehouseCode(sysOrderPackage.getDeliveryWarehouseCode());
        wmsOrderDTO.setWarehouseName(sysOrderPackage.getDeliveryWarehouse());

        return wmsOrderDTO;
    }

    @Override
    public void cancelWmsOrder() {
        String url = wmsUrl + Constants.WmsSystem.CANCEL_ORDER_URL;

    }

    /**
     * wms订单信息_xd
     *
     * @throws Exception
     */
    @Override
    public void getSysOrderWMSSpeedInfo() throws Exception {
        _log.info("批量拉取WMS订单状态开始");//   TODO
        List<Integer> wmsWarehouseIdList = systemOrderCommonService.getWmsWarehouseIdList();
        //1,查询出WMS所有配货中的包裹
        List<SysOrderPackage> wmsPackageList = sysOrderPackageMapper.getPackageByWarehouseId(wmsWarehouseIdList);
        List<Map<String, Object>> maps = new ArrayList<>();
        for (SysOrderPackage sysOrderPackage: wmsPackageList){
            Map<String, Object> map = new HashMap<>();
            map.put("warehouseCode", sysOrderPackage.getDeliveryWarehouseCode());
            map.put("packageNum", sysOrderPackage.getOrderTrackId());
            maps.add(map);
        }

        if (CollectionUtils.isEmpty(wmsPackageList)) {
            return;
        }
        _log.info("批量拉取WMS订单状态，传入的跟踪号列表为: {}", maps.toString());
        //调用wms返回结果
        String result = this.getOrderSpeedInfo(wmsPackageList.get(0).getDeliveryWarehouseId().toString(), maps);

        List<WareHouseWmsCallBack> orderSpeedInfoList = JSONArray.parseArray(result, WareHouseWmsCallBack.class);
        if (CollectionUtils.isEmpty(orderSpeedInfoList)) {
            _log.info("获取不到WMS 订单数据");
            return;
        }

        for (WareHouseWmsCallBack wmsCallBack : orderSpeedInfoList) {
            if (!wmsCallBack.getOrderStatus().equals(WmsEnum.SHIPPING_TIME.getDeliveryStatus()) && !wmsCallBack.getOrderStatus().equals(WmsEnum.SHIPPING_FAILED.getDeliveryStatus())){
                _log.info("包裹状态为：{}，不执行", wmsCallBack.getOrderStatus());
                continue;
            }
            String orderTrackId = null;
            try {
                orderTrackId = wmsCallBack.getPackageNum();
                String orderStatus = wmsCallBack.getOrderStatus();
                String speed = WmsEnum.getSpeedCode(orderStatus);
                String warehouseShipException = wmsCallBack.getReceiverMark();
                String channelOrderNumber = orderTrackId;
                String shipTrackNumber = wmsCallBack.getTrackingNum();
                String shipOrderId = wmsCallBack.getWaybillNum();
                BigDecimal actualShipCost = BigDecimal.ZERO;

                //组装调用订单回传参数
                WareHouseDeliverCallBack deliverCallBack = new WareHouseDeliverCallBack();
                deliverCallBack.setSpeed(speed);
                if (orderStatus.equals(WmsEnum.SHIPPING_FAILED.getDeliveryStatus())){
                    //状态为【5物流获取失败】才设置异常信息
                    deliverCallBack.setWarehouseShipException(warehouseShipException);
                }

                deliverCallBack.setOrderTrackId(channelOrderNumber);
                deliverCallBack.setWarehouseType(Constants.WarehouseType.WMS);
                deliverCallBack.setShipTrackNumber(shipTrackNumber);
                deliverCallBack.setShipOrderId(shipOrderId);
                deliverCallBack.setActualShipCost(actualShipCost);

                boolean isCallWarehouseDeliver = iGoodCangService.isUpdateSysLog(channelOrderNumber, speed,
                        shipTrackNumber, shipOrderId, Constants.WarehouseType.WMS);

                if (isCallWarehouseDeliver) {
                    _log.info("(定时任务批量获取ERP订单状态)包裹{} 调用订单回传方法,内容为 {}", orderTrackId, deliverCallBack);
                    iSystemOrderService.wareHouseDeliverCallBackNew(deliverCallBack);
                } else {
                    _log.info("(定时任务批量获取ERP订单状态)包裹{} 无需调用订单回传方法,内容为 {}", orderTrackId, deliverCallBack);
                }
            } catch (Exception e) {
                _log.error("包裹{} 更新WMS的状态异常", orderTrackId);
            }
        }
        _log.info("批量拉取WMS订单状态结束");
    }

    /**
     * 查询Wms包裹状态
     * @param warehouseId
     * @param maps
     * @return
     * @throws Exception
     */
    public String getOrderSpeedInfo(String warehouseId, List<Map<String, Object>> maps) throws Exception {
        String url = wmsUrl + Constants.WmsSystem.FIND_PACKAGE_STATE;

        WarehouseDTO warehouseDTO = systemOrderCommonService.getWarehouseInfo(warehouseId);
        String resp = null;
        try {
            URIBuilder uri=new URIBuilder(url);
            uri.addParameter("customerAppId",warehouseDTO.getAppKey());
            uri.addParameter("sign",warehouseDTO.getAppToken());

            _log.debug("WMS查询包裹状态,地址是: {}", uri.toString());
            resp= HttpUtil.wmsPost(uri.toString(), maps);
        } catch (Exception e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100407, "调用wms服务异常");
        }
        JSONObject respJson = JSONObject.parseObject(resp);
        _log.debug("WMS查询包裹状态返回: {}", respJson);
        if (!respJson.getBoolean("success")){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100407, respJson.getString("msg"));
        }

        return respJson.getString("data");
    }

}
