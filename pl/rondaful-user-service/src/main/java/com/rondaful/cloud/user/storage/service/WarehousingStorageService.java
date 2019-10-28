package com.rondaful.cloud.user.storage.service;

import com.rondaful.cloud.user.storage.entity.StorageApply;

public interface WarehousingStorageService {

    /**
     * 仓储物流商申请
     * @param storageApply
     * @return
     */
    Integer warehousingUserApply(StorageApply storageApply);

}
