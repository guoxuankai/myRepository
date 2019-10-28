package com.rondaful.cloud.order.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.entity.third.AppDTO;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.ThirdMsgTypeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.push.service.PushThirdService;
import com.rondaful.cloud.order.entity.system.SysOrderPackage;
import com.rondaful.cloud.order.mapper.SysOrderPackageMapper;
import com.rondaful.cloud.order.model.dto.sysorder.FeignResult;
import com.rondaful.cloud.order.remote.RemoteUserService;
import com.rondaful.cloud.order.service.IDeliverCallBackService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/8/2
 * @Description:
 */
@Service("deliverCallBackServiceImpl")
public class DeliverCallBackServiceImpl implements IDeliverCallBackService {

    @Autowired
    private RemoteUserService userService;

    @Autowired
    private PushThirdService pushService;

    @Autowired
    private SysOrderPackageMapper sysOrderPackageMapper;


    /**
     * 订单发货变更通知
     *
     * @param orderTrackId
     */
    @Override
    @Async
    public void sendDelivery(String orderTrackId) {
        FeignResult<List<AppDTO>> result = this.userService.getAppAll();
        if (CollectionUtils.isNotEmpty(result.getData())){

            SysOrderPackage sysOrderPackage = sysOrderPackageMapper.queryOrderPackageByOrderTrackId(orderTrackId);
            if (sysOrderPackage == null){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "包裹号查询不到数据");
            }

            JSONObject params=new JSONObject();
            params.put("orderTrackId",sysOrderPackage.getOrderTrackId());
            params.put("speed",sysOrderPackage.getPackageStatus());
            params.put("updateTime",sysOrderPackage.getDeliveryTime());
            params.put("shipTrackNumber",sysOrderPackage.getShipTrackNumber());
            params.put("shipOrderId", sysOrderPackage.getShipOrderId());
            params.put("shipCost", sysOrderPackage.getActualShipCost());
            params.put("warehouseShipException", sysOrderPackage.getWarehouseShipException());
            List<AppDTO> appDTOS= JSONArray.parseArray(JSONObject.toJSONString(result.getData()),AppDTO.class);
            appDTOS.forEach(dto->{
                this.pushService.send(dto.getAppKey(), ThirdMsgTypeEnum.INVENTORY,params.toJSONString());
            });
        }
    }
}
