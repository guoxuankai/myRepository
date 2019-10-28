package com.rondaful.cloud.user.storage.service.impl;


import com.rondaful.cloud.user.storage.entity.StorageApply;
import com.rondaful.cloud.user.storage.mapper.WarehousingStorageMapper;
import com.rondaful.cloud.user.storage.service.WarehousingStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("warehousingStorageService")
public class WarehousingStorageServiceImpl implements WarehousingStorageService {

    @Autowired
    private WarehousingStorageMapper warehousingStorageMapper;

    /**
     * 仓储物流商申请
     * @param storageApply
     * @return
     */
    @Override
    public Integer warehousingUserApply(StorageApply storageApply) {
        Integer result = null;
        if (storageApply != null)
            result = warehousingStorageMapper.warehousingUserApply(storageApply);
        return result;
    }
}
