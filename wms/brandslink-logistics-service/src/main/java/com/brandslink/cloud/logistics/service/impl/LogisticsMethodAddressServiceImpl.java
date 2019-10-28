package com.brandslink.cloud.logistics.service.impl;

import com.brandslink.cloud.common.service.impl.BaseServiceImpl;
import com.brandslink.cloud.logistics.mapper.LogisticsMethodAddressMapper;
import com.brandslink.cloud.logistics.model.LogisticsMethodAddressModel;
import com.brandslink.cloud.logistics.service.ILogisticsMethodAddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogisticsMethodAddressServiceImpl extends BaseServiceImpl<LogisticsMethodAddressModel> implements ILogisticsMethodAddressService {
    @Autowired
    private LogisticsMethodAddressMapper addressMapper;
    private final static Logger _log = LoggerFactory.getLogger(LogisticsMethodAddressServiceImpl.class);

    @Override
    public Long editMethodAddress(LogisticsMethodAddressModel addressModel) {
        addressMapper.insertUpdateSelective(addressModel);
        return addressModel.getId();
    }
}
