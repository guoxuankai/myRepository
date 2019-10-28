package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.procurement.Procurement;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ProcurementMapper extends BaseMapper<Procurement> {

    /**
     * 分页查询采购单
     * @param providerId
     * @param startTime
     * @param endTime
     * @param status
     * @param warehouseId
     * @return
     */
    List<Procurement> getsPage(@Param("providerId") Integer providerId,@Param("startTime") Date startTime,@Param("endTime") Date endTime,
                               @Param("status") Integer status,@Param("warehouseId") Integer warehouseId);


    /**
     * 批量插入
     * @param list
     * @return
     */
    Integer insertBatch(@Param("list") List<Procurement> list);
}