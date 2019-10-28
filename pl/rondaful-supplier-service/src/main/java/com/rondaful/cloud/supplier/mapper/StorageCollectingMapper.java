package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.storage.StorageCollecting;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StorageCollectingMapper extends BaseMapper<StorageCollecting> {

    /**
     * 批量插入揽件地址
     * @param list
     * @return
     */
    Integer insertBatch(@Param("list") List<StorageCollecting> list);

    /**
     * 根据入库单id获取揽件地址列表
     * @param storageId
     * @return
     */
    List<StorageCollecting> getsByStorageId(@Param("storageId") Long storageId);

    /**
     * 删除入库单记录
     * @param storageId
     * @return
     */
    Integer delByStorageId(@Param("storageId") Long storageId);
}