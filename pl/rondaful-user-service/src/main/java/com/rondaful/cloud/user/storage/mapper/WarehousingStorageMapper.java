package com.rondaful.cloud.user.storage.mapper;

import com.rondaful.cloud.user.storage.entity.StorageApply;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WarehousingStorageMapper {

    /**
     * 仓储物流商申请
     * @param storageApply
     * @return
     */
    Integer warehousingUserApply(StorageApply storageApply);



}
