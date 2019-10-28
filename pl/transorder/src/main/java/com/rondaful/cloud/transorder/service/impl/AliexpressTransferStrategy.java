package com.rondaful.cloud.transorder.service.impl;

import com.rondaful.cloud.transorder.entity.system.SysOrderDTO;
import com.rondaful.cloud.transorder.service.AliexpressOrderService;
import com.rondaful.cloud.transorder.service.TransferStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author guoxuankai
 * @date 2019/9/20 16:31
 */
@Component
public class AliexpressTransferStrategy implements TransferStrategy {


    @Autowired
    private AliexpressOrderService aliexpressOrderService;

    @Override
    public List<SysOrderDTO> assembleData(List<String> orderIds) {
        return aliexpressOrderService.assembleData(orderIds);
    }

    @Override
    public void updateConverStatus(String orderId, Byte convertible, String failureReason) {

    }
}
