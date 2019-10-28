package com.rondaful.cloud.transorder.service.impl;

import com.rondaful.cloud.transorder.entity.system.SysOrderDTO;
import com.rondaful.cloud.transorder.service.TransferStrategy;

import java.util.List;

/**
 * @author guoxuankai
 * @date 2019/9/20 16:31
 */
public class AmazonTransferStrategy implements TransferStrategy {

    @Override
    public List<SysOrderDTO> assembleData(List<String> ids) {
        return null;
    }

    @Override
    public void updateConverStatus(String orderId, Byte convertible, String failureReason) {

    }
}
