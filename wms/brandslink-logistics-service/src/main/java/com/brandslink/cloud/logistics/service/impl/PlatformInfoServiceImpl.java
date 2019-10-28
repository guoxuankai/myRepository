package com.brandslink.cloud.logistics.service.impl;

import com.brandslink.cloud.common.service.impl.BaseServiceImpl;
import com.brandslink.cloud.logistics.mapper.PlatformInfoMapper;
import com.brandslink.cloud.logistics.model.PlatformInfoModel;
import com.brandslink.cloud.logistics.service.IPlatformInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class PlatformInfoServiceImpl extends BaseServiceImpl<PlatformInfoModel> implements IPlatformInfoService {

    private final static Logger _log = LoggerFactory.getLogger(PlatformInfoServiceImpl.class);

    @Autowired
    private PlatformInfoMapper platformInfoMapper;

    @Override
    public Long editPlatform(PlatformInfoModel platformInfoModel) {
        platformInfoMapper.insertUpdateSelective(platformInfoModel);
        return platformInfoModel.getId();
    }
}
