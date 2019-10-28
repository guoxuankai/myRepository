package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.procurement.ProcurementSuggest;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ProcurementSuggestMapper extends BaseMapper<ProcurementSuggest> {


    /**
     * 分页查询授权建议
     * @param startTime
     * @param endTime
     * @param warehouseId
     * @param status
     * @return
     */
    List<ProcurementSuggest> getsSuggestPage(@Param("startTime") Date startTime,@Param("endTime") Date endTime,
                                             @Param("warehouseId") Integer warehouseId,@Param("status") Integer status);

    /**
     * 根据订单号获取采购建议
     * @param orderId
     * @return
     */
    ProcurementSuggest getByOrderId(String orderId);
}