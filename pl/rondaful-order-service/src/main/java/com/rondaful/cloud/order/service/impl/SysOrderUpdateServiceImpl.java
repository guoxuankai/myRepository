package com.rondaful.cloud.order.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.UserUtils;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.entity.SysOrderDetail;
import com.rondaful.cloud.order.entity.SysOrderLog;
import com.rondaful.cloud.order.entity.commodity.CommoditySpec;
import com.rondaful.cloud.order.entity.system.SysOrderNew;
import com.rondaful.cloud.order.entity.system.SysOrderPackage;
import com.rondaful.cloud.order.entity.system.SysOrderPackageDetail;
import com.rondaful.cloud.order.enums.OrderHandleLogEnum;
import com.rondaful.cloud.order.mapper.SysOrderDetailMapper;
import com.rondaful.cloud.order.mapper.SysOrderPackageDetailMapper;
import com.rondaful.cloud.order.mapper.SysOrderPackageMapper;
import com.rondaful.cloud.order.remote.RemoteCommodityService;
import com.rondaful.cloud.order.service.ISysOrderInvoiceService;
import com.rondaful.cloud.order.service.ISysOrderLogService;
import com.rondaful.cloud.order.service.ISysOrderUpdateService;
import com.rondaful.cloud.order.utils.OrderUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysOrderUpdateServiceImpl extends BaseServiceImpl<SysOrderNew> implements ISysOrderUpdateService {
    @Autowired
    private SysOrderServiceImpl sysOrderServiceImpl;
    @Autowired
    private RemoteCommodityService remoteCommodityService;

    @Autowired
    private SysOrderPackageMapper sysOrderPackageMapper;

    @Autowired
    private SysOrderPackageDetailMapper sysOrderPackageDetailMapper;

    @Autowired
    private SysOrderDetailMapper sysOrderDetailMapper;

    @Autowired
    private ISysOrderInvoiceService sysOrderInvoiceService;

    @Autowired
    private ISysOrderLogService sysOrderLogService;

    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;

    @Autowired
    private UserUtils userUtils;

    /**
     * 设置订单商品信息
     *
     * @param sysOrderNew
     */
    @Override
    public void setSysOrderItemInfo(SysOrderNew sysOrderNew, SysOrderNew sysOrder) {
        List<SysOrderDetail> sysOrderDetailList = new ArrayList<>();
        sysOrderNew.getSysOrderPackageList().forEach(sysOrderPackage -> {
            sysOrderPackage.getSysOrderPackageDetailList().forEach(sysOrderPackageDetail -> {
                SysOrderDetail sysOrderDetail = new SysOrderDetail();
                sysOrderDetail.setSkuQuantity(sysOrderPackageDetail.getSkuQuantity());
                sysOrderDetail.setSku(sysOrderPackageDetail.getSku());
                sysOrderDetail.setSourceOrderLineItemId(sysOrderPackageDetail.getSourceOrderLineItemId());
                sysOrderDetail.setSourceOrderId(sysOrderPackageDetail.getSourceOrderLineItemId());
                sysOrderDetail.setSourceOrderId(sysOrderPackageDetail.getBindStatus());
                sysOrderDetailList.add(sysOrderDetail);
            });
        });
        sysOrder.setSysOrderDetails(sysOrderDetailList);

        for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
            if (StringUtils.isBlank(sysOrderDetail.getSku())){
                continue;
            }
            String result = remoteCommodityService.test("1", "1", null, null, null, null,
                    sysOrderDetail.getSku(), null, null);
            String data = Utils.returnRemoteResultDataString(result, "调用商品服务异常");
            JSONObject parse1 = (JSONObject) JSONObject.parse(data);
            String pageInfo = parse1.getString("pageInfo");
            JSONObject parse2 = (JSONObject) JSONObject.parse(pageInfo);
            JSONArray list1 = parse2.getJSONArray("list");
            List<CommoditySpec> commodityDetails = list1.toJavaList(CommoditySpec.class);
            for (CommoditySpec commodityDetail : commodityDetails) {
                //体积
                sysOrderDetail.setBulk(commodityDetail.getPackingHeight().multiply(commodityDetail.getPackingWidth()).multiply(commodityDetail.getPackingLength()));
                //重量
                sysOrderDetail.setWeight(commodityDetail.getPackingWeight());
                //商品ID
                sysOrderDetail.setItemId(commodityDetail.getId());
                //商品URL
                sysOrderDetail.setItemUrl(commodityDetail.getMasterPicture());
                //商品名称
                sysOrderDetail.setItemName(commodityDetail.getCommodityNameCn());
                sysOrderDetail.setItemNameEn(commodityDetail.getCommodityNameEn());
                //商品属性
                sysOrderDetail.setItemAttr(commodityDetail.getCommoditySpec());
                //订单项SKU
                sysOrderDetail.setSku(commodityDetail.getSystemSku());
                //SKU标题
                sysOrderDetail.setSkuTitle(commodityDetail.getCommodityNameCn());
                //供应商ID
                sysOrderDetail.setSupplierId(Long.valueOf(commodityDetail.getSupplierId()));
                //供应商名称
                sysOrderDetail.setSupplierName(commodityDetail.getSupplierName());
                //供应商SKU
                sysOrderDetail.setSupplierSku(commodityDetail.getSupplierSku());
                //供应商SKU标题
                sysOrderDetail.setSupplierSkuTitle(commodityDetail.getCommodityNameCn());
                //服务费 优先取固定服务费
                sysOrderDetail.setFareTypeAmount(commodityDetail.getFeePriceUs() != null ? "1#" + commodityDetail.getFeePriceUs().toString() : "2#" + commodityDetail.getFeeRate().toString());
                //是否包邮
                sysOrderDetail.setFreeFreight(commodityDetail.getFreeFreight());
                sysOrderServiceImpl.setSupplyChainInfo(null, sysOrderDetail, false);
            }
        }
        //判断该订单是包邮还是不包邮还是部分包邮
        int count = 0;
        for (SysOrderDetail sysOrderDetail : sysOrderNew.getSysOrderDetails()) {
            count += sysOrderDetail.getFreeFreight();
        }
        if (count == 0) {
            sysOrderNew.setFreeFreightType(Byte.valueOf(String.valueOf(Constants.SysOrder.NOT_FREE_FREIGHT)));
        } else if (count == sysOrderNew.getSysOrderDetails().size()) {
            sysOrderNew.setFreeFreightType(Byte.valueOf(String.valueOf(Constants.SysOrder.FREE_FREIGHT)));
        } else {
            sysOrderNew.setFreeFreightType(Byte.valueOf(String.valueOf(Constants.SysOrder.PART_FREE_FREIGHT)));
        }
    }

    /**
     *  设置包裹详情信息
     * @param loginName
     * @param orderNew
     */
    @Override
    public void setOrderPackageDetailInfo(String loginName, SysOrderNew orderNew) {
        SysOrderPackage orderPackage = orderNew.getSysOrderPackageList().get(0);
        //设置包裹详情数据
        List<SysOrderPackageDetail> orderPackageDetailList = new ArrayList<>();
        for (SysOrderDetail sysOrderDetail: orderNew.getSysOrderDetails()){
            SysOrderPackageDetail orderPackageDetail = new SysOrderPackageDetail();
            orderPackageDetail.setSourceOrderLineItemId(sysOrderDetail.getSourceOrderLineItemId());
            orderPackageDetail.setSourceSku(sysOrderDetail.getSourceSku());
            orderPackageDetail.setOrderTrackId(orderPackage.getOrderTrackId());
            orderPackageDetail.setBindStatus(sysOrderDetail.getBindStatus());
            if (StringUtils.isNotBlank(sysOrderDetail.getSku())){
                orderPackageDetail.setSku(sysOrderDetail.getSku());
                orderPackageDetail.setSkuQuantity(sysOrderDetail.getSkuQuantity());
                orderPackageDetail.setSkuCost(sysOrderDetail.getItemCost());
                orderPackageDetail.setSkuUrl(sysOrderDetail.getItemUrl());
                orderPackageDetail.setSkuName(sysOrderDetail.getItemName());
                orderPackageDetail.setSkuNameEn(sysOrderDetail.getItemNameEn());
                orderPackageDetail.setSkuAttr(sysOrderDetail.getItemAttr());
                orderPackageDetail.setSkuPrice(sysOrderDetail.getItemPrice());
                orderPackageDetail.setBulk(sysOrderDetail.getBulk());
                orderPackageDetail.setWeight(sysOrderDetail.getWeight());
                orderPackageDetail.setBulk(sysOrderDetail.getBulk());
                orderPackageDetail.setSupplierId(Math.toIntExact(sysOrderDetail.getSupplierId()));
                orderPackageDetail.setSupplierName(sysOrderDetail.getSupplierName());
                orderPackageDetail.setSupplyChainCompanyId(sysOrderDetail.getSupplyChainCompanyId());
                orderPackageDetail.setSupplyChainCompanyName(sysOrderDetail.getSupplyChainCompanyName());
                orderPackageDetail.setFareTypeAmount(sysOrderDetail.getFareTypeAmount());
                orderPackageDetail.setFreeFreight(sysOrderDetail.getFreeFreight());
                orderPackageDetail.setCreater(loginName);
                orderPackageDetail.setModifier(loginName);
            }
            orderPackageDetailList.add(orderPackageDetail);

        }

        orderPackage.setSysOrderPackageDetailList(orderPackageDetailList);
        orderNew.setSysOrderPackageList(new ArrayList<SysOrderPackage>() {{
            add(orderPackage);
        }});
    }

    /**
     * 更新包裹信息
     * @param orderNew
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateInfo(SysOrderNew orderNew) {
        setSysOrderInvoiceInfo(orderNew);//新建或者编辑订单发票信息
        //删除包裹详情信息
        List<String> orderTrackId = orderNew.getSysOrderPackageList().stream().map(x -> x.getOrderTrackId()).collect(Collectors.toList());
        sysOrderPackageDetailMapper.deleteBatchBySysOrderTrackId(orderTrackId);

        for (SysOrderPackage orderPackage : orderNew.getSysOrderPackageList()) {//编辑包裹信息并且添加操作日志
            sysOrderPackageMapper.editPackageInfo(orderPackage);
            for (SysOrderPackageDetail item : orderPackage.getSysOrderPackageDetailList()) { //新增包裹详情
                sysOrderPackageDetailMapper.insertSelective(item);
            }
            sysOrderLogService.insertSelective(new SysOrderLog(orderNew.getSysOrderId(), OrderHandleLogEnum.Content.EDIT_PACKAGE.editPackage(orderPackage.getOrderTrackId(), orderPackage.getDeliveryWarehouse(), orderPackage.getDeliveryMethod()), OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(), userUtils.getUser().getUsername()));
        }

        //删除订单详情
        List<String> orderId = Arrays.asList(orderNew.getSysOrderId());
        sysOrderDetailMapper.deleteBatchBySysOrderId(orderId);

        //新增订单详情
        for (SysOrderDetail sysOrderDetail: orderNew.getSysOrderDetails()){
            sysOrderDetail.setSysOrderId(orderNew.getSysOrderId());
            sysOrderDetail.setOrderLineItemId(OrderUtils.getPLOrderItemNumber());
            sysOrderDetailMapper.insertSelective(sysOrderDetail);
        }


    }

    /**
     * 新建或者编辑订单发票信息
     * @param sysOrder
     */
    public void setSysOrderInvoiceInfo(SysOrderNew sysOrder) {
        if (sysOrder.getSysOrderInvoiceInsertOrUpdateDTO() != null) {
            String loginName = getLoginUserInformationByToken.getUserDTO().getLoginName();
            if ((sysOrderInvoiceService.getSysOrderInvoiceBySysOrderId(sysOrder.getSysOrderId()) == null)) {
                //新建  +创建人  +更新人
                sysOrder.getSysOrderInvoiceInsertOrUpdateDTO().setCreator(loginName);
                sysOrder.getSysOrderInvoiceInsertOrUpdateDTO().setModifier(loginName);
                sysOrderInvoiceService.insertOrUpdateSysOrderInvoice(sysOrder.getSysOrderInvoiceInsertOrUpdateDTO());
            } else {
                //编辑   只+更新人
                sysOrder.getSysOrderInvoiceInsertOrUpdateDTO().setModifier(loginName);
                sysOrderInvoiceService.insertOrUpdateSysOrderInvoice(sysOrder.getSysOrderInvoiceInsertOrUpdateDTO());
            }
        }
    }
}
