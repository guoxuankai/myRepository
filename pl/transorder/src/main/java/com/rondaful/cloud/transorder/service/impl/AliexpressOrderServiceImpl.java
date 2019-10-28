package com.rondaful.cloud.transorder.service.impl;

import com.rondaful.cloud.transorder.entity.aliexpress.AliexpressOrder;
import com.rondaful.cloud.transorder.entity.system.SysOrder;
import com.rondaful.cloud.transorder.entity.system.SysOrderDTO;
import com.rondaful.cloud.transorder.mapper.AliexpressOrderMapper;
import com.rondaful.cloud.transorder.service.AliexpressOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author guoxuankai
 * @date 2019/9/21 9:34
 */
@Service
public class AliexpressOrderServiceImpl implements AliexpressOrderService {

    @Autowired
    private AliexpressOrderMapper aliexpressOrderMapper;


    @Override
    public List<SysOrderDTO> assembleData(List<String> orderIds) {
        List<AliexpressOrder> aliexpressOrders = aliexpressOrderMapper.getsByOrderIds(orderIds);
        return null;
    }
}
