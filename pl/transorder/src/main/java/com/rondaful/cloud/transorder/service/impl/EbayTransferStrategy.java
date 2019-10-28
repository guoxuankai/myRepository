package com.rondaful.cloud.transorder.service.impl;

import com.rondaful.cloud.transorder.entity.ebay.EbayOrderStatus;
import com.rondaful.cloud.transorder.entity.system.SysOrderDTO;
import com.rondaful.cloud.transorder.mapper.EbayOrderStatusMapper;
import com.rondaful.cloud.transorder.service.EbayOrderService;
import com.rondaful.cloud.transorder.service.TransferStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author guoxuankai
 * @date 2019/9/20 16:31
 */
@Component
public class EbayTransferStrategy implements TransferStrategy {

    @Autowired
    private EbayOrderService ebayOrderService;

    @Autowired
    private EbayOrderStatusMapper ebayOrderStatusMapper;

    @Override
    public List<SysOrderDTO> assembleData(List<String> orderIds) {
        return ebayOrderService.assembleData(orderIds);
    }

    @Override
    public void updateConverStatus(String orderId, Byte convertible, String failureReason) {
        EbayOrderStatus ebayOrderStatus = new EbayOrderStatus();
        ebayOrderStatus.setOrderId(orderId);
        ebayOrderStatus.setConverSysStatus(convertible);
        ebayOrderStatus.setUpdateDate(new Date());
        ebayOrderStatusMapper.updateByOrderId(ebayOrderStatus);
    }

}
