package com.rondaful.cloud.transorder.service;

import com.rondaful.cloud.transorder.entity.system.SysOrderDTO;

import java.util.List;

/**
 * @author guoxuankai
 * @date 2019/9/20 16:25
 */
public class TransferContext {

    private TransferStrategy strategy;

    public TransferContext(TransferStrategy strategy) {
        this.strategy = strategy;
    }

    public List<SysOrderDTO> assembleData(List<String> orderIds) {
        return strategy.assembleData(orderIds);
    }

    public void updateConverStatus(String orderId, Byte convertible, String failureReason) {
        strategy.updateConverStatus(orderId, convertible, failureReason);
    }
}
