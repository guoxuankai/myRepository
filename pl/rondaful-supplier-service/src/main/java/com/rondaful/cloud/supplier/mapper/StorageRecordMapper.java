package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.storage.StorageRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StorageRecordMapper extends BaseMapper<StorageRecord> {

    /**
     * 分页获取入库单
     * @param warehouseId
     * @param receivingCode
     * @param verify
     * @return
     */
    List<StorageRecord> getsPage(@Param("warehouseIds") List<Integer> warehouseIds, @Param("supplierIds") List<Integer> supplierIds, @Param("receivingCode") String receivingCode, @Param("verify") Integer verify);

    /**
     * 根据仓库id获取需要同步状态的入库单
     * @param warehouseIds
     * @return
     */
    List<StorageRecord> getSyncStatus(@Param("warehouseIds") List<Integer> warehouseIds);

    /**
     * 根据状态查询
     * @param warehouseIds
     * @return
     */
    List<String> getSyncWmsStatus(@Param("warehouseIds") List<Integer> warehouseIds);

    /**
     * 批量跟新入库单状态
     * @param list
     * @return
     */
    Integer batchUpdate(@Param("list") List<StorageRecord> list);
}