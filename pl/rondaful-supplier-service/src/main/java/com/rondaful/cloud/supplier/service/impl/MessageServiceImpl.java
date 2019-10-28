package com.rondaful.cloud.supplier.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.enums.ThirdMsgTypeEnum;
import com.rondaful.cloud.common.push.service.PushThirdService;
import com.rondaful.cloud.supplier.entity.inventory.Inventory;
import com.rondaful.cloud.supplier.mapper.InventoryMapper;
import com.rondaful.cloud.supplier.model.dto.FeignResult;
import com.rondaful.cloud.supplier.model.dto.reomte.user.AppDTO;
import com.rondaful.cloud.supplier.model.enums.StatusEnums;
import com.rondaful.cloud.supplier.remote.RemoteUserService;
import com.rondaful.cloud.supplier.service.IMessageService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/8/2
 * @Description:
 */
@Service("messageServiceImpl")
public class MessageServiceImpl implements IMessageService {

    @Autowired
    private RemoteUserService userService;

    @Autowired
    private PushThirdService pushService;
    @Autowired
    private InventoryMapper inventoryMapper;

    /**
     * 库存变更通知
     *
     * @param warehouseId
     * @param supplierSku
     */
    @Override
    @Async
    public void sendInventory(Integer warehouseId, String supplierSku) {
        FeignResult<List<AppDTO>> result=this.userService.getAppAll();
        if (CollectionUtils.isNotEmpty(result.getData())){
            List<String> skus=new ArrayList<>();
            skus.add(supplierSku);
            List<Inventory> list=this.inventoryMapper.getsBySku(warehouseId,skus);
            if (CollectionUtils.isEmpty(list)){
                return;
            }
            JSONObject params=new JSONObject();
            params.put("pinlianSku",list.get(0).getPinlianSku());
            params.put("warehouseCode",list.get(0).getWarehouseId());
            params.put("availableQty",list.get(0).getAvailableQty());
            params.put("instransitQty",list.get(0).getInstransitQty());
            params.put("waitingShippingQty",list.get(0).getWaitingShippingQty());
            params.put("defectsQty",list.get(0).getDefectsQty());
            List<AppDTO> appDTOS= JSONArray.parseArray(JSONObject.toJSONString(result.getData()),AppDTO.class);
            appDTOS.forEach(dto->{
                if (StatusEnums.ACTIVATE.getStatus().equals(dto.getStatus()) &&StringUtils.isNotEmpty(dto.getRoleBack())){
                    this.pushService.send(dto.getAppKey(), ThirdMsgTypeEnum.INVENTORY,params.toJSONString());
                }
            });
        }
    }
}
