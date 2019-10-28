package com.brandslink.cloud.logistics.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.logistics.model.PlatformInfoModel;

public interface PlatformInfoMapper extends BaseMapper<PlatformInfoModel> {

    void insertUpdateSelective(PlatformInfoModel platformInfoModel);

}