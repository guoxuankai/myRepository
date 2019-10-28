package com.brandslink.cloud.logistics.service;

import com.brandslink.cloud.common.service.BaseService;
import com.brandslink.cloud.logistics.model.PlatformInfoModel;

public interface IPlatformInfoService extends BaseService<PlatformInfoModel> {

    Long editPlatform(PlatformInfoModel platformInfoModel);

}
