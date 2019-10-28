package com.rondaful.cloud.supplier.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.supplier.entity.procurement.ProcurementList;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProcurementListMapper extends BaseMapper<ProcurementList> {

    /**
     * 批量插入采购清单
     * @param list
     * @return
     */
    Integer insertBatch(@Param("list") List<ProcurementList> list);

    /**
     * 日常关注脑残设计  查询一个单
     * @param procurementId
     * @return
     */
    ProcurementList getOne(@Param("procurementId") Long procurementId);

    /**
     * 根据采购单货获取列表
     * @param procurementId
     * @return
     */
    List<ProcurementList> getByPId(@Param("procurementId") Long procurementId);

    /**
     * 批量修改订单项
     * @param list
     * @return
     */
    Integer updateBatch(@Param("list") List<ProcurementList> list);
}