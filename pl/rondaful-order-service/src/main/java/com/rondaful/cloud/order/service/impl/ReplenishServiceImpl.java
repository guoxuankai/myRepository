package com.rondaful.cloud.order.service.impl;

import com.rondaful.cloud.order.entity.goodcang.GoodCangOrder;
import com.rondaful.cloud.order.entity.system.SysOrderNew;
import com.rondaful.cloud.order.mapper.SysOrderNewMapper;
import com.rondaful.cloud.order.remote.RemoteErpService;
import com.rondaful.cloud.order.service.IGoodCangService;
import com.rondaful.cloud.order.service.ISystemOrderCommonService;
import com.rondaful.cloud.order.service.IreplenishService;
import com.rondaful.cloud.order.utils.CheckOrderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReplenishServiceImpl implements IreplenishService {

    @Autowired
    private SysOrderNewMapper sysOrderNewMapper;

    @Autowired
    private ISystemOrderCommonService systemOrderCommonService;

    @Autowired
    private SystemOrderServiceImpl systemOrderService;

    @Autowired
    private IGoodCangService goodCangService;

    @Autowired
    private RemoteErpService remoteErpService;


    @Override
    public String replenishDeliverGood(SysOrderNew sysOrder) throws Exception {
        this.validateRequiredField(new ArrayList<SysOrderNew>() {{
            add(sysOrder);
        }});

        //todo 判断邮件方式是否支持
        //boolean isSupportDeliverMethod = systemOrderCommonService.judgeSupportDeliverMethod(sysOrder);
//        if (!isSupportDeliverMethod) {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "订单中商品不支持此邮寄方式。。。");
//        }

        boolean isGoodCangOrder = systemOrderCommonService.isGoodCangWarehouse(sysOrder.getSysOrderPackageList().get(0).getDeliveryWarehouseId().toString());

        if (isGoodCangOrder) {
            GoodCangOrder goodCangOrder = systemOrderService.constructGoodCangOrderDataNew(sysOrder, sysOrder.getSysOrderPackageList().get(0));
            String referenceId = goodCangService.deliverGoodToGoodCang(goodCangOrder);
            return referenceId;
        } else {
            //todo 传入对象
            Map<String, Object> erpOrderMap =  systemOrderService.constructERPOrderDataNew(sysOrder, sysOrder.getSysOrderPackageList().get(0), sysOrder.getSysOrderDetails());
            remoteErpService.orderReceive(erpOrderMap);
            return "SUCCESS";
        }
    }

    private void validateRequiredField(List<SysOrderNew> sysOrders) {
        for (SysOrderNew sysOrder : sysOrders) {
            CheckOrderUtils.validateSysOrderDataForDeliverGood(sysOrder);
        }
    }
}
