package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.storage.StorageRecordItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StorageRecordItemMapper extends BaseMapper<StorageRecordItem> {

    /**
     * 批量插入入库记录明细
     * @param list
     * @return
     */
    Integer insertBatch(@Param("list") List<StorageRecordItem> list);

    /**
     * 根据入库单id获取
     * @param storageId
     * @return
     */
    List<StorageRecordItem> getsByStorageId(@Param("storageId") Long storageId);

    /**
     * 删除旧入库单列表
     * @param storageId
     * @return
     */
    Integer delByStorageId(@Param("storageId") Long storageId);

    /**
     * 批量修改
     * @param items
     * @return
     */
    Integer updateNum(StorageRecordItem item);
}