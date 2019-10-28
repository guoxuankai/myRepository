package com.rondaful.cloud.transorder.service;

import com.rondaful.cloud.transorder.entity.ConvertOrderVO;
import com.rondaful.cloud.transorder.entity.system.SysOrder;
import com.rondaful.cloud.transorder.entity.system.SysOrderDTO;

import java.util.List;

public interface TransferStrategy {

    /**
     * 组装数据
     * @param orderIds 订单id集合
     * @return
     */
    List<SysOrderDTO> assembleData(List<String> orderIds);


    void updateConverStatus(String orderId,Byte convertible,String failureReason);
}
