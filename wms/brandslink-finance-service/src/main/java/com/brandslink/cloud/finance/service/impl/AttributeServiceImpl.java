package com.brandslink.cloud.finance.service.impl;

import com.brandslink.cloud.finance.pojo.entity.Attribute;
import com.brandslink.cloud.finance.mapper.AttributeMapper;
import com.brandslink.cloud.finance.remote.RemoteTestService;
import com.brandslink.cloud.finance.service.AttributeService;
import com.brandslink.cloud.common.service.impl.BaseServiceImpl;
import com.codingapi.tx.annotation.TxTransaction;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AttributeServiceImpl extends BaseServiceImpl<Attribute> implements AttributeService {

    @Resource
    AttributeMapper attributeMapper;
    @Resource
    private RemoteTestService remoteTestService;

    @Override
    @TxTransaction(isStart = true)
    public Object updateAttribute(Attribute attribute) {

        int result = attributeMapper.update(attribute);
        Object o = remoteTestService.updateTest(attribute);
        int a = 1/0;
        return o;
    }
}
